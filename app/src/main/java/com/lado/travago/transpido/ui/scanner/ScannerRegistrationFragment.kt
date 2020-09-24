package com.lado.travago.transpido.ui.scanner

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
import com.lado.travago.transpido.databinding.FragmentScannerRegistrationBinding
import com.lado.travago.transpido.model.enums.SEX
import com.lado.travago.transpido.ui.agency.AgencyRegistrationActivity
import com.lado.travago.transpido.utils.Utils
import com.lado.travago.transpido.viewmodel.ScannerCreationViewModelFactory
import com.lado.travago.transpido.viewmodel.admin.ScannerCreationViewModel
import com.lado.travago.transpido.viewmodel.admin.ScannerCreationViewModel.FieldTags
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentScannerRegistrationBinding
    private lateinit var viewModel: ScannerCreationViewModel
    private val uiScope = CoroutineScope(Dispatchers.Main)
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
        uiScope.launch { createScanner() }

        return binding.root
    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        if(viewModel.photoField != null)
            binding.profilePhoto.setImageBitmap(viewModel.photoField)
        binding.name.editText!!.setText(viewModel.nameField)
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
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */
    private fun initViewModel() {
        //Data gotten from the agency
        val intentData = getIntentData()
        val viewModelFactory = ScannerCreationViewModelFactory(
            agencyName = intentData.first,
            agencyFirestorePath = intentData.second
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[ScannerCreationViewModel::class.java]
    }

    /**
     * We observe the live data [PhoneValidationFragment.done] to know when the phone verification is finished.
     * Then we obtain the generated id from the the arguments.
     */
    private suspend fun createScanner() {
        PhoneValidationFragment.done.observe(viewLifecycleOwner) {
            if (it) {
                uiScope.launch {
                    viewModel.setFields(
                        FieldTags.ID,
                        ScannerRegistrationFragmentArgs.fromBundle(requireArguments()).scannerID
                    )
                    viewModel.createScanner()
                }
            }
        }
    }


    /**
     * Gets the intent data which is passed from the Agency to launch the Scanner creation.
     * The intent contains the agency name and the database path to teh agency's document. Tis data wil
     * be used for the creation of the scanner.
     * We assume their values can not be null
     * @return A pair where first = agencyName and second = path
     */
    private fun getIntentData(): Pair<String, String> {
        val agencyFirestorePath =
            requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_OTA_PATH)!!
        val agencyName =
            requireActivity().intent.getStringExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME)!!
        showToast("$agencyName, $agencyFirestorePath")
        return agencyName to agencyFirestorePath
    }

    /**
     * Navigates to the phoneVerification fragment with the [phoneField]
     */
    private fun onBtnCreateClicked() =
        binding.btnCreateScanner.setOnClickListener {
            it.findNavController().navigate(
                ScannerRegistrationFragmentDirections.actionScannerRegistrationFragment2ToPhoneValidationFragment(
                    viewModel.phoneField
                )
            )
        }


    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.name.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.NAME, binding.name.editText.toString())
        }
        binding.phone.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.PHONE, binding.phone.editText.toString())
        }
        binding.birthplace.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.BIRTH_PLACE, binding.birthplace.editText.toString())
        }
        binding.checkBoxAdmin.setOnCheckedChangeListener { _, isAdmin ->
            viewModel.setFields(FieldTags.IS_ADMIN, isAdmin)
        }
        // An onClick listener to to initiate the picture selection when the profile photo imageView is clicked
        binding.profilePhoto.setOnClickListener {
            initPictureSelection()
        }
        // Add an onClick listener to the birthday endIcon to select the birthday
        binding.birthday.setOnClickListener { selectBirthDay() }
        binding.birthday.setEndIconOnClickListener { selectBirthDay() }
        binding.birthday.editText!!.setOnClickListener { selectBirthDay() }

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

    /**
     * initiate the date picker to select the birthday
     */
    private fun selectBirthDay() {
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.set(1900, 1, 1)//Date in 1900s
        val date1900s = calendar.timeInMillis
        Utils.getDatePicker(
            date1900s,
            today,
            "Select your birthday"
        ).run {
            showNow(fragmentManager!!, "")
            addOnPositiveButtonClickListener {
                viewModel.setFields(FieldTags.BIRTHDAY, it)
            }
        }
    }

    /**
     * Intent to select profile photo from gallery. Request code = [RC_LOAD_PHOTO]
     */
    private fun initPictureSelection() =
        startActivityForResult(
            Intent(Intent.ACTION_PICK).setType("image/*"),
            RC_LOAD_PHOTO
        )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                //Profile photo selection intent result
                RC_LOAD_PHOTO -> {
                    try {
                        val logoUri = data?.data!!
                        //convert image into stream
                        val photoStream =
                            requireActivity().contentResolver.openInputStream(logoUri)!!
                        val selectedPhoto = BitmapFactory.decodeStream(photoStream)
                        viewModel.setFields(FieldTags.PROFILE_PHOTO, selectedPhoto)

                        if (selectedPhoto.width >= 4000 && selectedPhoto.height >= 4000) {//In case image too large
                            showToast("The image is too large!")
                            initPictureSelection()
                        } else // Sets the profile to the selected image
                            binding.profilePhoto.setImageBitmap(viewModel.photoField!!)
                    } catch (e: Exception) {
                        showToast("Something went wrong when loading image. Try again")
                    }
                }
            }
        else
            showToast("You haven't picked any picture")
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

    companion object {
        const val RC_LOAD_PHOTO = 1
        const val TAG = "ScannerRegistFragment"
    }

}