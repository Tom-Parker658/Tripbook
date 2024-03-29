package com.lado.travago.tripbook.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityMainBinding
import com.lado.travago.tripbook.ui.booker.panel.viewmodel.JourneySearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: JourneySearchViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        loading()
    }

    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[JourneySearchViewModel::class.java]
    }

    private fun loading() {
        viewModel.loading.observe(this) {
            if (it == true) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
    }
}