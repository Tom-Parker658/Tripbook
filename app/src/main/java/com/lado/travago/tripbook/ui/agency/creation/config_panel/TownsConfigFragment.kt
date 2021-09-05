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
import com.lado.travago.tripbook.databinding.FragmentTownsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.databinding.ItemSimpleRecyclerLayoutBinding
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel.FieldTags
import com.lado.travago.tripbook.ui.recyclerview.adapters.SimpleAdapter
import com.lado.travago.tripbook.ui.recyclerview.adapters.SimpleClickListener
import com.lado.travago.tripbook.ui.recyclerview.adapters.TownClickListener
import com.lado.travago.tripbook.ui.recyclerview.adapters.TownConfigAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * A fragment used by agency admins to add and subtract towns and access trips from that town.
 * @property parentViewModel is the viewmodel to hold the booker info and is loaded before launch
 */
@ExperimentalCoroutinesApi
class TownsConfigFragment : Fragment() {
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var viewModel: TownsConfigViewModel
    private lateinit var binding: FragmentTownsConfigBinding
    private lateinit var adapter: TownConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_towns_config,
            container,
            false
        )
        viewModel = ViewModelProvider(this)[TownsConfigViewModel::class.java]
        snapshotListener
        handleFabVisibilities()

        observeLiveData()
        handleClicks()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
        } catch (e: Exception) {
            Log.d("TownsConfigFragment", e.message.toString())
            //TODO: Truly handle this error please future Lado
        }
    }

    /**
     * Configures the towns recycler
     */
    private fun setRecycler() {
        val recyclerManager = GridLayoutManager(context, 2)
        binding.recyclerTowns.layoutManager = recyclerManager
        binding.recyclerTowns.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.retryTowns.observe(viewLifecycleOwner) {
            if (it) CoroutineScope(Dispatchers.Main).launch {
                viewModel.getOriginalTowns()
            }
        }
        //We remove all towns in the current snap-shoted list from the list to be displayed during the addition process
        viewModel.currentTownsList.observe(viewLifecycleOwner) {
            //TODO: SHOULD NOT MODIFY VIEW_MODEL FROM FRAGMENT, change it as fast as possible
            viewModel.townNamesList.clear()
            if (it.isNotEmpty()) {
                it.forEach { currentDoc ->
                    //We also set the names to be use in auto complete
                    viewModel.setField(
                        FieldTags.TOWN_NAME_LIST,
                        currentDoc.getString("name")!!
                    )
                    val toBeRemovedMap = viewModel.townSimpleInfoMap.find { map ->
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
        //Submit list to inflate recycler view
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.townProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                )
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                )
            } else {
                binding.townProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("TownsConfig", it)
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.startTownSearch.observe(viewLifecycleOwner) {
            if (it) {
                val searchBinding: ItemSearchFormBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_search_form, null, false)
                searchBinding.searchBar.hint = "Town: "
                searchBinding.searchBar.helperText = "Enter the town name."

                //Sets adapter for the autocomplete text view
                val searchAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    viewModel.townNamesList
                )
                val textView = searchBinding.searchBar.editText as AutoCompleteTextView
                textView.setAdapter(searchAdapter)

                MaterialAlertDialogBuilder(requireContext()).apply {
                    setIcon(R.drawable.baseline_search_24)
                    setTitle("Search")
                    setView(searchBinding.root)
                    setPositiveButton("SEARCH") { dialog, _ ->
                        if (viewModel.currentTownsList.value!!.isNotEmpty()) {
                            viewModel.searchTown(searchBinding.searchBar.editText!!.text.toString())
                                ?.let { index ->
                                    if (index != -1) {
                                        binding.recyclerTowns.smoothScrollToPosition(index)
                                    } else {//If not found
                                        viewModel.setField(
                                            FieldTags.TOAST_MESSAGE,
                                            "Not found. Select from dropdown"
                                        )
                                    }
                                }
                        }
                        dialog.cancel()
                    }
                    setOnCancelListener {
                        viewModel.setField(FieldTags.START_TOWN_SEARCH, false)
                    }
                }.create().apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }.show()
            }
        }
        viewModel.onClose.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    TownsConfigFragmentDirections.actionTownsConfigFragmentToAgencyConfigCenterFragment()
                )
                viewModel.setField(FieldTags.ON_CLOSE, false)
                snapshotListener.remove()
