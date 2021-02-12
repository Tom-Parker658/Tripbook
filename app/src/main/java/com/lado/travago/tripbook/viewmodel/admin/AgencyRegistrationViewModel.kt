package com.lado.travago.tripbook.viewmodel.admin

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.OnlineTravelAgency
import com.lado.travago.tripbook.model.enums.Region
import com.lado.travago.tripbook.model.users.User
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyRegistrationViewModel : ViewModel() {
    private val authRepo = FirebaseAuthRepo()
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()

    //Contains a list of created scanners
    private val _listOfScanners = MutableLiveData<MutableList<User.UserBasicInfo>>()
    val listOfScanners get() = _listOfScanners

    var logoFilename = ""
        private set

    var otaPath = ""
        private set

    //Agency Fields
    var logoBitmap: Bitmap? = null
        private set
    var nameField = ""
        private set
    var mottoField = ""
        private set
    var supportEmailField = ""
        private set
    var supportPhoneField = ""
        private set
    var bankField = ""
        private set
    var momoField = ""
        private set
    var orangeMoneyField = ""
        private set
    var vehicleNumberField = ""
        private set
    var costPerKm = "10.0"
        private set

    val regions: MutableList<Region> = mutableListOf()

    //Loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading get() = _onLoading

    //Completion state
    private val _onOtaCreated = MutableLiveData(false)
    val onOtaCreated get() = _onOtaCreated


    /**
     * Adds a new scanner object) to the [_listOfScanners]
     */
    fun addCreatedScannerToList(userInfo: User.UserBasicInfo) {
        if (_listOfScanners.value == null)
            _listOfScanners.value = mutableListOf(userInfo)
        else
            _listOfScanners.value!! += userInfo
    }


    /**
     * Saves the agency fields to a viewModel variables
     */
    fun saveField(key: FieldTags, value: Any) {
        when (key) {
            FieldTags.NAME -> nameField = value.toString()
            FieldTags.MOTTO -> mottoField = value.toString()
            FieldTags.EMAIL -> supportEmailField = value.toString()
            FieldTags.PHONE -> supportPhoneField = value.toString()
            FieldTags.BANK -> bankField = value.toString()
            FieldTags.MOMO -> momoField = value.toString()
            FieldTags.ORANGE -> orangeMoneyField = value.toString()
            FieldTags.NUM_VEHICLES -> vehicleNumberField = value.toString()
            FieldTags.COST_PER_KM -> costPerKm = value.toString()
            FieldTags.REGIONS -> regions.add(value as Region)
            FieldTags.LOGO_NAME -> logoFilename = value.toString()
            FieldTags.LOGO_BITMAP -> logoBitmap = value as Bitmap
        }
    }


    /**
     * Creates the Agency and adds it to the db. When successful, it makes sure we navigate to the fragment to add scanners
     * Then finally, it deletes the temporary current account created for the process sake
     */
    suspend fun createOTA() {
        val logoStream = Utils.convertBitmapToStream(
            logoBitmap,
            Bitmap.CompressFormat.PNG,
            0
        )

        /**
         * Generate an anonymous account which can be use by the Scanner to create the Agency and returns
         * the state of the operation. We then continue if the state is success
         */
        authRepo.signInAnonymously().collect { authState ->
            when (authState) {
                is State.Loading -> startLoading()
                is State.Failed -> {
                    stopLoading()
                    Log.e(TAG, "Authentication: ${authState.message}")
                }
                is State.Success -> {
                    stopLoading()
                    /**
                     * Upload the logo to the storage
                     */
                    storageRepo.uploadPhoto(
                        logoStream,
                        "logo_${nameField}.jpg",
                        FirestoreTags.OnlineTransportAgency,
                        StorageTags.LOGO
                    ).collect { storageState ->
                        when (storageState) {
                            is State.Loading -> startLoading()
                            is State.Failed -> {
                                stopLoading()
                                Log.e(TAG, "Authentication: ${storageState.message}")
                            }
                            is State.Success -> {
                                storageState.data

                                val agencyMapData = OnlineTravelAgency(
                                    agencyName = nameField,
                                    agencyLogo = storageState.data,
                                    motto = mottoField,
                                    costPerKm = costPerKm.toDouble(),
                                    numberOfBuses = vehicleNumberField.toInt(),
                                    bankAccountNumber = bankField,
                                    mtnMoMoAccount = momoField,
                                    orangeMoneyAccount = orangeMoneyField,
                                    supportEmail = supportEmailField,
                                    supportContact = supportPhoneField
                                ).otaMap

                                /**
                                 * Adds the agency document to the OnlineTravelAgency collection and returns the
                                 * state of the operation.
                                 */
                                firestoreRepo.addDocument(
                                    agencyMapData,
                                    FirestoreTags.OnlineTransportAgency.name,
                                ).collect { dbState ->
                                    when (dbState) {
                                        is State.Loading -> startLoading()
                                        is State.Failed -> {
                                            stopLoading()
                                            Log.e(TAG, "FireStore OTA: ${dbState.message}")
                                        }
                                        //Returns reference to the new document
                                        is State.Success -> {
                                            otaPath = dbState.data
                                            stopLoading()
                                            /**
                                             * Adds the Countries subcollection to the Agency before adding the regions into it
                                             */
                                            firestoreRepo.setDocument(
                                                hashMapOf("list" to regions),
                                                "${otaPath}/Cameroon/Regions"
                                            ).collect { regionState ->
                                                when (regionState) {
                                                    is State.Loading -> startLoading()
                                                    is State.Failed -> {
                                                        stopLoading()
                                                        Log.e(
                                                            TAG,
                                                            "FireStore OTA: ${regionState.message}"
                                                        )
                                                    }
                                                    is State.Success -> {
                                                        /**
                                                         * Adds a sub collection to the Record collection and set the first document
                                                         * as the date of today
                                                         */
                                                        firestoreRepo.setDocument(
                                                            hashMapOf(),
                                                            "${FirestoreTags.Records}/${agencyMapData["agencyName"]}/stats/${Date()}",
                                                        ).collect {
                                                            when (it) {
                                                                is State.Loading -> startLoading()
                                                                is State.Failed -> {
                                                                    stopLoading()
                                                                    Log.e(
                                                                        TAG,
                                                                        "FireStore OTA: ${it.message}"
                                                                    )
                                                                }
                                                                is State.Success -> {
                                                                    stopLoading()
                                                                    _onOtaCreated.value = true
                                                                    //Delete the created anonymous user from the database
                                                                    authRepo.deleteCurrentUser()
                                                                        .collect { deleteState ->
                                                                            when (deleteState) {
                                                                                is State.Loading -> startLoading()
                                                                                is State.Failed -> {
                                                                                    Log.e(
                                                                                        TAG,
                                                                                        "FireStore OTA: ${deleteState.message}"
                                                                                    )
                                                                                    authRepo.signOutUser()
                                                                                }
                                                                                is State.Success -> {
                                                                                    Log.i(
                                                                                        TAG,
                                                                                        "Anonymous deleted"
                                                                                    )
                                                                                    stopLoading()
                                                                                }
                                                                            }
                                                                        }
                                                                }
                                                            }

                                                        }
                                                    }
                                                }

                                            }


                                        }
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    private fun startLoading() {
        _onLoading.value = true
    }

    private fun stopLoading() {
        _onLoading.value = false
    }

    /**
     * Cancels the state of [_onOtaCreated] after the navigation has been done
     */
    fun endNavigation() {
        _onOtaCreated.value = false
    }

    enum class FieldTags {
        NAME, MOTTO, EMAIL, LOGO_NAME, PHONE, BANK, MOMO, ORANGE, NUM_VEHICLES, COST_PER_KM, REGIONS, LOGO_BITMAP, PRICE_PER_KM
    }


    companion object OTABundleKeys {
        const val TAG = "AgencyRegistVM"
    }
}