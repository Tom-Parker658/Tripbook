package com.lado.travago.tripbook.ui.scanner.creation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentScannerRegistrationBinding
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.ui.agency.AgencyRegistrationActivity
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.viewmodel.ScannerCreationViewModelFactory
import com.lado.travago.tripbook.viewmodel.admin.ScannerCreationViewModel
import com.lado.travago.tripbook.viewmodel.admin.ScannerCreationViewModel.FieldTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentScannerRegistrationBinding
    //activityViewModels() gets the view-model from the parent activity
    private lateinit var viewModel: ScannerCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_scanner_registration,
            container,
            false
        )
        initViewModel()
        onFieldChange()
        onBtnCreateClicked()
        //Restore all fields after configuration changes
        restoreFields()

        return binding.root
    }

    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */
    private fun initViewModel(){
        //Data gotten from the agency
        val intentData = getIntentData()
        val viewModelFactory = ScannerCreationViewModelFactory(
            agencyName = intentData.first,
            agencyFirestorePath = intentData.second
        )
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ScannerCreationViewModel::class.java]
    }

    /**
     * Gets the intent data which is passed from the Agency to launch the Scanner creation.
     * The intent contains the agency name and the database path to teh agency's document. Tis data wil
     * be used for the creation of the scanner.
     * We assume their values can not be null
     * @return A pair where first = agencyName and second = path
     */
    private fun getIntentData(): Pair<String, String>{
        val agencyFirestorePath = requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_OTA_PATH) !!
        val agencyName = requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME) !!

        Log.i("ScannerCreationActivity", "agencyName=$agencyName, path=$agencyFirestorePath")
        return agencyName to agencyFirestorePath
    }



    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.park.editText!!.setText(viewModel.agencyName)
        viewModel.photoField?.let{ binding.profilePhoto.setImageBitmap(it)}
        binding.name.editText!!.setText(viewModel.nameField)
        binding.phone.editText!!.setText(viewModel.phoneField)
        binding.birthday.editText!!.setText(
            Utils.formatDate(
                viewModel.birthdayField,
                "MMMM, dd yyyy"
            )
        )
        binding.birthplace.editText!!.setText(viewModel.birthplaceField)
        binding.radioGroupSex.check(viewModel.sexFieldId)
        binding.checkBoxAdmin.isChecked = viewModel.isAdminField
    }

    /**
     * Starts the code verification by calling [ScannerCreationViewModel.startPhoneVerification].
     * Then navigates to the [ScannerPhoneValidationFragment]
     */
    private fun onBtnCreateClicked() = binding.btnCreateScanner.setOnClickListener {
        it.findNavController().navigate(
            ScannerRegistrationFragmentDirections.actionScannerRegistrationFragment2ToPhoneValidationFragment()
        )
        viewModel.startPhoneVerification(requireActivity())
        viewModel.startLoading()
    }


    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.name.editText!!.addTextChangedListener{
            viewModel.setFields(FieldTags.NAME, binding.name.editText!!.text.toString())
        }
        binding.phone.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.PHONE, binding.phone.editText!!.text.toString())
        }
        binding.birthplace.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.BIRTH_PLACE, binding.birthplace.editText!!.text.toString())
        }
        binding.checkBoxAdmin.setOnCheckedChangeListener { _, isAdmin ->
            viewModel.setFields(FieldTags.IS_ADMIN, isAdmin)
        }
        // An onClick listener to to initiate the picture selection when the profile photo imageView is clicked
        binding.profilePhoto.setOnClickListener {
            initPictureSelection()
        }
        // Add an onClick listener to the birthday endIcon to select the birthday
        binding.fabPickDate.setOnClickListener { selectBirthDay() }

        binding.radioGroupSex.setOnCheckedChangeListener { _, id ->
            viewModel.setFields(FieldTags.SEX_ID, id)
            val sex = when (id) {
                R.id.sex_male -> SEX.MALE
                R.id.sex_female -> SEX.FEMALE
                else -> SEX.UNKNOWN
            }
            viewModel.setFields(FieldTags.SEX, sex)
        }
    }
    private fun initPictureSelection() =
        pickScannerPhoto.launch("image/*")


    /**
     * initiate the date picker to select the birthday
     */
    private fun selectBirthDay() {
        val titleText = "Select your birthday"
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.set(1900, 1, 1)//Date in 1900s
        val date1900s = calendar.timeInMillis

        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(date1900s)//Smallest date which can be selected
            .setEnd(today)//Furthest day which can be selected
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
            viewModel.setFields(FieldTags.BIRTHDAY, it)
            binding.birthday.editText!!.setText(viewModel.formatDate(viewModel.birthdayField))
        }
        datePicker.showNow(parentFragmentManager, "")
    }


    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [ScannerCreationViewModel.photoField]
     * Then we set the [FragmentScannerRegistrationBinding.profilePhoto] bitmap to the selected image if the image is less than 4000*4000
     * else we re-launch the selection
     */
    private val pickScannerPhoto: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
        photoUri?.let {uri ->
            val photoStream = requireActivity().contentResolver.openInputStream(uri)!!
            viewModel.setFields(FieldTags.PROFILE_PHOTO, BitmapFactory.decodeStream(photoStream))

            if (viewModel.photoField!!.width >= 4000 && viewModel.photoField!!.height >= 4000) {//In case image too large
                showToast("The image is too large!")
                initPictureSelection()
            }
            else // Sets the profile to the selected image
                binding.profilePhoto.setImageBitmap(viewModel.photoField!!)
        }
    }


    /**
     * Helper method to display toasts
     */
    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()



}