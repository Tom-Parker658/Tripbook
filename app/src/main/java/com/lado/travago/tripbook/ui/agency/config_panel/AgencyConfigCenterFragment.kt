package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyConfigCenterBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemClickListener
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyConfigCenterFragment : Fragment() {
    private lateinit var binding: FragmentAgencyConfigCenterBinding
//    TODO: TEST COMMENTING
    private lateinit var parentViewModel: AgencyConfigViewModel

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
        initSettingsRecycler()

        return binding.root
    }

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

    private fun initSettingsRecycler() {
        val adminList = SummaryItem.getAdminScannerItems(resources)

        SummaryItemAdapter(
            SummaryItemClickListener {
                handleNavClicks(it)
            }
        ).apply {
            this.submitList(
                adminList
            )
            //TODO: TEST:COMMENTING
//            this.submitList(
//                if (parentViewModel.scannerDoc.value!!.getBoolean("isAdmin")!!)
//                    SummaryItem.adminScannerItems
//                else SummaryItem.adminScannerItems //TODO: Urgent: Change it with Scanner related panels
//            )
            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.recyclerConfigAdmin.layoutManager = linearLayoutManager
            binding.recyclerConfigAdmin.adapter = this
        }


    }

    private fun handleNavClicks(it: SummaryItem) {
        when (it.mainTitle) {
            // Trips
            getString(R.string.text_label_agency_config_trips) -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToTownsConfigFragment()
            )
            // Agency Profile
            getString(R.string.text_label_agency_config_profile) -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToAgencyCreationFragment()
            )
            // Money
            getString(R.string.text_label_agency_config_money) -> {
                //TODO: Payment Modules
            }
            // Scanners
            getString(R.string.text_label_agency_config_scanners) -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToScannerConfigFragment()
            )
            //Events
            getString(R.string.text_label_agency_config_events_planner) -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToAgencyEventPlannerFragment()
            )
            //Intervals
            getString(R.string.text_label_agency_config_intervals) -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToTripDepartureTimeConfigFragment()
            )
        }
    }
}