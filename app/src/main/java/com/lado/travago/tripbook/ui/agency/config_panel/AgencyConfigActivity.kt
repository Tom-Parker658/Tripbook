package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyConfigBinding
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * This activity is to manage the configuration of an agency
 * Functions:
 * manage navigation across all activities
 */
@ExperimentalCoroutinesApi
class AgencyConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyConfigBinding
    private lateinit var viewModel: AgencyConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AgencyConfigViewModel::class.java]
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)

        viewModel.retry.observe(this) {
            if (it) CoroutineScope(Dispatchers.Main).launch {
                viewModel.getCurrentBooker()
            }
        }

        viewModel.bookerDoc.observe(this) {
            //If this a scanner, he nav to panel
            if (it.exists()) binding =
                DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
            //If this just any user, he navigates to agency creation
            else {
                binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
                findNavController(R.id.my_agency_config_nav_host_fragment).navigate(R.id.action_agencyConfigCenterFragment_to_agencyCreationFragment)

            }
        }
    }

}