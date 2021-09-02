package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchBinding
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel
import kotlinx.coroutines.*

/**
 * Search screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TripSearchFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchBinding
    private lateinit var viewModel: TripSearchViewModel
    private var localitiesNameList = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_trip_search,
                container,
                false
            )
        initViewModel()
        adaptLocalityNames()
        restoreFields()
        onFieldChange()
        observeTownInfo()
        navigateToResultScreen()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[TripSearchViewModel::class.java]
    }

    private fun observeTownInfo() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressTrip.visibility = View.VISIBLE
            else binding.progressTrip.visibility = View.GONE
        }
        viewModel.retrySearch.observe(viewLifecycleOwner) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.searchLocalityTowns()
                }
            }
        }
        viewModel.onLocalityResultsFound.observe(viewLifecycleOwner) {
            if (it && localitiesNameList.contains(viewModel.locality)) {/*Show destination editText*/
                adaptDestinationNames()
                binding.inputDestination.visibility = View.VISIBLE
                binding.imageDestination.visibility = View.VISIBLE
            } else { /*Close destination editText if visible in this case*/
                viewModel.setFields(TripSearchViewModel.FieldTags.DESTINATION, "")
                binding.inputDestination.visibility = View.GONE
                binding.imageDestination.visibility = View.GONE
            }
        }

    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.inputLocality.editText!!.setText(viewModel.locality)
        binding.inputDestination.editText!!.setText(viewModel.destination)
        if (viewModel.destinationNameList.contains(viewModel.destination)) {
            binding.inputDestination.visibility = View.VISIBLE
            binding.imageDestination.visibility = View.VISIBLE
        } else {
            binding.inputDestination.visibility = View.GONE
            binding.imageDestination.visibility = View.GONE
        }
        binding.checkboxVip.isChecked = viewModel.vip
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.inputLocality.editText!!.addTextChangedListener {
            viewModel.setFields(TripSearchViewModel.FieldTags.LOCALITY, it.toString())
            //To avoid changes when loading
            if (localitiesNameList.contains(it.toString()))
                viewModel.setFields(TripSearchViewModel.FieldTags.RETRY_SEARCH, true)
            else {
                binding.inputDestination.visibility = View.GONE
                binding.imageDestination.visibility = View.GONE
                binding.btnSearchJourney.visibility = View.GONE
                binding.checkboxVip.visibility = View.GONE
            }
        }
        binding.inputDestination.editText!!.addTextChangedListener {
            viewModel.setFields(TripSearchViewModel.FieldTags.DESTINATION, it.toString())
            //If the booker has selected a valid locality we start the searching for destinations
            if (viewModel.destinationNameList.contains(it.toString())) {
                binding.btnSearchJourney.visibility = View.VISIBLE
                binding.checkboxVip.visibility = View.VISIBLE
            }else{
                binding.btnSearchJourney.visibility = View.GONE
                binding.checkboxVip.visibility = View.GONE
            }
        }
        binding.checkboxVip.setOnCheckedChangeListener { _, isVip ->
            viewModel.setFields(TripSearchViewModel.FieldTags.VIP, isVip)
        }
    }

    /**
     * Adapt the array of destinations to the location and destination auto complete text view
     */
    private fun adaptLocalityNames() {
        localitiesNameList = resources.getStringArray(R.array.localities).toList()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_textview,
            localitiesNameList
        )
        (binding.inputLocality.editText as AutoCompleteTextView).setAdapter(adapter)
    }

    private fun adaptDestinationNames() {
        if (viewModel.destinationNameList.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_textview,
                viewModel.destinationNameList
            )
            (binding.inputDestination.editText as AutoCompleteTextView).setAdapter(adapter)
        }
    }

    /**
     * Navigates to the result screen after the search button is clicked
     */
    private fun navigateToResultScreen() = binding.btnSearchJourney.setOnClickListener {
        findNavController().navigate(
            TripSearchFragmentDirections.actionTripSearchFragmentToTripSearchResultsFragment(
                viewModel.locality,
                viewModel.destination,
                viewModel.distance,
                "",""
            )
        )
    }

}