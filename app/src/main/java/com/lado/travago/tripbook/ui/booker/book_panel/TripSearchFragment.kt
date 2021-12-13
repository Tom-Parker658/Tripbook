package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
        val minDate = Date().time // The current date(today) in millis
        calendar.roll(Calendar.DATE, 30)
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
            .setSelection(viewModel.tripDateInMillis)
            .build()

        //Sets the value of the edit text to the formatted value of the selection
        datePicker.addOnPositiveButtonClickListener {
            if (Date().date > Date(it).date) datePicker()
            else {
                viewModel.setField(FieldTags.TRIP_DATE, it)
                binding.editTextDates.editText!!.setText(Utils.formatDate(it, "EEEE dd MMMM YYYY"))
            }
        }
        datePicker.showNow(childFragmentManager, "Date")

    }

    private fun timePicker() {
        val titleText = getString(R.string.text_pick_time)//TODO Translate
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(titleText)
            .setHour(viewModel.tripTime.hour)
            .setMinute(viewModel.tripTime.minutes)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            viewModel.setField(
                FieldTags.TRIP_TIME,
                TimeModel.from24Format(
                    timePicker.hour, timePicker.minute, 0
                )
            )
            binding.editTextTime.editText!!.setText(
                viewModel.tripTime.localTimeFormat()
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
            viewModel.tripTime.localTimeFormat()
        )
        binding.editTextDates.editText!!.setText(
            Utils.formatDate(
                viewModel.tripDateInMillis,
                getString(R.string.text_date_pattern_in_words)
            )
        )
    }

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
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
        val adapterLocality = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_textview,
            townNames
        )
        val adapterDestination = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_textview,
            townNames
        )
        (binding.editTextLocality.editText as AutoCompleteTextView).setAdapter(adapterLocality)
        (binding.editTextDestination.editText as AutoCompleteTextView).setAdapter(adapterDestination)
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
                        getString(R.string.text_message_not_found_drop_down)
                    )
                    binding.editTextLocality.requestFocus()
                }
                viewModel.townNames.contains(viewModel.destination) -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        getString(R.string.text_message_not_found_drop_down)
                    )
                    binding.editTextLocality.requestFocus()
                }
                viewModel.locality == viewModel.destination -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        getString(R.string.text_error_locality_same_as_destination)
                    )
                }

                viewModel.tripDateInMillis == 0L -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Choose the travel date"
                    )
                    binding.editTextDates.requestFocus()
                }
                else -> {
                    val tripTitle =
                        "${viewModel.locality} ${getString(R.string.text_to)} ${viewModel.destination}"
                    findNavController().navigate(
                        TripSearchFragmentDirections.actionTripSearchFragmentToTripSearchResultsFragment(
                            viewModel.locality,
                            viewModel.destination,
                            viewModel.tripDateInMillis,
                            viewModel.tripTime.fullTimeInMillis,
                            tripTitle
                        )
                    )
                }
            }

        }
    }
}