package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.databinding.ItemTripPriceFormBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.ui.recyclerview.adapters.TripsClickListener
import com.lado.travago.tripbook.ui.recyclerview.adapters.TripsConfigAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * Used to configure the journeys of a specific town for an agency
 */
@ExperimentalCoroutinesApi
class TripsConfigFragment : Fragment() {
    private lateinit var binding: FragmentTripsConfigBinding
    private lateinit var viewModel: TripsConfigViewModel
    private lateinit var adapter: TripsConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_trips_config,
            container,
            false
        )
        viewModel = ViewModelProvider(this)[TripsConfigViewModel::class.java]
        setup()
        observeLiveData()
        try {
            setRecycler()
        } catch (e: Exception) {
            Log.d("Trips", "${e.message}")
        }
        return binding.root
    }

    private fun setup() {
        viewModel.setField(TripsConfigViewModel.FieldTag.TOWN_ID, requireArguments()["townID"]!!)
        viewModel.setField(
            TripsConfigViewModel.FieldTag.TOWN_NAME,
            requireArguments()["townName"]!!
        )
        binding.textMasterLabel.text = "From ${viewModel.townName} to: "
    }

    /**
     * Configures the towns recycler
     */
    private fun setRecycler() {
        val recyclerManager = GridLayoutManager(context, 2)
        binding.recyclerTrips.layoutManager = recyclerManager
        binding.recyclerTrips.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.retryTrips.observe(viewLifecycleOwner) {
            if (it) CoroutineScope(Dispatchers.Main).launch { viewModel.getTrips() }
        }

        //Submit list to inflate recycler view
        viewModel.tripDocList.observe(viewLifecycleOwner) {
            adapter = TripsConfigAdapter(
                exemptedTripsList = viewModel.exemptedTripList,
                clickListener = TripsClickListener { tripId, buttonTag ->
                    when (buttonTag) {//Remove or add a town from the exemption list
                        TripsConfigViewModel.TripButtonTags.TRIPS_BUTTON_PRICES -> {
                            viewModel.setField(TripsConfigViewModel.FieldTag.ON_PRICE_FORM, true)
                            viewModel.setField(TripsConfigViewModel.FieldTag.TRIP_ID, tripId)
                        }
                        TripsConfigViewModel.TripButtonTags.TRIPS_SWITCH_ACTIVATE -> {
                            viewModel.exemptTrip(tripId)
                        }
                        TripsConfigViewModel.TripButtonTags.TRIPS_CHECK_VIP -> {
//                                viewModel.exemptVIP(tripId)
//                                tripsAdapter.submitList(viewModel.tripDocList.value!!)
                        }
                    }
                },
                pricePerKM = viewModel.pricePerKM,
                vipPricePerKM = viewModel.vipPricePerKM,
                optionMapList = viewModel.optionMapList
            )
            setRecycler()
            adapter.submitList(it)
        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.tripsProgressBar.visibility = View.VISIBLE
            else binding.tripsProgressBar.visibility = View.GONE
        }
        binding.fabSearchTown.setOnClickListener {
            viewModel.setField(TripsConfigViewModel.FieldTag.START_TRIP_SEARCH, true)
        }
        viewModel.startTripSearch.observe(viewLifecycleOwner) {
            if (it) {
                val searchBinding: ItemSearchFormBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_search_form, null, false)
                searchBinding.searchBar.hint = " Destination Town: "
                searchBinding.searchBar.helperText = "Enter destination town name."

                //Sets adapter for the autocomplete text view
                val searchAdapter = android.widget.ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    viewModel.tripNameList
                )

                val textView =
                    searchBinding.searchBar.editText as android.widget.AutoCompleteTextView
                textView.setAdapter(searchAdapter)

                MaterialAlertDialogBuilder(requireContext()).apply {
                    setIcon(R.drawable.baseline_search_24)
                    setTitle("Search")
                    setView(searchBinding.root)
                    setPositiveButton("SEARCH") { dialog, _ ->
                        viewModel.searchTrip(searchBinding.searchBar.editText!!.text.toString())
                            .let { index ->
                                if (index != -1) {
                                    binding.recyclerTrips.smoothScrollToPosition(index)
                                } else {//If not found
                                    viewModel.setField(
                                        TripsConfigViewModel.FieldTag.TOAST_MESSAGE,
                                        "Not found. Select from dropdown"
                                    )
                                }
                            }
                        viewModel.setField(TripsConfigViewModel.FieldTag.START_TRIP_SEARCH, false)
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    setOnCancelListener {
                        viewModel.setField(TripsConfigViewModel.FieldTag.START_TRIP_SEARCH, false)
                    }
                    setOnDismissListener {
                        viewModel.setField(TripsConfigViewModel.FieldTag.START_TRIP_SEARCH, false)
                    }
                }.create().apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }.show()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("TripsConfig", it)
                viewModel.setField(TripsConfigViewModel.FieldTag.TOAST_MESSAGE, "")
            }
        }
        viewModel.onPriceForm.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.setField(TripsConfigViewModel.FieldTag.TOAST_MESSAGE, "INFLATION")
                AppDialogFragment(viewModel).showNow(childFragmentManager, "AppDialog")
            }
        }

    }


    class AppDialogFragment(val viewModel: TripsConfigViewModel) : DialogFragment() {
        @SuppressLint("DialogFragmentCallbacksDetector")
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            val priceBinding: ItemTripPriceFormBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_trip_price_form,
                null,
                true
            )

            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setTitle("PRICES")
                .setView(priceBinding.root)
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.cancel()
                    dialog.dismiss()
                    viewModel.setField(TripsConfigViewModel.FieldTag.ON_PRICE_FORM, false)
                }
                .setOnCancelListener {
                    viewModel.setField(TripsConfigViewModel.FieldTag.ON_PRICE_FORM, false)
                }
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    viewModel.setField(
                        TripsConfigViewModel.FieldTag.NORMAL_PRICE,
                        priceBinding.priceNormal.editText!!.toString()
                    )
                    viewModel.setField(
                        TripsConfigViewModel.FieldTag.VIP_PRICE,
                        priceBinding.priceVip.editText!!.toString()
                    )
                    dialog.cancel()
                    viewModel.setField(TripsConfigViewModel.FieldTag.ON_PRICE_FORM, false)
                }
                .create()
        }
    }

}