//                viewModel.setField(TownsConfigViewModel.FieldTags.RETRY_TOWNS, true)
            }
        }
        viewModel.onShowAddTrip.observe(viewLifecycleOwner) {
            if (it) {
                AddTownsDialogFragment(
                    viewModel,
                    parentViewModel
                ).showNow(childFragmentManager, "")
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleFabVisibilities() {
        // Toggle visibilities fab
        binding.fabToggleToolbox.setOnClickListener { toggleFab ->
            binding.fabAddTown.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSearchTown.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSortTowns.let {
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
        }
    }

    private fun handleClicks() {
        binding.fabSearchTown.setOnClickListener {
            viewModel.setField(FieldTags.START_TOWN_SEARCH, true)
        }
        binding.fabSortTowns.setOnClickListener {
            //1-We create a spinner with options
            MaterialAlertDialogBuilder(requireContext()).apply {
                setIcon(R.drawable.baseline_sort_24)
                setTitle("Sort By?")
                setSingleChoiceItems(
                    arrayOf("None", "Name", "Region"),
                    viewModel.sortCheckedItem
                ) { dialog, which ->
                    when (which) {
                        1 -> {
                            viewModel.sortResult(TownsConfigViewModel.SortTags.TOWN_NAMES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortResult(TownsConfigViewModel.SortTags.REGIONS)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 2)
                        }
                    }
                    adapter.submitList(viewModel.currentTownsList.value)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

            }.create().show()
        }
        binding.fabAddTown.setOnClickListener {
            viewModel.setField(FieldTags.ON_SHOW_ADD, true)
        }
        binding.fabRemoveSelection.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.commitToDeleteList(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
            binding.fabRemoveSelection.hide()
        }
    }

    /* Add a snapshot listener to the towns collection */
    private val snapshotListener
        get() =
            viewModel.firestoreRepo.db.collection(
                "OnlineTransportAgency/${
                    parentViewModel.bookerDoc.value!!.getString("agencyID")
                }/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/"
            ).addSnapshotListener(requireActivity()) { snapshot, error ->
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        adapter = TownConfigAdapter(
                            clickListener = TownClickListener { townID, buttonTag ->
                                when (buttonTag) {
                                    TownsConfigViewModel.TownButtonTags.TOWN_CHECK_TO_DELETE -> {
                                        viewModel.removeTown(townID.split("+").first())
                                        //This to rightly inflate the close button
                                        val index =
                                            snapshot.documents.indexOf(snapshot.documents.find {
                                                it.id == townID.split("+").first()
                                            })
                                        adapter.notifyItemChanged(index)
                                        //To show or hide the delete town fab
                                        if (viewModel.toDeleteIDList.value!!.isNotEmpty()) binding.fabRemoveSelection.show()
                                        else binding.fabRemoveSelection.hide()

                                    }
                                    TownsConfigViewModel.TownButtonTags.TOWN_BUTTON_TRIPS -> {
                                        //We navigate to the trip config fragment of that current town
                                        if (townID.isNotBlank()) {
                                            findNavController().navigate(
                                                TownsConfigFragmentDirections.actionTownsConfigFragmentToTripsConfigFragment(
                                                    townID.split("+").first(),//Being the town id
                                                    townID.split("+").last()//Being the town name
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            viewModel.toDeleteIDList.value!!
                        )
                        viewModel.setField(
                            FieldTags.CURRENT_TOWNS,
                            snapshot.documents
                        )
                        adapter.submitList(snapshot.documents)
                        setRecycler()
                    } else {
                        try {
                            adapter.notifyDataSetChanged()
                        } catch (e: Exception) {/*Just in-case we cleared all the list of items*/
                        }
                    }
                }
                error?.handleError { }
            }


    class AddTownsDialogFragment(
        private val viewModel: TownsConfigViewModel,
        private val parentViewModel: AgencyConfigViewModel
    ) : DialogFragment() {
        @SuppressLint("DialogFragmentCallbacksDetector")
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            super.onCreateDialog(savedInstanceState)
            val recyclerBinding: ItemSimpleRecyclerLayoutBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_simple_recycler_layout,
                null,
                false
            )
            var simpleAdapter: SimpleAdapter? = null
            simpleAdapter = SimpleAdapter(
                clickListener = SimpleClickListener { townID ->
                    viewModel.addTown(townID)
                    val index =
                        viewModel.townSimpleInfoMap.indexOf(viewModel.townSimpleInfoMap.find { it["id"] == townID })
                    simpleAdapter?.notifyItemChanged(index)
                },

                viewModel.toAddIDList.value!!
            )
            val recyclerManager = LinearLayoutManager(requireContext())
            recyclerBinding.recyclerView.layoutManager = recyclerManager
            recyclerBinding.recyclerView.adapter = simpleAdapter
            viewModel.townSimpleInfoMap.sortBy { it["name"] }
            simpleAdapter.submitList(
                viewModel.townSimpleInfoMap
            )
            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setTitle("Towns")
                .setIcon(R.drawable.baseline_add_24)
                .setView(recyclerBinding.root)
                .setPositiveButton("Add Selected Towns") { dialog, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.commitToAddList(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
                    }
                    dialog.dismiss()
                    dialog.cancel()
                }
                .setOnCancelListener {
                    viewModel.setField(FieldTags.ON_SHOW_ADD, false)
                }
                .setOnDismissListener {
                    viewModel.setField(FieldTags.ON_SHOW_ADD, false)
                }
                .create()
        }

    }
}