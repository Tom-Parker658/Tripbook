package com.lado.travago.tripbook.ui.agency.creation.config_panel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * This activity is to manage the configuration of an agency
 * Functions:
 * manage navigation across all activities
 */
@ExperimentalCoroutinesApi
class AgencyConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
    }
//
//    /**
//     * Instantiate all view-models
//     */
//    private fun initViewModels() {
//        townsConfigViewModel = ViewModelProvider(this)[TownsConfigViewModel::class.java]
//        tripsConfigViewModel = ViewModelProvider(this)[TripsConfigViewModel::class.java]
//    }
//
//    /**
//     * Navigate through all fragments by observing specific viewModel
//     */
//    private fun navigate() {
//        /**
//         * @see TownsConfigFragment.observeLiveData
//         */
//        townsConfigViewModel.onNavigateToTrip.observe(this) {
//            if (it) {
//                val tripArguments = Bundle().apply {
//                    putString("TOWN_ID", townsConfigViewModel.townId)
//                    putString("TOWN_NAME", townsConfigViewModel.townName)
//                }
//
//                findNavController(binding.myAgencyConfigNavHostFragment).navigate(
//                    R.id.action_townsConfigFragment_to_tripsConfigFragment,
//                    tripArguments
//                )
//                townsConfigViewModel.setField(TownsConfigViewModel.FieldTag.NAVIGATE_TO_TRIP, false)
//            }
//        }
//    }
}