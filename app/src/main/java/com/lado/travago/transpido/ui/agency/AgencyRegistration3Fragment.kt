package com.lado.travago.transpido.ui.agency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentAgencyRegistration3Binding
import com.lado.travago.transpido.ui.scanner.ScannerCreationActivity
import com.lado.travago.transpido.viewmodel.admin.AgencyRegistrationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyRegistration3Fragment() : Fragment() {
    private lateinit var binding: FragmentAgencyRegistration3Binding
    private lateinit var viewModel: AgencyRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[AgencyRegistrationViewModel::class.java]
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_registration3,
            container,
            false
        )

        //Init Binding
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        onFabAddScannerClicked()
        return binding.root
    }

    private fun onFabAddScannerClicked(){
        //Starts the scanner creation activity
        binding.fabAddScanner.setOnClickListener {
            val scannerIntent = Intent(requireActivity(), ScannerCreationActivity::class.java)
                .putExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME, viewModel.nameField)
                .putExtra(AgencyRegistrationActivity.KEY_OTA_PATH, viewModel.otaPath)

            startActivityForResult(scannerIntent, AgencyRegistrationActivity.RC_SCANNER_CREATION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
            when(requestCode){
                AgencyRegistrationActivity.RC_SCANNER_CREATION -> {
                    val scannerData = data!!
                    Log.i("AgencyRegistFrag3", "Name = ${scannerData.getStringExtra(AgencyRegistrationActivity.KEY_SCANNER_NAME)}")
                    Log.i("AgencyRegistFrag3", "URL = ${scannerData.getStringExtra(AgencyRegistrationActivity.KEY_SCANNER_URL)}")
                }
            }
    }

}