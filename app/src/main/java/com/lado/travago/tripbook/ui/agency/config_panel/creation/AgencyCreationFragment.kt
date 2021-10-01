package com.lado.travago.tripbook.ui.agency.config_panel.creation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyCreationBinding
import com.lado.travago.tripbook.ui.agency.config_panel.creation.AgencyCreationViewModel.*
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreationFragment : Fragment() {
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentAgencyCreationBinding
    private lateinit var viewModel: AgencyCreationViewModel

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_creation,
            container,
            false
        )
        initViewModels()
        //Restore data to the textFields after any configuration change
        restoreSavedData()
        onFieldChange()
        onNextClicked()

        observeLiveData()
        return binding.root
    }

    private fun observeLiveData() {
        viewModel.retry.observe(viewLifecycleOwner) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getExistingAgencyData(parentViewModel.bookerDoc.value!!.getString("agencyID"))
                }
            }
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        //We check to see if to save a new logo or not
        viewModel.startSaving.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.logoBitmap == null && binding.logoField.drawable == viewModel.logoBitmap!!.toDrawable(
                        requireActivity().resources
                    )
                ) {
                    viewModel.setField(FieldTags.ON_LOGO_SAVED, true)
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        //TODO: Emulator commenting
//                        viewModel.saveLogo()
                        if (viewModel.agencyDbData.exists()) /*We update*/ viewModel.updateAgencyInfo(
                            parentViewModel.bookerDoc.value!!
                        )
                        else /*We create*/ viewModel.createAgency(parentViewModel.bookerDoc.value!!)

                    }
                }
                viewModel.setField(FieldTags.START_SAVING, false)
            }
        }
        //We check to see to create or modify agency
        //TODO: Emulator commenting
//        viewModel.onLogoSaved.observe(viewLifecycleOwner) {
//            if (it) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    if (viewModel.agencyDbData.exists()) /*We update*/ viewModel.updateAgencyInfo(
//                        parentViewModel.bookerDoc.value!!
//                    )
//                    else /*We create*/ viewModel.createAgency(parentViewModel.bookerDoc.value!!)
//                }
//            }
//        }
        viewModel.startFilling.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.fillExistingData()
                restoreSavedData()
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        //In this case we go back to the launcher activity which is actually th config activity
        viewModel.onInfoSaved.observe(viewLifecycleOwner) {
            if (it) {
                if (!viewModel.agencyDbData.exists())
                    CoroutineScope(Dispatchers.Main).launch {
                        parentViewModel.getCurrentBooker()
                    }
                findNavController().navigate(
                    AgencyCreationFragmentDirections.actionAgencyCreationFragmentToAgencyConfigCenterFragment()
                )
            }
        }
        viewModel.onVerificationFailed.observe(viewLifecycleOwner) {
            //We end activity if we can't verify we are editing or creating an agency
            if (it) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_message_error_auth_fail)
                )
                findNavController().navigate(
                    AgencyCreationFragmentDirections.actionAgencyCreationFragmentToAgencyConfigCenterFragment()
                )
            }
        }
    }

    /**
     * Saves the content of the fields to the viewModel when any field is changes
     */
    private fun onFieldChange() {
        //Tries to load the logo gotten from the database
        if (viewModel.logoUrl.isNotBlank()) binding.logoField.loadImageFromUrl(viewModel.logoUrl)
        else viewModel.setField(FieldTags.LOGO_BITMAP, binding.logoField.drawable.toBitmap(75, 75))

        binding.name.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NAME, it.toString())
        }
        binding.decreeNumber.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.DECREE_NUMBER, it.toString())
        }
        binding.nameOfCEO.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.CEO_NAME, it.toString())
        }
        binding.creationYear.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.CREATION_YEAR, it.toString())
        }

        binding.motto.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.MOTTO, it.toString())
        }

        binding.supportEmail.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.SUPPORT_EMAIL, it.toString())
        }
        binding.supportPhone1.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.SUPPORT_PHONE_1, it.toString())
            viewModel.setField(
                FieldTags.PHONE_CODE_1,
                binding.countryCodePicker1.selectedCountryCodeAsInt
            )
        }
        binding.supportPhone2.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.SUPPORT_PHONE_2, it.toString())
            viewModel.setField(
                FieldTags.PHONE_CODE_2,
                binding.countryCodePicker2.selectedCountryCodeAsInt
            )
        }
        //Launches logo selection when logo image is tapped
        binding.logoField.setOnClickListener {
            initLogoSelection()
        }
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
        binding.decreeNumber.editText!!.setText(viewModel.decreeNumberField)
        binding.supportPhone1.editText!!.setText(viewModel.supportPhone1Field.removeSpaces())
        binding.supportPhone2.editText!!.setText(viewModel.supportPhone2Field.removeSpaces())
        binding.countryCodePicker1.setCountryForPhoneCode(viewModel.phoneCode1)
        binding.countryCodePicker2.setCountryForPhoneCode(viewModel.phoneCode2)
        viewModel.logoBitmap?.let {
            binding.logoField.setImageBitmap(it)
        }
        binding.nameOfCEO.editText!!.setText(viewModel.nameCEOField)
        binding.supportEmail.editText!!.setText(viewModel.supportEmailField)
        binding.motto.editText!!.setText(viewModel.mottoField)
    }

    private fun initViewModels() {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        viewModel = ViewModelProvider(this)[AgencyCreationViewModel::class.java]
    }

    /**
     * Launches the [pickAgencyLogo] event with the parameter image/
     */
    private fun initLogoSelection() = pickAgencyLogo.launch("image/*")

    /**
     * Navigates to the next screen when the next button is clicked
     */
    private fun onNextClicked() = binding.btnNext.setOnClickListener {
        if (binding.countryCodePicker1.isValidFullNumber) {
            if (binding.countryCodePicker2.isValidFullNumber || binding.supportPhone2.editText!!.text.toString()
                    .isBlank()
            ) {
                //In case we are ok with the phone fields, we can start field checking
                viewModel.checkFields()
            } else {
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_message_error_invalid_phone))
                binding.supportPhone2.requestFocus()
            }
        } else {
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_message_error_invalid_phone))
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
            if (viewModel.logoBitmap?.byteCount!! < 0) { //In case image larger than 10-MegaByte
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_message_error_photo_large))
                initLogoSelection()
            } else // Sets the logo field to the name of the selected photo
                binding.logoField.setImageBitmap(viewModel.logoBitmap)
        }
    }
}