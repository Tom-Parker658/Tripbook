package com.lado.travago.transpido.ui.traveller

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.lado.travago.transpido.R
import com.lado.travago.transpido.databinding.FragmentSearchJourneyBinding
import com.lado.travago.transpido.viewmodel.traveller.SearchJourneyViewModel
import com.lado.travago.transpido.viewmodel.SearchJourneyViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


/**
 * Search screen fragment
 */
class JourneySearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchJourneyBinding
    private lateinit var searchJourneyViewModel: SearchJourneyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search_journey, container, false)

        //Initialises the Places API and set a client for us to use if not already initialized
        if (!Places.isInitialized())
            Places.initialize(requireActivity().applicationContext, getString(R.string.api_key))
        val placesClient = Places.createClient(requireContext())

        //Request permissions
        requestLocateDevice()

        //Initialises searchJourneyViewModel with a SearchJourneyViewModel object containing the placesClient
        val application = requireActivity().application
        val viewModelFactory = SearchJourneyViewModelFactory(application, placesClient)
        searchJourneyViewModel = ViewModelProvider(this, viewModelFactory)[SearchJourneyViewModel::class.java]

        //When the locate me fab is clicked and set focus to the location edit text
        binding.fabLocateMe.setOnClickListener {
            locateMe()
            (binding.inputLocation.editText as MaterialAutoCompleteTextView).showDropDown()
//            //show keyboard
//            val imm = getSystemService(requireContext(), InputMethodManager::class.java)
//            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        //As the user enter text in location field
        (binding.inputLocation.editText as MaterialAutoCompleteTextView).run {
            addTextChangedListener {
                //Adapt the adapter to the location field
                this.setAdapter(onUserTypeComplete(this))
            }
        }

        //As the user enter text in Destination field
        (binding.inputDestination.editText as MaterialAutoCompleteTextView).run {
            addTextChangedListener {
                //Adapt the adapter to the destination field
                setAdapter(onUserTypeComplete(this))
            }
        }

        //When the travelDate field is tapped
        (binding.inputTravelDate.editText as TextInputEditText).run {
            setOnClickListener {
                //We set the travelDate edit view text to the formatted date
                pickDate(this)
            }

        }

        //When the travelTime field is tapped
        (binding.inputTravelTime.editText as MaterialAutoCompleteTextView).run {
            setOnClickListener {
                //Populate the dropdown menu
                travelTimeOptionAdapter(this)
            }
        }

        return binding.root
    }


    /**
     * Helper method to request the permission to locate device
     */
    private fun requestLocateDevice() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_CODE
        )
    }

    /**
     *  Provides the autocomplete options to the location and destination fields as the user types
     *  @return an adapter which can be adapted onto any auto complete text view
     */
    private fun onUserTypeComplete(autoCompleteTextView: AutoCompleteTextView): ArrayAdapter<String> {
        //Returns a list of Place autocomplete based on the user's input
        val autoCompleteOptions = searchJourneyViewModel.autoComplete(autoCompleteTextView.text.toString())
        //An adapter to adapt the list of places name to the location autocomplete textField layout
        return ArrayAdapter(requireContext(),
            R.layout.item_place_autocomplete,
            autoCompleteOptions)
    }

    /**
     * Provide the autocomplete of Location text field when the locate me fab is tapped
     */
    private fun locateMe() {
        //Returns a list of device location likelihood places
        val autoCompleteOptions = searchJourneyViewModel.locateTraveller { requestLocateDevice() }
        //An adapter to adapt the list of places name to the location autocomplete textField layout
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_place_autocomplete, autoCompleteOptions)
        (binding.inputLocation.editText as AutoCompleteTextView).setAdapter(adapter)
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

    /**
     * This adapt the list of takeoff intervals to the autocomplete editText dropdown
     * @param autoCompleteTextView represent the required autocomplete editText
     */
    private fun travelTimeOptionAdapter(autoCompleteTextView: AutoCompleteTextView) {
        //Below is all the possible travel intervals
        val takeOffPeriods = listOf(
            "Morning from 4 a.m -> 12 a.m",
            "Afternoon from 1 p.m -> 5 p.m ",
            "Night from from 6 p.m -> 12 p.m"
        )
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_place_autocomplete, takeOffPeriods)
        autoCompleteTextView.setAdapter(adapter)
    }


    companion object {
        //Request code to request permission
        const val REQUEST_LOCATION_CODE = 1
    }
}