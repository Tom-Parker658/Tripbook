package com.lado.travago.tripbook.ui.booker.creation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBookerCreationBinding
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.ui.agency.config_panel.AgencyConfigActivity
import com.lado.travago.tripbook.ui.booker.book_panel.BooksActivity
import com.lado.travago.tripbook.ui.booker.book_panel.TripSearchActivity
import com.lado.travago.tripbook.ui.booker.creation.viewmodel.BookerSignInViewModel
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookerCreationBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var viewModel: BookerSignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booker_creation)
        bottomNavView = binding.bookerBottomNav.bookerBottomNav
        setupFragmentNavigation()
        getIntentData()
        setupNavigation()
    }
    /**<------------------Business Logic--------------------------------------->*/

    private fun getIntentData(){
        viewModel = ViewModelProvider(this)[BookerSignInViewModel::class.java]
        val caller = intent.extras?.get("caller") as SignUpCaller?
        if(caller != null){
            viewModel.setField(BookerSignInViewModel.FieldTags.SIGNUP_CALLER, caller)
        }else{
            viewModel.setField(BookerSignInViewModel.FieldTags.SIGNUP_CALLER, SignUpCaller.USER)
        }
    }




    /**<-------------------Navigation-------------------------------------------->*/
    override fun onRestart() {
        bottomNavView.selectedItemId = R.id.action_booker_account
        super.onRestart()
    }
    override fun onResume() {
        bottomNavView.selectedItemId = R.id.action_booker_account
        super.onResume()
    }

    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.booker_creation_nav_host)
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
        val navController = findNavController(R.id.booker_creation_nav_host)
        return navController.navigateUp()
    }

    private fun setupNavigation() {
        bottomNavView.selectedItemId = R.id.action_booker_account
        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_trip_search -> {
                    startActivity(
                        Intent(this, TripSearchActivity::class.java)
                    )
                    true
                }
                R.id.action_my_books -> {
                    startActivity(
                        Intent(this, BooksActivity::class.java)
                    )
                    true
                }
                R.id.action_booker_help -> {
                    false
                }
                R.id.action_booker_my_agency -> {
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

