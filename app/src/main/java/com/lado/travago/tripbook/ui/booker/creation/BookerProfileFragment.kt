package com.lado.travago.tripbook.ui.booker.creation

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker

import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.utils.Utils
import java.util.*

import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerProfileBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerProfileViewModel
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerProfileViewModel.*
import com.lado.travago.tripbook.utils.AdminUtils
import com.lado.travago.tripbook.utils.Utils.removeSpaces
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.*


/**
 * Booker info screen
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerProfileFragment : Fragment() {
    private lateinit var binding: FragmentBookerProfileBinding
    private lateinit var viewModel: BookerProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BookerProfileViewModel::class.java]
        val arguments = BookerProfileFragmentArgs.fromBundle(requireArguments())

        //We want to call it only once
        if (viewModel.existingProfileDoc.value == null && !arguments.isNewBooker) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getExistingProfile()
            }
        }

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_profile,
            null,
            false
        )
        getArgsAndRespond()
        observeLiveData()
        phoneWidgetConfig()
        //Restore field should come before field change
        restoreFields()
        onFieldChange()
        getSharedData()
        return binding.root
    }

    fun getArgsAndRespond() {
        getSharedData()
        val args = BookerProfileFragmentArgs.fromBundle(requireArguments())
        viewModel.setField(FieldTags.ARG_CALLER, args.caller)
        viewModel.setField(FieldTags.ARG_IS_NEW_BOOKER, args.isNewBooker)
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
            viewModel.setField(FieldTags.PROFILE_INFO_CHANGED, true)
        }

        binding.name.editText!!.doAfterTextChanged {
            viewModel.setField(FieldTags.NAME, it.toString())
            viewModel.setField(FieldTags.PROFILE_INFO_CHANGED, true)
        }

        binding.nationality.editText!!.doAfterTextChanged {
            viewModel.setField(FieldTags.NATIONALITY, it.toString())
            viewModel.setField(FieldTags.PROFILE_INFO_CHANGED, true)
        }

        // An onClick listener to to initiate the picture selection when the profile photo imageView is clicked
        binding.profilePhoto.setOnClickListener {
            initPictureSelection()
        }

        binding.textProfilePhoto.setOnClickListener {
            initPictureSelection()
        }

        // Add an onClick listener to the birthday endIcon to select the birthday
        binding.birthday.setEndIconOnClickListener {
            datePicker()
        }
        binding.birthday.setOnClickListener {
            datePicker()
        }

        binding.chipGroupSex.setOnCheckedChangeListener { _, id ->
            val sex = when (id) {
                R.id.sex_male -> SEX.MALE
                R.id.sex_female -> SEX.FEMALE
                else -> SEX.UNKNOWN
            }
            viewModel.setField(FieldTags.SEX, sex)
            viewModel.setField(FieldTags.PROFILE_INFO_CHANGED, true)
        }

        binding.btnSaveInfo.setOnClickListener {
            checkFields()
        }
    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        viewModel.photoField?.let {
            binding.profilePhoto.setImageBitmap(it)
        }
        binding.recoveryPhone.editText!!.setText(viewModel.recoveryPhoneField)
        binding.name.editText!!.setText(viewModel.nameField)
        binding.birthday.editText!!.setText(
            Utils.formatDate(
                viewModel.birthdayField,
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

    private fun checkFields() {
        if (binding.name.editText!!.text.isBlank()) binding.name.requestFocus()
        else if (viewModel.photoUrl == "" && viewModel.photoField == null) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                getString(R.string.warn_select_photo)
            )
        } else if (binding.nationality.editText!!.text.isBlank()) {
            binding.nationality.requestFocus()
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.warn_is_required))
        } else if (!binding.sexMale.isChecked && !binding.sexFemale.isChecked) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                getString(R.string.warn_gender_required)
            )
            binding.chipGroupSex.requestFocus()
        } else if (!binding.countryCodePicker.isValidFullNumber) {
            viewModel.setField(FieldTags.TOAST_MESSAGE, getString(R.string.warn_invalid_phone))
            binding.recoveryPhone.requestFocus()
        } else if (binding.recoveryPhone.editText!!.text.toString() == viewModel.authRepo.currentUser!!.phoneNumber) {
            viewModel.setField(
                FieldTags.TOAST_MESSAGE,
                getString(R.string.warn_recovery_phone_differnce_with_phone)
            )
        } else {
            when {
                viewModel.profilePhotoChanged.value == true -> {
                    viewModel.setField(FieldTags.ON_LOADING, true)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.uploadProfilePhoto()
                    }
                }
                viewModel.profileInfoChanged.value == true -> CoroutineScope(Dispatchers.Main).launch {
                    viewModel.saveBookerInfo()
                }
                else -> viewModel.setField(FieldTags.ON_INFO_SAVED, true)
            }

        }
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

        //We create our date picker which the user will use to enter his travel day
        //Showing the created date picker onScreen
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(bounds)//Constrain the possible dates
            .setTitleText(R.string.text_date)//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(viewModel.birthdayField)
            .build()

        //Sets the value of the edit text to the formatted value of the selection
        datePicker.addOnPositiveButtonClickListener {
            if (Date().date < Date(it).date) datePicker()
            else {
                viewModel.setField(FieldTags.BIRTHDAY, it)
                binding.birthday.editText!!.setText(
                    Utils.formatDate(
                        viewModel.birthdayField,
                        getString(R.string.text_date_pattern_in_words)
                    )
                )
                viewModel.setField(FieldTags.PROFILE_INFO_CHANGED, true)
            }
        }

        datePicker.showNow(childFragmentManager, "")
    }

    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() =
        binding.countryCodePicker.registerCarrierNumberEditText(binding.recoveryPhone.editText)

    private fun initPictureSelection() = pickScannerPhoto.launch("image/*")

    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [BookerSignInViewModel.photoField]
     * else we re-launch the selection
     */
    private val pickScannerPhoto: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
            photoUri?.let { uri ->
                val photoStream = requireActivity().contentResolver.openInputStream(uri)!!
                viewModel.setField(
                    FieldTags.PROFILE_PHOTO,
                    BitmapFactory.decodeStream(photoStream)
                )
                binding.profilePhoto.setImageBitmap(viewModel.photoField!!)
                viewModel.setField(FieldTags.PROFILE_PHOTO_CHANGED, true)
            }
        }


    /**
     * We 'try' get the phoneNumber of the booker from the devices SharedPreferences
     */
    private fun getSharedData() {
        val preferences =
            requireActivity().getSharedPreferences("Booker_Phone_Info", Context.MODE_PRIVATE)

        val phone = preferences.getString("bookerPhoneNumber", "").toString()
        val phoneCode = preferences.getInt("bookerCountryCode", -1)

        viewModel.setField(FieldTags.PREFERENCE_PHONE, phone)
        viewModel.setField(FieldTags.PREFERENCE_CODE, phoneCode)

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
            restoreFields()
            binding.profilePhoto.loadImageFromUrl(viewModel.photoUrl)
        }

        viewModel.onFailed.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    getString(R.string.warn_internet_required)
                )

                viewModel.setField(FieldTags.ON_INFO_SAVED, true)
            }
        }

        viewModel.onInfoSaved.observe(viewLifecycleOwner) {
            if (it) {
                //To creation center
                when(viewModel.caller){
                    SignUpCaller.PHONE_CHANGE -> {

                    }
                    SignUpCaller.USER -> {
                        findNavController().navigateUp()
                    }
                    SignUpCaller.OTHER_ACTIVITY -> {
                        if(!viewModel.onFailed.value!!)
                            requireActivity().finishActivity(Activity.RESULT_OK)
                        else
                            requireActivity().finishActivity(Activity.RESULT_CANCELED)
                    }
                }
            }
        }
    }

}