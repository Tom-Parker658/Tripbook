package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.databinding.ItemSimpleRecyclerLayoutBinding
import com.lado.travago.tripbook.databinding.ItemTripPriceFormBinding
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel.FieldTags
import com.lado.travago.tripbook.ui.recyclerview.adapters.SimpleAdapter
import com.lado.travago.tripbook.ui.recyclerview.adapters.SimpleClickListener
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
        snapshotListener
        handleFabVisibilities()

        observeLiveData()
        handleClicks()
        return binding.root
    }

    private fun setup() {
        val tripArgs = TripsConfigFragmentArgs.fromBundle(requireArguments())
        viewModel.setField(FieldTags.TOWN_ID, tripArgs.townID)
        viewModel.setField(FieldTags.TOWN_NAME, tripArgs.townName)
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
            if (it) CoroutineScope(Dispatchers.Main).launch {
                viewModel.getOriginalTrips()
            }
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.tripsProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.tripsProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.currentTripsList.observe(viewLifecycleOwner) {
            //TODO: SHOULD NOT MODIFY VIEW_MODEL FROM FRAGMENT, change it as fast as possible
            viewModel.tripNamesList.clear()
            if (it.isNotEmpty()) {
                it.forEach { currentDoc ->
                    /**Donot touch*/
                    viewModel.doBackGroundJob(
                        it,
                        parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                    )
                    //We also set the names to be use in auto complete
                    viewModel.setField(
                        FieldTags.TRIP_NAME_LIST,
                        currentDoc["destination"].toString()
                    )
                    val toBeRemovedMap = viewModel.tripsSimpleInfoMap.find { map ->
                        map["id"] == currentDoc.id
                    }
                    if (toBeRemovedMap != null) {
                        viewModel.setField(
                            FieldTags.REMOVE_MAP,
                            toBeRemovedMap
                        )
                    }
                }
            }
        }

        /*viewModel.toDeleteIDList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                try {
                    adapter.notifyDataSetChanged()

                } catch (e: Exception) {
                    //NOTHING
                }
            }
        }*/

        /**
         * Looks for the position of the changed item and rebinds it
         */
        viewModel.onRebindItem.observe(viewLifecycleOwner) {
            if (it) {
                val tripMap = viewModel.currentTripsList.value!!.withIndex().find { map ->
                    map.value.id == viewModel.tripID
                }!!
                adapter.notifyItemChanged(tripMap.index)
                viewModel.setField(FieldTags.REBIND_ITEM, false)
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("TripsConfig", it)
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onNormalPriceForm.observe(viewLifecycleOwner) {
            if (it) NormalPriceDialogFragment(viewModel).showNow(
                childFragmentManager,
                "AppDialog"
            )
        }
        viewModel.onVipPriceForm.observe(viewLifecycleOwner) {
            if (it) VipPriceDialogFragment(viewModel).showNow(
                childFragmentManager,
                "AppDialog"
            )
        }
        viewModel.onClose.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(TripsConfigFragmentDirections.actionTripsConfigFragmentToTownsConfigFragment())
                viewModel.setField(FieldTags.ON_CLOSE, false)
                snapshotListener.remove()
            }
        }
        viewModel.onShowAddTrip.observe(viewLifecycleOwner) {
            if (it) AddTripsDialogFragment(viewModel, parentViewModel).showNow(
                childFragmentManager,
                "AppDialog"
            )
        }
        viewModel.startTripSearch.observe(viewLifecycleOwner) {
            if (it) {
                val searchBinding: ItemSearchFormBinding =
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_search_form,
                        null,
                        false
                    )
                searchBinding.searchBar.hint = " Destination Town: "
                searchBinding.searchBar.helperText = "Enter destination town name."

                //Sets adapter for the autocomplete text view
                val searchAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    viewModel.tripNamesList
                )

                val textView: AutoCompleteTextView =
                    searchBinding.searchBar.editText as AutoCompleteTextView
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
                                        FieldTags.TOAST_MESSAGE,
                                        "Not found. Select from dropdown"
                                    )
                                }
                            }
                        viewModel.setField(FieldTags.START_TRIP_SEARCH, false)
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    setOnCancelListener {
                        viewModel.setField(FieldTags.START_TRIP_SEARCH, false)
                    }
                    setOnDismissListener {
                        viewModel.setField(FieldTags.START_TRIP_SEARCH, false)
                    }
                }.create().apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
        } catch (e: Exception) {
            Log.d("TripsConfigFragment", e.message.toString())
            //TODO: Truly handle this error please future Lado
        }
    }

    private fun handleFabVisibilities() {
        // Toggle visibilities fab
        binding.fabToggleTripsToolbox.setOnClickListener { toggleFab ->
            binding.fabAddTrips.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSearchTrip.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSortTrips.let {
                if (it.isShown) {
                    it.hide()
                    (toggleFab as FloatingActionButton).setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.baseline_visibility_24,
                            requireActivity().theme
                        )
                    )
                } else {
                    (toggleFab as FloatingActionButton).setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.baseline_visibility_off_24,
                            requireActivity().theme
                        )
                    )
                    it.show()
                }
            }
            binding.btnTripSave.let {
                if (it.isShown) it.visibility = View.GONE else it.visibility = View.VISIBLE
            }
        }
    }

    private fun handleClicks() {
        binding.fabSearchTrip.setOnClickListener {
            viewModel.setField(FieldTags.START_TRIP_SEARCH, true)
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
                            viewModel.setField(FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.TRIP_PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 2)
                        }
                        3 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.TRIP_VIP_PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 3)
                        }
                        4 -> {
                            viewModel.sortTripsResult(TripsConfigViewModel.SortTags.DISTANCE)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 4)
                        }
                    }
                    adapter.submitList(viewModel.currentTripsList.value)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

            }.create().show()
        }
        binding.fabRemoveTripSelection.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.commitToDeleteList(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
            binding.fabRemoveTripSelection.hide()
        }
        binding.fabAddTrips.setOnClickListener {
            viewModel.setField(FieldTags.SHOW_ADD_TRIP, true)
        }
        binding.btnTripSave.setOnClickListener {
            snapshotListener.remove()
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.commitAllChangesToDataBase(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
        }
    }

    /* Add a snapshot listener to the trips collection */
    private val snapshotListener
        get() =
            viewModel.firestoreRepo.db.collection(
                "/OnlineTransportAgency/${
                    parentViewModel.bookerDoc.value!!.getString("agencyID")
                }/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency"
            ).whereArrayContains("townIDs", viewModel.townID)
                //We get all trips to and from the selected location
                .addSnapshotListener(requireActivity()) { snapshot, error ->
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            adapter = TripsConfigAdapter(
                                clickListener = TripsClickListener { tripID, buttonTag ->
                                    viewModel.setField(FieldTags.TRIP_ID, tripID)
                                    when (buttonTag) {//Remove
                                        TripsConfigViewModel.TripButtonTags.TRIP_CHECK_TO_DELETE -> {
                                            viewModel.removeTrip(tripID)
                                            //This to rightly inflate the close button
                                            viewModel.setField(FieldTags.REBIND_ITEM, true)
                                            viewModel.setField(FieldTags.REBIND_ITEM, false)
                                            //To show or hide the delete town fab
                                            if (viewModel.toDeleteIDList.value!!.isNotEmpty()) binding.fabRemoveTripSelection.show()
                                            else binding.fabRemoveTripSelection.hide()
                                        }
                                        TripsConfigViewModel.TripButtonTags.TRIPS_BUTTON_NORMAL_PRICE -> {
                                            viewModel.setField(
                                                FieldTags.ON_NORMAL_PRICE_FORM,
                                                true
                                            )
                                        }
                                        TripsConfigViewModel.TripButtonTags.TRIPS_BUTTON_VIP_PRICE -> {
                                            viewModel.setField(
                                                FieldTags.ON_VIP_PRICE_FORM,
                                                true
                                            )
                                        }
                                        TripsConfigViewModel.TripButtonTags.TRIPS_CHECK_VIP -> {
                                            viewModel.exemptVIP(tripID)
                                            viewModel.setField(FieldTags.REBIND_ITEM, true)
                                        }
                                    }
                                },
                                toDeleteIDList = viewModel.toDeleteIDList.value!!,
                                changesMapList = viewModel.localChangesMapList.value!!,
                                currentTownName = viewModel.townName
                            )
                            viewModel.setField(
                                FieldTags.CURRENT_TRIPS,
                                snapshot.documents
                            )
                            //We transform all documents to change Map
//                    viewModel.convertToNewChangeMap(snapshot)

                            adapter.submitList(snapshot.documents)
                            setRecycler()
                        } else {
                            try {
                                adapter.notifyDataSetChanged()
                            } catch (e: Exception) {/*Just incase we cleared all the list of items*/
                            }
                        }
                    }
                    error?.handleError { }
                }

    class AddTripsDialogFragment(
        private val viewModel: TripsConfigViewModel,
        private val parentViewModel: AgencyConfigViewModel
    ) : DialogFragment() {
        @SuppressLint("DialogFragmentCallbacksDetector")
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            super.onCreateDialog(savedInstanceState)
            val recyclerBinding: ItemSimpleRecyclerLayoutBinding =
                DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_simple_recycler_layout,
                    null,
                    true
                )
            var simpleAdapter: SimpleAdapter? = null
            simpleAdapter = SimpleAdapter(
                clickListener = SimpleClickListener { tripID ->
                    viewModel.addTrip(tripID)
                    val index =
                        viewModel.tripsSimpleInfoMap.withIndex()
                            .find { it.value["id"] == tripID }!!.index
                    simpleAdapter?.notifyItemChanged(index)
                },
                viewModel.toAddIDList.value!!
            )

            val recyclerManager = LinearLayoutManager(requireContext())
            recyclerBinding.recyclerView.layoutManager = recyclerManager
            recyclerBinding.recyclerView.adapter = simpleAdapter
            viewModel.tripsSimpleInfoMap.sortBy {
                it["name"]
            }
            simpleAdapter.submitList(
                viewModel.tripsSimpleInfoMap
            )
            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setTitle("Trips From ${viewModel.townName} To: ")
                .setIcon(R.drawable.baseline_add_24)
                .setView(recyclerBinding.root)
                .setPositiveButton("Add Selected Trips") { dialog, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.commitToAddList(
                            parentViewModel.bookerDoc.value!!.getString(
                                "agencyID"
                            )!!
                        )
                    }
                    dialog.dismiss()
                    dialog.cancel()
                }
                .setOnCancelListener {
                    viewModel.setField(FieldTags.SHOW_ADD_TRIP, false)
                }
                .setOnDismissListener {
                    viewModel.setField(FieldTags.SHOW_ADD_TRIP, false)
                }
                .create()
        }

    }

    class NormalPriceDialogFragment(val viewModel: TripsConfigViewModel) :
        DialogFragment() {
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
                        FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setOnCancelListener {
                    viewModel.setField(
                        FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setOnDismissListener {
                    viewModel.setField(
                        FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    val price =
                        if (priceBinding.price.editText!!.text.toString().isBlank()) 0L
                        else priceBinding.price.editText!!.text.toString().toLong()

                    viewModel.changeNormalPrice(viewModel.tripID, price)
                    dialog.cancel()
                    viewModel.setField(
                        FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                    viewModel.setField(FieldTags.REBIND_ITEM, true)
                }
                .create()
        }
    }

    class VipPriceDialogFragment(val viewModel: TripsConfigViewModel) :
        DialogFragment() {
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
                    viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setOnCancelListener {
                    viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setOnDismissListener {
                    viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    val price =
                        if (priceBinding.price.editText!!.text.toString().isBlank()) 0L
                        else priceBinding.price.editText!!.text.toString().toLong()

                    viewModel.changeVIPPrice(viewModel.tripID, price)

                    dialog.cancel()
                    viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
                    //RBind the current item
                    viewModel.setField(FieldTags.REBIND_ITEM, true)
                }
                .create()
        }
    }

}
