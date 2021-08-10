package com.lado.travago.tripbook.ui.booker.creation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerCreation1Binding

import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.*
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_booker_creation1, container, false)
        viewModel = ViewModelProvider(requireActivity())[BookerCreationViewModel::class.java]

        phoneWidgetConfig()
        onFieldChanged()
        binding.countryCodePicker.setCountryForPhoneCode(savedInstanceState?.getInt("COUNTRY_CODE") ?: 237)
        onButtonSendSMS()
        return binding.root
    }

    /**
     * Saves country code
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("COUNTRY_CODE", binding.countryCodePicker.selectedCountryCodeAsInt)
        super.onSaveInstanceState(outState)
    }

    private fun onFieldChanged(){
        binding.phone.editText!!.addTextChangedListener { text ->
            viewModel.setField(FieldTags.PHONE, text.toString())
        }

        binding.phone.editText!!.setText(viewModel.phoneField)
    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() = binding.countryCodePicker.registerCarrierNumberEditText(binding.phone.editText)

    private fun onButtonSendSMS(){
        binding.btnSendSms.setOnClickListener {
            if(binding.countryCodePicker.isValidFullNumber) {
                viewModel.setField(
                    FieldTags.FULL_PHONE,
                    binding.countryCodePicker.fullNumberWithPlus
                )
                viewModel.setField(FieldTags.SEND_CODE, true)
            }
            else viewModel.setField(FieldTags.TOAST_MESSAGE, "Invalid phone number!")
        }

    }

}