package com.lado.travago.tripbook.ui.agency.creation

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
class AgencyCreationViewModel : ViewModel() {
    private val authRepo = FirebaseAuthRepo()
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()

    var logoFilename = ""
        private set
    var otaPath = ""
        private set

    //LIVEDATA
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    private val _saveInfo = MutableLiveData(false)
    val saveInfo get() = _saveInfo

    //Agency Fields
    var logoBitmap: Bitmap? = null
        private set
    var nameField = ""
        private set
    var mottoField = ""
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
    var nameCEOField = ""
        private set
    var creationYearField = 0
        private set
    var decreeNumberField = ""
        private set
    var supportPhone1Field = ""
        private set
    var supportPhone2Field = ""
        private set
    var fullSupportPhone1Field = ""
        private set
    var fullSupportPhone2Field = ""
        private set
    var supportEmailField = ""
        private set

    //For the config screen
    val regionsList = listOf("")
    val countryList = listOf("")

    //Loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading get() = _onLoading

    //Completion state
    private val _onOtaCreated = MutableLiveData(false)
    val onOtaCreated get() = _onOtaCreated

    enum class FieldTags {
        NAME, MOTTO, SUPPORT_EMAIL, SUPPORT_PHONE_1, SUPPORT_PHONE_2, FULL_SUPPORT_PHONE_1, FULL_SUPPORT_PHONE_2, BANK_NUMBER, MOMO_NUMBER, ORANGE_NUMBER, COST_PER_KM, LOGO_BITMAP, CEO_NAME, CREATION_DECREE, CREATION_YEAR, TOAST_MESSAGE, SAVE_INFO
    }

    /**
     * Saves the agency fields to a viewModel variables
     */
    fun saveField(key: FieldTags, value: Any) {
        when (key) {
            // Fields
            FieldTags.NAME -> nameField = value.toString()
            FieldTags.MOTTO -> mottoField = value.toString()
            FieldTags.SUPPORT_EMAIL -> supportEmailField = value.toString()
            FieldTags.SUPPORT_PHONE_1 -> supportPhone1Field = value.toString()
            FieldTags.SUPPORT_PHONE_2 -> supportPhone2Field = value.toString()
            FieldTags.FULL_SUPPORT_PHONE_1 -> fullSupportPhone1Field = value.toString()
            FieldTags.FULL_SUPPORT_PHONE_2 -> fullSupportPhone2Field = value.toString()
            FieldTags.BANK_NUMBER -> bankField = value.toString()
            FieldTags.MOMO_NUMBER -> momoField = value.toString()
            FieldTags.ORANGE_NUMBER -> orangeMoneyField = value.toString()
            FieldTags.CEO_NAME -> nameCEOField = value.toString()
            FieldTags.COST_PER_KM -> costPerKm = value.toString()
            FieldTags.CREATION_DECREE -> decreeNumberField = value.toString()
            FieldTags.CREATION_YEAR -> creationYearField = if(value.toString().isBlank()) 0 else value.toString().toInt()
            FieldTags.LOGO_BITMAP -> logoBitmap = value as Bitmap
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.SAVE_INFO -> _saveInfo.value = true
        }
    }

    /**
     * Creates the Agency and adds it to the db. When successful, it makes sure we navigate to the fragment to add scanners
     * Then finally, it deletes the temporary current account created for the process sake
     */
    suspend fun saveAgencyInfo() {
        val logoStream = Utils.convertBitmapToStream(
            logoBitmap,
            Bitmap.CompressFormat.PNG,
            0
        )
        stopLoading()
        /**
         * Upload the logo to the storage
         */
        storageRepo.uploadPhoto(
            logoStream,
            "$nameField.jpg",
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
                        name = nameField,
                        logoUrl = storageState.data,
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

    companion object OTABundleKeys {
        const val TAG = "AgencyRegistVM"
    }
}