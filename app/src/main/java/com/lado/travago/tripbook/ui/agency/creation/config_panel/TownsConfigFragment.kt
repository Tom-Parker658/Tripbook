package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.util.*
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTownsConfigBinding
import com.lado.travago.tripbook.databinding.ItemSearchFormBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.ui.recyclerview.adapters.TownClickListener
import com.lado.travago.tripbook.ui.recyclerview.adapters.TownConfigAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


/**
 * A fragment used by agency admins to add and subtract towns and access trips from that town.
 */
@ExperimentalCoroutinesApi
class TownsConfigFragment : Fragment() {
    private lateinit var viewModel: TownsConfigViewModel
    private lateinit var binding: FragmentTownsConfigBinding
    private lateinit var adapter: TownConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_towns_config,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity())[TownsConfigViewModel::class.java]
        observeLiveData()
        handleClicks()
        try {
            setRecycler()
        } catch (e: Exception) {
            //TODO: LOad list first
        }
        return binding.root
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
            if (it) CoroutineScope(Dispatchers.Main).launch { viewModel.getTownsData() }
        }
        //Submit list to inflate recycler view
        viewModel.townDocList.observe(viewLifecycleOwner) {
            adapter = TownConfigAdapter(
                exemptedTownsList = viewModel.exemptedTownList,

                clickListener = TownClickListener { townId, buttonTag ->
                    when (buttonTag) {//Remove or add a town from the exemption list
                        TownsConfigViewModel.TownButtonTags.TOWN_SWITCH_ACTIVATE -> {
                            viewModel.exemptTown(townId.split("+").first())
                        }
                        TownsConfigViewModel.TownButtonTags.TOWN_BUTTON_TRIPS -> {
                            //We navigate to the trip config fragment of that current town
                            if (townId.isNotBlank()) {
                                findNavController().navigate(
                                    TownsConfigFragmentDirections.actionTownsConfigFragmentToTripsConfigFragment(
                                        townId.split("+").first(),//Being the town id
                                        townId.split("+").last()//Being the town name
                                    )
                                )
                            }
                        }
                    }
                })
            setRecycler()
            adapter.submitList(it)
            binding.fabSearchTown.visibility = View.VISIBLE
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.townProgressBar.visibility = View.VISIBLE
            else binding.townProgressBar.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("TownsConfig", it)
            }
        }
        binding.fabSearchTown.setOnClickListener {
            viewModel.setField(TownsConfigViewModel.FieldTag.START_TOWN_SEARCH, true)
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
                    setPositiveButton("SEARCH") { _, _ ->
                        if (viewModel.townDocList.value!!.isNotEmpty()) {
                            viewModel.searchTown(searchBinding.searchBar.editText!!.text.toString())
                                ?.let { index ->
                                    if (index != -1) {
                                        binding.recyclerTowns.smoothScrollToPosition(index)
                                    } else {//If not found
                                        viewModel.setField(
                                            TownsConfigViewModel.FieldTag.TOAST_MESSAGE,
                                            "Not found. Select from dropdown"
                                        )
                                    }
                                }
                        }
                        viewModel.setField(TownsConfigViewModel.FieldTag.START_TOWN_SEARCH, false)
                    }
                    setOnCancelListener {
                        viewModel.setField(TownsConfigViewModel.FieldTag.START_TOWN_SEARCH, false)
                    }
                }.create().apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }.show()
            }
        }
        viewModel.onClose.observe(viewLifecycleOwner) {
            if (it) {
                onDestroy()
                findNavController().navigate(
                    TownsConfigFragmentDirections.actionTownsConfigFragmentToAgencyCreationFinalFragment()
                )
                viewModel.setField(TownsConfigViewModel.FieldTag.ON_CLOSE, false)

            }
        }

    }

    private fun handleClicks(){
        binding.btnCancelTown.setOnClickListener {
            viewModel.setField(TownsConfigViewModel.FieldTag.ON_CLOSE, true)
        }
        binding.btnSaveTowns.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.uploadTownChanges()
            }
        }
    }
}