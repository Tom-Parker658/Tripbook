package com.lado.travago.tripbook.ui.agency.config_panel

import android.annotation.SuppressLint
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
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTownsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.databinding.ItemSimpleRecyclerLayoutBinding
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TownsConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TownsConfigViewModel.FieldTags
import com.lado.travago.tripbook.ui.recycler_adapters.SimpleAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SimpleClickListener
import com.lado.travago.tripbook.ui.recycler_adapters.TownClickListener
import com.lado.travago.tripbook.ui.recycler_adapters.TownConfigAdapter
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

        //Set the listener for agency towns documents
        viewModel.agencyTownsListener(
            requireActivity(),
            parentViewModel.bookerDoc.value!!.getString("agencyID")!!
        )

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
    private fun setSpanSize(spanSize: Int) {
        val recyclerManager = GridLayoutManager(context, spanSize)
        binding.recyclerTowns.layoutManager = recyclerManager
        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)

        if (this::adapter.isInitialized)
            binding.recyclerTowns.adapter = adapter

        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)
    }

    private fun observeLiveData() {
        viewModel.notifyAdapterChanges.observe(viewLifecycleOwner) {
            if (it)
                if (this::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                    viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, false)
                }
        }
        viewModel.agencyTownsList.observe(viewLifecycleOwner) {
            viewModel.townNamesListClear()
            if (it.isNotEmpty()) {
                adapter = TownConfigAdapter(
                    clickListener = TownClickListener { townID, buttonTag ->
                        when (buttonTag) {
                            TownsConfigViewModel.TownButtonTags.TOWN_CHECK_TO_DELETE -> {
                                viewModel.removeTown(townID.split("+").first())
                                //This to rightly inflate the close button
                                val index = viewModel.agencyTownsList.value!!.indexOf(
                                    viewModel.agencyTownsList.value!!.find { townDoc ->
                                        townDoc.id == townID.split("+").first()
                                    })
                                adapter.notifyItemChanged(index)
                                //To show or hide the delete town fab but in other not to annoy, we do it only when we have 1 selection
                                if (viewModel.toDeleteIDList.value!!.size == 1)
                                    binding.fabRemoveSelection.show()
                                else if (viewModel.toDeleteIDList.value!!.isEmpty())
                                    binding.fabRemoveSelection.hide()
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
                                    viewModel.agencyTownsListener(
                                        requireActivity(),
                                        parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                                    ).remove()
                                }
                            }
                        }
                    },
                    viewModel.toDeleteIDList.value!!,
                    resources
                )
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
                if (this::adapter.isInitialized) {
                    adapter.submitList(it)
                    setSpanSize(viewModel.spanSize.value!!)
                }

            }
        }
        viewModel.spanSize.observe(viewLifecycleOwner) {
            setSpanSize(it)
        }
        viewModel.retryTowns.observe(viewLifecycleOwner) {
            if (it) CoroutineScope(Dispatchers.Main).launch {
                viewModel.getOriginalTowns()
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
            } else {
                binding.townProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it?.isNotBlank() == true) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.startTownSearch.observe(viewLifecycleOwner) {
            if (it) {
                val searchBinding: ItemSearchFormBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_search_form, null, false)
                searchBinding.searchBar.hint = getString(R.string.text_town)

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
                    setTitle(getString(R.string.text_dialog_search))
                    setView(searchBinding.root)
                    setPositiveButton(getString(R.string.text_dialog_search)) { dialog, _ ->
                        if (viewModel.agencyTownsList.value!!.isNotEmpty()) {
                            viewModel.searchTown(searchBinding.searchBar.editText!!.text.toString())
                                ?.let { index ->
                                    if (index != -1) {
                                        //We rebind the searched item and make it glow
                                        binding.recyclerTowns.smoothScrollToPosition(index)
                                        adapter.notifyItemChanged(index)
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
                viewModel.agencyTownsListener(
                    requireActivity(),
                    parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                ).remove()
//                viewModel.setField(TownsConfigViewModel.FieldTags.RETRY_TOWNS, true)
            }
        }
        viewModel.toDeleteIDList.observe(viewLifecycleOwner) {
            if (it.isEmpty())
                viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)
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
        viewModel.fabVisibilityState.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabAddTown.show()
                binding.fabSearchTown.show()
                binding.fabTownsSpanSize.show()
                binding.fabSortTowns.show()
                binding.fabToggleToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_off_24,
                        requireActivity().theme
                    )
                )
                if (viewModel.toDeleteIDList.value!!.isNotEmpty())
                    binding.fabRemoveSelection.show()
            } else {
                binding.fabAddTown.hide()
                binding.fabSearchTown.hide()
                binding.fabTownsSpanSize.hide()
                binding.fabSortTowns.hide()
                binding.fabToggleToolbox.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_24,
                        requireActivity().theme
                    )
                )
                if (viewModel.toDeleteIDList.value!!.isNotEmpty())
                    binding.fabRemoveSelection.hide()
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
                setIcon(R.drawable.baseline_filter_alt_24)
                setTitle(R.string.text_dialog_title_sort_by)
                setSingleChoiceItems(
                    arrayOf(
                        getString(R.string.text_sort_by_none),
                        getString(R.string.text_sort_by_name_asc),
                        getString(R.string.text_sort_by_region_asc)
                    ),
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
                    if (this@TownsConfigFragment::adapter.isInitialized) {
                        adapter.submitList(viewModel.agencyTownsList.value)
                        viewModel.setField(FieldTags.NOTIFY_DATA_CHANGED, true)
                    }
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
        binding.fabTownsSpanSize.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.text_items_per_row))
                .setSingleChoiceItems(
                    arrayOf("1", "2", "3", "4", "5", "6"),
                    viewModel.spanSize.value!! - 1
                ) { dialogInterface, index ->
                    viewModel.setField(FieldTags.SPAN_SIZE, index + 1)
                    dialogInterface.cancel()
                }
                .show()
        }
        binding.fabToggleToolbox.setOnClickListener { viewModel.invertFabVisibility() }
    }

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDetach()
    }

    class AddTownsDialogFragment(
        private val viewModel: TownsConfigViewModel,
        private val parentViewModel: AgencyConfigViewModel
    ) : DialogFragment() {
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
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
                .setTitle(R.string.text_town)
                .setIcon(R.drawable.baseline_add_24)
                .setView(recyclerBinding.root)
                .setPositiveButton(R.string.text_btn_add) { dialog, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.commitToAddList(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
                    }
                    dialog.cancel()
                }.create()
        }

        override fun onCancel(dialog: DialogInterface) {
            viewModel.setField(FieldTags.ON_SHOW_ADD, false)
            super.onCancel(dialog)
        }

        override fun onDismiss(dialog: DialogInterface) {
            viewModel.setField(FieldTags.ON_SHOW_ADD, false)
            super.onDismiss(dialog)
        }

    }
}