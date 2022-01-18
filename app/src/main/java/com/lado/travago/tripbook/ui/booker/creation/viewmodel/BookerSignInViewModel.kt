package com.lado.travago.tripbook.ui.booker.creation.viewmodel

import android.app.Activity
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hbb20.CountryCodePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.AdminUtils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class BookerSignInViewModel : ViewModel() {
    //FirebaseRepo utilities for db, signIn and cloud storage
    val firestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()

    lateinit var res: Resources
    lateinit var caller: SignUpCaller

    //LiveData to know when a process is in loading state
    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    //LiveData to hold all toast messages
    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //LiveData to know when to send verification code by sms
    private val _sendCode = MutableLiveData(false)
    val sendCode: LiveData<Boolean> get() = _sendCode

    /**To know if the code has been sent(resp resent). It is originally null until the first code is send then from that point, it is non-null
    //Setting it null originally is to avoid to try create the phone credential with no verificationId [createCredential]*/
    private val _onCodeSent = MutableLiveData<Boolean>()
    val onCodeSent: LiveData<Boolean> get() = _onCodeSent

    //LiveData to know when to re-send verification code by sms
    private val _resendCode = MutableLiveData(false)
    val resendCode: LiveData<Boolean> get() = _resendCode

    //LiveData to know when to start phone authentication upon automatic code verification
    private val _onPhoneVerified = MutableLiveData(false)
    val onPhoneVerified: LiveData<Boolean> get() = _onPhoneVerified

    //livedata to know when to we have completed the phone swapping operation
    private val _onPhoneSwapped = MutableLiveData(false)
    val onPhoneSwapped: LiveData<Boolean> get() = _onPhoneSwapped

    //Livedata to know when to navigate to the info screen
    private val _onSignUp = MutableLiveData(false)
    val onSignUp: MutableLiveData<Boolean> get() = _onSignUp

    //Livedata to know when to navigate to the info screen
    private val _onSignIn = MutableLiveData(false)
    val onSignIn: MutableLiveData<Boolean> get() = _onSignIn

    //Livedata to know when to just exit to the BookerCreationCenter fragment
    private val _onClose = MutableLiveData(false)
    val onClose: LiveData<Boolean> get() = _onClose

    val countDown = TimeModel.CountDown(60_000L)

    //Values for phoneAuth screen
    var phoneField = ""
        private set
    var phoneCountryCode = 237
        private set
    var verificationCode = ""
        private set

    lateinit var phoneCredential: PhoneAuthCredential
    private var verificationId = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    /**
     * For phone swapping purposes only
     */
    var newPhoneField = ""
        private set
    var oldPhoneField = ""
        private set
    var newPhoneCountryCode = 0
        private set
    var oldPhoneCountryCode = 0
        private set

    var codeLastSentAt: TimeModel? = null

    /**--------------------------Beta Testing------------------------*/
    //Callback to be called during phone verification

    private val phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            phoneCredential = credential
            _onPhoneVerified.value = true
            _onLoading.value = false
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            _toastMessage.value = exception.handleError { }
            _onLoading.value = false
        }

        override fun onCodeSent(
            verificationID: String,
            forceResendToken: PhoneAuthProvider.ForceResendingToken,
        ) {
            resendToken = forceResendToken
            verificationId = verificationID
            _onCodeSent.value = true

            countDown.start()

            _toastMessage.value =
                "${res.getString(R.string.text_helper_check_sms_at)} +${phoneCountryCode}${phoneField}"
            _onLoading.value = false
        }

    }

    /**
     * Sends a verification code to the user's phone
     */
    fun sendVerificationCode(hostActivity: Activity) {
        if (codeLastSentAt == null || codeLastSentAt!!.absDifference(TimeModel.now()) > 60_000L) {
            _onLoading.value = true
        }
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+$phoneCountryCode${phoneField.removeSpaces()}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(hostActivity)
                .setCallbacks(phoneCallback)
                .build()
        )
        codeLastSentAt = TimeModel.now()
        _sendCode.value = true
    }

    /**
     * Used to resend after time out
     */
    fun resendVerificationCode(hostActivity: Activity) {
        _onLoading.value = true
        _resendCode.value = false//To avoid resending the code on any configuration change
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+$phoneCountryCode${phoneField.removeSpaces()}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(hostActivity)
                .setCallbacks(phoneCallback)
                .setForceResendingToken(resendToken)
                .build()
        )
    }

    /**
     * After code has been sent, we use the user's input 6-digit verification code to verify then login or signUp
     */
    fun createCredential() {
        phoneCredential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
    }

    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.BOOKER_PHONE -> phoneField = value.toString()
        FieldTags.VERIFICATION_CODE -> verificationCode = value.toString()
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
        FieldTags.ON_PHONE_VERIFIED -> _onPhoneVerified.value = value as Boolean
        FieldTags.BOOKER_COUNTRY_CODE -> phoneCountryCode = value as Int
        FieldTags.ON_CODE_SENT -> _onCodeSent.value = value as Boolean
        FieldTags.RES -> res = value as Resources

        FieldTags.OLD_PHONE_CODE -> oldPhoneCountryCode = value as Int
        FieldTags.NEW_PHONE_CODE -> newPhoneCountryCode = value as Int
        FieldTags.OLD_PHONE -> oldPhoneField = value as String
        FieldTags.NEW_PHONE -> newPhoneField = value as String
        FieldTags.SIGNUP_CALLER -> caller = value as SignUpCaller
        FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
    }

    enum class FieldTags {
        ON_PHONE_VERIFIED, VERIFICATION_CODE, TOAST_MESSAGE, BOOKER_COUNTRY_CODE, BOOKER_PHONE, ON_CODE_SENT,
        RES, OLD_PHONE_CODE, OLD_PHONE, NEW_PHONE_CODE, NEW_PHONE, SIGNUP_CALLER, ON_LOADING
    }

    /**
     * Sign Up -> account creation and profile creation
     * Sign In -> log in
     */
    suspend fun signUpOrSignIn() {
        _onLoading.value = true
        authRepo.signInWithPhoneAuthCredential(phoneCredential).collect { authState ->
            when (authState) {
                is State.Success -> {
                    //Get id from created user
                    val bookerGeneratedID = authState.data.uid
                    //Checks if the booker is  logging-in or signing-up
                    firestoreRepo.getDocument("${FirestoreTags.Bookers}/$bookerGeneratedID")
                        .collect { it ->
                            when (it) {
                                is State.Failed -> {
                                    authRepo.signOutUser()
                                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                                        _toastMessage.value =
                                            res.getString(R.string.warn_wrong_confirmation_code)
                                        _onCodeSent.value = true
                                    } else {
                                        _toastMessage.value = it.exception.handleError {}
                                    }
                                    _onLoading.value = false
                                }
                                is State.Success -> {
                                    //Sign up/Sign in
                                    if (it.data.exists()) {
                                        _onSignIn.value = true
                                        _onSignUp.value = false
                                    } else {
                                        _onSignIn.value = false
                                        _onSignUp.value = true
                                    }
                                    _onLoading.value = false
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
                is State.Loading -> _onLoading.value = true
            }
        }
    }

    suspend fun changePhoneNumber() {
        authRepo.swapPhoneNumbers(phoneCredential).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        _toastMessage.value =
                            res.getString(R.string.warn_wrong_confirmation_code)
                        _onCodeSent.value = true
                    } else {
                        _toastMessage.value = it.exception.handleError {}
                    }
                    _onLoading.value = false
                }
                is State.Loading -> _onLoading
                is State.Success -> {
                    _onPhoneSwapped.value = true
                    _onLoading.value = false
                }
            }
        }
    }


}



