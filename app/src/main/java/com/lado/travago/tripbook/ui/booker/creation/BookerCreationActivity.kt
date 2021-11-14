package com.lado.travago.tripbook.ui.booker.creation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBookerCreationBinding
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.agency.config_panel.AgencyConfigActivity
//import com.lado.travago.tripbook.databinding.ActivityUserCreationBinding
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.FieldTags
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
@InternalCoroutinesApi

class BookerCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookerCreationBinding
    private lateinit var viewModel: BookerCreationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Restore all fields after configuration changes
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booker_creation)
        initViewModel()
        setupNavigation()

        observeLiveData()
        showProgressBar()
    }

    private fun setupNavigation() {
        val navController = findNavController(binding.bookerCreationNavHost.id)
        NavigationUI.setupWithNavController(binding.bottomBookerNav, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                //Within this view
                R.id.nav_booker_creation -> {
                    if (viewModel.authRepo.currentUser == null) {
                        controller.navigate(BookerCreation1FragmentDirections.actionBookerCreation1FragmentToBookerCreationFinalFragment())
                    }
                }
                R.id.bookerCreation1Fragment -> {
                    if (viewModel.authRepo.currentUser == null) {
                        controller.navigate(BookerCreation1FragmentDirections.actionBookerCreation1FragmentToBookerCreationFinalFragment())
                    }
                }
                R.id.bookerCreation2Fragment -> {
                    destination.label =
                        "${getString(R.string.frag_booker_creation_2)}: ${viewModel.bookerPhoneField}"
                }
                R.id.bookerCreationFinalFragment -> {
                    if (viewModel.authRepo.currentUser == null)
                        binding.bottomBookerNav.visibility = View.GONE
                    else binding.bottomBookerNav.visibility = View.VISIBLE
                }

            }
        }
    }

    /**
     * Observes live-data and reacts accordingly
     */
    private fun observeLiveData() {
        viewModel.onLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
        viewModel.toastMessage.observe(this) {
            if (it.isNotBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                Log.i("BookerCreation", it)
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.sendCode.observe(this) {
            if (it) {
                viewModel.startLoading()
                sendVerificationCode()
                viewModel.setField(FieldTags.SEND_CODE, false)
            }
        }
        viewModel.resendCode.observe(this) {
            if (it) {
                viewModel.startLoading()
                resendVerificationCode()
                viewModel.setField(FieldTags.RESEND_CODE, false)
            }
        }
        viewModel.onCodeSent.observe(this) {
            if (it) {
                try {
                    findNavController(binding.bookerCreationNavHost.id).navigate(R.id.action_bookerCreation1Fragment_to_bookerCreation2Fragment)
                } catch (exception: Exception) {/*In case we are resending the sms  already at the booker creation 2 screen */
                }
            }
        }
        viewModel.onPhoneVerified.observe(this) {
            if (it) {
                viewModel.startLoading()
                createCredentials()
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.loginOrSignup()
                }
                viewModel.setField(FieldTags.ON_PHONE_VERIFIED, false)
            }
        }
        viewModel.startInfoUpload.observe(this) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.setField(FieldTags.START_INFO_UPLOAD, false)
                    viewModel.saveBookerInfo()
                }
            }
        }
        viewModel.navToInfoScreen.observe(this) {
            if (it) {
                viewModel.stopLoading()
                findNavController(binding.bookerCreationNavHost.id).navigate(R.id.action_bookerCreation2Fragment_to_bookerCreationFinalFragment)
                viewModel.setField(FieldTags.NAV_TO_INFO, false)
            }
        }

        viewModel.onBookerCreated.observe(this) {
            if (it) {
                navigateToLauncherUI()
                finish()
            }
        }
    }

    //Callback to be called during phone verification
    private val phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            viewModel.setField(FieldTags.PHONE_CREDENTIAL, credential)
            viewModel.setField(FieldTags.ON_PHONE_VERIFIED, true)
            viewModel.startLoading()
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                exception.handleError { })
            viewModel.stopLoading()
        }

        override fun onCodeSent(
            verificationID: String,
            forceResendToken: PhoneAuthProvider.ForceResendingToken
        ) {
            viewModel.setField(FieldTags.RESEND_TOKEN, forceResendToken)
            viewModel.setField(FieldTags.VERIFICATION_ID, verificationID)
            viewModel.setField(FieldTags.ON_CODE_SENT, true)
            viewModel.setField(FieldTags.TOAST_MESSAGE, "Check your SMS!")
            viewModel.stopLoading()
        }

    }

    /**
     * After code has been sent, we use the user's input 6-digit verification code to verify then login or signUp
     */
    private fun createCredentials() {
        val credential =
            PhoneAuthProvider.getCredential(viewModel.verificationId, viewModel.verificationCode)
        viewModel.setField(FieldTags.PHONE_CREDENTIAL, credential)
    }


    /**
     * Sends a verification code to the user's phone
     */
    private fun sendVerificationCode() = PhoneAuthProvider.verifyPhoneNumber(
        PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+${viewModel.bookerCountryCode}${viewModel.bookerPhoneField.removeSpaces()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(phoneCallback)
            .build()
    )

    /**
     * Used to resend after time out
     */
    private fun resendVerificationCode() {
        val phoneAuthOptions = PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+${viewModel.bookerCountryCode}${viewModel.bookerPhoneField.removeSpaces()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(phoneCallback)
            .setForceResendingToken(viewModel.resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(
            phoneAuthOptions
        )
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[BookerCreationViewModel::class.java]
    }

    /**
     * Navigates back to the UI which originally launched this creation as an intent. It returns with no data as intent,
     * But only with the RESULT status
     */
    //TODO: TEST: For now, we navigate to config center lobby
    private fun navigateToLauncherUI() {
        startActivity(
            Intent(this, AgencyConfigActivity::class.java)
        )
        finish()
//        viewModel.onBookerCreated.observe(this) {
//            if (it) {
//                setResult(Activity.RESULT_OK, null)
//                finish()
//                viewModel.stopLoading()
//            }
//        }
    }

    /**
     * Observe the [BookerCreationViewModel.onLoading] live data to know when a process is actually in the loading state
     * inorder to show the progress bar. It then makes the progress bar invisible if there is no loading
     * process anymore
     */
    private fun showProgressBar() =
        viewModel.onLoading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

}