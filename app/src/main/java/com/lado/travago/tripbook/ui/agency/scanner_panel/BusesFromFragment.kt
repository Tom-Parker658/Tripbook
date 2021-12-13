package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBusesFromBinding
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel.*
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.ScannerPanelViewModel
import com.lado.travago.tripbook.ui.recycler_adapters.BusOverviewAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.BusOverviewClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
class BusesFromFragment : Fragment() {
    private lateinit var parentViewModel: ScannerPanelViewModel
    private lateinit var viewModel: BusesManageViewModel
    private lateinit var adapter: BusOverviewAdapter
    private lateinit var binding: FragmentBusesFromBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_buses_from, container, false)
        parentViewModel = ViewModelProvider(requireActivity())[ScannerPanelViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[BusesManageViewModel::class.java]
        observeLiveData()


        handleFabVisibilities()
        handleClicks()
        return binding.root
    }

    private fun datePicker() {
        val titleText = "Select day"

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(titleText)//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        //Sets the value of the edit text to the formatted value of the selection
        datePicker.addOnPositiveButtonClickListener {
            viewModel.setField(FieldTags.DATE_IN_MILLIS_FROM, it)
        }

        datePicker.showNow(childFragmentManager, "")
    }

    private fun spanPicker() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.text_items_per_row)
            .setSingleChoiceItems(
                arrayOf("1", "2", "3", "4", "5", "6"),
                viewModel.spanSizeLocality.value!! - 1
            ) { dialogInterface, index ->
                viewModel.setField(FieldTags.SPAN_SIZE, index + 1)
                dialogInterface.cancel()
            }
            .create()
            .show()
    }

    private fun sortResults() {
        TODO("")
    }

    private fun handleClicks() {
        binding.fabToggleBusToolbox.setOnClickListener { viewModel.invertFabVisibility() }
        binding.fabBusSpanSize.setOnClickListener {
            spanPicker()
        }
        binding.fabDate.setOnClickListener { datePicker() }
    }

    /**
     * Configures the trips recycler and (re)-init recycler
     */
    private fun setSpanSize(spanSize: Int) {
        val recyclerManager = GridLayoutManager(context, spanSize)
        binding.recyclerBusOverview.layoutManager = recyclerManager
        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)

        if (this::adapter.isInitialized)
            binding.recyclerBusOverview.adapter = adapter

        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)
    }

    private fun handleFabVisibilities() {
        viewModel.fabVisibilityState.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabDate.show()
                binding.fabSortBus.show()
                binding.fabBusSpanSize.show()
                binding.fabToggleBusToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_off_24,
                        requireActivity().theme
                    )
                )
            } else {
                binding.fabDate.hide()
                binding.fabSortBus.hide()
                binding.fabBusSpanSize.hide()
                binding.fabToggleBusToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_24,
                        requireActivity().theme
                    )
                )
            }
        }
    }

    private fun observeLiveData() {
        viewModel.spanSizeLocality.observe(viewLifecycleOwner) {
            setSpanSize(it)
        }
        viewModel.notifyAdapterChanges.observe(viewLifecycleOwner) {
            if (it) {
                adapter = BusOverviewAdapter(
                    clickListener = BusOverviewClickListener { townName ->
                        //We set the the selected town name
                        viewModel.setField(FieldTags.SELECTED_TOWN_NAME, townName)
                        findNavController().navigate(
                            BusesFromFragmentDirections.actionBusesFromFragmentToBusesToFragment()
                        )
                    },
                    resources
                )
                val selectedDateOverviews =
                    viewModel.bookOverviewsOn(viewModel.formattedTravelDate())

                adapter.submitList(selectedDateOverviews)
                setSpanSize(viewModel.spanSizeLocality.value!!)
                viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, false)

                //
                adapter.notifyDataSetChanged()
            }
        }

//        viewModel.localityOverviews.observe(viewLifecycleOwner) {
//            adapter = BusOverviewAdapter(
//                clickListener = BusOverviewClickListener { townName ->
//                    //We set the the selected town name
//                    viewModel.setField(FieldTags.SELECTED_TOWN_NAME, townName)
//                    findNavController().navigate(
//                        BusesFromFragmentDirections.actionBusesFromFragmentToBusesToFragment()
//                    )
//                },
//                resources,
//                viewModel.formattedTravelDate()
//            )
//            adapter.notifyDataSetChanged()
//            adapter.submitList(it)
//        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.busProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.busProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        //When the scanner selects different date, we search for trips for that day and refresh data
        viewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.selectedDate.text = viewModel.formattedTravelDate()

            viewModel.getAllBooks(
                parentViewModel.bookerDoc.value!!.getString("agencyID")!!
            )
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it?.isNotBlank() == true) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
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
