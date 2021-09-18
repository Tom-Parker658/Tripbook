package com.lado.travago.tripbook.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityBookerBinding

import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BookerActivity : AppCompatActivity() {
    private lateinit var viewModel: TripSearchViewModel
    private lateinit var binding: ActivityBookerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booker)
        loading()
    }

    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[TripSearchViewModel::class.java]
    }

    private fun loading() {
//        viewModel.onLoading.observe(this) {
//            if (it == true) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.GONE
//        }
    }
}