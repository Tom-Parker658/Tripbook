package com.lado.travago.tripbook.ui.agency.creation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyConfigCenterBinding
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyConfigCenterFragment : Fragment() {
    private lateinit var binding: FragmentAgencyConfigCenterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_config_center,
            container,
            false
        )
        onClickListeners()

        return binding.root
    }

    private fun onClickListeners() {
        /**
         * Launches for configuration of trips or journeys
         */
        binding.btnTrips.setOnClickListener {
            findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToTownsConfigFragment()
            )
        }
        binding.btnScanners.setOnClickListener {
            findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToScannerConfigFragment()
            )
        }
        binding.btnInfo.setOnClickListener {
            findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToAgencyCreationFragment()
            )
        }
        //TODO: For now, we navigate to the creation for bookers
        binding.btnDeleteAgency.setOnClickListener {
            startActivity(Intent(requireContext(), BookerCreationActivity::class.java))
        }
    }
}