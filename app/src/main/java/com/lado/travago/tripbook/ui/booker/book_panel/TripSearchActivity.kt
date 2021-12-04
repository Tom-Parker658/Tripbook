package com.lado.travago.tripbook.ui.booker.book_panel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityTripSearchBinding
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class TripSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSearchBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var viewModel: TripsConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_search)
        viewModel = ViewModelProvider(this)[TripsConfigViewModel::class.java]

        bottomNavView = binding.bottomNavTripSearch.bookerBottomNav

        val navController = findNavController(R.id.trip_search_nav_host)

        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            /* We want to show the bottom nav bar only on the first page(TripSearch page) and no where else
            for this activity */
            if (destination.id != navController.graph.startDestination) {
                hideBottomBar()
            } else showBottomBar()

        }
        /*We want to go to other activities only from the first page of the current activity*/
        if (navController.currentDestination?.id == navController.graph.startDestination)
            setupNavigation()
    }


    override fun onResume() {
        bottomNavView.selectedItemId = R.id.action_trip_search
        super.onResume()
    }

    private fun hideBottomBar() {
        if (bottomNavView.isShown) {
            val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}
                    override fun onAnimationEnd(p0: Animation?) {
                        bottomNavView.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}
                }
                )
            }
            bottomNavView.animation = slideDownAnimation
        } else {//Just to make sure this thing never appears in any fragment apart from the search page
            bottomNavView.visibility = View.GONE
        }

    }

    private fun showBottomBar() {
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    bottomNavView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            }
            )
        }
        bottomNavView.animation = slideUpAnimation
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.trip_search_nav_host)
        return navController.navigateUp()
    }

    private fun setupNavigation() {
        bottomNavView.selectedItemId = R.id.action_trip_search
        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_my_books -> {
                    startActivity(
                        Intent(this, BooksActivity::class.java)
                    )
                    true
                }
                R.id.action_booker_info -> {
                    startActivity(
                        Intent(this, BookerCreationActivity::class.java)
                    )
                    true
                }
                R.id.action_booker_wallet -> {
                    //TODO: Wallet Panel
                    true
                }
                R.id.action_help -> {
                    //TODO: Help pages
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

/*    private fun setupNav() {
        val navController = findNavController(R.id.trip_search_nav_host)
        NavigationUI.setupWithNavController(binding.bottomBookerNav, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                //Within this view
                R.id.tripSearchFragment -> {
                    binding.bottomBookerNav.visibility = View.VISIBLE
                }
                controller.graph.startDestination -> binding.bottomBookerNav.visibility =
                    View.VISIBLE
                R.id.tripSearchResultsFragment -> {
                    binding.bottomBookerNav.visibility = View.GONE
                    val args = TripSearchResultsFragmentArgs.fromBundle(arguments!!)
                    destination.label =
                        "${args.localityName} ${R.string.text_label_to} ${args.destinationName}"
                    binding.bookProgression.progress = 1
                }
                R.id.tripDetailsFragment -> {
                    //TODO: Remove this and continue to payment
                    binding.bottomBookerNav.visibility = View.VISIBLE
                    binding.bookProgression.progress = 2
                }

            }
        }
    }*/

/*
    private fun setupNav() {
        setSupportActionBar(binding.tripToolbar)
        navController =
            (supportFragmentManager.findFragmentById(R.id.my_trip_search_nav_host_fragment) as NavHostFragment).navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.layout.fragment_trip_search -> {
                    binding.tripToolbar.navigationIcon = null
                    binding.tripToolbar.title = "Explore the Universe"
                    binding.bookProgression.progress = 1
                }
                R.layout.fragment_trip_search_result -> {
                    binding.tripToolbar.navigationIcon =
                        resources.getDrawable(R.drawable.baseline_arrow_back_24)
                    binding.tripToolbar.title =
                        "${arguments?.get("location")}-${arguments?.get("destination")}, ${
                            arguments?.get("distance")
                        }"
                    binding.bookProgression.progress = 2
                }
                R.layout.fragment_trip_detail -> {
                    binding.tripToolbar.navigationIcon =
                        resources.getDrawable(R.drawable.baseline_arrow_back_24)
                    binding.bookProgression.progress = 3
                }
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
    }*/

}