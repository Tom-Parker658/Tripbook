package com.lado.travago.tripbook.ui.booker.creation

import androidx.core.widget.addTextChangedListener
import com.lado.travago.tripbook.databinding.FragmentBookerSignInBinding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel

import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel.*
import com.lado.travago.tripbook.utils.AdminUtils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.*

/**
 * A fragment for the phone auth using the country code picker
 */

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerSignInFragment : Fragment() {
    private lateinit var binding: FragmentBookerSignInBinding
    private lateinit var viewModel: BookerSignInViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_sign_in,
            container,
            false
        )
        createViewModel()

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        getAndRespondToArgs()
        phoneWidgetConfig()
        restoreFields()
        binding.phone.requestFocus()
        onFieldChanged()
        onButtonSendSMS()
        watchCountDownTimer()
        observeLiveData()
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(requireActivity())[BookerSignInViewModel::class.java]
        viewModel.setField(FieldTags.RES, resources)
    }

    private fun onFieldChanged() {
        binding.phone.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.BOOKER_PHONE, it.toString())
            viewModel.setField(
                FieldTags.BOOKER_COUNTRY_CODE,
                binding.countryCodePicker.selectedCountryCodeAsInt
            )
        }
    }

    private fun restoreFields() {
        binding.phone.editText!!.setText(viewModel.phoneField)
        binding.countryCodePicker.setCountryForPhoneCode(viewModel.phoneCountryCode)
    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() =
        binding.countryCodePicker.registerCarrierNumberEditText(binding.phone.editText)

    private fun onButtonSendSMS() =
        binding.btnSendSms.setOnClickListener {
            if (binding.countryCodePicker.isValidFullNumber) {
                viewModel.sendVerificationCode(
                    requireActivity()
                )
            } else {
                binding.phone.requestFocus()
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.warn_invalid_phone))
            }
        }


    /**
     * Watches our timer and shows the resend button when the count down is finished
     */
    private fun watchCountDownTimer() {
        viewModel.countDown.isRunning.observe(viewLifecycleOwner) {
            if (it) {
                binding.chronoTimer.visibility = View.VISIBLE
                binding.btnSendSms.visibility = View.INVISIBLE

                viewModel.countDown.left.observe(viewLifecycleOwner) { txt ->
                    val text = "$txt ${getString(R.string.text_qty_left)}"
                    binding.chronoTimer.text = text
                }

            } else if (viewModel.countDown.isEnded.value!!) {
                binding.chronoTimer.visibility = View.GONE
                binding.btnSendSms.visibility = View.VISIBLE
            }
        }

    }

    private fun getAndRespondToArgs() {
        val args = BookerSignInFragmentArgs.fromBundle(requireArguments())
        viewModel.setField(FieldTags.SIGNUP_CALLER, args.caller)
        when (args.caller) {
            //TODO: Serves nothing
            SignUpCaller.PHONE_CHANGE -> {
                viewModel.setField(FieldTags.BOOKER_PHONE, viewModel.newPhoneField)
                viewModel.setField(FieldTags.BOOKER_COUNTRY_CODE, viewModel.newPhoneCountryCode)
                restoreFields()
                binding.btnSendSms.callOnClick()
//                viewModel.sendVerificationCode(requireActivity())
            }
            SignUpCaller.USER -> {}
            SignUpCaller.OTHER_ACTIVITY -> {}
        }
    }

    private fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBookerCreation.visibility = View.VISIBLE
            else binding.progressBookerCreation.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onCodeSent.observe(viewLifecycleOwner) {
            if (it) {
                navigateToConfirmation()
                viewModel.setField(FieldTags.ON_CODE_SENT, false)
            }
        }

    }

    private fun navigateToConfirmation() {
        val formattedPhone = "+${viewModel.phoneCountryCode} ${viewModel.phoneField}"
        findNavController().navigate(
            BookerSignInFragmentDirections.actionBookerSignInFragmentToBookerConfirmationFragment(
                formattedPhone
            )
        )
    }

}