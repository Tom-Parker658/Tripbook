package com.lado.travago.tripbook.ui.booker.creation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBookerCreationBinding
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookerCreationBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var viewModel: BookerSignInViewModel
    private lateinit var uiUtils: UIUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booker_creation)
        uiUtils = UIUtils(null, this, this)
        bottomNavView = binding.bookerBottomNav.bookerBottomNav
        setupFragmentNavigation()
        uiUtils.activityNavigation(bottomNavView, R.id.action_booker_account)
        uiUtils.onNetworkChange(binding.networkStateHeader)
        getIntentData()
    }

    override fun onResume() {
        // disable transition when coming back from an activity
        overridePendingTransition(0, 0)
        super.onResume()

    }

    /**<------------------Business Logic--------------------------------------->*/

    private fun getIntentData() {
        viewModel = ViewModelProvider(this)[BookerSignInViewModel::class.java]
        val caller = intent.extras?.get("caller") as SignUpCaller?
        if (caller != null) {
            viewModel.setField(BookerSignInViewModel.FieldTags.SIGNUP_CALLER, caller)
        } else {
            viewModel.setField(BookerSignInViewModel.FieldTags.SIGNUP_CALLER, SignUpCaller.USER)
        }
    }


    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.booker_creation_nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            /* We want to show the bottom nav bar only on the first page(TripSearch page) and no where else
            for this activity */
            if (destination.id != navController.graph.startDestination ) {
                uiUtils.bottomBarVisibility(false, bottomNavView)
            } else uiUtils.bottomBarVisibility(true, bottomNavView)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.booker_creation_nav_host)
        return navController.navigateUp()
    }


}

