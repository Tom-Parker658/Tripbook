package com.lado.travago.tripbook.ui.booker.creation

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker

import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.*
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

import android.widget.AutoCompleteTextView

import android.widget.ArrayAdapter
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerCreationFinalBinding


/**
 * Booker info screen
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreationFinalFragment : Fragment() {
    private lateinit var binding: FragmentBookerCreationFinalBinding
    private lateinit var viewModel: BookerCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, com.lado.travago.tripbook.R.layout.fragment_booker_creation_final, null, false)
        viewModel = ViewModelProvider(requireActivity())[BookerCreationViewModel::class.java]
        binding.countryCodePicker.setCountryForPhoneCode(savedInstanceState?.getInt("COUNTRY_CODE") ?: 237)
        phoneWidgetConfig()
        occupationAutoComplete()
        onFieldChange()
        restoreFields()
        return binding.root
    }
    private fun occupationAutoComplete(){
    //Sets adapter for the autocomplete text view
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            viewModel.occupationList
        )
        val textView = binding.occupation.editText as AutoCompleteTextView
        textView.setAdapter(adapter)
    }

    /**
     * Saves country Code
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("COUNTRY_CODE", binding.countryCodePicker.selectedCountryCodeAsInt)
        super.onSaveInstanceState(outState)
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     * credential represent either email or phoneNumber
     */
    private fun onFieldChange() {
        binding.recoveryPhone.editText!!.addTextChangedListener{
            viewModel.setField(FieldTags.RECOVERY_PHONE, it.toString())
        }

        binding.name.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NAME, it.toString())
        }

        binding.occupation.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.OCCUPATION, it.toString())
        }

        binding.nationality.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.NATIONALITY, it.toString())
        }

        // An onClick listener to to initiate the picture selection when the profile photo imageView is clicked
        binding.profilePhoto.setOnClickListener {
            initPictureSelection()
        }

        binding.textProfilePhoto.setOnClickListener {
            initPictureSelection()
        }

        // Add an onClick listener to the birthday endIcon to select the birthday
        binding.fabPickDate.setOnClickListener { selectBirthDay() }
        binding.birthday.setOnClickListener {  selectBirthDay() }

        binding.radioGroupSex.setOnCheckedChangeListener { _, id ->
            viewModel.setField(FieldTags.SEX_ID, id)
            val sex = when (id) {
                R.id.sex_male -> SEX.MALE
                R.id.sex_female -> SEX.FEMALE
                else -> SEX.UNKNOWN
            }
            viewModel.setField(FieldTags.SEX, sex)
        }
        binding.btnSaveInfo.setOnClickListener {
            if(binding.countryCodePicker.isValidFullNumber)  viewModel.setField(FieldTags.FULL_PHONE, binding.countryCodePicker.fullNumberWithPlus)
            else viewModel.setField(FieldTags.FULL_PHONE, "")
            viewModel.checkFields(this)
        }
    }

    private fun selectBirthDay() {
        val titleText = "Select your birthday"
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.set(1900, 1, 1)//Date in 1900s
        val date1900s = calendar.timeInMillis

        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(date1900s)//Smallest date which can be selected
            .setEnd(today)//Furthest
            .build()
        //We create our date picker which the user will use to enter his travel day
        //Showing the created date picker onScreen
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(bounds)//Constrain the possible dates
            .setTitleText(titleText)//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        //Sets the value of the edit text to the formatted value of the selection
        datePicker.addOnPositiveButtonClickListener {
            viewModel.setField(FieldTags.BIRTHDAY, it)
            binding.birthday.editText!!.setText(viewModel.formatDate(viewModel.birthdayField))
        }
        datePicker.showNow(requireActivity().supportFragmentManager, "")
    }


    /**
     * Configure the phone + country_code mechanics
     */
    private fun phoneWidgetConfig() = binding.countryCodePicker.registerCarrierNumberEditText(binding.recoveryPhone.editText)



    private fun initPictureSelection() = pickScannerPhoto.launch("image/*")

    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [BookerCreationViewModel.photoField]
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
                if (viewModel.photoField!!.width >= 4000 && viewModel.photoField!!.height >= 4000) {//In case image too large
                    Toast.makeText(requireContext(), "The image is too large!", Toast.LENGTH_LONG).show()
                    initPictureSelection()
                } else // Sets the profile to the selected image
                    binding.profilePhoto.setImageBitmap(viewModel.photoField!!)
            }
        }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.occupation.editText!!.setText(viewModel.occupationField)
        viewModel.photoField?.let { binding.profilePhoto.setImageBitmap(it) }
        binding.name.editText!!.setText(viewModel.nameField)
        binding.recoveryPhone.editText!!.setText(viewModel.recoveryPhoneField)
        binding.birthday.editText!!.setText(
            Utils.formatDate(
                viewModel.birthdayField,
                "MMMM, dd yyyy"
            )
        )
        binding. nationality.editText!!.setText(viewModel.nationalityField)
        binding.radioGroupSex.check(viewModel.sexFieldId)
    }



}