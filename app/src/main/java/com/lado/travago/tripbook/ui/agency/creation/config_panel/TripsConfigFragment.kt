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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.databinding.ItemTripPriceFormBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.AgencyConfigViewModel
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
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentTripsConfigBinding
    private lateinit var viewModel: TripsConfigViewModel
    private lateinit var adapter: TripsConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
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
        handleButtonClick()
        try {
            setRecycler()
        } catch (e: Exception) {
            Log.d("Trips", "${e.message}")
        }
        return binding.root
    }

    private fun setup() {
        val tripArgs = TripsConfigFragmentArgs.fromBundle(requireArguments())
        viewModel.setField(TripsConfigViewModel.FieldTags.TOWN_ID, tripArgs.townID)
        viewModel.setField(TripsConfigViewModel.FieldTags.TOWN_NAME, tripArgs.townName)
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
            if (it) CoroutineScope(Dispatchers.Main).launch { viewModel.getTrips(parentViewModel.bookerDoc.value!!.getString("agencyID")!!) }
        }

        //Submit list to inflate recycler view
        viewModel.tripDocList.observe(viewLifecycleOwner) {
            adapter = TripsConfigAdapter(
                clickListener = TripsClickListener { tripId, buttonTag ->
                    viewModel.setField(TripsConfigViewModel.FieldTags.TRIP_ID, tripId)
                    when (buttonTag) {//Remove or add a town from the exemption list
                        TripsConfigViewModel.TripButtonTags.TRIPS_BUTTON_NORMAL_PRICE -> {
                            viewModel.setField(
                                TripsConfigViewModel.FieldTags.ON_NORMAL_PRICE_FORM,
                                true
                            )
                        }
                        TripsConfigViewModel.TripButtonTags.TRIPS_BUTTON_VIP_PRICE -> {
                            viewModel.setField(
                                TripsConfigViewModel.FieldTags.ON_VIP_PRICE_FORM,
                                true
                            )
                        }
                        TripsConfigViewModel.TripButtonTags.TRIPS_SWITCH_ACTIVATE -> {
                            viewModel.exemptTrip(tripId)
                        }
                        TripsConfigViewModel.TripButtonTags.TRIPS_CHECK_VIP -> {
                            viewModel.exemptVIP(tripId)
                            viewModel.setField(TripsConfigViewModel.FieldTags.REBIND_ITEM, true)
                        }
                    }
                },
                pricePerKM = viewModel.pricePerKM,
                vipPricePerKM = viewModel.vipPricePerKM,
                priceChangesMap = viewModel.tripChangesMapList
            )
            setRecycler()
            adapter.submitList(it)
            binding.fabSearchTown.visibility = View.VISIBLE
        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.tripsProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                binding.tripsProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
        binding.fabSearchTown.setOnClickListener {
            viewModel.setField(TripsConfigViewModel.FieldTags.START_TRIP_SEARCH, true)
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
                            ?.let { index ->
                                if (index != -1) {
                                    binding.recyclerTrips.smoothScrollToPosition(index)
                                } else {//If not found
                                    viewModel.setField(
                                        TripsConfigViewModel.FieldTags.TOAST_MESSAGE,
                                        "Not found. Select from dropdown"
                                    )
                                }
                            }
                        viewModel.setField(TripsConfigViewModel.FieldTags.START_TRIP_SEARCH, false)
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    setOnCancelListener {
                        viewModel.setField(TripsConfigViewModel.FieldTags.START_TRIP_SEARCH, false)
                    }
                    setOnDismissListener {
                        viewModel.setField(TripsConfigViewModel.FieldTags.START_TRIP_SEARCH, false)
                    }
                }.create().apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }.show()
            }
        }
        /**
         * Looks for the position of the changed item
         */
        viewModel.onRebindItem.observe(viewLifecycleOwner) {
            if (it) {
                val tripDoc = viewModel.tripDocList.value!!.find { doc ->
                    doc.id == viewModel.tripID
                }
                adapter.notifyItemChanged(viewModel.tripDocList.value!!.indexOf(tripDoc!!))
                viewModel.setField(TripsConfigViewModel.FieldTags.REBIND_ITEM, false)
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("TripsConfig", it)
                viewModel.setField(TripsConfigViewModel.FieldTags.TOAST_MESSAGE, "")
            }
        }

        viewModel.onNormalPriceForm.observe(viewLifecycleOwner) {
            if (it) NormalPriceDialogFragment(viewModel).showNow(
                childFragmentManager,
                "AppDialog"
            )
        }

        viewModel.onVipPriceForm.observe(viewLifecycleOwner) {
            if (it) VipPriceDialogFragment(viewModel).showNow(childFragmentManager, "AppDialog")
        }

        viewModel.onClose.observe(viewLifecycleOwner){
            if(it) {
                findNavController().navigate(TripsConfigFragmentDirections.actionTripsConfigFragmentToTownsConfigFragment())
                viewModel.setField(TripsConfigViewModel.FieldTags.ON_CLOSE, false)
            }
        }
    }

    private fun handleButtonClick(){
        binding.btnTripSave.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch{
                viewModel.uploadTripChanges(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
        }
        binding.fabSortTrips.setOnClickListener {
            //1-We create a spinner with options
            MaterialAlertDialogBuilder(requireContext()).apply {
                setIcon(R.drawable.baseline_sort_24)
                setTitle("Sort By?")
                setSingleChoiceItems(
                    arrayOf("None", "Name", "Price", "VIP Price", "distance"),
                    viewModel.sortCheckedItem
                ) { dialog, which ->
                    when (which) {
                        1 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.TRIP_NAMES)
                            viewModel.setField(TripsConfigViewModel.FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.TRIP_PRICES)
                            viewModel.setField(TripsConfigViewModel.FieldTags.CHECKED_ITEM, 2)
                        }
                        3 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.TRIP_VIP_PRICES)
                            viewModel.setField(TripsConfigViewModel.FieldTags.CHECKED_ITEM, 3)
                        }
                        4 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.DISTANCE)
                            viewModel.setField(TripsConfigViewModel.FieldTags.CHECKED_ITEM, 4)
                        }
                    }
                    dialog.dismiss()
                    adapter.notifyDataSetChanged()
                }

            }.create().show()
        }
    }

    class NormalPriceDialogFragment(val viewModel: TripsConfigViewModel) : DialogFragment() {
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
                .setTitle("Normal Price")
                .setView(priceBinding.root)
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.cancel()
                    dialog.dismiss()
                    viewModel.setField(
                        TripsConfigViewModel.FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setOnCancelListener {
                    viewModel.setField(
                        TripsConfigViewModel.FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    val price = if (priceBinding.price.editText!!.text.toString().isBlank()) 0L
                    else priceBinding.price.editText!!.text.toString().toLong()

                    viewModel.changeNormalPrice(viewModel.tripID, price)
                    dialog.cancel()
                    viewModel.setField(
                        TripsConfigViewModel.FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                    viewModel.setField(TripsConfigViewModel.FieldTags.REBIND_ITEM, true)
                }
                .create()
        }
    }

    class VipPriceDialogFragment(val viewModel: TripsConfigViewModel) : DialogFragment() {
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
                .setTitle("VIP Price")
                .setView(priceBinding.root)
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.cancel()
                    dialog.dismiss()
                    viewModel.setField(TripsConfigViewModel.FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setOnCancelListener {
                    viewModel.setField(TripsConfigViewModel.FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    val price = if (priceBinding.price.editText!!.text.toString().isBlank()) 0L
                    else priceBinding.price.editText!!.text.toString().toLong()

                    viewModel.changeVIPPrice(viewModel.tripID, price)
                    dialog.cancel()
                    viewModel.setField(TripsConfigViewModel.FieldTags.ON_VIP_PRICE_FORM, false)
                    //RBind the current item
                    viewModel.setField(TripsConfigViewModel.FieldTags.REBIND_ITEM, true)
                }
                .create()
        }
    }

}