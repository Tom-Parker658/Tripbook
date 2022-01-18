package com.lado.travago.tripbook.ui.booker.creation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerConfirmationBinding
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel.*
import com.lado.travago.tripbook.utils.UIUtils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * SMS confirmation layout
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerConfirmationFragment : Fragment() {
    private lateinit var binding: FragmentBookerConfirmationBinding
    private lateinit var viewModel: BookerSignInViewModel
    private lateinit var uiUtils: UIUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_confirmation,
            container,
            false
        )
        createViewModel()
        uiUtils = UIUtils(this, requireActivity(), viewLifecycleOwner)
        return binding.root
    }

    override fun onStart() {
        onFieldChange()
        observeLiveData()
        watchCountDownTimer()
        super.onStart()
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(requireActivity())[BookerSignInViewModel::class.java]
        viewModel.setField(FieldTags.RES, resources)
    }

    private fun onFieldChange() {
        viewModel.setField(FieldTags.RES, resources)
        binding.verificationCode.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.VERIFICATION_CODE, it.toString())
        }

        binding.btnConfirmCode.setOnClickListener {
            checkField()
        }
        binding.btnResendCode.setOnClickListener {
            viewModel.resendVerificationCode(requireActivity())
        }
        binding.verificationCode.editText!!.setText(viewModel.verificationCode)
        //For cases when the phone verification is done automatically
        viewModel.onPhoneVerified.observe(viewLifecycleOwner) {
            if (it) {
                signUpOrIn()
                viewModel.setField(FieldTags.ON_PHONE_VERIFIED, false)
            }
        }
    }

    /**
     * Watches our timer and shows the resend button when the count down is finished
     */
    private fun watchCountDownTimer() {
        viewModel.countDown.isRunning.observe(viewLifecycleOwner) {
            if (it) {
                binding.chronoTimer.visibility = View.VISIBLE
                binding.btnResendCode.visibility = View.INVISIBLE

                viewModel.countDown.left.observe(viewLifecycleOwner) { txt ->
                    val text = "$txt ${getString(R.string.text_qty_left)}"
                    binding.chronoTimer.text = text
                }

            } else if (viewModel.countDown.isEnded.value!!) {
                binding.chronoTimer.visibility = View.GONE
                binding.btnResendCode.visibility = View.VISIBLE
            }
        }

    }

    //Signup process or change number process
    private fun signUpOrIn() {
        viewModel.createCredential()
        when (viewModel.caller) {
            SignUpCaller.PHONE_CHANGE -> {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.changePhoneNumber()
                }
            }
            else -> CoroutineScope(Dispatchers.Main).launch {
                viewModel.signUpOrSignIn()
            }
        }
    }

    private fun checkField() {
        if (binding.verificationCode.editText!!.text.isNotBlank() && binding.verificationCode.editText!!.text.length == 6) {
            signUpOrIn()
        } else viewModel.setField(//TODO: Sort of unneccesary action since by all ways, we will still know if the code is wrong or correct
            FieldTags.TOAST_MESSAGE,
            getString(R.string.warn_wrong_confirmation_code)
        )
    }


    fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar3.visibility = View.VISIBLE
            else binding.progressBar3.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }

        viewModel.onCodeSent.observe(viewLifecycleOwner) {
            if (it) binding.verificationCode.requestFocus()

        }

        viewModel.onPhoneSwapped.observe(viewLifecycleOwner) {
            if (it) {
                //We save the new credentials to the cache memory
                uiUtils.editSharedPreference(UIUtils.SP_STRING_BOOKER_PHONE,
                    viewModel.phoneField.removeSpaces())
                uiUtils.editSharedPreference(UIUtils.SP_INT_BOOKER_COUNTRY_CODE,
                    viewModel.phoneCountryCode)

                val newData = hashMapOf<String, Any?>(
                    "phone" to viewModel.phoneField,
                    "PhoneCountryCode" to viewModel.phoneCountryCode
                )

                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.firestoreRepo.updateDocument(
                        newData,
                        "Bookers/${viewModel.authRepo.currentUser!!.uid}"
                    ).collect { state ->
                        when (state) {
                            is State.Failed -> {
                                viewModel.setField(
                                    FieldTags.TOAST_MESSAGE,
                                    state.exception.handleError { }
                                )
                            }
                            is State.Loading -> viewModel.setField(
                                FieldTags.ON_LOADING,
                                true
                            )
                            is State.Success -> {
                                findNavController().navigate(R.id.bookerCreationCenter)
                                viewModel.setField(
                                    FieldTags.TOAST_MESSAGE,
                                    getString(R.string.congrats_phone_changed)
                                )

                            }
                        }
                    }
                }

            }
        }

        //This is when the booker is new
        viewModel.onSignUp.observe(viewLifecycleOwner) {
            if (it) {
                /**
                 * We are saving the booker phone number so that we can get it and save it under the profile
                 * at anytime
                 */
                //We save the new credentials to the cache memory
                uiUtils.editSharedPreference(UIUtils.SP_STRING_BOOKER_PHONE,
                    viewModel.phoneField.removeSpaces())
                uiUtils.editSharedPreference(UIUtils.SP_INT_BOOKER_COUNTRY_CODE,
                    viewModel.phoneCountryCode)
                uiUtils.editSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST, false)

                findNavController().navigate(
                    BookerConfirmationFragmentDirections.actionBookerConfirmationFragmentToBookerProfileFragment(
                        viewModel.caller
                    )
                )
            }
        }

        //The booker is not new
        viewModel.onSignIn.observe(viewLifecycleOwner) {
            if (it) {
                uiUtils.editSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST, true)
                when (viewModel.caller) {
                    SignUpCaller.USER -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.bookerCreationCenter)
                    }
                    SignUpCaller.OTHER_ACTIVITY -> {
                        findNavController().popBackStack()
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                    else -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.bookerCreationCenter)
                    }
                }
            }
        }
    }

}