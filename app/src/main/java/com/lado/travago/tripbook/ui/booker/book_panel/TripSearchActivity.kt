package com.lado.travago.tripbook.ui.booker.book_panel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.lado.travago.tripbook.NavBookerCreationDirections
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityTripSearchBinding
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSearchBinding

    //    private lateinit var navController: NavController
    private lateinit var viewModel: TripsConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_search)
        viewModel = ViewModelProvider(this)[TripsConfigViewModel::class.java]
        setupNav()
    }

    private fun setupNav() {
        val navController = NavController(this)
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
    }
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