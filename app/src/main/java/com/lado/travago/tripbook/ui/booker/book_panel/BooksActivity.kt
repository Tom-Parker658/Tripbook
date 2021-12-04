package com.lado.travago.tripbook.ui.booker.book_panel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBooksBinding
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BooksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBooksBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_books)
        super.onCreate(savedInstanceState)
        setupNavigation()
    }


    private fun setupNavigation() {
        bottomNavigationView = binding.bookerBottomNav.bookerBottomNav
        bottomNavigationView.selectedItemId = R.id.action_trip_search
        bottomNavigationView.setOnItemSelectedListener {
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
        bottomNavigationView.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.action_trip_search -> {
                    false
                }
                R.id.myBooksFragment -> {
                    false
                }
                R.id.action_booker_info -> {
                    false
                }
                R.id.action_booker_wallet -> {
                    //TODO: Wallet Panel
                    false
                }
                R.id.action_help -> {
                    //TODO: Help pages
                    false
                }
                else -> {
                    false
                }
            }
        }

    }

    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        super.overridePendingTransition(0, 0 )
    }


}