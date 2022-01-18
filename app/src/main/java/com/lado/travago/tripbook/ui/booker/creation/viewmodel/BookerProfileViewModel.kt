package com.lado.travago.tripbook.ui.booker.creation.viewmodel


import android.content.res.Resources
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
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
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.properties.Delegates

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

    //To know when the user has modified his profile photo
    private val _profilePhotoChanged = MutableLiveData(false)
    val profilePhotoChanged: LiveData<Boolean> get() = _profilePhotoChanged

    //To know when Booker info have changed
    private val _profileInfoChanged = MutableLiveData(false)
    val profileInfoChanged: LiveData<Boolean> get() = _profileInfoChanged

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
    var birthdayField = Date().time
        private set
    var recoveryPhoneField = ""
        private set
    var photoField: Bitmap? = null
        private set
    var sex = SEX.UNKNOWN
        private set
    var nationalityField = ""
        private set
    var photoUrl = ""
    private var bookerGeneratedID = authRepo.currentUser!!.uid

    var recoveryPhoneCountryCode = 0
        private set

    //Navigation Info
    lateinit var caller: SignUpCaller


    enum class FieldTags {
        CACHED_PHONE, CACHED_COUNTRY_CODE, NAME, SEX, BIRTHDAY, PROFILE_PHOTO, ON_LOADING, TOAST_MESSAGE, RECOVERY_PHONE, NATIONALITY, RECOVERY_COUNTRY_CODE /*Tags*/,
        ON_INFO_SAVED, PROFILE_PHOTO_CHANGED, PROFILE_INFO_CHANGED, RES, ARG_CALLER
    }

    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
        FieldTags.NAME -> nameField = value.toString()
        FieldTags.BIRTHDAY -> birthdayField = value as Long
        FieldTags.RECOVERY_PHONE -> recoveryPhoneField = value.toString().removeSpaces()
        FieldTags.PROFILE_PHOTO -> photoField = value as Bitmap?
        FieldTags.SEX -> sex = value as SEX
        FieldTags.NATIONALITY -> nationalityField = value.toString()
        FieldTags.RECOVERY_COUNTRY_CODE -> recoveryPhoneCountryCode = value as Int
        FieldTags.PROFILE_PHOTO_CHANGED -> _profilePhotoChanged.value = value as Boolean
        FieldTags.PROFILE_INFO_CHANGED -> _profileInfoChanged.value = value as Boolean
        FieldTags.ON_INFO_SAVED -> _onInfoSaved.value = value as Boolean
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
                            birthdayField = getLong("birthdayInMillis")!!
                            photoUrl = getString("photoUrl")!!
                            nationalityField = getString("nationality")!!
                            phoneNumber = getString("phone")!!
                            phoneCountryCode = getLong("phoneCountryCode")!!.toInt()
                            recoveryPhoneField = getString("recoveryPhone")!!
                            recoveryPhoneCountryCode = getLong("recoveryPhoneCountryCode")!!.toInt()
                        }
                        _existingProfileDoc.value = it.data!!
                    } else _toastMessage.value = res.getString(R.string.create_profile)
                    _onLoading.value = false
                }
            }
        }
    }


    suspend fun uploadProfilePhoto() {
        _onLoading.value = true
        val photoStream = Utils.convertBitmapToStream(
            photoField,
            Bitmap.CompressFormat.WEBP_LOSSLESS,
            0
        )
        //Upload the profile photo to the cloud storage and retrieve url
        storageRepo.uploadPhoto(
            photoStream,
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
                    //We save info  only when they have been modified my the booker else we just finish
                    if (_profileInfoChanged.value == true) saveBookerInfo()
                    else _onInfoSaved.value = true
                }
            }
        }
    }

    /**
     * Adds a booker to the database
     */
    suspend fun saveBookerInfo() {
        val dataMap = hashMapOf<String, Any?>(
            "name" to nameField,
            "sex" to sex.toString(),
            "phone" to phoneNumber.removeSpaces(),
            "phoneCountryCode" to phoneCountryCode,
            "birthdayInMillis" to birthdayField,
            "photoUrl" to photoUrl,
            "nationality" to nationalityField,
            "recoveryPhoneCountryCode" to recoveryPhoneCountryCode,
            "recoveryPhone" to recoveryPhoneField.removeSpaces(),
            "lastModifiedOn" to Timestamp.now()
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

}