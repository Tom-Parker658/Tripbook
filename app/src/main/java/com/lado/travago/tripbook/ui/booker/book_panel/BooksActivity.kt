package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBooksBinding
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BooksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBooksBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var uiUtils: UIUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_books)
        uiUtils = UIUtils(null, this, this)
        bottomNavView = binding.bookerBottomNav.bookerBottomNav
        setupFragmentNavigation()
        uiUtils.activityNavigation(bottomNavView, R.id.action_trip_search)

        super.onCreate(savedInstanceState)

    }

    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.books_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id != navController.graph.startDestination)
                uiUtils.bottomBarVisibility(false, bottomNavView)
            else uiUtils.bottomBarVisibility(true, bottomNavView)
        }
    }


}