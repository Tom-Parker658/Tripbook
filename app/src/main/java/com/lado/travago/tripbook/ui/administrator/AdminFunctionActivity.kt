package com.lado.travago.tripbook.ui.administrator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAdminFunctionBinding
import com.lado.travago.tripbook.repo.osm_services.TownEntity
import kotlinx.coroutines.*

/**
 * Used by me to do specific task to the database
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AdminFunctionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminFunctionBinding
    private lateinit var viewModel: AdminFunctionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_function)
        viewModel = ViewModelProvider(this,
            AdminFunctionViewModel.AdminFunctionViewModelFactory(application))[AdminFunctionViewModel::class.java]
        //Uploads the journeys to the database
        binding.button.setOnClickListener {
            viewModel.readOSMTowns()
        }
        observeLiveData()
        binding.root
    }


    private fun observeLiveData() {
        viewModel.text.observe(this) {
            if (it?.isNotBlank() == true) {
                binding.nameText.text = it
            }
        }
        viewModel.onLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
        viewModel.towns.observe(this) {
            parseResults(it)
        }

//        viewModel.allTowns.observe(this) {
//            if (it.isNotEmpty()) {
//                Log.d("ADMIN", it.toString())
//                parseResults(it, "TOWN")
//            }
//        }
        viewModel.toastMessage.observe(this) {
            if (it.isNotBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                Log.d("AdminFunctions", it)
            }
        }
    }

    private fun parseResults(townEntityList: List<TownEntity>) {
        binding.nameText.text = townEntityList.toString()
    }

}