package com.lado.travago.tripbook.ui.booker.creation

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityUserCreationBinding
import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationActivity
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.UserCreationViewModelFactory
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel.FieldTags
import kotlinx.coroutines.*
import java.util.*


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreationActivity : AppCompatActivity() {
    private lateinit var viewModel: BookerCreationViewModel
    private lateinit var binding: ActivityUserCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Restore all fields after configuration changes
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_creation)
        initViewModel()
        regulateRegistrationUI()
        navigateToLauncherUI()
        onFieldChange()
        restoreFields()
        viewModel.loading.observe(this){
            if(it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
        onBtnCreateClicked()
        showProgressBar()
    }

    override fun onStart() {
        super.onStart()
        regulateRegistrationUI()
    }

    /**
     * This functions regulates the layout depending if we are registering a scanner or booker
     */
    private fun regulateRegistrationUI() {
        viewModel.isUserAScanner.observe(this) {
            if (!viewModel.isUserAScanner.value!!) {
                //In this case, we dealing with a booker registration
                binding.checkBoxAdmin.visibility = View.GONE
                binding.btnCreateUser.text = "Create Booker"
            } else {
                //Here it is a scanner, thus we delete the occupation field, since we are a scanner automatically
                binding.occupation.visibility = View.GONE
                binding.btnCreateUser.text = "Create Scanner"
                viewModel.setFields(FieldTags.OCCUPATION, OCCUPATION.SCANNER)
                binding.textLabelUserOccupation.visibility = View.GONE
            }
        }

    }

    /**
     *Create user using the password and email
     */
    private fun onBtnCreateClicked() {
        binding.btnCreateUser.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.createUser()
            }
        }
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     * credential represent either email or phoneNumber
     */
    private fun onFieldChange() {
        binding.credential.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.EMAIL, binding.credential.editText!!.text.toString())
        }
        binding.password.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.PASSWORD, binding.password.editText!!.text.toString())
        }
        binding.name.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.NAME, binding.name.editText!!.text.toString())
        }
        binding.credential.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.EMAIL, binding.credential.editText!!.text.toString())
        }
        binding.birthplace.editText!!.addTextChangedListener {
            viewModel.setFields(
                FieldTags.BIRTH_PLACE,
                binding.birthplace.editText!!.text.toString()
            )
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

    private fun initPictureSelection() = pickScannerPhoto.launch("image/*")

    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [BookerCreationViewModel.photoField]
     * Then we set the [ActivityUserCreationBinding.profilePhoto] bitmap to the selected image if the image is less than 4000*4000
     * else we re-launch the selection
     */
    private val pickScannerPhoto: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
            photoUri?.let { uri ->
                val photoStream = contentResolver.openInputStream(uri)!!
                viewModel.setFields(
                    FieldTags.PROFILE_PHOTO,
                    BitmapFactory.decodeStream(photoStream)
                )
                if (viewModel.photoField!!.width >= 4000 && viewModel.photoField!!.height >= 4000) {//In case image too large
                    Toast.makeText(this, "The image is too large!", Toast.LENGTH_LONG).show()
                    initPictureSelection()
                } else // Sets the profile to the selected image
                    binding.profilePhoto.setImageBitmap(viewModel.photoField!!)
            }
        }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.occupation.editText!!.setText(viewModel.agencyName)
        viewModel.photoField?.let { binding.profilePhoto.setImageBitmap(it) }
        binding.name.editText!!.setText(viewModel.nameField)
        binding.password.editText!!.setText(viewModel.passwordField)
        binding.credential.editText!!.setText(viewModel.email)
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
     * initiate the date picker to select the birthday
     */
    private fun selectBirthDay() {
        packageName
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
        datePicker.showNow(supportFragmentManager, "")
    }

    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */
    private fun initViewModel() {
        //Data gotten from the agency
        val intentData = getIntentData()
        val viewModelFactory = UserCreationViewModelFactory(
            agencyName = intentData.first,
            agencyId = intentData.second,
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[BookerCreationViewModel::class.java]
    }

    /**
     * Gets the intent data which is passed from the LauncherUI
     * Depending on the content of the intent, we can know which type of User(Booker or Scanner) we registering
     * @return A pair where first = agencyName and second = path and third = agencyId
     */
    private fun getIntentData(): Pair<String?, String?> {
        val agencyName = intent.getStringExtra(AgencyCreationActivity.KEY_AGENCY_NAME)
        val agencyFirestorePath = intent.getStringExtra(AgencyCreationActivity.KEY_OTA_PATH)
        val agencyId = intent.getStringExtra(AgencyCreationActivity.KEY_AGENCY_ID)

        Log.i("BookerCreationActivity", "agencyName=$agencyName, path=$agencyFirestorePath")
        return Pair(agencyName, agencyId)
    }

    /**
     * Navigates back to the UI which originally launched this creation as an intent. It returns with no data as intent,
     * But only with the RESULT status
     */
    private fun navigateToLauncherUI() =
        viewModel.userCreated.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK, null)
                finish()
                viewModel.stopLoading()
            }
        }

    /**
     * Observe the [BookerCreationViewModel.loading] live data to know when a process is actually in the loading state
     * inorder to show the progress bar. It then makes the progress bar invisible if there is no loading
     * process anymore
     */
    private fun showProgressBar() =
        viewModel.loading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }

    //Phone Auth
    /**
    //Callback to be called during phone verification
    private val phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
    credentials = credential
    viewModel.setFields(FieldTags.ON_PHONE_VERIFIED, true)
    viewModel.startLoading()
    }

    override fun onVerificationFailed(exception: FirebaseException) {
    Log.e("AUTH", exception.message!!)
    viewModel.stopLoading()
    }

    override fun onCodeSent(
    id: String,
    forceResendToken: PhoneAuthProvider.ForceResendingToken
    ) {
    Log.i("PhoneAuth", "Code has been sent")
    resendToken = forceResendToken
    verificationId = id
    viewModel.setFields(FieldTags.ON_CODE_SENT, true)
    viewModel.stopLoading()
    }

    }

    /**
     * Launches the confirmation screen immediately the sms codehas been sent to the user phone
    */
    private fun showConfirmationDialog() {
    viewModel.onCodeSent.observe(this) {
    if (it) inflateConfirmationDialog()
    }
    }


    /**
     * Shows the confirmation code dialog
    */
    private fun inflateConfirmationDialog() {
    //This the layout, which will serve as the confirmation code editText
    val codeBinding: ItemCodeConfirmationBinding = DataBindingUtil.inflate(
    layoutInflater,
    R.layout.item_code_confirmation,
    null,
    false
    )

    //The dialog to be inflated
    val dialog = MaterialAlertDialogBuilder(this)
    .setTitle("Verify ${viewModel.emailField}")
    .setMessage("Check your SMS at ${viewModel.emailField}!")
    .setView(codeBinding.root)
    .setNeutralButton("Cancel") { _, _ ->
    resendVerificationCode()
    }
    .setNegativeButton("CANCEL") { dialog, _ ->
    dialog.cancel()
    dialog.dismiss()
    }
    .setPositiveButton("CONFIRM") { dialog, _ ->
    if (codeBinding.code.editText!!.length() == 6) {
    viewModel.setFields(
    FieldTags.SMS_CODE,
    codeBinding.code.editText!!.text.toString()
    )
    createCredentials()
    }
    }
    .create()

    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    dialog.show()
    }

    /**
     * After code has been sent, we use the user's input 6-digit verification code
    */
    private fun createCredentials(){
    credentials = PhoneAuthProvider.getCredential(verificationId, viewModel.smsCodeField)
    viewModel.setFields(FieldTags.ON_PHONE_VERIFIED, true)
    }

    /**
     * Sends a verification code to the user's phone
    */
    private fun startPhoneVerification() =
    PhoneAuthProvider.verifyPhoneNumber(
    PhoneAuthOptions.newBuilder()
    .setPhoneNumber(viewModel.emailField)
    .setTimeout(60L, TimeUnit.SECONDS)
    .setActivity(this)
    .setCallbacks(phoneCallback)
    .build()
    )

    /**
     * Used to resend after time out
    */
    private fun resendVerificationCode() {
    val phoneAuthOptions = PhoneAuthOptions.newBuilder()
    .setPhoneNumber(viewModel.emailField)
    .setTimeout(60L, TimeUnit.SECONDS)
    .setActivity(this)
    .setCallbacks(phoneCallback)
    .setForceResendingToken(resendToken)
    .build()
    PhoneAuthProvider.verifyPhoneNumber(
    phoneAuthOptions
    )
    }
     */
}