package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyConfigBinding
import com.lado.travago.tripbook.model.enums.NotificationType
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.notification.NotificationFragmentArgs
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * This activity is to manage the configuration of an agency
 * Functions:
 * manage navigation across all activities
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyConfigBinding
    private lateinit var viewModel: AgencyConfigViewModel
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var uiUtils: UIUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiUtils = UIUtils(null, this, this)
        viewModel = ViewModelProvider(this)[AgencyConfigViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_config)
        bottomNavView = binding.bottomNavAgencyConfig.bookerBottomNav
        setupFragmentNavigation()
        uiUtils.activityNavigation(bottomNavView, R.id.action_agency_config)
        uiUtils.onNetworkChange(binding.networkStateHeader)
//
        /*
        Once the booker has created an agency or owns one or is a scanner, we automatically get the
        the agency ID for all the fragments associated to configuration
        */
        viewModel.bookerIsNotScanner.observe(this) {
            if (it == false) {
                viewModel.agencyDoc(this, viewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
        }
//        viewModel.hasProfile.observe(this) {
//            if (it && viewModel.authRepo.currentUser != null) {
//                Log.d("GATEWAY_ACTIVITY", "LOGGED")
//                val navController = findNavController(R.id.agency_config_nav_host)
//                if (navController.currentDestination?.id == R.id.notificationFragment) {
//                    Log.d("GATEWAY_ACTIVITY", "CURRENT DESTINATION NOT")
//                    if (viewModel.navArgs.value != Bundle.EMPTY) {
//                        Log.d("GATEWAY_ACTIVITY", "TRIED TO NAV UP")
//                        viewModel.setField(AgencyConfigViewModel.FieldTags.NAV_ARGS, Bundle.EMPTY)
//                        navController.navigateUp()
//                    }
//                }
//            }
//        }

//        viewModel.bookerDoc.observe(this) {
//            if (it.exists() || it != null) {
//                val navController = findNavController(R.id.agency_config_nav_host)
//                if (navController.currentDestination?.id == R.id.notificationFragment && navController.currentDestination?.arguments?.get("notificationType") as NotificationType == NotificationType.ACCOUNT_NOT_FOUND) {
//                    uiUtils.editSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST, true)
//                    navController.navigateUp()
//                    viewModel.setField(AgencyConfigViewModel.FieldTags.HAS_PROFILE, true)
//                }
//            }
//        }
    }

    /**<-------------------------------Navigation----------------------->**/
    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.agency_config_nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                navController.graph.startDestination -> {
                    uiUtils.bottomBarVisibility(true, bottomNavView)
                }
                R.id.notification_fragment_agency -> {
                    uiUtils.bottomBarVisibility(true, bottomNavView)
//                    if (uiUtils.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true) {
//                        navController.navigate(R.id.agencyGatewayFragment)
//                    }
                }
                else -> uiUtils.bottomBarVisibility(false, bottomNavView)
            }
        }
    }
//
//    fun controlGateway() {
//        viewModel.navArgs.observe(this) {
//            if (it == Bundle.EMPTY) {
//                val navController = findNavController(R.id.agency_config_nav_host)
//                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.notificationFragment) {
//                    navController.navigate(R.id.agencyGatewayFragment)
//                    navController.popBackStack()
//                }
//            }
//        }
//        viewModel.authRepo.firebaseAuth.addAuthStateListener {
//            if (it.currentUser != null) {
//                val navController = findNavController(R.id.agency_config_nav_host)
//                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.notificationFragment) {
//                    navController.navigate(R.id.agencyGatewayFragment)
//                    navController.popBackStack()
//                }
//            }
//        }
//    }

    /**
     * NB, important
     */
    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.agency_config_nav_host)
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()

        // disable transition when coming back from an activity
        overridePendingTransition(0, 0)
    }

}