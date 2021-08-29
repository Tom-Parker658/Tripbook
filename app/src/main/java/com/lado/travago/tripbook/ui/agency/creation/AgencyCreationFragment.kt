package com.lado.travago.tripbook.ui.agency.creation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyCreationBinding
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationViewModel.*
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreationFragment : Fragment() {
    private lateinit var binding: FragmentAgencyCreationBinding
    private lateinit var viewModel: AgencyCreationViewModel

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

        initViewModel()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getExistingAgencyData()
            viewModel.fillExistingData()
        }

        //Restore data to the textFields after any configuration change
        try {
            restoreSavedData()
        } catch (e: Exception) {
            //TODO: NOTHING
        }
        onFieldChange()
        onNextClicked()
        observeLiveData()

        return binding.root
    }

    private fun observeLiveData() {
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
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.saveInfo.observe(viewLifecycleOwner) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.setField(FieldTags.SAVE_INFO, false)
                    viewModel.saveAgencyInfo()
                }
            }

        }

        //In this case we go back to the launcher activity which is actually th config activity
        viewModel.onInfoSaved.observe(viewLifecycleOwner) {
            if (it) {
                //TODO:
            }
        }

        viewModel.onVerificationFailed.observe(viewLifecycleOwner) {
            //We end activity if we can't verify we are editing or creating an agency
            if (it) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Verification failed!")
                    .setIcon(R.drawable.round_cancel_24)
                    .setMessage("We could not determine if you are creating or editing an agency!")
                    .setOnDismissListener {

                    }
                    .setView(binding.root)
                    .create().show()
            }
        }
    }

    /**
     * Saves the content of the fields to the viewModel when any field is changes
     */
    private fun onFieldChange() {
        //Tries to load the logo gotten from the database
        if (viewModel.logoUrl.isNotBlank()) binding.logoField.loadImageFromUrl(viewModel.logoUrl)
        binding.name.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NAME, it.toString())
        }
        binding.decreeNumber.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.DECREE_NUMBER, it.toString())
        }
        binding.nameOfCEO.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.CEO_NAME, it.toString())
        }
        binding.momo.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.MOMO_NUMBER, it.toString())
        }
        binding.motto.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.MOTTO, it.toString())
        }
        binding.orangeMoney.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.ORANGE_NUMBER, it.toString())
        }
        binding.bank.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.BANK_NUMBER, it.toString())
        }
        binding.supportEmail.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.SUPPORT_EMAIL, it.toString())
        }
        binding.supportPhone1.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.SUPPORT_PHONE_1, it.toString())
        }
        binding.supportPhone2.editText!!.addTextChangedListener {
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
        viewModel.logoBitmap?.let {
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
            viewModel.setField(
                FieldTags.FULL_SUPPORT_PHONE_1,
                binding.countryCodePicker1.fullNumberWithPlus
            )
            if (binding.countryCodePicker2.isValidFullNumber || binding.supportPhone2.editText!!.text.toString()
                    .isBlank()
            ) {
                try {
                    binding.countryCodePicker2.fullNumberWithPlus.let {
                        viewModel.setField(FieldTags.FULL_SUPPORT_PHONE_2, it)
                    }
                    //In case we are ok with the phone fields, we can start field checking
                    viewModel.checkFields(this@AgencyCreationFragment)
                } catch (e: Exception) {//In case phone is empty}
                }
            } else {
                viewModel.setField(FieldTags.TOAST_MESSAGE, "Enter valid number or leave it empty")
                binding.supportPhone2.requestFocus()
            }
        } else {
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
            if (viewModel.logoBitmap?.byteCount!! > (1024 * 1024 * 2)) { //In case image larger than 2-MegaByte
                viewModel.setField(FieldTags.TOAST_MESSAGE, "Photo is too large!!")
                initLogoSelection()
            } else // Sets the logo field to the name of the selected photo
                binding.logoField.setImageBitmap(viewModel.logoBitmap)
        }
    }

}