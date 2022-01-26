package com.lado.travago.tripbook.ui.booker.creation.viewmodel


import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.model.enums.SEX.Companion.toSEX
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.StorageTags
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

/**
 * A place where the booker can create or change his profile
 */
@ExperimentalCoroutinesApi
class BookerProfileViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()
    val storageRepo = StorageRepo()

    lateinit var res: Resources

    private val _onFailed = MutableLiveData(false)
    val onFailed: LiveData<Boolean> get() = _onFailed

    private val _existingProfileDoc = MutableLiveData<DocumentSnapshot>()
    val existingProfileDoc: LiveData<DocumentSnapshot> get() = _existingProfileDoc

    private val _startSaving = MutableLiveData(false)
    val startSaving: LiveData<Boolean> get() = _startSaving

    private val _onPhotoSaved = MutableLiveData(false)
    val onPhotoSaved: LiveData<Boolean> get() = _onPhotoSaved

    //LiveData to know when were are done to navigate away to the launcher UI  after login or signup
    private val _onInfoSaved = MutableLiveData(false)
    val onInfoSaved: LiveData<Boolean> get() = _onInfoSaved

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    // Gotten from shared preference or gotten from existing profile
    var phoneCountryCode = 0
    var phoneNumber = ""

    //Values of the fields from the creationForm
    var nameField = ""
        private set
    var birthdayInMillis = Date().time
        private set
    var recoveryPhoneField = ""
        private set
    var sex = SEX.UNKNOWN
        private set
    var nationalityField = ""
        private set
    var photoUri: Uri? = null
        private set
    var photoUrl = ""
    private var bookerGeneratedID = authRepo.currentUser!!.uid

    var recoveryPhoneCountryCode = 0
        private set

    //Navigation Info
    lateinit var caller: SignUpCaller


    enum class FieldTags {
        CACHED_PHONE, CACHED_COUNTRY_CODE, NAME, PHOTO_URI, SEX, BIRTHDAY, ON_LOADING, TOAST_MESSAGE, RECOVERY_PHONE, NATIONALITY, RECOVERY_COUNTRY_CODE /*Tags*/,
        /*ON_INFO_SAVED*/ START_SAVING, RES, ARG_CALLER
    }

    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString().trim()
        FieldTags.NAME -> nameField = value.toString().trim()
        FieldTags.BIRTHDAY -> birthdayInMillis = value as Long
        FieldTags.RECOVERY_PHONE -> recoveryPhoneField = value.toString().removeSpaces()
        FieldTags.PHOTO_URI -> photoUri = value as Uri
        FieldTags.SEX -> sex = value as SEX
        FieldTags.NATIONALITY -> nationalityField = value.toString().trim()
        FieldTags.RECOVERY_COUNTRY_CODE -> recoveryPhoneCountryCode = value as Int
        FieldTags.START_SAVING -> _startSaving.value = value as Boolean
        FieldTags.CACHED_PHONE -> phoneNumber = (value as String).removeSpaces()
        FieldTags.CACHED_COUNTRY_CODE -> phoneCountryCode = value as Int
        FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
        FieldTags.RES -> res = value as Resources
        FieldTags.ARG_CALLER -> caller = value as SignUpCaller
    }

    suspend fun getExistingProfile() {
        firestoreRepo.getDocument("Bookers/${authRepo.currentUser!!.uid}").collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _onFailed.value = true
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    if (it.data.exists()) {
                        /**Fills the views backing fields with existing data*/
                        it.data.run {
                            nameField = getString("name")!!
                            sex = getString("sex")!!.toSEX()
                            birthdayInMillis = getLong("birthdayInMillis")!!
                            photoUrl = getString("photoUrl")!!
                            nationalityField = getString("nationality")!!
                            phoneNumber = getString("phone")!!
                            phoneCountryCode = getLong("phoneCountryCode")!!.toInt()
                            recoveryPhoneField = getString("recoveryPhone")!!
                            recoveryPhoneCountryCode = getLong("recoveryPhoneCountryCode")!!.toInt()
                        }
                        _existingProfileDoc.value = it.data!!
                    }
                    _onLoading.value = false
                }
            }
        }
    }


    suspend fun uploadProfilePhoto() {
        //Upload the profile photo to the cloud storage and retrieve url
        if (photoUri != null)
            storageRepo.uploadFile(
                photoUri!!,
                bookerGeneratedID,
                FirestoreTags.Users,
                StorageTags.PROFILE
            ).collect { storageState ->
                when (storageState) {
                    is State.Loading -> _onLoading.value = true
                    is State.Failed -> {
                        _onLoading.value = false
                        _toastMessage.value =
                            storageState.exception.handleError { /**TODO: Handle Error lambda*/ }
                    }
                    is State.Success -> {
                        photoUrl = storageState.data
                        _onPhotoSaved.value = true
                    }
                }
            }
        else  _onPhotoSaved.value = true

    }

    /**
     * Adds a booker to the database
     */
    suspend fun createBookerProfile() {
        val dataMap = hashMapOf<String, Any?>(
            "name" to nameField,
            "sex" to sex.toString(),
            "phone" to phoneNumber,
            "phoneCountryCode" to phoneCountryCode,
            "birthdayInMillis" to birthdayInMillis,
            "photoUrl" to photoUrl,
            "nationality" to nationalityField,
            "recoveryPhoneCountryCode" to recoveryPhoneCountryCode,
            "recoveryPhone" to recoveryPhoneField,
            "addedOn" to Timestamp.now()
        )
        //Complete the creation process for signUp
        firestoreRepo.setDocument(
            dataMap,
            "${FirestoreTags.Bookers}/${bookerGeneratedID}"
        ).collect {
            when (it) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                    _onLoading.value = false
                }
                is State.Success -> {
                    _onInfoSaved.value = true
                }
            }
        }

    }
    /**
     * Adds a booker to the database
     */
    suspend fun updateBookerProfile(){
        val dataMap = hashMapOf<String, Any?>(
        "name" to nameField,
        "sex" to sex.toString(),
        "birthdayInMillis" to birthdayInMillis,
        "photoUrl" to photoUrl,
        "nationality" to nationalityField,
        "recoveryPhoneCountryCode" to recoveryPhoneCountryCode,
        "recoveryPhone" to recoveryPhoneField.removeSpaces(),
        "lastModifiedOn" to Timestamp.now()
        )

        //Complete the creation process for signUp
        firestoreRepo.updateDocument(
            dataMap,
            "${FirestoreTags.Bookers}/${bookerGeneratedID}"
        ).collect {
            when (it) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                    _onLoading.value = false
                }
                is State.Success -> {
                    _onInfoSaved.value = true
                }
            }
        }
    }

}