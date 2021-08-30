package com.lado.travago.tripbook.ui.agency.creation

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Transaction
import com.lado.travago.tripbook.model.admin.OnlineTravelAgency
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
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

    //LIVEDATA to display messages as toasts
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    //hold data for the agency if it exist already
    lateinit var agencyDbData: DocumentSnapshot

    //Livedata to know when to end the activity in case we could not verify the agency exist already or not
    private val _onVerificationFailed = MutableLiveData(false)
    val onVerificationFailed get() = _onVerificationFailed

    //To either save or re-upload agency logo to FireStorage
    private val _onLogoSaved = MutableLiveData(false)
    val onLogoSaved: LiveData<Boolean> get() = _onLogoSaved

    private val _startSaving = MutableLiveData(false)
    val startSaving: LiveData<Boolean> get() = _startSaving

    //Live data to know when to navigate back when all info has been saved
    private val _onInfoSaved = MutableLiveData(false)
    val onInfoSaved: LiveData<Boolean> get() = _onInfoSaved

    //Retry searching existing data
    private val _retry = MutableLiveData(true)
    val retry: LiveData<Boolean> get() = _retry

    //Live data to start filling data
    private val _startFilling = MutableLiveData(false)
    val startFilling: LiveData<Boolean> get() = _startFilling

    //Loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

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
    var nameCEOField = ""
        private set
    var creationYearField = ""
        private set
    var decreeNumberField = ""
        private set
    var supportPhone1Field = ""
        private set
    var supportPhone2Field = ""
        private set
    var phoneCode1 = 237
        private set
    var phoneCode2 = 237
        private set
    var supportEmailField = ""
        private set
    var logoUrl: String = ""
        private set

    enum class FieldTags {
        SUPPORT_PHONE_1,
        SUPPORT_PHONE_2,
        PHONE_CODE_1,
        PHONE_CODE_2,
        NAME,
        MOTTO,
        SUPPORT_EMAIL,
        BANK_NUMBER,
        MOMO_NUMBER,
        ORANGE_NUMBER,
        LOGO_BITMAP,
        CEO_NAME,
        DECREE_NUMBER,
        CREATION_YEAR,
        ON_LOGO_SAVED,
        TOAST_MESSAGE,
        START_SAVING
    }

    /**
     * Checks if th agency already exist in the database or is under creation
     */
    suspend fun getExistingAgencyData() {
        _retry.value = false
        firestoreRepo.getDocument("Bookers/${authRepo.firebaseAuth.currentUser!!.uid}").collect {
            when (it) {
                is State.Success -> {
                    firestoreRepo.getDocument("OnlineTransportAgency/${it.data["agency"]}")
                        .collect { agencyDataState ->
                            when (agencyDataState) {
                                is State.Loading -> _onLoading.value = true
                                is State.Success -> {
                                    agencyDbData = agencyDataState.data!!
                                    _startFilling.value = true
                                    _startFilling.value = false
                                    _onLoading.value = false
                                }
                                is State.Failed -> {
                                    _onLoading.value = false
                                    _onVerificationFailed.value = true
                                    _onVerificationFailed.value = false
                                }
                            }
                        }
                }
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _onLoading.value = false
                    _onVerificationFailed.value = true
                    _onVerificationFailed.value = false
                }
            }
        }
    }

    /**
     * Populate fields with existing data if any
     * Should be called only when the [_onVerificationFailed]==false
     */
    fun fillExistingData() {
        if (agencyDbData.exists()) {//If agency already existed we fill fields
            _toastMessage.value = "Modify your agency"
            nameField = agencyDbData.getString("agencyName")!!
            decreeNumberField = agencyDbData.getString("creationDecree")!!
            nameCEOField = agencyDbData.getString("nameCEO")!!
            creationYearField = agencyDbData.getLong("creationYear")!!.toString()
            mottoField = agencyDbData.getString("motto")!!
            supportEmailField = agencyDbData.getString("supportEmail")!!
            bankField = agencyDbData.getLong("bankNumber")!!.toString()
            momoField = agencyDbData.getString("mtnMoneyNumber")!!
            orangeMoneyField = agencyDbData.getString("orangeMoneyNumber")!!
            logoUrl = agencyDbData.getString("logoUrl")!!
            phoneCode1 = agencyDbData.getLong("phoneCode1")!!.toInt()
            phoneCode2 = agencyDbData.getLong("phoneCode2")!!.toInt()
            supportPhone1Field = agencyDbData.getString("supportPhone1")!!
            supportPhone2Field = agencyDbData.getString("supportPhone2")!!
        } else _toastMessage.value = "Create your agency"
    }

    /**
     * Saves the agency fields to a viewModel variables
     */
    fun setField(key: FieldTags, value: Any) {
        when (key) {
            FieldTags.NAME -> nameField = value.toString()
            FieldTags.MOTTO -> mottoField = value.toString()
            FieldTags.CEO_NAME -> nameCEOField = value.toString()
            FieldTags.PHONE_CODE_1 -> phoneCode1 = value as Int
            FieldTags.PHONE_CODE_2 -> phoneCode2 = value as Int
            FieldTags.SUPPORT_PHONE_1 -> supportPhone1Field = value.toString()
            FieldTags.SUPPORT_PHONE_2 -> supportPhone2Field = value.toString()
            FieldTags.SUPPORT_EMAIL -> supportEmailField = value.toString()
            FieldTags.BANK_NUMBER -> bankField = value.toString()
            FieldTags.MOMO_NUMBER -> momoField = value.toString()
            FieldTags.ORANGE_NUMBER -> orangeMoneyField = value.toString()
            FieldTags.DECREE_NUMBER -> decreeNumberField = value.toString()
            FieldTags.CREATION_YEAR -> creationYearField = value.toString()
            FieldTags.LOGO_BITMAP -> logoBitmap = value as Bitmap
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOGO_SAVED -> _onLogoSaved.value = true
            FieldTags.START_SAVING -> _startSaving.value = false
        }
    }

    /**
     * Upload the logo to the storage
     */
    suspend fun saveLogo() {
        _onLoading.value = true
        val logoStream = Utils.convertBitmapToStream(
            logoBitmap,
            Bitmap.CompressFormat.PNG,
            0
        )
        storageRepo.uploadPhoto(
            logoStream,
            "$nameField.jpg",
            FirestoreTags.OnlineTransportAgency,
            StorageTags.LOGO
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    logoUrl = it.data

                    //Activate and deactivate
                    _onLogoSaved.value = true
                    _onLogoSaved.value = false

                    _onLoading.value = true
                }
            }
        }
    }

    /**
     * Saves a fresh copy of the agency info for a new agency
     */
    fun createAgency(): Task<Transaction> =
        firestoreRepo.db.runTransaction { transaction ->
            val newAgencyMap = OnlineTravelAgency(
                agencyName = nameField,
                logoUrl = logoUrl,
                motto = mottoField,
                nameCEO = nameCEOField,
                creationDecree = decreeNumberField,
                bankNumber = bankField,
                mtnMoneyNumber = momoField,
                orangeMoneyNumber = orangeMoneyField,
                supportEmail = supportEmailField,
                supportPhone1 = supportPhone1Field.removeSpaces(),
                supportPhone2 = supportPhone2Field.removeSpaces(),
                modifiedOn = null,
                creationYear = creationYearField.toInt(),
                phone1 = supportPhone1Field,
                phone2 = supportPhone2Field,
                supportCountryCode1 = phoneCode1,
                supportCountryCode2 = phoneCode2
            ).otaMap
            val db = firestoreRepo.db
            val bookerRef =
                transaction.get(db.document("Bookers/${authRepo.firebaseAuth.currentUser!!.uid}"))
            val agencyDoc =
                transaction.get(db.collection("OnlineTransportAgency").document())
            val recordDoc =
                transaction.get(db.document("${agencyDoc.reference.path}/Record/${Timestamp.now()}"))
            val scannerDoc =
                transaction.get(db.document("${agencyDoc.reference.path}/Scanners/${authRepo.firebaseAuth.currentUser!!.uid}"))

            val creatorScannerMap = hashMapOf<String, Any?>(
                "name" to bookerRef.getString("name"),
                "phone" to bookerRef.getString("phone"),
                "photoUrl" to bookerRef.getString("photoUrl"),
                "isAdmin" to true,
                "isOwner" to true,
                "active" to true,
                "scansNumber" to 0,
                "addedOn" to Timestamp.now(),
            )

            /**1- We Upload the new agency info into firestore*/
            transaction.set(agencyDoc.reference, newAgencyMap)

            /**2- Adds the current user to the list of scanners with the admin tag and owner tag to true*/
            transaction.set(scannerDoc.reference, creatorScannerMap)

            /**3- We make sure that the creator booker document contains knows that he is affiliated to an agency*/
            transaction.update(
                bookerRef.reference,
                "agency",
                agencyDoc.id
            )
            /**4-We create a record doc*/
            val changeMap = mapOf<String, Any>(
                "creation" to mapOf(
                    "scannerId" to bookerRef.id,
                    "name" to "${bookerRef["name"]}",
                    "action" to "Created Agency",
                    "doneAt" to Timestamp.now()
                )
            )
            transaction.set(recordDoc.reference, changeMap)
        }.addOnSuccessListener {
            _onLoading.value = false
            _onInfoSaved.value = true
            _onInfoSaved.value = false
        }.addOnFailureListener {
            _onLoading.value = false
            _toastMessage.value = it.handleError { }
        }


    /**
     * Updates the agency to db
     */
    fun updateAgencyInfo(): Task<Transaction> =
        firestoreRepo.db.runTransaction { transaction ->
            val agencyMapData = OnlineTravelAgency(
                agencyName = nameField,
                logoUrl = logoUrl,
                motto = mottoField,
                nameCEO = nameCEOField,
                creationDecree = decreeNumberField,
                bankNumber = bankField,
                mtnMoneyNumber = momoField,
                orangeMoneyNumber = orangeMoneyField,
                supportEmail = supportEmailField,
                supportPhone1 = supportPhone1Field.removeSpaces(),
                supportPhone2 = supportPhone2Field.removeSpaces(),
                creationYear = creationYearField.toInt(),
                phone1 = supportPhone1Field,
                phone2 = supportPhone2Field,
                supportCountryCode1 = phoneCode1,
                supportCountryCode2 = phoneCode2,
                modifiedOn = Timestamp.now()
            ).otaMap
            val db = firestoreRepo.db
            //Gets before writes
            val bookerDoc =
                transaction.get(db.document("Bookers/${authRepo.firebaseAuth.currentUser!!.uid}"))
            val agencyDoc =
                transaction.get(db.document("OnlineTransportAgency/${bookerDoc.getString("agencyID")}"))
            val recordDoc =
                transaction.get(db.document("${agencyDoc.reference.path}/Record/${Timestamp.now()}"))

            /**1- We Update agency info into firestore*/
            transaction.update(agencyDoc.reference, agencyMapData)

            /**2- We add to records that this current admin scanner changed some details*/
            val changeMap = mapOf<String, Any>(
                "changes_${bookerDoc["name"]}" to mapOf(
                    "scannerId" to bookerDoc.id,
                    "name" to "${bookerDoc["name"]}",
                    "action" to "Changed some agency details",
                    "doneAt" to Timestamp.now()
                )
            )
            transaction.update(recordDoc.reference, changeMap)
        }.addOnSuccessListener {
                _onLoading.value = false
                _onInfoSaved.value = true
                _onInfoSaved.value = false
        }.addOnFailureListener {
            _onLoading.value = false
            _toastMessage.value = it.handleError { }
        }

    /**
     * This implements only the most basic checking for an email address's validity -- that it contains
     * an '@' and contains no characters disallowed by RFC 2822. This is an overly lenient definition of
     * validity. We want to generally be lenient here since this class is only intended to encapsulate what's
     * in a barcode, not "judge" it.
     */
    private fun isBasicallyValidEmailAddress(email: String): Boolean {
        val regex = Pattern.compile("[a-zA-Z0-9@.!#$%&'*+\\-/=?^_`{|}~]+")
        return regex.matcher(email)
            .matches() && email.indexOf('@') >= 0
    }

    fun checkFields() =
        if (nameField.isBlank() || nameCEOField.isBlank() || creationYearField.isBlank() || bankField.isBlank() || supportEmailField.isBlank() || momoField.isBlank() || mottoField.isBlank() || orangeMoneyField.isBlank() || decreeNumberField.isBlank()) {
            _toastMessage.value = "Do not leave some fields empty"
        } else if (!isBasicallyValidEmailAddress(supportEmailField)) {
            _toastMessage.value = "Invalid Email Address"
        } else {
            _startSaving.value = true
            _startSaving.value = false
        }

}