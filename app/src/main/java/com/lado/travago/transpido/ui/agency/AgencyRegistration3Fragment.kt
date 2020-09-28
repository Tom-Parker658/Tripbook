package com.lado.travago.transpido.ui.agency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentAgencyRegistration3Binding
import com.lado.travago.transpido.model.admin.Scanner
import com.lado.travago.transpido.utils.contracts.ScannerCreationContract
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
        viewModel = ViewModelProvider(requireActivity())[AgencyRegistrationViewModel::class.java]

        //Init Binding
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        onFabAddScannerClicked()
        return binding.root
    }

    private fun onFabAddScannerClicked(){
        //Starts the scanner creation activity using the agency name and the agency firestore path
        binding.fabAddScanner.setOnClickListener {
            val agencyNameToPathPair = viewModel.nameField to viewModel.otaPath
            Log.i("ScannerCreationActivity",
                "${viewModel.nameField}, ${viewModel.otaPath}"
            )
            scannerBasicInfo.launch(agencyNameToPathPair)
        }
    }

    /**
     * A contract call to launch the scannerCreationActivity and return the result which is a [Scanner.ScannerBasicInfo] object.
     * This object is added to the list of scanners from the view model
     * @see Scanner.ScannerBasicInfo
     * @see ScannerCreationContract
     */
    private val scannerBasicInfo = registerForActivityResult(ScannerCreationContract()){info ->
        viewModel.addCreatedScannerToList(info)
        Log.i("Scanner Info", info.toString())
    }

}