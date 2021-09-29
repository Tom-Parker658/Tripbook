package com.lado.travago.tripbook.ui.administrator

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAdminFunctionBinding
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
        viewModel = ViewModelProvider(this)[AdminFunctionViewModel::class.java]
        //Uploads the journeys to the database
        binding.button.setOnClickListener {
            doTheUpload()
            Toast.makeText(this, "Do it!", Toast.LENGTH_LONG).show()
        }
        observeLiveData()
        binding.root
    }

    private fun doTheUpload() = CoroutineScope(Dispatchers.Main).launch {
        viewModel.addTrips()
    }

    private fun observeLiveData(){
        viewModel.text.observe(this){
            if(it.isNotBlank()) {
                binding.nameText.text = it
            }
        }
        viewModel.toastMessage.observe(this){
            if(it.isNotBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                Log.d("AdminFunctions", it)
            }
        }
    }



}