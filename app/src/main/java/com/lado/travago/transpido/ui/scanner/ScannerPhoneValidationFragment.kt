package com.lado.travago.transpido.ui.scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentScannerPhoneValidationBinding
import com.lado.travago.transpido.ui.agency.AgencyRegistrationActivity
import com.lado.travago.transpido.viewmodel.ScannerCreationViewModelFactory
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
    private lateinit var binding: FragmentScannerPhoneValidationBinding
    private lateinit var viewModel: ScannerCreationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(layoutInflater, R.layout.fragment_scanner_phone_validation, container, false)

        initViewModel()
        restoreFields()
        onFieldChange()
        onClickBtnVerify()
        onClickBtnResend()
        createScanner()
        enableLayout()


        return binding.root
    }

    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */
    private fun initViewModel(){
        //Data gotten from the agency
        val intentData = getIntentData()
        val viewModelFactory = ScannerCreationViewModelFactory(
            agencyName = intentData.first,
            agencyFirestorePath = intentData.second
        )
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ScannerCreationViewModel::class.java]
    }

    /**
     * Gets the intent data which is passed from the Agency to launch the Scanner creation.
     * The intent contains the agency name and the database path to teh agency's document. Tis data wil
     * be used for the creation of the scanner.
     * We assume their values can not be null
     * @return A pair where first = agencyName and second = path
     */
    private fun getIntentData(): Pair<String, String>{
        val agencyFirestorePath = requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_OTA_PATH) !!
        val agencyName = requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME) !!

        Log.i("ScannerCreationActivity", "agencyName=$agencyName, path=$agencyFirestorePath")
        return agencyName to agencyFirestorePath
    }


    /**
     * A function which checks if the code has been sent. If then, we activate the layout to let the user input his
     * verification code. Observes [ScannerCreationViewModel.onCodeSent] and if true, we remove the loading spin
     */
    private fun enableLayout() = viewModel.onCodeSent.observe(viewLifecycleOwner){
        if(it) viewModel.stopLoading()
    }
    /**
     * Creates the scanner when the [ScannerCreationViewModel.onCodeVerified] is made true.
     */
    private fun createScanner(){
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

//    /**
//     * Helper method to display snackbars
//     */
//    private fun showSnackbar(message: String) = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()

    companion object{
        const val TAG = "PhoneValidationFrag"
    }
}