package com.lado.travago.tripbook.ui.booker.creation

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker

import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.utils.Utils
import java.util.*

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerProfileBinding
import com.lado.travago.tripbook.model.enums.PlaceHolder
import com.lado.travago.tripbook.model.enums.SEX.Companion.toSEX
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerProfileViewModel
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerProfileViewModel.*
import com.lado.travago.tripbook.ui.notification.ImageViewerFragmentArgs
import com.lado.travago.tripbook.utils.UIUtils
import com.lado.travago.tripbook.utils.imageFromUri
import com.lado.travago.tripbook.utils.imageFromUrl
import kotlinx.coroutines.*


/**
 * Booker info screen
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerProfileFragment : Fragment() {
    private lateinit var binding: FragmentBookerProfileBinding
    private lateinit var viewModel: BookerProfileViewModel
    private lateinit var uiUtils: UIUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val hasChanged = haveFieldsBeenModified()
                    if (hasChanged) {
                        Log.d("HAS CHANGED", hasChanged.toString())
                        uiUtils.dialog(
                            getString(R.string.text_unsaved_changes),
                            R.drawable.outline_info_24,
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[BookerProfileViewModel::class.java]
        uiUtils = UIUtils(this, requireActivity(), viewLifecycleOwner)

        //We want to call it only once and only when the booker has an existing profile
        if (uiUtils.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true) {
            if (viewModel.existingProfileDoc.value == null)
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getExistingProfile()
                }
        } else viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.create_profile))

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_profile,
            null,
            false
        )

        observeLiveData()
        getArgsAndRespond()
        restoreFields()
        onFieldChange()
        clickListeners()
        phoneWidgetConfig()
        getCachedCredentials()

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        //Restore field should come before field change
        setFragmentResultListener("PHOTO") { requestKey: String, bundle: Bundle ->
            if (requestKey == "PHOTO" && !bundle.isEmpty) {
                (bundle["PHOTO_URI"] as Uri?)?.let {
                    viewModel.setField(FieldTags.PHOTO_URI, it)
                    binding.profilePhoto.imageFromUri(viewModel.photoUri!!, PlaceHolder.PERSON)
                }
            }
        }

    }

    private fun getArgsAndRespond() {
        getCachedCredentials()
        val args = BookerProfileFragmentArgs.fromBundle(requireArguments())
        viewModel.setField(FieldTags.ARG_CALLER, args.caller)
    }

    private fun clickListeners() {
        binding.profilePhoto.setOnClickListener {
            val args = ImageViewerFragmentArgs(
                viewModel.photoUrl,
                null,
                viewModel.photoUri,
                true,
                true,
                false,
                getString(R.string.desc_agency_logo),
                PlaceHolder.PERSON
            ).toBundle()
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.expand_out)
                .setExitAnim(R.anim.shrink_in)
//                .setPopEnterAnim(R.anim.expand_out)
//                .setPopExitAnim(R.anim.shrink_in)
                .build()
            findNavController().navigate(R.id.imageViewerFragment, args, navOptions)
        }
        binding.btnSaveInfo.setOnClickListener {
            verifyFields()
        }
        binding.birthday.setOnClickListener {
            datePicker()
        }
        binding.birthday.setEndIconOnClickListener {
            datePicker()
        }
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     * credential represent either email or phoneNumber
     */
    private fun onFieldChange() {
        viewModel.setField(FieldTags.RES, resources)
        binding.recoveryPhone.editText!!.doAfterTextChanged {
            viewModel.setField(FieldTags.RECOVERY_PHONE, it.toString())
            viewModel.setField(
                FieldTags.RECOVERY_COUNTRY_CODE,
                binding.countryCodePicker.selectedCountryCodeAsInt
            )
        }

        binding.name.editText!!.doAfterTextChanged {
            viewModel.setField(FieldTags.NAME, it.toString())
        }

        binding.nationality.editText!!.doAfterTextChanged {
            viewModel.setField(FieldTags.NATIONALITY, it.toString())
        }

        binding.chipGroupSex.setOnCheckedChangeListener { _, id ->
            val sex = when (id) {
                R.id.sex_male -> SEX.MALE
                R.id.sex_female -> SEX.FEMALE
                else -> SEX.UNKNOWN
            }
            viewModel.setField(FieldTags.SEX, sex)
        }
    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        when {
            //Load the Photo
            viewModel.photoUri == null && viewModel.photoUrl.isNotBlank() -> {
                binding.profilePhoto.imageFromUrl(viewModel.photoUrl,
                    PlaceHolder.PERSON,
                    binding.progressBarPhoto
                )
            }
            viewModel.photoUri != null && viewModel.photoUrl.isBlank() -> {
                binding.profilePhoto.imageFromUri(viewModel.photoUri!!,
                    PlaceHolder.PERSON
                )
            }
            else -> {//In this case, we are during the agency creation

            }

        }

        binding.recoveryPhone.editText!!.setText(viewModel.recoveryPhoneField)
        binding.name.editText!!.setText(viewModel.nameField)
        binding.birthday.editText!!.setText(
            Utils.formatDate(
                viewModel.birthdayInMillis,
                getString(R.string.text_date_pattern_in_words)
            )
        )
        binding.nationality.editText!!.setText(viewModel.nationalityField)

        binding.chipGroupSex.check(
            when (viewModel.sex) {
                SEX.FEMALE -> binding.sexFemale.id
                SEX.MALE -> binding.sexMale.id
                SEX.UNKNOWN -> 1800//Dummy
            }
        )
        if (viewModel.recoveryPhoneCountryCode != 0)
            binding.countryCodePicker.setCountryForPhoneCode(viewModel.recoveryPhoneCountryCode)
        else binding.countryCodePicker.defaultCountryCode
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.set(1920, 1, 1)//Date in 1900s
        val date1920s = calendar.timeInMillis

        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(date1920s)//Smallest date which can be selected
            .setEnd(today)//Furthest
            .build()

        uiUtils.datePicker(
            childFragmentManager,
            getString(R.string.text_birth_on),
            viewModel.birthdayInMillis,
            bounds,
            MaterialDatePicker.INPUT_MODE_CALENDAR,
            positiveListener = {
                if (Date().date < Date(it).date) datePicker()
                else {
                    viewModel.setField(FieldTags.BIRTHDAY, it)
                    binding.birthday.editText!!.setText(
                        Utils.formatDate(
                            viewModel.birthdayInMillis,
                            getString(R.string.text_date_pattern_in_words)
                        )
                    )
                }
            }
        )

    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() =
        binding.countryCodePicker.registerCarrierNumberEditText(binding.recoveryPhone.editText)


    /**
     * We 'try' get the phoneNumber of the booker from the devices SharedPreferences
     */
    private fun getCachedCredentials() {
        val phone = (uiUtils.getSharedPreference(UIUtils.SP_STRING_BOOKER_PHONE) as String?) ?: ""
        val phoneCode =
            (uiUtils.getSharedPreference(UIUtils.SP_INT_BOOKER_COUNTRY_CODE) as Int?) ?: -1

        viewModel.setField(FieldTags.CACHED_PHONE, phone)
        viewModel.setField(FieldTags.CACHED_COUNTRY_CODE, phoneCode)

    }

    private fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar2.visibility = View.VISIBLE
            else binding.progressBar2.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }

        viewModel.existingProfileDoc.observe(viewLifecycleOwner) {
            if (it?.exists() == true) {
                restoreFields()
            }
        }

        viewModel.onFailed.observe(viewLifecycleOwner) {
//            if (it) {
//                viewModel.setField(
//                    FieldTags.TOAST_MESSAGE,
//                    getString(R.string.warn_internet_required)
//                )
//
//                viewModel.setField(FieldTags.ON_INFO_SAVED, true)
//            }
        }

        viewModel.startSaving.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("PROFILE", "startSavingCalled")
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.uploadProfilePhoto()
                }
            }
        }
        viewModel.onPhotoSaved.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("PROFILE", "PhotoCalled")
                CoroutineScope(Dispatchers.Main).launch {
                    if (viewModel.existingProfileDoc.value?.exists() == true)
                        viewModel.updateBookerProfile()
                    else viewModel.createBookerProfile()
                }
            }
        }

        viewModel.onInfoSaved.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("PROFILE", "CreationFinishedCalled")
                uiUtils.editSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST, true)
                //To creation center
                when (viewModel.caller) {
                    SignUpCaller.PHONE_CHANGE -> {
                        //Can't really happen
                        findNavController().navigateUp()
                    }
                    SignUpCaller.USER -> {
                        findNavController().navigateUp()
                    }
                    SignUpCaller.OTHER_ACTIVITY -> {
                        if (!viewModel.onFailed.value!!) {
                            requireActivity().setResult(Activity.RESULT_OK)
                            requireActivity().finish()
                        } else {
                            requireActivity().finishActivity(Activity.RESULT_CANCELED)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun verifyFields() {
        when {
            viewModel.nameField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.name.requestFocus()
            }

            viewModel.nationalityField.isBlank() -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE,
                    getString(R.string.text_field_is_required))
                binding.nationality.requestFocus()
            }

            !binding.countryCodePicker.isValidFullNumber -> {
                viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.text_invalid_phone))
                binding.recoveryPhone.requestFocus()
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
        val scannerDoc = viewModel.existingProfileDoc.value
        return if (scannerDoc?.exists() == true) {
            !(
                    //If true, then some fields have changed
                    viewModel.nameField == scannerDoc.getString("name")!! &&
                            viewModel.birthdayInMillis == scannerDoc.getLong("birthdayInMillis")!! &&
                            viewModel.nationalityField == scannerDoc.getString("nationality")!! &&
                            viewModel.photoUri == null &&
                            viewModel.recoveryPhoneField == scannerDoc.getString("recoveryPhone")!! &&
                            viewModel.recoveryPhoneCountryCode == scannerDoc.getLong("recoveryPhoneCountryCode")!!
                        .toInt() &&
                            viewModel.sex == scannerDoc.getString("sex")!!.toSEX()
                    )
        } else true// This is true when we are creating the account hence it is normal tha we want to save changes in this case
    }
}