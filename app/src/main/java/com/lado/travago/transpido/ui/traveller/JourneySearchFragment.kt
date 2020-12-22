package com.lado.travago.transpido.ui.traveller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentSearchJourneyBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*


/**
 * Search screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class JourneySearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchJourneyBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_search_journey,
                container,
                false
            )

        return binding.root
    }

    /**
     * Generate a Date picker and return the picked date formatted which then set to
     * the text of the [textInputEditText]. The travel date can be any day
     * from the current day to the 2 weeks after
     */
    private fun pickDate(textInputEditText: TextInputEditText) {
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.roll(Calendar.WEEK_OF_YEAR, 2)//Rolling the current date by 2weeks
        val twoWeeksAfter = calendar.timeInMillis// The date after 2 weeks

        //We create constraint so that the user can only select dates from that current day
        //to 2 weeks after only
        val bounds = CalendarConstraints.Builder()
            .setStart(today)//Smallest date which can be selected
            .setEnd(twoWeeksAfter)//Furthest day which can be selected(2 weeks away)
            .build()

        //We create our date picker which the user will use to enter his travel day
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(bounds)//Constrain the possible dates to be between 2 weeks only So the user can only select in a 2 week interval
            .setTitleText("Choose your Departure Day!")//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)//Set date picker interface to display a calendar
            .build()
        //Showing the created date picker onScreen
        datePicker.showNow(requireFragmentManager(), "JourneySearchFragment")
        //When the user chooses a date, we format it from milliseconds to a nice looking date
        textInputEditText.run {
            datePicker.addOnPositiveButtonClickListener {
                if (it in today until twoWeeksAfter) {
                    Log.i("JourneySearchFragment", "time=$it")
                    this.setText(SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date(
                        it)))
                } else {
                    Toast.makeText(requireContext(),
                        "Invalid Date!! Choose a date from now to 2 week.",
                        Toast.LENGTH_LONG).show()
                    pickDate(this)
                }

            }
        }

    }

}