package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.text.InputType
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchViewModel.*
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.*
import java.util.*

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
        clickListeners()
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

    private fun clickListeners() {
        //DATE
        binding.editTextDates.setEndIconOnClickListener {
            datePicker()
        }
        binding.editTextDates.editText!!.setOnClickListener {
            datePicker()
        }
        binding.editTextDates.editText!!.inputType = InputType.TYPE_NULL

        //DATE
        binding.editTextTime.setEndIconOnClickListener {
            timePicker()
        }
        binding.editTextTime.editText!!.setOnClickListener {
            timePicker()
        }
        binding.editTextTime.editText!!.inputType = InputType.TYPE_NULL
    }

    private fun datePicker() {
        val titleText = "Trip Date"
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val minDate = calendar.timeInMillis // The current date(today) in millis
        calendar.roll(Calendar.DATE, 29)
        val maxDate = calendar.timeInMillis
        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(minDate)//Smallest date which can be selected
            .setEnd(maxDate)
            .build()
        //We create our date picker which the user will use to enter his travel day
        //Showing the created date picker onScreen
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(bounds)//Constrain the possible dates
            .setTitleText(titleText)//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        //Sets the value of the edit text to the formatted value of the selection
        datePicker.addOnPositiveButtonClickListener {
            viewModel.setField(FieldTags.TRIP_DATE, it)
            binding.editTextDates.editText!!.setText(Utils.formatDate(it, "MMMM, dd YYYY"))
        }
        datePicker.showNow(childFragmentManager, "")
    }


    private fun timePicker() {
        val titleText = "Trip Time"
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(titleText)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            viewModel.setField(
                FieldTags.TRIP_TIME,
                TimeModel.from24Format(
                    timePicker.hour, timePicker.minute, null
                )
            )
            binding.editTextTime.editText!!.setText(
                viewModel.tripTime!!.formattedTime(
                    TimeModel.TimeFormat.FORMAT_24H
                )
            )
        }
        timePicker.showNow(childFragmentManager, "")
    }


    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.editTextLocality.editText!!.setText(viewModel.locality)
        binding.editTextDestination.editText!!.setText(viewModel.destination)
        binding.editTextTime.editText!!.setText(
            viewModel.tripTime!!.formattedTime(
                TimeModel.TimeFormat.FORMAT_24H
            )
        )
        binding.editTextDates.editText!!.setText(
            Utils.formatDate(
                viewModel.tripDateInMillis,
                "MMMM, dd YYYY"
            )
        )
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.editTextLocality.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.LOCALITY, it.toString())
        }
        binding.editTextDestination.editText!!.addTextChangedListener {
            viewModel.setField(FieldTags.DESTINATION, it.toString())
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
        (binding.editTextLocality.editText as AutoCompleteTextView).setAdapter(adapter)
        (binding.editTextLocality.editText as AutoCompleteTextView).setAdapter(adapter)
    }

    /**
     * Navigates to the result screen after the search button is clicked
     */
    private fun navigateToResultScreen() {
        binding.btnSearchJourney.setOnClickListener {
            when {
                viewModel.townNames.contains(viewModel.locality) -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Please choose your locality from the dropdown"
                    )
                    binding.editTextLocality.requestFocus()
                }
                viewModel.townNames.contains(viewModel.destination) -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Please choose your destination from the dropdown"
                    )
                    binding.editTextLocality.requestFocus()
                }
                viewModel.locality == viewModel.destination -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "It is logically impossible for your locality to be your destination, right?"
                    )
                }
                viewModel.tripTime == null -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "You must choose the time to go"
                    )
                    binding.editTextTime.requestFocus()
                }
                viewModel.tripDateInMillis == 0L -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Choose the travel date"
                    )
                    binding.editTextDates.requestFocus()
                }
                else -> {
                    findNavController().navigate(
                        TripSearchFragmentDirections.actionTripSearchFragmentToTripSearchResultsFragment(
                            viewModel.locality,
                            viewModel.destination,
                            viewModel.tripDateInMillis,
                            viewModel.tripTime!!.hour,
                            viewModel.tripTime!!.minutes
                        )
                    )
                }
            }

        }
    }
}