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
import com.lado.travago.tripbook.databinding.FragmentBookerCreation2Binding
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * SMS confirmation layout
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreation2Fragment : Fragment() {
    private lateinit var binding: FragmentBookerCreation2Binding
    private lateinit var viewModel: BookerCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_creation2,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity())[BookerCreationViewModel::class.java]

        onFieldChange()
        return binding.root
    }

    private fun onFieldChange() {
        val headline = "Check SMS at: +${viewModel.bookerCountryCode} ${viewModel.bookerPhoneField}"
        binding.textCodeConfirmation.text = headline
        binding.verificationCode.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.VERIFICATION_CODE, it.toString())
        }
        binding.verificationCode.editText!!.setText(viewModel.verificationCode)
        binding.btnConfirmCode.setOnClickListener {
            viewModel.setField(FieldTags.ON_PHONE_VERIFIED, true)
        }
        binding.btnResendCode.setOnClickListener {
            viewModel.setField(FieldTags.RESEND_CODE, true)
        }
    }

}