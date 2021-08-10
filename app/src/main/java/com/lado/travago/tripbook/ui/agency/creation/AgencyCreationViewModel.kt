package com.lado.travago.tripbook.ui.agency.creation

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.OnlineTravelAgency
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
import java.util.regex.Pattern

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

    //LIVEDATA to display messages as toasts
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage
    //Live data to trigger the save of information into the db
    private val _saveInfo = MutableLiveData(false)
    val saveInfo get() = _saveInfo
    //Live data to know when to navigate back when all info has been saved
    private val _onInfoSaved = MutableLiveData(false)
    val onInfoSaved get() = _onInfoSaved

    //Agency Fields
    var logoBitmap: Bitmap? = null
        private set
    var nameField = ""
        private set
    var mottoField = ""
        private set
    var bankField = 0
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
    private val _loading = MutableLiveData(false)
    val loading get() = _loading

    enum class FieldTags {
        NAME, MOTTO, SUPPORT_EMAIL, SUPPORT_PHONE_1, SUPPORT_PHONE_2, FULL_SUPPORT_PHONE_1, FULL_SUPPORT_PHONE_2, BANK_NUMBER, MOMO_NUMBER, ORANGE_NUMBER, COST_PER_KM, LOGO_BITMAP, CEO_NAME, DECREE_NUMBER, CREATION_YEAR, TOAST_MESSAGE, SAVE_INFO
    }

    /**
     * Saves the agency fields to a viewModel variables
     */
    fun setField(key: FieldTags, value: Any) {
        when (key) {
            // Fields
            FieldTags.NAME -> nameField = value.toString()
            FieldTags.MOTTO -> mottoField = value.toString()
            FieldTags.SUPPORT_EMAIL -> supportEmailField = value.toString()
            FieldTags.SUPPORT_PHONE_1 -> supportPhone1Field = value.toString()
            FieldTags.SUPPORT_PHONE_2 -> supportPhone2Field = value.toString()
            FieldTags.FULL_SUPPORT_PHONE_1 -> fullSupportPhone1Field = value.toString()
            FieldTags.FULL_SUPPORT_PHONE_2 -> fullSupportPhone2Field = value.toString()
            FieldTags.BANK_NUMBER -> bankField = if(value.toString().isBlank()) 0 else value.toString().toInt()
            FieldTags.MOMO_NUMBER -> momoField = value.toString()
            FieldTags.ORANGE_NUMBER -> orangeMoneyField = value.toString()
            FieldTags.CEO_NAME -> nameCEOField = value.toString()
            FieldTags.COST_PER_KM -> costPerKm = value.toString()
            FieldTags.DECREE_NUMBER -> decreeNumberField = value.toString()
            FieldTags.CREATION_YEAR -> creationYearField = if(value.toString().isBlank()) 0 else value.toString().toInt()
            FieldTags.LOGO_BITMAP -> logoBitmap = value as Bitmap
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.SAVE_INFO -> _saveInfo.value = true
        }
    }

    /**
     * Saves the Agency information to the database then navigae back to the lobby
     */
    suspend fun saveAgencyInfo() {
        _loading.value = true
        val logoStream = Utils.convertBitmapToStream(
            logoBitmap,
            Bitmap.CompressFormat.PNG,
            0
        )
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
                    _loading.value = false
                    _toastMessage.value = storageState.message
                }
                is State.Success -> {
                    storageState.data

                    val agencyMapData = OnlineTravelAgency(
                        agencyName = nameField,
                        logoUrl = storageState.data,
                        motto = mottoField,
                        nameCEO = nameCEOField,
                        creationDecree = decreeNumberField,
                        bankNumber = bankField,
                        mtnMoneyNumber = momoField,
                        orangeMoneyNumber = orangeMoneyField,
                        supportEmail = supportEmailField,
                        supportPhone1 = supportPhone1Field,
                        supportPhone2 = supportPhone2Field
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
                                _loading.value = false
                                _toastMessage.value = storageState.data
                            }
                            //Returns reference to the new document
                            is State.Success -> {
                                _loading.value = false
                                /**
                                 * Navigates to the main lobby [AgencyCreationFragmentFinal]
                                 */
                                _toastMessage.value = "Done! Path=[${dbState.data}]"
                                _onInfoSaved.value = true
                            }
                        }
                    }


                }
            }
        }

    }


    /**
     * This implements only the most basic checking for an email address's validity -- that it contains
     * an '@' and contains no characters disallowed by RFC 2822. This is an overly lenient definition of
     * validity. We want to generally be lenient here since this class is only intended to encapsulate what's
     * in a barcode, not "judge" it.
     */
    private fun isBasicallyValidEmailAddress(email: String?): Boolean {
        val regex = Pattern.compile("[a-zA-Z0-9@.!#$%&'*+\\-/=?^_`{|}~]+")
        return email != null && regex.matcher(email)
            .matches() && email.indexOf('@') >= 0
    }

    fun checkFields(fragment: Fragment) = when(fragment){
        is AgencyCreation1Fragment -> {
            if (nameField.isBlank() || mottoField.isBlank() || nameCEOField.isBlank() || creationYearField == 0 || bankField == 0 || supportEmailField.isBlank() || momoField.isBlank() || momoField.isNotBlank() || orangeMoneyField.isBlank() || decreeNumberField.isBlank()) {
                _toastMessage.value = "Do not leave some fields empty"
            }else if(!isBasicallyValidEmailAddress(supportEmailField)){
                _toastMessage.value = "Invalid Email Address"
            }else{
                _saveInfo.value = true
            }
        }else -> {
            //TODO: ADD other things
        }
    }

    fun startLoading() {
        _loading.value = true
    }

    fun stopLoading() {
        _loading.value = false
    }

}