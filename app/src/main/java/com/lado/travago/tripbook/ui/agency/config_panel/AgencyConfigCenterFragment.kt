package com.lado.travago.tripbook.ui.agency.config_panel

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyConfigCenterBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.scanner_panel.ScannerPanelActivity
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemClickListener
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.*
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyConfigCenterFragment : Fragment() {
    private lateinit var binding: FragmentAgencyConfigCenterBinding
    private var isReady by Delegates.notNull<Boolean>()
    private lateinit var uiUtils: UIUtils

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
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        uiUtils = UIUtils(this, requireActivity(), viewLifecycleOwner)
        initGoOnlineButton()
        initSettingsRecycler()

        return binding.root
    }

    private fun initGoOnlineButton() {
        when {
            parentViewModel.agencyDoc.value!!.getBoolean("hasOngoingEvent")!! -> {
                parentViewModel.agencyDoc.value?.run {
                    isReady =
                        getBoolean("hasScanners")!! && getBoolean("hasSTrips")!! && getBoolean("hasEvents")!! && getBoolean(
                            "hasTakeOffPeriods")!! && getBoolean("hasConfiguredPayments")!!
                }

                if (!isReady) {
                    (binding.btnUploadAgency as MaterialButton).apply {
                        strokeColor =
                            ColorStateList.valueOf(resources.getColor(R.color.quantum_grey))
                        setTextColor(resources.getColor(R.color.quantum_grey))
                        iconTint = ColorStateList.valueOf(resources.getColor(R.color.quantum_grey))
                    }
                    binding.btnUploadAgency.setOnClickListener {
                        Toast.makeText(requireContext(),
                            getString(R.string.text_message_agency_not_ready_go_online),
                            Toast.LENGTH_LONG).show()
                    }
                } else
                    binding.btnUploadAgency.setOnClickListener {
                        uiUtils.dialog(
                            getString(R.string.text_go_online),
                            R.drawable.outline_info_24,
                            getString(R.string.text_message_agency_ready_go_online),
                            getString(R.string.text_continue),
                            null,
                            getString(R.string.text_cancel),
                            onPositiveListener = { dialog, _ ->
                                //TODO: Get online
                                dialog.dismiss()
                            },

                            null,
                            onNeutralListener = { dialog, _ ->
                                dialog.dismiss()
                            },
                        )

                    }
            }

            else -> binding.btnUploadAgency.visibility = View.GONE
        }

    }


    private fun initSettingsRecycler() {
        val optionsList = SummaryItem.adminAgencyConfigOptions(resources,
            parentViewModel.scannerDoc.value!!,
            parentViewModel.agencyDoc.value!!
        )

        SummaryItemAdapter(
            SummaryItemClickListener {
                handleNavClicks(it)
            }
        ).apply {
            this.submitList(
                optionsList
            )

            val linearLayoutManager = LinearLayoutManager(requireContext())
            binding.recyclerConfigAdmin.layoutManager = linearLayoutManager
            binding.recyclerConfigAdmin.adapter = this
        }

    }

    private fun handleNavClicks(it: SummaryItem) {
        when (it.id) {
            // Agency Profile
            SummaryItem.ITEM_AGENCY_PROFILE_ID -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToAgencyProfileFragment()
            )
            // Trips
            SummaryItem.ITEM_AGENCY_TRIPS_ID -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToTownsConfigFragment()
            )
            // Money
            SummaryItem.ITEM_MONEY_ID -> {
                //TODO: Payment Modules
            }
            // Scanners
            SummaryItem.ITEM_SCANNERS_ID -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToScannerConfigFragment()
            )
            //Events
            SummaryItem.ITEM_PLAN_EVENTS_ID -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToAgencyEventPlannerFragment()
            )
            //Intervals
            SummaryItem.ITEM_TAKE_OFF_TIME_ID -> findNavController().navigate(
                AgencyConfigCenterFragmentDirections.actionAgencyConfigCenterFragmentToTripDepartureTimeConfigFragment()
            )
            //All the agencies books
            SummaryItem.ITEM_SCAN_BOOKS_ID -> startActivity(
                Intent(
                    requireActivity(),
                    ScannerPanelActivity::class.java
                )
            )
        }
    }
}