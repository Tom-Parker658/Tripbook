package com.lado.travago.tripbook.ui.agency.config_panel

import android.app.Dialog
import android.content.DialogInterface
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
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsConfigViewModel.*
import com.lado.travago.tripbook.ui.recycler_adapters.SimpleAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SimpleClickListener
import com.lado.travago.tripbook.ui.recycler_adapters.TripsClickListener
import com.lado.travago.tripbook.ui.recycler_adapters.TripsConfigAdapter
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
        binding.textMasterLabel.text = "From ${viewModel.currentTownName} to: "
    }

    /**
     * Configures the towns recycler
     */
    private fun initRecycler(spanCount: Int) {
        val recyclerManager = GridLayoutManager(context, spanCount)
        binding.recyclerTrips.layoutManager = recyclerManager
        if (this::adapter.isInitialized) binding.recyclerTrips.adapter = adapter
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
            viewModel.tripNamesListClear()
            if (it.isNotEmpty()) {
                it.forEach { currentDoc ->
                    /**
                     * We get the town names which are not the current town name
                     */
                    val townNamesMap = (currentDoc["townNames"] as Map<String, String>)
                    val otherTownName =
                        if (townNamesMap["town1"] == viewModel.currentTownName) townNamesMap["town2"].toString()
                        else townNamesMap["town1"].toString()

                    viewModel.setField(
                        FieldTags.TRIP_NAME_LIST,
                        otherTownName
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
        viewModel.toDeleteIDList.observe(viewLifecycleOwner) {
            if (this::adapter.isInitialized) adapter.notifyDataSetChanged()
        }


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
                searchBinding.searchBar.hint = getString(R.string.text_label_town)

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
                    setTitle(R.string.text_dialog_search)
                    setView(searchBinding.root)
                    setPositiveButton(R.string.text_dialog_search) { dialog, _ ->
                        viewModel.searchTrip(searchBinding.searchBar.editText!!.text.toString())
                            ?.let { index ->
                                if (index != -1) {
                                    binding.recyclerTrips.smoothScrollToPosition(index)
                                    if (this@TripsConfigFragment::adapter.isInitialized) adapter.notifyItemChanged(
                                        index
                                    )
                                } else {//If not found
                                    viewModel.setField(
                                        FieldTags.TOAST_MESSAGE,
                                        getString(R.string.text_message_not_found_drop_down)
                                    )
                                }
                            }
                        viewModel.setField(FieldTags.START_TRIP_SEARCH, false)
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
        viewModel.spanSize.observe(viewLifecycleOwner) {
            if (this::adapter.isInitialized) initRecycler(it)
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
            binding.fabTripSpanSize.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSearchTrip.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabTripSpanSize.let {
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
                    arrayOf(
                        getString(R.string.text_sort_by_none),
                        getString(R.string.text_sort_town_by_name),
                        getString(R.string.text_sort_trip_by_price),
                        getString(R.string.text_sort_trip_by_vip_price),
                        getString(R.string.text_sort_trip_by_distance)
                    ),
                    viewModel.sortCheckedItem
                ) { dialog, which ->
                    when (which) {
                        1 -> {
                            viewModel.sortTripsResult(SortTags.TRIP_NAMES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortTripsResult(SortTags.TRIP_PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 2)
                        }
                        3 -> {
                            viewModel.sortTripsResult(SortTags.TRIP_VIP_PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 3)
                        }
                        4 -> {
                            viewModel.sortTripsResult(SortTags.DISTANCE)
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
        binding.fabTripSpanSize.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.text_dialog_title_span_size)
                .setSingleChoiceItems(
                    arrayOf("1", "2", "3", "4", "5", "6"),
                    viewModel.spanSize.value!!
                ) { dialogInterface, index ->
                    viewModel.setField(FieldTags.SPAN_SIZE, index + 1)
                    dialogInterface.cancel()
                }
        }
    }
/*

     // Special function to configure the types of buses

    private fun showBusTypesDialog(tripID: String) {
        val tripDoc = viewModel.currentTripsList.value!!.find {
            it.id == tripID
        }!!
        val existingTripMap = viewModel.localChangesMapList.value!!.withIndex().find {
            it.value["tripID"] == tripID
        }
        val booleanArray = BooleanArray(3)
        if (existingTripMap != null) {
            booleanArray[0] =
                (existingTripMap.value["busTypes"] as Map<String, Boolean>)["seaterSeventy"]!!
            booleanArray[1] =
                (existingTripMap.value["busTypes"] as Map<String, Boolean>)["seaterCoaster"]!!
            booleanArray[2] =
                (existingTripMap.value["busTypes"] as Map<String, Boolean>)["seaterNormal"]!!

        } else {
            booleanArray[0] = (tripDoc["busTypes"] as Map<String, Boolean>)["seaterSeventy"]!!
            booleanArray[1] = (tripDoc["busTypes"] as Map<String, Boolean>)["seaterCoaster"]!!
            booleanArray[2] = (tripDoc["busTypes"] as Map<String, Boolean>)["seaterNormal"]!!
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Types of Bus")
            .setMessage("Choose the different types of bus which can be used by bookers for this trip.")
            .setMultiChoiceItems(
                arrayOf("70 Seater Bus", "Coaster Bus", "Normal Bus"),
                booleanArray
            ) { _: DialogInterface, index: Int, value: Boolean ->
                when (index) {
                    0 -> viewModel.configBusTypes(tripID, BusTypes.SEATER_SEVENTY, value)
                    1 -> viewModel.configBusTypes(tripID, BusTypes.SEATER_COASTER, value)
                    2 -> viewModel.configBusTypes(tripID, BusTypes.SEATER_NORMAL, value)
                }
            }
            .setIcon(R.drawable.baseline_directions_bus_24)
            .setNeutralButton("Done") { dialogInterface, _ ->
                dialogInterface.dismiss()
                dialogInterface.cancel()
                //We re-bind object to show
                viewModel.setField(FieldTags.REBIND_ITEM, true)
            }
            .setOnDismissListener {
                viewModel.setField(FieldTags.REBIND_ITEM, true)
            }
            .setOnCancelListener {
                viewModel.setField(FieldTags.REBIND_ITEM, true)
            }
    }
    */


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
                                        TripButtonTags.TRIP_CHECK_TO_DELETE -> {
                                            viewModel.removeTrip(tripID)
                                            //This to rightly inflate the close button
                                            viewModel.setField(FieldTags.REBIND_ITEM, true)
                                            viewModel.setField(FieldTags.REBIND_ITEM, false)
                                            //To show or hide the delete town fab

                                            if (viewModel.toDeleteIDList.value!!.isNotEmpty()) binding.fabRemoveTripSelection.show()
                                            else binding.fabRemoveTripSelection.hide()
                                        }
                                        TripButtonTags.TRIPS_BUTTON_NORMAL_PRICE -> {
                                            viewModel.setField(
                                                FieldTags.ON_NORMAL_PRICE_FORM,
                                                true
                                            )
                                        }
                                        TripButtonTags.TRIPS_BUTTON_VIP_PRICE -> {
                                            viewModel.setField(
                                                FieldTags.ON_VIP_PRICE_FORM,
                                                true
                                            )
                                        }
                                        TripButtonTags.TRIPS_CHECK_VIP -> {
                                            viewModel.exemptVIP(tripID)
                                            viewModel.setField(FieldTags.REBIND_ITEM, true)
                                        }
//                                        TripButtonTags.TRIPS_BUTTON_BUS_TYPES -> {
//                                            showBusTypesDialog(tripID)
//                                        }
                                    }
                                },
                                toDeleteIDList = viewModel.toDeleteIDList.value!!,
                                changesMapList = viewModel.localChangesMapList.value!!,
                                currentTownName = viewModel.currentTownName
                            )
                            viewModel.setField(
                                FieldTags.CURRENT_TRIPS,
                                snapshot.documents
                            )
                            adapter.submitList(snapshot.documents)
                            initRecycler(viewModel.spanSize.value!!)
                        } else
                            if (this::adapter.isInitialized) adapter.notifyDataSetChanged()
                    }
                    error?.handleError { }
                }

    class AddTripsDialogFragment(
        private val viewModel: TripsConfigViewModel,
        private val parentViewModel: AgencyConfigViewModel
    ) : DialogFragment() {
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
                .setTitle("${getString(R.string.text_dialog_title_from)}: Trips From ${viewModel.currentTownName}")
                .setIcon(R.drawable.baseline_add_24)
                .setView(recyclerBinding.root)
                .setPositiveButton(R.string.text_dialog_btn_add) { dialog, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.commitToAddList(
                            parentViewModel.bookerDoc.value!!.getString(
                                "agencyID"
                            )!!
                        )
                    }
                    dialog.cancel()
                }.create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            viewModel.setField(FieldTags.SHOW_ADD_TRIP, false)
            super.onDismiss(dialog)
        }

        override fun onCancel(dialog: DialogInterface) {
            viewModel.setField(FieldTags.SHOW_ADD_TRIP, false)
            super.onCancel(dialog)
        }

    }

    class NormalPriceDialogFragment(val viewModel: TripsConfigViewModel) :
        DialogFragment() {
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            val priceBinding: ItemTripPriceFormBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_trip_price_form,
                null,
                true
            )
            priceBinding.price.setHint(R.string.text_e_hint_trips_config_standard_price)

            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setView(priceBinding.root)
                .setNegativeButton(R.string.text_btn_cancel) { dialog, _ ->
                    dialog.cancel()
                    dialog.dismiss()
                    viewModel.setField(
                        FieldTags.ON_NORMAL_PRICE_FORM,
                        false
                    )
                }
                .setPositiveButton(R.string.text_btn_save) { dialog, _ ->
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


    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDetach()
    }

    class VipPriceDialogFragment(val viewModel: TripsConfigViewModel) :
        DialogFragment() {
        override fun onDismiss(dialog: DialogInterface) {
            viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
            super.onDismiss(dialog)
        }

        override fun onCancel(dialog: DialogInterface) {
            viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
            super.onCancel(dialog)
        }

        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            val priceBinding: ItemTripPriceFormBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_trip_price_form,
                null,
                true
            )
            priceBinding.price.setHint(R.string.text_e_hint_trips_config_vip_price)
            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setView(priceBinding.root)
                .setNegativeButton(R.string.text_btn_cancel) { dialog, _ ->
                    dialog.cancel()
                    dialog.dismiss()
                    viewModel.setField(FieldTags.ON_VIP_PRICE_FORM, false)
                }
                .setPositiveButton(R.string.text_btn_save) { dialog, _ ->
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
