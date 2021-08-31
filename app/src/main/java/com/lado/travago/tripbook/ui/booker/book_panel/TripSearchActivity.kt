package com.lado.travago.tripbook.ui.booker.book_panel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityTripSearchBinding

class TripSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSearchBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_search)
        setupNav()
    }

    private fun setupNav(){
        setSupportActionBar(binding.tripToolbar)
        navController = findNavController(binding.myTripSearchNavHostFragment.id)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when(destination.id){
                R.layout.fragment_trip_search ->{
                    binding.tripToolbar.navigationIcon = null
                    binding.tripToolbar.title = "Tripbook"
                    binding.bookProgression.progress = 0
                }
                R.layout.fragment_trip_search_result -> {
                    binding.tripToolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_arrow_back_24)
                    binding.tripToolbar.title = "${arguments?.get("location")}-${arguments?.get("destination")}, ${arguments?.get("distance")}"
                    binding.bookProgression.progress = 1
                }
                R.layout.fragment_trip_detail -> {
                    binding.tripToolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_arrow_back_24)
                    binding.bookProgression.progress = 2
                }
            }
        }
    }
}