package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.type.DateTime
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripDetailBinding
import com.lado.travago.tripbook.model.admin.BusMatrix
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripsDetailsViewModel
import com.lado.travago.tripbook.ui.recycler_adapters.BusSeatAdapter
import java.util.*

/**
 * Seat selection and bus customisation fragment
 */

class TripDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTripDetailBinding
    private lateinit var adapter: BusSeatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_trip_detail, container, false)

        return binding.root
    }

    /**
     * Configures the towns recycler
     */
    private fun setRecycler(busType: BusMatrix.BusType) {
        val recyclerManager = GridLayoutManager(context, 6)
        binding.recyclerBus.layoutManager = recyclerManager
        binding.recyclerBus.adapter = adapter
    }

    /**
     * Show the different dialogs when the date and time icons are clicked respectively
     */
    private fun handleClicks() {
        binding.editTextDate.setEndIconOnClickListener {
            datePicker()
        }
        binding.editTextTime.setOnClickListener {
            timePicker()
        }
        binding.editTextDate.setOnClickListener {
            datePicker()
        }
        binding.editTextTime.setEndIconOnClickListener {
            timePicker()
        }
    }

    private fun datePicker() {
        val titleText = "When are you travelling?"
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val minDate = calendar.timeInMillis // The current date(today) in millis
        calendar.roll(Calendar.DAY_OF_MONTH, 29)
        val maxDate = calendar.timeInMillis // 30 days in the future
        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(minDate)//Smallest date which can be selected
            .setEnd(maxDate)//Furthest
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
            viewModel.setField(TripsDetailsViewModel.FieldTags.TRAVEL_DATE, it)
            binding.birthday.editText!!.setText(viewModel.formatDate(viewModel.birthdayField))
        }
        datePicker.showNow(childFragmentManager, "")
    }

    private fun timePicker() {
        val titleText = "At what time?"
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(titleText)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(DateTime.getDefaultInstance().hours)
            .setMinute(DateTime.getDefaultInstance().minutes)
            .build()
        timePicker.addOnNegativeButtonClickListener {
            viewModel.setField(TripsDetailsViewModel.FieldTags.TRAVEL_TIME_HOUR, timePicker.hour)
            viewModel.setField(
                TripsDetailsViewModel.FieldTags.TRAVEL_TIME_MINUTE,
                timePicker.minute
            )
            checkTimeValidity()
        }
        timePicker.showNow(childFragmentManager, "")
    }

    /**
     * Checks if the time selected has passed or is less than 30minutes away from now
     */
    private fun checkTimeValidity() {
        val currentHour = DateTime.getDefaultInstance().hours
        val currentMinute = DateTime.getDefaultInstance().minutes
        val timeDifference: Int =
            if (currentHour == viewModel.travelTimeHour) viewModel.travelTimeMinute - currentHour
            else -1
        if (timeDifference != -1) {
            viewModel.setField(
                TripsDetailsViewModel.FieldTags.TOAST_MESSAGE,
                "Your time must be at least 30 minutes from now"
            )
            timePicker()
        }
    }

}