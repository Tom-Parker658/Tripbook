package com.lado.travago.tripbook.ui.booker.creation

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.model.users.Booker
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
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class BookerCreationViewModel : ViewModel() {
    //FirebaseRepo utilities for db, signIn and cloud storage
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()
    private val authRepo = FirebaseAuthRepo()

    //LiveData to know when a process is in loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading get() = _onLoading

    //LiveData to hold all toast messages
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    //LiveData to know when to send verification code by sms
    private val _sendCode = MutableLiveData(false)
    val sendCode get() = _sendCode

    //LiveData to know when to navigate to the confirmation screen
    private val _onCodeSent = MutableLiveData(false)
    val onCodeSent get() = _onCodeSent

    //LiveData to know when to re-send verification code by sms
    private val _resendCode = MutableLiveData(false)
    val resendCode get() = _resendCode

    //LiveData to know when to start phone authentication
    private val _onPhoneVerified = MutableLiveData(false)
    val onPhoneVerified get() = _onPhoneVerified

    //Livedata to know when to navigate to the info screen
    private val _navToInfoScreen = MutableLiveData(false)
    val navToInfoScreen get() = _navToInfoScreen

    //LiveData to know when or not to upload booker information to the database *True it is a Sign-Up process*
    private val _startInfoUpload = MutableLiveData(false)
    val startInfoUpload get() = _startInfoUpload

    //LiveData to know when were are done to navigate away to the launcher UI  after login or signup
    private val _onBookerCreated = MutableLiveData(false)
    val onBookerCreated get() = _onBookerCreated

    //Values for phoneAuth screen
    var bookerPhoneField = ""
        private set
    var verificationCode = ""
        private set
    private lateinit var phoneCredential: PhoneAuthCredential
    var verificationId = ""
        private set
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

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
    var sexFieldId = 0
        private set
    var nationalityField = ""
        private set
    private var photoUrl = ""
    var occupationField = ""
        private set
    private var bookerGeneratedID = ""
    val occupationList = OCCUPATION.values().toList()

    //Country Code for recovery
    var bookerCountryCode = 237
        private set
    var recoveryCountryCode = 237
        private set


    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.BOOKER_PHONE -> bookerPhoneField = value.toString()
        FieldTags.VERIFICATION_CODE -> verificationCode = value.toString()
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
        FieldTags.SEND_CODE -> _sendCode.value = value as Boolean
        FieldTags.NAV_TO_INFO -> navToInfoScreen.value = value as Boolean
        FieldTags.RESEND_CODE -> _resendCode.value = value as Boolean
        FieldTags.ON_PHONE_VERIFIED -> onPhoneVerified.value = value as Boolean
        FieldTags.START_INFO_UPLOAD -> _startInfoUpload.value = value as Boolean
        FieldTags.ON_BOOKER_CREATED -> _onBookerCreated.value = value as Boolean
        FieldTags.ON_CODE_SENT -> _onCodeSent.value = value as Boolean
        FieldTags.NAME -> nameField = value.toString()
        FieldTags.BIRTHDAY -> birthdayField = value as Long
        FieldTags.RECOVERY_PHONE -> recoveryPhoneField = value.toString()
        FieldTags.SEX_ID -> sexFieldId = value as Int
        FieldTags.PHONE_CREDENTIAL -> phoneCredential = value as PhoneAuthCredential
        FieldTags.PROFILE_PHOTO -> photoField = value as Bitmap?
        FieldTags.SEX -> sex = value as SEX
        FieldTags.NATIONALITY -> nationalityField = value.toString()
//        IntentTags.ID -> generatedID = value as String
//        IntentTags.PASSWORD -> passwordField = value.toString()
        FieldTags.OCCUPATION -> occupationField = value.toString()
        FieldTags.RECOVERY_COUNTRY_CODE -> recoveryCountryCode = value as Int
        FieldTags.BOOKER_COUNTRY_CODE -> bookerCountryCode = value as Int
        FieldTags.VERIFICATION_ID -> verificationId = value.toString()
        FieldTags.RESEND_TOKEN -> resendToken = value as PhoneAuthProvider.ForceResendingToken
    }


    /**
     * Contains different identifiers for the fields in our creation form
     */
    enum class FieldTags {
        BOOKER_PHONE, NAME, NAV_TO_INFO, SEX_ID, SEX, BIRTHDAY, PROFILE_PHOTO, OCCUPATION, SEND_CODE, RESEND_CODE, ON_PHONE_VERIFIED, VERIFICATION_CODE, TOAST_MESSAGE, RECOVERY_PHONE, PHONE_CREDENTIAL, START_INFO_UPLOAD, ON_BOOKER_CREATED, ON_CODE_SENT, NATIONALITY, RECOVERY_COUNTRY_CODE, BOOKER_COUNTRY_CODE, RESEND_TOKEN, VERIFICATION_ID
    }


    /**
     * Adds a booker to the database
     */
    suspend fun saveBookerInfo() {
        val photoStream = Utils.convertBitmapToStream(photoField, Bitmap.CompressFormat.PNG, 0)
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
                    onLoading.value = false
                    _toastMessage.value =
                        storageState.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Success -> {
                    _toastMessage.value = "FireStorage OK! link =${storageState.data}"
                    photoUrl = storageState.data
                    //The scanner map as a booker
                    val booker = Booker(
                        name = nameField,
                        sex = sex,
                        birthdayInMillis = birthdayField,
                        photoUrl = photoUrl,
                        nationality = nationalityField,
                        occupation = occupationField,
                        phone = "+${bookerCountryCode}${bookerPhoneField.removeSpaces()}",
                        recoveryPhoneNumber = "+${recoveryCountryCode}${recoveryPhoneField.removeSpaces()}" // Actually recovery now
                    )
                    //Complete the creation process for signUp
                    firestoreRepo.setDocument(
                        booker.bookerMap,
                        "${FirestoreTags.Bookers}/${bookerGeneratedID}"
                    ).collect {
                        when (it) {
                            is State.Success -> {
                                _toastMessage.value = "Welcome!"
                                _onBookerCreated.value = true
                                _onBookerCreated.value = false
                            }
                            is State.Loading -> _onLoading.value = true
                            is State.Failed -> {
                                _toastMessage.value =
                                    it.exception.handleError { /**TODO: Handle Error lambda*/ }
                                _onLoading.value = false
                            }
                        }
                    }

                }
            }

        }

    }

    /**
     * To phone authenticate the booker.
     * If there exist a document under  Booker/{uid}, then the user is just login-In else we navigate to the info screen for sign-Up
     */
    suspend fun loginOrSignup() {
        authRepo.signInWithPhoneAuthCredential(phoneCredential).collect { authState ->
            when (authState) {
                is State.Success -> {
                    _toastMessage.value = "Booker phone auth succeeded!"
                    //Get id from created user
                    bookerGeneratedID = authState.data.uid
                    //Checks if the booker is  logging-in or signing-up
                    firestoreRepo.getDocument("${FirestoreTags.Bookers}/$bookerGeneratedID")
                        .collect {
                            when (it) {
                                is State.Failed -> {
                                    authRepo.signOutUser()
                                    _toastMessage.value = it.exception.handleError { }
                                    _onLoading.value = false
                                }
                                is State.Success -> {
                                    if (!it.data.exists()) {
                                        _navToInfoScreen.value = true
                                        _navToInfoScreen.value = false
                                    } else {
                                        _onBookerCreated.value = true
                                        _onBookerCreated.value = false
                                    }
                                }
                                is State.Loading -> _onLoading.value = true
                            }
                        }
                }
                is State.Failed -> {
                    _toastMessage.value =
                        authState.exception.handleError { /**TODO: Handle Error lambda*/ }
                    _onLoading.value = false
                }
                is State.Loading -> {
                    _onLoading.value = true
                }
            }
        }
    }

    fun startLoading() {
        _onLoading.value = true
    }

    fun stopLoading() {
        _onLoading.value = false
    }

    fun formatDate(timeInMillis: Long) = Utils.formatDate(timeInMillis, "MMMM, dd YYYY")

    /**
     * Field checkers for each layout
     */
    fun checkFields(fragment: Fragment) {
        when (fragment) {
            is BookerCreation1Fragment -> {
                val fullPhone = "+${bookerCountryCode}${bookerPhoneField}"
                if (fullPhone.isNotBlank()) {
                    _sendCode.value = true
                    _sendCode.value = false
                } else _toastMessage.value = "Invalid phone numbers!"
            }
            is BookerCreation2Fragment -> {
                if (verificationCode.isNotBlank()) {
                    _onPhoneVerified.value = true
                    _onPhoneVerified.value = false
                } else _toastMessage.value = "Enter the verification code"
            }
            is BookerCreationFinalFragment -> {
                val fullPhone = "+${recoveryCountryCode}${recoveryPhoneField}"
                if (nameField.isBlank() || nationalityField.isBlank() || occupationField == "" || sex == SEX.UNKNOWN)
                    _toastMessage.value = "Don't leave a field empty or un touched!"
                else if (fullPhone.isBlank()) _toastMessage.value = "Invalid phone numbers!"
                else if (fullPhone == FirebaseAuth.getInstance().currentUser?.phoneNumber) _toastMessage.value =
                    "Recovery phone number should be different from ${FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "phone number"}"
                else {
                    _startInfoUpload.value = true
                    _startInfoUpload.value = false
                }
            }
            else -> {
                //Never occurs
            }
        }
    }

}



