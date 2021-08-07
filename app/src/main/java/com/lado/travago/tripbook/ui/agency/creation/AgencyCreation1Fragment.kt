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
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyRegistration1Binding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreation1Fragment : Fragment() {
    private var logoFileName = ""
    private lateinit var binding: FragmentAgencyRegistration1Binding
    private lateinit var viewModel: AgencyCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_registration1,
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
        binding.name.editText!!.addTextChangedListener {
            viewModel.saveField(AgencyCreationViewModel.FieldTags.NAME, binding.name.editText!!.text.toString())
        }
        binding.logo.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.LOGO_NAME, binding.logo.editText!!.text.toString())
        }
        binding.phone.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.PHONE, binding.phone.editText!!.text.toString())
        }
        binding.momo.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.MOMO, binding.momo.editText!!.text.toString())
        }
        binding.motto.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.MOTTO, binding.motto.editText!!.text.toString())
        }
        binding.orangeMoney.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.ORANGE, binding.orangeMoney.editText!!.text.toString())
        }
        binding.bank.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.BANK, binding.bank.editText!!.text.toString())
        }
        binding.email.editText!!.addTextChangedListener{
            viewModel.saveField(AgencyCreationViewModel.FieldTags.EMAIL, binding.email.editText!!.text.toString())
        }
        //Launches logo selection when the edit text is tapped
        binding.logo.editText!!.setOnClickListener {
            initLogoSelection()
        }
        //Launches logo selection when the end icon of textField is tapped
        binding.logo.setEndIconOnClickListener {
            initLogoSelection()
        }

    }

    /**
     * Restore all saved data to their respective views
     */
    private fun restoreSavedData() {
        binding.name.editText!!.setText(viewModel.nameField)
        binding.logo.editText!!.setText(viewModel.logoFilename)
        binding.momo.editText!!.setText(viewModel.momoField)
        binding.motto.editText!!.setText(viewModel.mottoField)
        binding.email.editText!!.setText(viewModel.supportEmailField)
        binding.phone.editText!!.setText(viewModel.supportPhoneField)
        binding.orangeMoney.editText!!.setText(viewModel.orangeMoneyField)
        binding.bank.editText!!.setText(viewModel.bankField)
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[AgencyCreationViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * Launches the [pickAgencyLogo] event with the parameter image/
     */
    private fun initLogoSelection() = pickAgencyLogo.launch("image/*")


    /**
     * Navigates to the next screen when the next button is clicked
     */
    private fun onNextClicked(){
        binding.btnNext.setOnClickListener {
            val anyError: String? = when {
                viewModel.logoBitmap == null -> {
                    "You must select logo picture"
                }
                binding.name.editText!!.text.isBlank() -> {
                    binding.name.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.momo.editText!!.text.isBlank() -> {
                    binding.momo.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.orangeMoney.editText!!.text.isBlank() -> {
                    binding.orangeMoney.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.phone.editText!!.text.isBlank() -> {
                    binding.phone.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.email.editText!!.text.isBlank() -> {
                    binding.email.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.logo.editText!!.text.isBlank() -> {
                    binding.logo.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.bank.editText!!.text.isBlank() -> {
                    binding.bank.editText!!.requestFocus()
                    requiredFieldMessage
                }
                binding.motto.editText!!.text.isBlank() -> {
                    binding.motto.editText!!.requestFocus()
                    requiredFieldMessage
                }
                else -> null
            }
            when (anyError) {
                //Navigate to the next screen
                null -> it.findNavController().navigate(com.lado.travago.tripbook.ui.agency.AgencyRegistration1FragmentDirections.actionAgencyRegistration1FragmentToAgencyRegistration2Fragment())
                else -> showSnackbar(anyError)
            }
        }
    }


    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [AgencyCreationViewModel.logoBitmap]
     * Then we set the [FragmentScannerRegistrationBinding.profilePhoto] value to the name of selected image if the image is less than 4000*4000
     * else we re-launch the event
     */
    private val pickAgencyLogo: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { logoUri ->
        logoUri?.let {uri ->
            val logoStream = requireActivity().contentResolver.openInputStream(uri)!!
            viewModel.saveField(
                AgencyCreationViewModel.FieldTags.LOGO_BITMAP,
                BitmapFactory.decodeStream(logoStream)
            )

            if (viewModel.logoBitmap!!.width >= 4000 && viewModel.logoBitmap!!.height >= 4000) { //In case image too large
                showToast("The image is too large!")
                initLogoSelection()
            }

            else // Sets the logo field to the name of the selected photo
                binding.logo.editText!!.setText(uri.lastPathSegment)
        }
    }

    /**
     * Helper method to display toasts
     */
    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    /**
     * Helper method to display snackbars
     */
    private fun showSnackbar(message: String) =
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()

    companion object{
        const val requiredFieldMessage = "This field is required!"
    }
}