package com.lado.travago.tripbook.ui.agency.creation

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lado.travago.tripbook.model.admin.OnlineTravelAgency
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
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
import java.util.regex.Pattern

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyCreationViewModel : ViewModel() {
    private val authRepo = FirebaseAuthRepo()
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()

//    var logoFilename = ""
//        private set
//    var otaPath = ""
//        private set

    //LIVEDATA to display messages as toasts
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    //Livedata to hold data f the agency if it exist already
    private val _agencyDbData = MutableLiveData<DocumentSnapshot>()
    val agencyDbData get() = _agencyDbData

    //Livedata to know when to end the activity in case we could not verify the agency exist already or not
    private val _onVerificationFailed = MutableLiveData(false)
    val onVerificationFailed get() = _onVerificationFailed

    //To either save or re-upload agency logo to FireStorage
    private val _onSaveLogo = MutableLiveData(false)
    val onSaveLogo: LiveData<Boolean> get() = _onSaveLogo

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
    var bankField = ""
        private set
    var momoField = ""
        private set
    var orangeMoneyField = ""
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
    var phoneCode1 = ""
        private set
    var phoneCode2 = ""
        private set
    var supportEmailField = ""
        private set
    var logoUrl: String = ""
        private set


    //Loading state
    private val _loading = MutableLiveData(false)
    val onLoading get() = _loading

    enum class FieldTags {
        PHONE_CODE_1, PHONE_CODE_2, NAME, MOTTO, SUPPORT_EMAIL, SUPPORT_PHONE_1, SUPPORT_PHONE_2, FULL_SUPPORT_PHONE_1, FULL_SUPPORT_PHONE_2, BANK_NUMBER, MOMO_NUMBER, ORANGE_NUMBER, COST_PER_KM, LOGO_BITMAP, CEO_NAME, DECREE_NUMBER, CREATION_YEAR, TOAST_MESSAGE, SAVE_INFO,
    }

    /**
     * Checks if th agency already exist in the database or is under creation
     */
    suspend fun getExistingAgencyData() =
        firestoreRepo.getDocument("Bookers/${authRepo.firebaseAuth.currentUser!!.uid}").collect {
            when (it) {
                is State.Success -> {
                    firestoreRepo.getDocument("OnlineTransportAgency/${it.data["agency"]}")
                        .collect { agencyDataState ->
                            when (agencyDataState) {
                                is State.Loading -> _loading.value = true
                                is State.Success -> {
                                    _agencyDbData.value = agencyDataState.data!!
                                    _loading.value = false
                                }
                                is State.Failed -> {
                                    _loading.value = false
                                    _onVerificationFailed.value = true
                                }
                            }
                        }
                }
                is State.Loading -> _loading.value = true
                is State.Failed -> {
                    _loading.value = false
                    _onVerificationFailed.value = true
                }
            }

        }

    /**
     * Populate fields with existing data if any
     * Should be called only when the [_onVerificationFailed]==false
     */
    fun fillExistingData() =
        if (_agencyDbData.value!!.exists()) {//If agency already existed we fill fields
            _toastMessage.value = "Modify your agency"
            nameField = _agencyDbData.value!!.getString("agencyName")!!
            decreeNumberField = _agencyDbData.value!!.getString("creationDecree")!!
            nameCEOField = _agencyDbData.value!!.getString("nameCEO")!!
            creationYearField = _agencyDbData.value!!.getLong("creationYear")!!.toInt()
            mottoField = _agencyDbData.value!!.getString("motto")!!
            supportEmailField = _agencyDbData.value!!.getString("supportEmail")!!
            bankField = _agencyDbData.value!!.getLong("bankNumber")!!.toString()
            momoField = _agencyDbData.value!!.getString("mtnMoneyNumber")!!
            orangeMoneyField = _agencyDbData.value!!.getString("orangeMoneyNumber")!!
            logoUrl = _agencyDbData.value!!.getString("logoUrl")!!
            fullSupportPhone1Field = _agencyDbData.value!!.getString("supportPhone1")!!
            fullSupportPhone1Field = _agencyDbData.value!!.getString("supportPhone1")!!
            supportPhone1Field = _agencyDbData.value!!.getString("supportPhone1")!!
            supportPhone2Field = _agencyDbData.value!!.getString("supportPhone2")!!
            phoneCode1 = _agencyDbData.value!!.getString("phoneCode1")!!
            phoneCode2 = _agencyDbData.value!!.getString("phoneCode2")!!
        } else _toastMessage.value = "Create your agency"


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
            FieldTags.PHONE_CODE_1 -> phoneCode1 = value.toString()
            FieldTags.PHONE_CODE_2 -> phoneCode2 = value.toString()
            FieldTags.BANK_NUMBER -> bankField = value.toString()
            FieldTags.MOMO_NUMBER -> momoField = value.toString()
            FieldTags.ORANGE_NUMBER -> orangeMoneyField = value.toString()
            FieldTags.CEO_NAME -> nameCEOField = value.toString()
            FieldTags.COST_PER_KM -> costPerKm = value.toString()
            FieldTags.DECREE_NUMBER -> decreeNumberField = value.toString()
            FieldTags.CREATION_YEAR -> creationYearField =
                if (value.toString().isBlank()) 0 else value.toString().toInt()
            FieldTags.LOGO_BITMAP -> logoBitmap = value as Bitmap
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.SAVE_INFO -> _saveInfo.value = true
        }
    }

    suspend fun saveLogo(){
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
            when (storageState) {
                is State.Loading -> _loading.value = true
                is State.Failed -> {
                    _loading.value = false
                    _toastMessage.value =
                        storageState.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Success -> {

                    )
    }

    /**
     * Saves the Agency information to the database then navigate back to the lobby
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
                    storageState.data
                    val db = firestoreRepo.db
                    FirebaseFirestore.getInstance().runTransaction { transaction ->
                        /**0-We get the current booker document*/
                        val currentUserID = authRepo.firebaseAuth.currentUser!!.uid
                        //If there is no signup user, we get a null exception and the transaction fails
                        val bookerSnapshot = transaction.get(db.document("Bookers/$currentUserID"))
                        val scannerMap = hashMapOf<String, Any?>(
                            "name" to bookerSnapshot.getString("name"),
                            "phone" to bookerSnapshot.getString("phone"),
                            "photoUrl" to bookerSnapshot.getString("photoUrl"),
                            "isAdmin" to true,
                            "isOwner" to true,
                            "active" to true,
                            "scansNumber" to 0,
                            "addedOn" to Timestamp.now(),
                        )
                        //We check if this agency already exists if yes, Configuration else Creation
                        if (bookerSnapshot.get("agencyID") == null) {//Creation by the owner
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
                                supportPhone2 = supportPhone2Field,
                                modifiedOn = null,
                                creationYear = creationYearField,
                                phone1 = supportPhone1Field,
                                phone2 = supportPhone2Field,
                                supportCountryCode1 = phoneCode1,
                                supportCountryCode2 = phoneCode2
                            ).otaMap

                            /**1- We Upload the new agency info into firestore*/
                            val agencySnapshot = db.collection("OnlineTransportAgency").document()
                            transaction.set(agencySnapshot, agencyMapData)

                            /**2- Adds the current user to the list of scanners with the admin tag and owner tag to true*/
                            val scannerSnapshot =
                                db.document("${agencySnapshot.path}/Scanners/$currentUserID")
                            transaction.set(scannerSnapshot, scannerMap)

                            /**3- We add the agency uid to the current scanner agency field*/
                            transaction.update(
                                bookerSnapshot.reference,
                                "agency",
                                agencySnapshot.id
                            )
                        } else {// Just updating agency data
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
                                supportPhone2 = supportPhone2Field,
                                creationYear = creationYearField,
                                phone1 = supportPhone1Field,
                                phone2 = supportPhone2Field,
                                supportCountryCode1 = phoneCode1,
                                supportCountryCode2 = phoneCode2,
                                modifiedOn = Timestamp.now()
                            ).otaMap

                            /**1- We Update agency info into firestore*/
                            val agencyDocRef =
                                db.document("OnlineTransportAgency/${bookerSnapshot.getString("agencyID")}")
                            transaction.set(agencyDocRef, agencyMapData)
                            /**2- We add to records that this current admin scanner changer some details*/
                            val changeText = hashMapOf(
                                "change" to """
                                    ScannerId: ${bookerSnapshot.id}
                                    Name: ${bookerSnapshot["name"]}
                                    Action: Changed some agency details.                                        
                                    """.trimIndent()
                            )
                            val recordDocRef =
                                db.document("${agencyDocRef.path}/Record/${agencyMapData["modifiedOn"]}")
                            transaction.set(recordDocRef, agencyMapData)
                        }
                    }
                        .addOnSuccessListener {
                            _loading.value = false
                            _onInfoSaved.value = true
                        }.addOnFailureListener {
                            _loading.value = false
                            _toastMessage.value = it.message
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

    fun checkFields(fragment: Fragment) = when (fragment) {
        is AgencyCreationFragment -> {
            if (nameField.isBlank() || nameCEOField.isBlank() || creationYearField == 0 || bankField.isBlank() || supportEmailField.isBlank() || momoField.isBlank() || mottoField.isBlank() || orangeMoneyField.isBlank() || decreeNumberField.isBlank()) {
                _toastMessage.value = "Do not leave some fields empty"
                _saveInfo.value = true
            } else if (!isBasicallyValidEmailAddress(supportEmailField)) {
                _toastMessage.value = "Invalid Email Address"
            } else {
                _saveInfo.value = true
            }
        }
        else -> {
            //TODO: ADD other things
        }
    }


}