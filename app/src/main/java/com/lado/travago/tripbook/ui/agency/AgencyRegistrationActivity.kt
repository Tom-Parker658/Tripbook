package com.lado.travago.tripbook.ui.agency

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyRegistrationBinding
import com.lado.travago.tripbook.viewmodel.admin.AgencyRegistrationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyRegistrationBinding
    lateinit var viewModel: AgencyRegistrationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_registration)

        viewModel = ViewModelProvider(this)[AgencyRegistrationViewModel::class.java]

        //Shows the loading spinner when the onLoading is true
        onLoading()

        //setups action bar with the back button
        val navController = findNavController(R.id.myAgencyNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }


    private fun onLoading() {
        viewModel.onLoading.observe(this) {
            if (it == true) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
    }

    /**
     * Navigate up in the stack when the back button is clicked
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myAgencyNavHostFragment)
        return navController.navigateUp()
    }

    companion object {
        const val KEY_OTA_PATH = "path"
        const val KEY_AGENCY_NAME = "agencyName"
        const val KEY_SCANNER_NAME = "scannerName"
        const val KEY_AGENCY_ID = "agencyId"
        const val KEY_SCANNER_PHONE = "scannerPhone"
        const val KEY_SCANNER_BIRTHDAY = "scannerBirthday"
        const val KEY_SCANNER_IS_ADMIN = "scannerIsAdmin"
        const val KEY_SCANNER_URL = "scannerPhotoUrl"
        const val RC_SCANNER_CREATION = 1
    }

}