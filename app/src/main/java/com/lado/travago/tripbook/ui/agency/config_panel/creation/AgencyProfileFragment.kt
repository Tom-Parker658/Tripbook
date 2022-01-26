package com.lado.travago.tripbook.ui.agency.config_panel.creation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyProfileBinding
import com.lado.travago.tripbook.model.enums.PlaceHolder
import com.lado.travago.tripbook.ui.agency.config_panel.creation.AgencyCreationViewModel.FieldTags
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.notification.ImageViewerFragmentArgs
import com.lado.travago.tripbook.utils.UIUtils
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import com.lado.travago.tripbook.utils.imageFromUri
import com.lado.travago.tripbook.utils.imageFromUrl
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyProfileFragment : Fragment() {
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentAgencyProfileBinding
    private lateinit var viewModel: AgencyCreationViewModel
    private lateinit var uiUtils: UIUtils

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val hasChanged = haveFieldsBeenModified()
                    if (hasChanged) {
                        Log.d("HAS CHANGED", hasChanged.toString())
                        uiUtils.warningDialog(
                            getString(R.string.text_unsaved_changes),
                            "All your changes will be discarded if you don't save. What should we do?",
                            getString(R.string.save_changes),
                            null,
                            getString(R.string.text_do_not_save),
                            onPositiveListener = { dialog, _ ->
                                dialog.cancel()
                                verifyFields()
                            },
                            onNegativeListener = null,
                            onNeutralListener = { dialog, _ ->
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                        )
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_profile,
            container,
            false
        )
        initViewModels()
        uiUtils = UIUtils(this, requireActivity(), viewLifecycleOwner)
        //Restore data to the textFields after any configuration change
        observeLiveData()

        restoreSavedData()
        onFieldChange()
        clickListeners()

        return binding.root
    }

    override fun onStart() {
        setFragmentResultListener("PHOTO") { requestKey: String, bundle: Bundle ->
            if (requestKey == "PHOTO" && !bundle.isEmpty) {
                (bundle["PHOTO_URI"] as Uri?)?.let {
                    viewModel.setField(FieldTags.LOGO_URI, it)
                    binding.logoField.imageFromUri(viewModel.logoUri!!, PlaceHolder.AGENCY)
                }
            }
        }
        super.onStart()
    }

    private fun datePicker() {
        val maxDate = Date().time // The current date(today) in millis
        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setEnd(maxDate)
            .build()

        uiUtils.datePicker(
            childFragmentManager,
            getString(R.string.text_creation_date),
            viewModel.creationDateInMillis,
            bounds,
            INPUT_MODE_CALENDAR,
            positiveListener = {
                if (Date().date < Date(it).date) datePicker()
                else {
                    viewModel.setField(FieldTags.CREATION_DATE_IN_MILLIS, it)
                    binding.creationDate.editText!!.setText(Utils.formatDate(viewModel.creationDateInMillis,
                        "EEEE dd MMMM YYYY"))
                }
            }
        )
    }

    private fun observeLiveData() {
        parentViewModel.agencyDoc.observe(viewLifecycleOwner) {
            if (it?.exists() == true && viewModel.firstRun) {
                viewModel.fillExistingData(it)
                restoreSavedData()
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
//        We check to see if to save a new logo or not TODO
        viewModel.startSaving.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.logoUri == null) {
                    viewModel.setField(FieldTags.ON_LOGO_SAVED, true)
                    viewModel.setField(FieldTags.ON_LOGO_SAVED, false)
                } else {
                    //We save agency to database iff the logoUri is not empty
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.saveLogo()
                    }
                }
                viewModel.setField(FieldTags.START_SAVING, false)
            }
        }
        //We check to see to create or modify agency
        viewModel.onLogoSaved.observe(viewLifecycleOwner)
        {
            if (it) {
                val fieldsHaveBeenModified = haveFieldsBeenModified()
                CoroutineScope(Dispatchers.Main).launch {
                    if (parentViewModel.bookerIsNotScanner.value != true) {
                        if (fieldsHaveBeenModified) {/*We update*/
                            viewModel.updateAgencyInfo(
                                parentViewModel.bookerDoc.value!!
                            )
                        } else viewModel.setField(FieldTags.ON_INFO_SAVED, true)
                        /*We create*/

                    } else viewModel.createAgency(parentViewModel.bookerDoc.value!!)
                }
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner)
        {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        //In this case we go back to the launcher activity which is actually the config activity
        viewModel.onInfoSaved.observe(viewLifecycleOwner)
        {
            if (it) {
                findNavController().navigateUp()
            }
        }

    }

    /**
     * Saves the content of the fields to the viewModel when any field is changes
     */
    private fun onFieldChange() {
        //Tries to load the logo selected from local storage if any
        binding.name.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NAME, it.toString())
        }
        binding.decreeNumber.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.DECREE_NUMBER, it.toString())
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

        //Setup the phone formatter
        binding.countryCodePicker1.registerCarrierNumberEditText(binding.supportPhone1.editText)
        binding.countryCodePicker2.registerCarrierNumberEditText(binding.supportPhone2.editText)
    }

    private fun clickListeners() {
        //Launches logo selection when logo image is tapped
        binding.logoField.setOnClickListener {
            val args = ImageViewerFragmentArgs.Builder(
                viewModel.logoUrl,
                null,
                viewModel.logoUri,
                true,
                false,
                false,
                getString(R.string.desc_agency_logo),
                PlaceHolder.AGENCY
            ).build().toBundle()
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.expand_out)
                .setExitAnim(R.anim.shrink_in)
//                .setPopEnterAnim(R.anim.expand_out)
//                .setPopExitAnim(R.anim.shrink_in)
                .build()
            findNavController().navigate(R.id.imageViewerFragment, args, navOptions)
        }

        binding.btnNext.setOnClickListener {
            verifyFields()
        }

        binding.creationDate.setOnClickListener {
            datePicker()
        }

        binding.creationDate.setEndIconOnClickListener { datePicker() }
    }

    /**
     * Restore all saved data to their respective views
     */
    private fun restoreSavedData() {
        when {
            //Load the logo
            viewModel.logoUri == null && viewModel.logoUrl.isNotBlank() -> {
                binding.logoField.imageFromUrl(viewModel.logoUrl,
                    PlaceHolder.AGENCY,
                    binding.progressBarLogo
                )
            }
            viewModel.logoUri != null && viewModel.logoUrl.isBlank() -> {
                binding.logoField.imageFromUri(viewModel.logoUri!!,
                    PlaceHolder.AGENCY
                )
            }
            else -> {//In this case, we are during the agency creation

            }

        }
        //Set the button text
        when (parentViewModel.bookerIsNotScanner.value) {
            true -> {
                binding.btnNext.setText(R.string.create)
                (binding.btnNext as MaterialButton).setIconResource(R.drawable.outline_add_agency_24)
            }
            false -> {
                binding.btnNext.setText(R.string.save_changes)
                (binding.btnNext as MaterialButton).setIconResource(R.drawable.baseline_save_24)
            }
            else -> {
                //This can't happen actually
            }
        }

        binding.name.editText!!.setText(viewModel.nameField)
        binding.creationDate.editText!!.setText(Utils.formatDate(viewModel.creationDateInMillis,
            "EEEE dd MMMM YYYY"))
        binding.decreeNumber.editText!!.setText(viewModel.decreeNumberField)
        binding.supportPhone1.editText!!.setText(viewModel.supportPhone1Field.removeSpaces())
        binding.supportPhone2.editText!!.setText(viewModel.supportPhone2Field.removeSpaces())
        binding.countryCodePicker1.setCountryForPhoneCode(viewModel.phoneCode1)
        binding.countryCodePicker2.setCountryForPhoneCode(viewModel.phoneCode2)

        binding.supportEmail.editText!!.setText(viewModel.supportEmailField)
        binding.motto.editText!!.setText(viewModel.mottoField)
    }

    private fun initViewModels() {
        parentViewModel =
            ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        viewModel = ViewModelProvider(this)[AgencyCreationViewModel::class.java]
    }

    private fun verifyFields() {
        when {
            viewModel.nameField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.name.requestFocus()
            }
            viewModel.logoUri == null && viewModel.logoUrl.isBlank() -> {
                binding.textLogo.requestFocus()
            }
            viewModel.decreeNumberField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.decreeNumber.requestFocus()
            }
            viewModel.mottoField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.motto.requestFocus()
            }
            viewModel.supportEmailField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.supportEmail.requestFocus()
            }
            !Utils.isBasicallyValidEmailAddress(viewModel.supportEmailField) -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_invalid_email))
                binding.supportEmail.requestFocus()
            }
            !binding.countryCodePicker1.isValidFullNumber -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_invalid_phone))
                binding.supportPhone1.requestFocus()
            }
            !binding.countryCodePicker1.isValidFullNumber && viewModel.supportPhone2Field.isNotBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    "${getString(R.string.text_invalid_phone)}\n${getString(R.string.text_field_is_optional)}")
                binding.supportPhone2.requestFocus()
            }

            //TODO: Check if booker is connected before saving
            else -> {
                viewModel.setField(FieldTags.START_SAVING, true)
                viewModel.setField(FieldTags.START_SAVING, false)
            }
        }
    }

    /**
     * We check if any change have been made i.e if we have edit the field
     */
    private fun haveFieldsBeenModified(): Boolean {
        /*Log.d("HAS_CHANGED_DECREE",
            "${viewModel.decreeNumberField == agencyDoc.getString("creationDecree")!!}")
        Log.d("HAS_CHANGED_DATE",
            "${viewModel.creationDateInMillis == agencyDoc.getLong("creationDateInMillis")!!}")
        Log.d("HAS_CHANGED_MOTTO", "${viewModel.mottoField == agencyDoc.getString("motto")!!}")
        Log.d("HAS_CHANGED_EMAIL",
            "${viewModel.supportEmailField == agencyDoc.getString("supportEmail")!!}")
        Log.d("HAS_CHANGED_URI", "${viewModel.logoUri == null}")
        Log.d("HAS_CHANGED_PHONE1",
            "${viewModel.phoneCode1 == agencyDoc.getLong("phoneCode1")!!.toInt()}")
        Log.d("HAS_CHANGED_PHONE2",
            "${viewModel.phoneCode2 == agencyDoc.getLong("phoneCode2")!!.toInt()}")
        Log.d("HAS_CHANGED_SUP_PHONE1",
            "${viewModel.supportPhone1Field} == ${agencyDoc.getString("supportPhone1")!!}")
        Log.d("HAS_CHANGED_SUP_PHONE2",
            "${viewModel.supportPhone2Field} == ${agencyDoc.getString("supportPhone2")!!}")
*/

        val agencyDoc = parentViewModel.agencyDoc.value
        return if (agencyDoc?.exists() == true) {
            !(
                    //If true, then some fields have changed
                    viewModel.nameField == agencyDoc.getString("agencyName")!! &&
                            viewModel.decreeNumberField == agencyDoc.getString("creationDecree")!! &&
                            viewModel.creationDateInMillis == agencyDoc.getLong("creationDateInMillis")!! &&
                            viewModel.mottoField == agencyDoc.getString("motto")!! &&
                            viewModel.supportEmailField == agencyDoc.getString("supportEmail")!! &&
                            viewModel.logoUri == null &&
                            viewModel.phoneCode1 == agencyDoc.getLong("phoneCode1")!!.toInt() &&
                            viewModel.phoneCode2 == agencyDoc.getLong("phoneCode2")!!.toInt() &&
                            viewModel.supportPhone1Field == agencyDoc.getString("supportPhone1")!! &&
                            viewModel.supportPhone2Field == agencyDoc.getString("supportPhone2")!!
                    )
        } else true // This is true when we are creating the agency hence it is normal that we want to save changes in this case
    }


}