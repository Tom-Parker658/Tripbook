package com.lado.travago.transpido.ui.scanner

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.ActivityScannerCreationBinding
import com.lado.travago.transpido.ui.agency.AgencyRegistrationActivity
import com.lado.travago.transpido.viewmodel.ScannerCreationViewModelFactory
import com.lado.travago.transpido.viewmodel.admin.ScannerCreationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerCreationActivity() : AppCompatActivity() {
    private lateinit var viewModel: ScannerCreationViewModel
    private lateinit var binding: ActivityScannerCreationBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scanner_creation)
        initViewModel()
        showProgressBar()
        navigateToAgencyFragment()
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
        viewModel = ViewModelProvider(this, viewModelFactory)[ScannerCreationViewModel::class.java]
    }

    /**
     * Gets the intent data which is passed from the Agency to launch the Scanner creation.
     * The intent contains the agency name and the database path to teh agency's document. Tis data wil
     * be used for the creation of the scanner.
     * We assume their values can not be null
     * @return A pair where first = agencyName and second = path
     */
    private fun getIntentData(): Pair<String, String>{
        val agencyFirestorePath = intent.getStringExtra(AgencyRegistrationActivity.KEY_OTA_PATH) !!
        val agencyName = intent.getStringExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME) !!

        Log.i("ScannerCreationActivity", "agencyName=$agencyName, path=$agencyFirestorePath")
        return agencyName to agencyFirestorePath
    }

    /**
     * Send an intent containing the data about the the scanner once the scanner has been created.
     * This intent shall be gotten from [onActivityResult] with request code [AgencyRegistrationActivity.RC_SCANNER_CREATION]
     */
    private fun navigateToAgencyFragment(){
        viewModel.scannerCreated.observe(this) {
            if(it){
                val scannerDataIntent  = Intent()
                    .putExtra(AgencyRegistrationActivity.KEY_SCANNER_NAME, viewModel.nameField)
                    .putExtra(AgencyRegistrationActivity.KEY_SCANNER_BIRTHDAY, viewModel.birthdayField)
                    .putExtra(AgencyRegistrationActivity.KEY_SCANNER_IS_ADMIN, viewModel.isAdminField)
                    .putExtra(AgencyRegistrationActivity.KEY_SCANNER_PHONE, viewModel.phoneField)
                    .putExtra(AgencyRegistrationActivity.KEY_SCANNER_URL, viewModel.url)
                setResult(Activity.RESULT_OK, scannerDataIntent)
            }
        }
    }

    /**
     * Observe the [ScannerCreationViewModel.loading] live data to know when a process is actually in the loading state
     * inorder to show the progress bar. It then makes the progress bar invisible if there is no loading
     * process anymore
     */
    private fun showProgressBar()=
        viewModel.loading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }

}