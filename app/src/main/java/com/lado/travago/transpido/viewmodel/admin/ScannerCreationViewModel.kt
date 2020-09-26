package com.lado.travago.transpido.viewmodel.admin

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.lado.travago.transpido.model.admin.OnlineTravelAgency
import com.lado.travago.transpido.model.admin.Scanner
import com.lado.travago.transpido.model.enums.SEX
import com.lado.travago.transpido.repo.FirestoreTags
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.StorageTags
import com.lado.travago.transpido.repo.firebase.FirebaseAuthRepo
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.repo.firebase.StorageRepo
import com.lado.travago.transpido.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerCreationViewModel(private val agencyFirestorePath: String, private val agencyName: String) : ViewModel() {

    //FirebaseRepo utilities for db, signIn and cloud storage
    private val firestoreRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()
    private val authRepo = FirebaseAuthRepo()

    //LiveData to know when a process is in loading state
    private val _loading = MutableLiveData(false)
    val loading get() = _loading

    //LiveData to know when the creation of the scanner is completed
    private val _scannerCreated = MutableLiveData(false)
    val scannerCreated get() = _scannerCreated
    //LiveData to know if the scanner phone has been verified
    private val _onCodeVerified = MutableLiveData(false)
    val onCodeVerified get() = _onCodeVerified

    //LiveData to know if the verification code has been sent
    private val _onCodeSent = MutableLiveData(false)
    val onCodeSent get() = _onCodeSent

    //liveData which holds the phone number of
    private lateinit var generatedID: String

    //Values of the fields from the creationForm
    var nameField = ""
        private set
    var birthdayField = 0L
        private set
    var phoneField = ""
        private set
    var birthplaceField = ""
        private set
    var isAdminField = false
        private set
    var photoField: Bitmap? = null
        private set
    var sex = SEX.UNKNOWN
        private set
    var sexFieldId = 0
        private set
    var url = ""
        private set
    var smsCodeField = ""//verification code
        private set
    //Authentication variables
    lateinit var phoneCredential: PhoneAuthCredential
        private set
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
        private set
    var verificationId = ""
        private set

    /**
     * A function to set the value the fields from the creation form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setFields(fieldTag: FieldTags, value: Any) = when(fieldTag){
        FieldTags.NAME -> this.nameField = value.toString()
        FieldTags.BIRTHDAY -> birthdayField = value as Long
        FieldTags.PHONE -> phoneField = value.toString()
        FieldTags.SEX_ID -> sexFieldId = value as Int
        FieldTags.IS_ADMIN -> isAdminField = value as Boolean
        FieldTags.PROFILE_PHOTO -> photoField = value as Bitmap
        FieldTags.BIRTH_PLACE -> birthplaceField = value.toString()
        FieldTags.SEX -> sex = value as SEX
        FieldTags.ID -> generatedID = value as String
        FieldTags.SMS_CODE -> smsCodeField = value.toString()
    }

    /**
     * Sends a verification code to the user's phone
     */
    fun startPhoneVerification(activity: Activity){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneField,
            60L,
            TimeUnit.SECONDS,
            activity,
            phoneCallback
        )
    }

    /**
     * Used to resend after time out
     */
    fun resendVerificationCode(activity: Activity){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneField,
            60L,
            TimeUnit.SECONDS,
            activity,
            phoneCallback,
            resendToken
        )
    }

    //Callback to be called during phone verification
    private val phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            phoneCredential = credential
            _onCodeVerified.value = true
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            _onCodeVerified.value = false
            Log.e("AUTH", exception.message!!)
        }

        override fun onCodeSent(id: String, forceResendToken: PhoneAuthProvider.ForceResendingToken) {
            resendToken = forceResendToken
            verificationId = id
            _onCodeSent.value = true
//            super.onCodeSent(id, forceResendToken)
        }
    }

    /**
     * After code has been sent, we use the user's input 6-digit verification code
     */
    fun createCredentials(){
        phoneCredential = PhoneAuthProvider.getCredential(verificationId, smsCodeField)
        _onCodeVerified.value = true
    }


    /**
     * Contains different identifiers for the fields in our creation form
     * @property SMS_CODE is the verification code which the user received by sms
     */
    enum class FieldTags {
        NAME, SEX_ID, SEX, IS_ADMIN, PHONE, BIRTHDAY, BIRTH_PLACE, PROFILE_PHOTO, ID, SMS_CODE
    }


    /**
     * Adds the scanner to the db, upload the photo
     */
    suspend fun createScanner(){
        /**
         * Uses [FirebaseAuth] to sign in scanner to firestore and generate an id
         */
        authRepo.signInWithPhoneAuthCredential(phoneCredential).collect{authState ->
            when(authState){
                is State.Success -> {
                    onCodeVerified.value = false
                    //Get id from created user
                    generatedID = authState.data.uid

                    val photoStream = Utils.convertBitmapToStream(photoField!!, Bitmap.CompressFormat.PNG, 0)
                    //Upload the profile photo to the cloud storage and retrieve url
                    storageRepo.uploadPhoto(
                        photoStream,
                        generatedID,
                        FirestoreTags.Scanner,
                        StorageTags.PROFILE
                    ).collect{storageState ->
                        when(storageState){
                            is State.Loading -> _loading.value = true
                            is State.Failed -> {
                                loading.value = false
                                Log.e(TAG, storageState.message)
                            }
                            is State.Success -> {
                                url = storageState.data
                                val scanner = Scanner(
                                    generatedID,
                                    nameField,
                                    birthdayField,
                                    sex,
                                    url,
                                    phoneField,
                                    agencyName,
                                    null,
                                    birthplaceField,
                                    "Cameroon",
                                    isAdminField
                                )

                                //Adds the scanner to the database
                                firestoreRepo.setDocument(
                                    scanner.scannerMap,
                                    "${FirestoreTags.Scanner}/${scanner.scannerMap["uid"]}"
                                ).collect {dbState ->
                                    when(dbState){
                                        is State.Loading -> _loading.value = true
                                        is State.Failed -> {
                                            _loading.value = false
                                            Log.e(TAG, dbState.message)
                                        }
                                        is State.Success -> {
                                            _loading.value = false
                                            val otaScannerMap = OnlineTravelAgency.otaScannerMap(scanner)
                                            //e.g OTA/General/Scanners/Administrator/scanner/Tom Joe
                                            val otaScannerPath =
                                                "$agencyFirestorePath/Scanners${
                                                    if(scanner.isAdmin) "Administrators" else "Standard"} ${scanner.name}"

                                            //Add some data about the scanner to the document of the agency
                                            firestoreRepo.setDocument(
                                                otaScannerMap,
                                                otaScannerPath
                                            ).collect {dbState1 ->
                                                when(dbState1){
                                                    is State.Loading -> _loading.value = true
                                                    is State.Failed -> {
                                                        _loading.value = false
                                                        Log.e(TAG, dbState1.message)
                                                    }
                                                    is State.Success -> {
                                                        _loading.value = false
                                                        //Complete the creation process
                                                        _scannerCreated.value = true
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
                is State.Failed -> {

                }
            }
        }


    }

    fun formatDate(timeInMillis: Long) =
        Utils.formatDate(timeInMillis, "MMMM, dd YYYY")

    companion object{
        const val TAG = "ScannerCreationVM"
    }
}


