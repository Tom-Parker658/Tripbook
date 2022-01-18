package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityTripSearchBinding
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class TripSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSearchBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var uiUtils: UIUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_search)
        uiUtils = UIUtils(null, this, this)
        bottomNavView = binding.bottomNavTripSearch.bookerBottomNav
        setupFragmentNavigation()
        uiUtils.activityNavigation(bottomNavView, R.id.action_trip_search)
        uiUtils.onNetworkChange(binding.networkStateHeader)
    }

    override fun onResume() {
        super.onResume()

        // disable transition when coming back from an activity
        overridePendingTransition(0, 0)
    }

    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.trip_search_nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            /* We want to show the bottom nav bar only on the first page(TripSearch page) and no where else
            for this activity */
            if (destination.id != navController.graph.startDestination)
                uiUtils.bottomBarVisibility(false, bottomNavView)
            else uiUtils.bottomBarVisibility(true, bottomNavView)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.trip_search_nav_host)
        return navController.navigateUp()
    }


}