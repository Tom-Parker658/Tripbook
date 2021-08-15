package com.lado.travago.tripbook.ui.agency.creation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CCPCountry
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyCreation1Binding
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationViewModel.*
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreation1Fragment : Fragment() {
    private var logoFileName = ""
    private lateinit var binding: FragmentAgencyCreation1Binding
    private lateinit var viewModel: AgencyCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_creation1,
            container,
            false
        )

        initViewModel()
        //Restore data to the textFields after any configuration change
        restoreSavedData()
        onFieldChange()
        onNextClicked()

        return binding.root
    }


    /**
     * Saves the content of the fields to the viewModel when any field is changes
     */
    private fun onFieldChange() {
        //Tries to load the logo gotten from the database
        if (viewModel.logoUrl.isNotBlank())
            binding.logoField.loadImageFromUrl(viewModel.logoUrl)

        binding.name.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NAME, it.toString())
        }
        binding.decreeNumber.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.DECREE_NUMBER, it.toString())
        }
        binding.nameOfCEO.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.CEO_NAME, it.toString())
        }
        binding.momo.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.MOMO_NUMBER, it.toString())
        }
        binding.motto.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.MOTTO, it.toString())
        }
        binding.orangeMoney.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.ORANGE_NUMBER, it.toString())
        }
        binding.bank.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.BANK_NUMBER, it.toString())
        }
        binding.supportEmail.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.SUPPORT_EMAIL, it.toString())
        }
        binding.supportPhone1.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.SUPPORT_PHONE_1, it.toString())
        }
        binding.supportPhone2.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.SUPPORT_PHONE_2, it.toString())
        }
        //Launches logo selection when logo image is tapped
        binding.logoField.setOnClickListener {
            initLogoSelection()
        }
        //Launches logo selection when the edit text is tapped
        binding.textLogo.setOnClickListener {
            initLogoSelection()
        }
        //Setup the phone formatter
        binding.countryCodePicker1.registerCarrierNumberEditText(binding.supportPhone1.editText)
        binding.countryCodePicker2.registerCarrierNumberEditText(binding.supportPhone2.editText)
    }


    /**
     * Restore all saved data to their respective views
     */
    private fun restoreSavedData() {
        binding.name.editText!!.setText(viewModel.nameField)
        binding.creationYear.editText!!.setText(viewModel.creationYearField)
        binding.supportPhone1.editText!!.setText(viewModel.supportPhone1Field)
        binding.supportPhone2.editText!!.setText(viewModel.supportPhone2Field)
        binding.momo.editText!!.setText(viewModel.momoField)
        viewModel.logoBitmap?.let{
            binding.logoField.setImageBitmap(it)
        }
        binding.nameOfCEO.editText!!.setText(viewModel.nameCEOField)
        binding.supportEmail.editText!!.setText(viewModel.supportEmailField)
        binding.motto.editText!!.setText(viewModel.mottoField)
        binding.orangeMoney.editText!!.setText(viewModel.orangeMoneyField)
        binding.bank.editText!!.setText(viewModel.bankField)
        binding.countryCodePicker1.setCountryForPhoneCode(viewModel.phoneCode1.toInt())
        binding.countryCodePicker2.setCountryForPhoneCode(viewModel.phoneCode2.toInt())
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[AgencyCreationViewModel::class.java]
    }

    /**
     * Launches the [pickAgencyLogo] event with the parameter image/
     */
    private fun initLogoSelection() = pickAgencyLogo.launch("image/*")


    /**
     * Navigates to the next screen when the next button is clicked
     */
    private fun onNextClicked() = binding.btnNext.setOnClickListener {
        if(binding.countryCodePicker1.isValidFullNumber ) {
            viewModel.setField(FieldTags.FULL_SUPPORT_PHONE_1, binding.countryCodePicker1.fullNumberWithPlus)
            if(binding.countryCodePicker2.isValidFullNumber || binding.supportPhone2.editText!!.text.toString().isBlank()) {
                try{
                    binding.countryCodePicker2.fullNumberWithPlus.let {
                        viewModel.setField(FieldTags.FULL_SUPPORT_PHONE_2, it)
                    }
                    //In case we are ok with the phone fields, we can start field checking
                    viewModel.checkFields(this@AgencyCreation1Fragment)
                }catch (e: Exception){//In case phone is empty}
            }
        }
            else{
                viewModel.setField(FieldTags.TOAST_MESSAGE, "Enter valid number or leave it empty")
                binding.supportPhone2.requestFocus()
            }
        }else{
            viewModel.setField(FieldTags.TOAST_MESSAGE, "Invalid support phone number")
            binding.supportPhone1.requestFocus()
        }
    }




    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [AgencyCreationViewModel.logoBitmap]
     * else we re-launch the event
     */
    private val pickAgencyLogo: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { logoUri ->
        logoUri?.let { it ->
            val logoStream = requireActivity().contentResolver.openInputStream(it)!!
            viewModel.setField(
                FieldTags.LOGO_BITMAP,
                BitmapFactory.decodeStream(logoStream)
            )
            if (viewModel.logoBitmap?.byteCount!! > (1024*1024*2)) { //In case image larger than 2-MegaByte
                viewModel.setField(FieldTags.TOAST_MESSAGE, "Photo is too large!!")
                initLogoSelection()
            }
            else // Sets the logo field to the name of the selected photo
                binding.logoField.setImageBitmap(viewModel.logoBitmap)
        }
    }

}