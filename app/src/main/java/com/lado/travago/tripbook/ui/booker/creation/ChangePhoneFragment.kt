package com.lado.travago.tripbook.ui.booker.creation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentChangePhoneBinding
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel.*
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel
import com.lado.travago.tripbook.utils.AdminUtils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ChangePhoneFragment : Fragment() {
    private lateinit var binding: FragmentChangePhoneBinding
    private lateinit var viewModel: BookerSignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_change_phone,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity())[BookerSignInViewModel::class.java]
        phoneWidgetConfig()
        restoreFields()
        onFieldsChanged()
        observeLiveData()
        return binding.root
    }

    private fun onFieldsChanged() {
        binding.countryCodePickerOld.setOnCountryChangeListener {
            viewModel.setField(
                FieldTags.OLD_PHONE_CODE,
                binding.countryCodePickerOld.selectedCountryCodeAsInt
            )
        }
        binding.countryCodePickerNew.setOnCountryChangeListener {
            viewModel.setField(
                FieldTags.NEW_PHONE_CODE,
                binding.countryCodePickerNew.selectedCountryCodeAsInt
            )
        }
        binding.newPhone.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NEW_PHONE, it.toString().removeSpaces())
        }
        binding.oldPhone.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.OLD_PHONE, it.toString().removeSpaces())
        }
        binding.btnSwapPhoneNum.setOnClickListener {
            checkFields()
        }
    }

    private fun restoreFields() {
        if (viewModel.newPhoneCountryCode != 0)
            binding.countryCodePickerNew.setCountryForPhoneCode(viewModel.newPhoneCountryCode)
        if (viewModel.oldPhoneCountryCode != 0)
            binding.countryCodePickerOld.setCountryForPhoneCode(viewModel.oldPhoneCountryCode)

        binding.newPhone.editText!!.setText(viewModel.newPhoneField)
        binding.oldPhone.editText!!.setText(viewModel.oldPhoneField)
    }

    fun observeLiveData() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() {
        binding.countryCodePickerOld.registerCarrierNumberEditText(binding.oldPhone.editText)
        binding.countryCodePickerNew.registerCarrierNumberEditText(binding.newPhone.editText)
    }

    private fun checkFields() {
        if (!binding.countryCodePickerNew.isValidFullNumber) {
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.warn_invalid_phone))
            binding.newPhone.requestFocus()
        } else if (!binding.countryCodePickerOld.isValidFullNumber) {
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.warn_invalid_phone))
            binding.oldPhone.requestFocus()
        } else if (binding.countryCodePickerOld.fullNumberWithPlus != viewModel.authRepo.currentUser!!.phoneNumber) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                getString(R.string.warn_old_phone_not_match)
            )
            binding.oldPhone.requestFocus()
        } else if (binding.countryCodePickerNew.fullNumberWithPlus == viewModel.authRepo.currentUser!!.phoneNumber) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                getString(R.string.warn_old_phone_same_as_new)
            )
            binding.newPhone.requestFocus()
        } else {
            viewModel.setField(FieldTags.NEW_PHONE_CODE, binding.countryCodePickerNew.selectedCountryCodeAsInt)
            viewModel.setField(FieldTags.OLD_PHONE_CODE, binding.countryCodePickerOld.selectedCountryCodeAsInt)
            findNavController().navigate(
                ChangePhoneFragmentDirections.actionChangePhoneFragmentToBookerSignInFragment(
                    SignUpCaller.PHONE_CHANGE
                )
            )
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.sign_in_with_new_phone))
        }
    }

}