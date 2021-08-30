package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.AgencyConfigViewModel
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

        viewModel.retry.observe(this) {
            if (it)
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getCurrentBooker()
                }
        }
        viewModel.bookerDoc.observe(this) {
            if (it.exists()) binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
            else binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
        }
    }

}