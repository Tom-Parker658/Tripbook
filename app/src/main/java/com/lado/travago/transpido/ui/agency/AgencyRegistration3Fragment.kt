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
import com.lado.travago.transpido.ui.recyclerview.adapters.AgencyScannersAdapter
import com.lado.travago.transpido.utils.contracts.ScannerCreationContract
import com.lado.travago.transpido.viewmodel.admin.AgencyRegistrationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyRegistration3Fragment : Fragment() {
    private lateinit var binding: FragmentAgencyRegistration3Binding
    private lateinit var viewModel: AgencyRegistrationViewModel
    private val adapter = AgencyScannersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_registration3,
            container,
            false
        )
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[AgencyRegistrationViewModel::class.java]

        //Observes the list of scanners and updates the number of them
        updateNumberOfScanners()
        onFabAddScannerClicked()
        setAdapter()
        updateRecyclerView()
//        testRecyclerView()
        return binding.root
    }

    //Updates the number of scanners
    private fun updateNumberOfScanners() = viewModel.listOfScanners.observe(viewLifecycleOwner) {
        binding.numberOfScanners.text = it.size.toString()
    }

    /**
     * Initialises and sets the recycler view adapter to the defined adapter of [AgencyScannersAdapter]
     */
    private fun setAdapter() {
        binding.recyclerView.adapter = adapter
    }

    //Starts the scanner creation activity using the agency name and the agency firestore path
    private fun onFabAddScannerClicked() = binding.fabAddScanner.setOnClickListener {
        val agencyNameToPathPair = viewModel.nameField to viewModel.otaPath
        Log.i(
            "ScannerCreationActivity",
            "${viewModel.nameField}, ${viewModel.otaPath}"
        )
        scannerBasicInfo.launch(agencyNameToPathPair)
    }

    /**
     * A contract call to launch the scannerCreationActivity and return the result which is a [Scanner.ScannerBasicInfo] object.
     * This object is added to the list of scanners from the view model
     * @see Scanner.ScannerBasicInfo
     * @see ScannerCreationContract
     */
    private val scannerBasicInfo = registerForActivityResult(ScannerCreationContract()) { info ->
        viewModel.addCreatedScannerToList(info)
        Log.i("Scanner Info", info.toString())
    }

    //Adds the newly created scannerInfo to the adapter list to add it ot the recyclerView
    private fun updateRecyclerView() = viewModel.listOfScanners.observe(viewLifecycleOwner) {
        it?.let { scannerBasicInfo ->
            adapter.submitList(scannerBasicInfo)
        }
    }

}