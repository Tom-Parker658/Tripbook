package com.lado.travago.tripbook.ui.booker.book_panel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBooksBinding
import com.lado.travago.tripbook.ui.booker.creation.BookerCreation1FragmentDirections

class BooksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBooksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_books)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(binding.booksNavHostFragment.id)
        NavigationUI.setupWithNavController(binding.bottomBookerNav, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                //Within this view
                R.id.myBooksFragment -> {
                    binding.bottomBookerNav.visibility = View.VISIBLE
                }
                R.id.bookDetailsFragment ->{
                    binding.bottomBookerNav.visibility = View.GONE
                }

            }
        }
    }
}