package com.lado.travago.tripbook.ui.agency.creation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyCreationBinding
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationViewModel.FieldTags
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyCreationBinding
    lateinit var viewModel: AgencyCreationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_creation)

        viewModel = ViewModelProvider(this)[AgencyCreationViewModel::class.java]

        observeLiveData()
    }


    private fun observeLiveData() {
        viewModel.loading.observe(this) {
            if (it == true) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
        viewModel.toastMessage.observe(this) {
            if (it.isNotBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.saveInfo.observe(this) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.setField(FieldTags.SAVE_INFO, false)
                    viewModel.saveAgencyInfo()
                }
            }
        }
        //In this case we go back to the launcher activity which is actually th config activity
        viewModel.onInfoSaved.observe(this) {
            if (it) {
                finish()
            }
        }
        viewModel.onVerificationFailed.observe(this) {
            //We end activity if we can't verify we are editing or creating an agency
            if (it) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verification failed!")
                    .setIcon(R.drawable.round_cancel_24)
                    .setMessage("We could not determine if you are creating or editing an agency!")
                    .setOnDismissListener { finish() }
                    .setView(binding.root)
                    .setOnCancelListener { finish() }
                    .setPositiveButton("CLOSE") { _, _ ->
                       finish()
                    }
                    .create().show()
            }
        }
    }
}