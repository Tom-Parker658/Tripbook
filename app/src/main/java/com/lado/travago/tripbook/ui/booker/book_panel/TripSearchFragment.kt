package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchBinding
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel.*
import kotlinx.coroutines.*

/**
 * Search screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TripSearchFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchBinding
    private lateinit var viewModel: TripSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_trip_search,
            container,
            false
        )
        initViewModel()
        adaptAutoCompleteNames()
        restoreFields()
        onFieldChange()
        navigateToResultScreen()
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[TripSearchViewModel::class.java]
    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.inputLocality.editText!!.setText(viewModel.locality)
        binding.inputDestination.editText!!.setText(viewModel.destination)
        binding.checkboxVip.isChecked = viewModel.isVip
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.inputLocality.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.LOCALITY, it.toString())
            //To avoid changes when loading
        }

        binding.inputDestination.editText!!.addTextChangedListener {
            viewModel.setFields(FieldTags.DESTINATION, it.toString())
            //If the booker has selected a valid locality we start the searching for destinations
        }
        binding.checkboxVip.setOnCheckedChangeListener { _, isVip ->
            viewModel.setFields(FieldTags.VIP, isVip)
        }
    }

    /**
     * Adapt the array of destinations to the location and destination auto complete text view
     */
    private fun adaptAutoCompleteNames() {
        val townNames = resources.getStringArray(R.array.localities).toList()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_textview,
            townNames
        )
        (binding.inputLocality.editText as AutoCompleteTextView).setAdapter(adapter)
        (binding.inputDestination.editText as AutoCompleteTextView).setAdapter(adapter)
    }

    /* private fun adaptDestinationNames() {
         destinationNames = resources.getStringArray(R.array.localities).toMutableList()
         val adapter = ArrayAdapter(
             requireContext(),
             R.layout.item_dropdown_textview,
             destinationNames
         )
         (binding.inputDestination.editText as AutoCompleteTextView).setAdapter(adapter)
     }*/

    /**
     * Navigates to the result screen after the search button is clicked
     */
    private fun navigateToResultScreen() {
        binding.btnSearchJourney.setOnClickListener {
            when {
                viewModel.townNames.contains(viewModel.locality) -> {
                    viewModel.setFields(
                        FieldTags.TOAST_MESSAGE,
                        "Please choose your locality from the dropdown"
                    )
                    binding.inputLocality.requestFocus()
                }
                viewModel.townNames.contains(viewModel.destination) -> {
                    viewModel.setFields(
                        FieldTags.TOAST_MESSAGE,
                        "Please choose your destination from the dropdown"
                    )
                    binding.inputLocality.requestFocus()
                }
                viewModel.locality == viewModel.destination -> {
                    viewModel.setFields(
                        FieldTags.TOAST_MESSAGE,
                        "It is logically impossible for your locality to be your destination, right?"
                    )
                }
                else -> {
                    findNavController().navigate(
                        TripSearchFragmentDirections.actionTripSearchFragmentToTripSearchResultsFragment(
                            viewModel.locality,
                            viewModel.destination,
                            viewModel.isVip
                        )
                    )
                }
            }

        }
    }
}