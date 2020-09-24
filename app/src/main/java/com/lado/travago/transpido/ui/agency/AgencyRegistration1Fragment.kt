package com.lado.travago.transpido.ui.agency

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentAgencyRegistration1Binding
import com.lado.travago.transpido.viewmodel.admin.AgencyRegistrationViewModel
import com.lado.travago.transpido.viewmodel.admin.AgencyRegistrationViewModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.FileNotFoundException


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyRegistration1Fragment(): Fragment() {
    private var logoFileName = ""
    private lateinit var binding: FragmentAgencyRegistration1Binding
    private lateinit var viewModel: AgencyRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
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
        binding.name.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.NAME, binding.name.editText!!.text.toString())
        }
        binding.logo.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.LOGO_NAME, binding.logo.editText!!.text.toString())
        }
        binding.phone.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.PHONE, binding.phone.editText!!.text.toString())
        }
        binding.momo.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.MOMO, binding.momo.editText!!.text.toString())
        }
        binding.motto.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.MOTTO, binding.motto.editText!!.text.toString())
        }
        binding.orangeMoney.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.ORANGE, binding.orangeMoney.editText!!.text.toString())
        }
        binding.bank.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.BANK, binding.bank.editText!!.text.toString())
        }
        binding.email.editText!!.addTextChangedListener{
            viewModel.saveField(FieldTags.EMAIL, binding.email.editText!!.text.toString())
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
        viewModel = ViewModelProvider(requireActivity())[AgencyRegistrationViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * Launches the intent to select the log from the gallery
     */
    private fun initLogoSelection(){
        val photoIntentPicker = Intent(Intent.ACTION_PICK)
            .setType("image/*")

        startActivityForResult(photoIntentPicker, AgencyRegistrationActivity.RC_LOAD_LOGO)
    }


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
                null -> it.findNavController().navigate(AgencyRegistration1FragmentDirections.actionAgencyRegistration1FragmentToAgencyRegistration2Fragment())
                else -> showSnackbar(anyError)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        // Gets the selected logo picture
            if (requestCode == AgencyRegistrationActivity.RC_LOAD_LOGO) {
                try {
                    val logoUri = data?.data!!
                    //Adds the filename of the bitmap
                    logoFileName = logoUri.lastPathSegment!!
                    val logoStream = requireActivity().contentResolver.openInputStream(logoUri)!!
                    //convert image into stream
                    viewModel.saveField(FieldTags.LOGO_BITMAP, BitmapFactory.decodeStream(logoStream))

                    if (viewModel.logoBitmap!!.width >= 4000 && viewModel.logoBitmap!!.height >= 4000) {
                        showToast("Choose a smaller logo")
                        initLogoSelection()
                    } else {
                        binding.logo.editText!!.setText(logoFileName)// Set the logo field to the filename of the logo
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    showToast("Something went wrong when loading image. Try again",)
                }
            } else {
                showToast("You haven't picked any logo")
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