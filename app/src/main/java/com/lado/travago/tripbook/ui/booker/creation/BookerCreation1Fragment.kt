package com.lado.travago.tripbook.ui.booker.creation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerCreation1Binding

import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.*
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * A fragment for the phone auth using the country code picker
 */

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreation1Fragment : Fragment() {
    private lateinit var binding: FragmentBookerCreation1Binding
    private lateinit var viewModel: BookerCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_creation1,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity())[BookerCreationViewModel::class.java]

        phoneWidgetConfig()
        onFieldChanged()
        onButtonSendSMS()
        return binding.root
    }

    private fun onFieldChanged() {
        binding.phone.editText!!.setText(viewModel.bookerPhoneField.removeSpaces())
        binding.countryCodePicker.setCountryForPhoneCode(viewModel.bookerCountryCode)
        binding.phone.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.BOOKER_PHONE, it.toString())
            viewModel.setField(
                FieldTags.BOOKER_COUNTRY_CODE,
                binding.countryCodePicker.selectedCountryCodeAsInt
            )
        }
    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() =
        binding.countryCodePicker.registerCarrierNumberEditText(binding.phone.editText)

    private fun onButtonSendSMS() =
        binding.btnSendSms.setOnClickListener {
            if (binding.countryCodePicker.isValidFullNumber) {
                viewModel.setField(FieldTags.SEND_CODE, true)
            } else viewModel.setField(FieldTags.TOAST_MESSAGE, "Invalid phone number!")
        }
    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

}