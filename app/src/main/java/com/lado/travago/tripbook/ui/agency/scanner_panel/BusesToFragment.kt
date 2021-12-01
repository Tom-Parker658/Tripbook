package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBusesToBinding
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel.*
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.ScannerPanelViewModel
import com.lado.travago.tripbook.ui.recycler_adapters.BusOverviewAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.BusOverviewClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [BusesToFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@ExperimentalCoroutinesApi
class BusesToFragment : Fragment() {
    private lateinit var viewModel: BusesManageViewModel
    private lateinit var parentViewModel: ScannerPanelViewModel
    private lateinit var binding: FragmentBusesToBinding
    private lateinit var adapter: BusOverviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_buses_to,
            container,
            false
        )
        parentViewModel = ViewModelProvider(requireActivity())[ScannerPanelViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[BusesManageViewModel::class.java]

        //Header and tile date
        val headerText =
            "${getString(R.string.text_dialog_title_from)}: ${viewModel.selectedLocality}"
        binding.headerBusTo.text = headerText
        binding.textSelectedDate.text = viewModel.formattedTravelDate()

        handleClicks()
        observeLiveData()
        handleFabVisibilities()

        return binding.root
    }

    private fun spanPicker() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.text_dialog_title_span_size)
            .setSingleChoiceItems(
                arrayOf("1", "2", "3", "4", "5", "6"),
                viewModel.spanSizeDestination.value!! - 1
            ) { dialogInterface, index ->
                viewModel.setField(FieldTags.SPAN_SIZE_DESTINATION, index + 1)
                dialogInterface.cancel()
            }
            .create()
            .show()
    }

    private fun sortResults() {
        TODO("")
    }

    private fun handleClicks() {
        binding.fabToggleBusToToolbox.setOnClickListener { viewModel.invertFabVisibility() }
        binding.fabBusToSpanSize.setOnClickListener { spanPicker() }
    }

    private fun observeLiveData() {
        viewModel.spanSizeDestination.observe(viewLifecycleOwner) {
            setSpanSize(it)
        }

        viewModel.localityOverviews.observe(viewLifecycleOwner) {
            viewModel.onLocalitySelected()
        }

        viewModel.destinationOverviews.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                adapter = BusOverviewAdapter(
                    clickListener = BusOverviewClickListener {
                        viewModel.setField(FieldTags.SELECTED_DESTINATION, it)
                        findNavController().navigate(BusesToFragmentDirections.actionBusesToFragmentToFragmentTicketScanPanel())
                    },
                    resources
                )
                adapter.submitList(list)
                setSpanSize(viewModel.spanSizeDestination.value!!)
            }
        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBusToBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBusToBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

    }

    /**
     * Configures the trips recycler and (re)-init recycler
     */
    private fun setSpanSize(spanSize: Int) {
        val recyclerManager = GridLayoutManager(context, spanSize)
        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)

        if (this::adapter.isInitialized)
            binding.recyclerBusToOverview.adapter = adapter

        binding.recyclerBusToOverview.layoutManager = recyclerManager
        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, false)
    }

    private fun handleFabVisibilities() {
        viewModel.fabVisibilityState.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabSortBusTo.show()
                binding.fabToggleBusToToolbox.show()
                binding.fabToggleBusToToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_off_24,
                        requireActivity().theme
                    )
                )
            } else {
                binding.fabSortBusTo.hide()
                binding.fabBusToSpanSize.hide()
                binding.fabToggleBusToToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_24,
                        requireActivity().theme
                    )
                )
            }
        }
    }

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        try {
            viewModel.registrationObject.remove()
        } catch (e: Exception) {
            //Not initialized
        }
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDetach()
    }

}