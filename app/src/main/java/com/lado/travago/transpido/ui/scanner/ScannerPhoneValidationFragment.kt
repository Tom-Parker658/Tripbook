package com.lado.travago.transpido.ui.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.PhoneAuthCredential
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentScannerPhoneValidationBinding
import com.lado.travago.transpido.viewmodel.admin.ScannerCreationViewModel
import com.lado.travago.transpido.viewmodel.admin.ScannerCreationViewModel.FieldTags
import kotlinx.coroutines.*

/**
 * A fragment which will be used for phone sms confirmation.
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerPhoneValidationFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var phoneAuthCredential: PhoneAuthCredential
    private lateinit var binding: FragmentScannerPhoneValidationBinding
    private val viewModel: ScannerCreationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(layoutInflater, R.layout.fragment_scanner_phone_validation, container, false)
        restoreFields()
        onFieldChange()
        onClickBtnVerify()
        onClickBtnResend()

        return binding.root
    }

    /**
     * Creates the scanner when the [ScannerCreationViewModel.onCodeVerified] is made true.
     */
    fun createScanner(){
        viewModel.onCodeVerified.observe(viewLifecycleOwner){
            uiScope.launch {
                if (it) viewModel.createScanner()
            }
        }
    }

    /**
     * When the verify button is clicked, we want to use the code to generate the phoneCredentials.
     * Later, we make the [ScannerCreationViewModel.onCodeVerified] true
     */
    private fun onClickBtnVerify()=
        binding.btnVerify.setOnClickListener {
            viewModel.createCredentials()
        }

    /**
     * Re-sends verification code after the resend button is tapped.
     */
    private fun onClickBtnResend() =
        binding.btnResend.setOnClickListener {
            viewModel.resendVerificationCode(requireActivity())
        }

    //Saves the value of the verification code field to view model
    private fun onFieldChange(){
        binding.verifyCode.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.SMS_CODE, binding.verifyCode.editText!!.text.toString())
        }
    }
    //Restore the value of the verification code field from view model
    private fun restoreFields(){
        binding.verifyCode.editText!!.setText(viewModel.smsCodeField)
    }


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
    }
}