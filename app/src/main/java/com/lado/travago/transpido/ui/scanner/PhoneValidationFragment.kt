package com.lado.travago.transpido.ui.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentPhoneValidationBinding
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.firebase.FirebaseAuthRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * A fragment which will be used for phone sms confirmation.
 */
@ExperimentalCoroutinesApi
class PhoneValidationFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var phoneAuthCredential: PhoneAuthCredential
    private lateinit var binding: FragmentPhoneValidationBinding
    private lateinit var phoneNumber: String
    private lateinit var verificationID: String
    private val onCodeVerified = MutableLiveData(false)
    private val authRepo = FirebaseAuthRepo()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(layoutInflater, R.layout.fragment_phone_validation, container, false)
        getPhoneArguments()
        authenticateScanner()
        onClickBtnVerify()

        return binding.root
    }

    /**
     * We get the phone number gotten from the initial navigator.
     * We then set the heading of the screen to "Verify +[phoneNumber]"
     */
    private fun getPhoneArguments(){
        phoneNumber = PhoneValidationFragmentArgs.fromBundle(requireArguments()).phoneNumber
        val headingText = "Verify +$phoneNumber"
        binding.heading.text = headingText
    }

    /**
     * Verifies the confirmation code and generate the auth credentials.
     * These auth credentials will be used to generate an ID for the user
     */
    private fun verifyCode(id: String, code: String){
        phoneAuthCredential = PhoneAuthProvider.getCredential(id, code)

        // We assume the code has been verified. If later there is an error, we can still change it
        onCodeVerified.value = true
    }

    /**
     *  We signIn the user to firebase auth gets an ID. this ID is now used to navigate to the original
     *  navigator with the ID. this id will be used to identify that user to cloud firestore
     */
    private suspend fun generateVerificationId()=
        authRepo.signInWithPhoneAuthCredential(phoneAuthCredential).collect{authState ->
            when(authState){
                is State.Success -> {
                    onCodeVerified.value = false
                    binding.progressBar.visibility = View.GONE
                    done.value = true
                    PhoneValidationFragmentDirections.actionPhoneValidationFragmentToScannerRegistrationFragment2(authState.data.uid)
                }
                is State.Failed -> {
                    showSnackbar("Something went wrong! Try Again")
                }
            }
        }


    private fun onClickBtnVerify()=
        binding.btnVerify.setOnClickListener {
            if(!::phoneAuthCredential.isInitialized)
                verifyCode(verificationID, binding.verifyCode.editText.toString())
    
            onCodeVerified.observe(viewLifecycleOwner){
                uiScope.launch {
                    if (it) {
                        generateVerificationId()
                    }
                }
            }
        }




    private fun authenticateScanner()= PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60L,
            TimeUnit.SECONDS,
            requireActivity(),
            /**
             * The callback method which specifies what to do in different states of the phoneAuth process.
             * In case the code is to b sent, we navigate to the [PhoneValidationFragment]
             */
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //In case the phone does the code verification automatically
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthCredential = credential
                onCodeVerified.value = true
            }
            //In case verification fails we tell the user to try later
            override fun onVerificationFailed(exception: FirebaseException) {
                onCodeVerified.value = false
//            Log.e(ScannerRegistrationFragment.TAG, exception.message.toString())
                when(exception) {
                    is FirebaseTooManyRequestsException -> showSnackbar("Try Again in a minute!")
                    is FirebaseAuthInvalidCredentialsException -> showSnackbar("Invalid Verification code: Try Again!")
                    else -> showSnackbar("Something went wrong. Try again!")
                }
            }
            // After the verification code has been sent, we verify the phoneNumber
            override fun onCodeSent(
                id: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                binding.progressBar.visibility = View.GONE
            }
        }
        )

    /**
     * Helper method to display toasts
     */
    private fun showToast(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    /**
     * Helper method to display snackbars
     */
    private fun showSnackbar(message: String) = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()

    companion object{
        const val TAG = "PhoneValidationFrag"
        val done = MutableLiveData(false)
    }
}