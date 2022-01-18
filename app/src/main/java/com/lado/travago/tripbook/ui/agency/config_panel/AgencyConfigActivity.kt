package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
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
//        controlGateway()
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
                    if (uiUtils.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true) {
                        navController.navigate(R.id.agencyGatewayFragment)
                    }
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.agency_config_nav_host)
        return navController.navigateUp()
    }

    override fun onResume() {
        super.onResume()

        // disable transition when coming back from an activity
        overridePendingTransition(0, 0)
    }

}