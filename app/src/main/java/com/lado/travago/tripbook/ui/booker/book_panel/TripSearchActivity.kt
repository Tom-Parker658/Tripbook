package com.lado.travago.tripbook.ui.booker.book_panel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityTripSearchBinding
import com.lado.travago.tripbook.ui.agency.config_panel.AgencyConfigActivity
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class TripSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSearchBinding
    private lateinit var bottomNavView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_search)

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

    override fun onRestart() {
        bottomNavView.selectedItemId = R.id.action_trip_search
        super.onRestart()
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
                R.id.action_booker_account -> {
                    startActivity(
                        Intent(this, BookerCreationActivity::class.java)
                    )
                    true
                }
                R.id.action_booker_help -> {
                    //TODO: Remove this, this is just to add things to the universal database
//                    startActivity(
//                        Intent(this, AdminFunctionActivity::class.java)
//                    )
                    false
                }
                R.id.action_booker_my_agency -> {
                    //TODO: Help pages
                    startActivity(
                        Intent(this, AgencyConfigActivity::class.java)
                    )
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

}