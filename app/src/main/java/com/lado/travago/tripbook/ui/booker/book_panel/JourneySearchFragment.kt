package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentSearchJourneyBinding
import com.lado.travago.tripbook.model.enums.TravelTime
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.JourneySearchViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Search screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class JourneySearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchJourneyBinding
    private lateinit var viewModel: JourneySearchViewModel

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
        initViewModel()
        onFieldChange()
        adaptDestinations()
        navigateToResultScreen()

        restoreFields()
        return binding.root
    }
    /**
     * Initialises [viewModel] using the agencyName and the path gotten from the agency launched-bundle
     */
    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[JourneySearchViewModel::class.java]
    }

    /**
     * Loads all values of the fields from the viewModel in case of config change
     */
    private fun restoreFields() {
        binding.inputLocation.editText!!.setText(viewModel.location)
        binding.inputDestination.editText!!.setText(viewModel.destination)
        binding.checkboxVip.isChecked = viewModel.vip
        binding.inputTravelDate.editText!!.setText(
            Utils.formatDate(
                viewModel.dateInMillis,
                "EEEE, dd MMMM yyyy"
            )
        )
        binding.optionTravelTime.check(
            when(viewModel.travelTime){
                TravelTime.MORNING -> binding.optionMorning.id
                TravelTime.NIGHT -> binding.optionNight.id
                TravelTime.AFTERNOON -> binding.optionNoon.id
                else -> 0
            }
        )
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.inputLocation.editText!!.addTextChangedListener {
            viewModel.setFields(JourneySearchViewModel.FieldTags.LOCATION, binding.inputLocation.editText!!.text.toString())
        }
        binding.inputDestination.editText!!.addTextChangedListener{
            viewModel.setFields(JourneySearchViewModel.FieldTags.DESTINATION, binding.inputDestination.editText!!.text.toString())
        }

        binding.checkboxVip.setOnCheckedChangeListener { _, isVip ->
            viewModel.setFields(JourneySearchViewModel.FieldTags.VIP, isVip)
        }
        // Add an onClick listener to the birthday endIcon to select the birthday
        binding.fabSelectDate.setOnClickListener { pickDate() }

        binding.optionTravelTime.setOnCheckedChangeListener { _, id ->
            val travelTime = when (id) {
                binding.optionNoon.id-> TravelTime.AFTERNOON
                binding.optionMorning.id-> TravelTime.MORNING
                binding.optionNight.id-> TravelTime.NIGHT
                else -> TravelTime.UNKNOWN
            }
            viewModel.setFields(JourneySearchViewModel.FieldTags.TRAVEL_TIME, travelTime)
        }
    }

    /**
     * Generate a Date picker and return the picked date formatted which then set to
     * the text of the [textInputEditText]. The travel date can be any day
     * from the current day to the 2 weeks after
     */
    private fun pickDate() {
        val calendar = Calendar.getInstance()//An instance of the current Calendar
        val today = calendar.timeInMillis// The current date in millis
        calendar.roll(Calendar.WEEK_OF_YEAR, 2)//Rolling the current date by 2weeks
        val oneMonthAfter = calendar.timeInMillis// The date after 2 weeks


        //We create our date picker which the user will use to enter his travel day
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose your Departure Day!")//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)//Set date picker interface to display a calendar
            .build()

        //When the user chooses a date, we format it from milliseconds to a nice looking date

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setFields(JourneySearchViewModel.FieldTags.TRAVEL_DAY, it)
            binding.inputTravelDate.editText!!.setText(SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date(it)))

//            else {
//                Toast.makeText(requireContext(),
//                    "Invalid Date!! Choose a date from now to 2 week.",
//                    Toast.LENGTH_LONG).show()
//                pickDate()
//            }
        }
        datePicker.showNow(parentFragmentManager, "")
    }

    /**
     * Adapt the array of destinations to the location and destination auto complete text view
     */
    private fun adaptDestinations(){
        val destinations =  resources.getStringArray(R.array.destinations)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_textview, destinations.toList())
        (binding.inputLocation.editText as AutoCompleteTextView).setAdapter(adapter)
        (binding.inputDestination.editText as AutoCompleteTextView).setAdapter(adapter)
    }

    /**
     * Navigates to the result screen after the search button is clicked
     */
    private fun navigateToResultScreen(){
        binding.btnSearchJourney.setOnClickListener {
            if(viewModel.location!="" && viewModel.destination!="" && viewModel.dateInMillis!=0L && viewModel.travelTime!=TravelTime.UNKNOWN){
                it.findNavController().navigate(JourneySearchFragmentDirections.actionJourneySearchFragmentToJourneySearchResultFragment())
                //Searches for the journeys
                CoroutineScope(Dispatchers.Main).launch {
//                    viewModel.searchMyJourney()
                }
            }
            else{
                Toast.makeText(requireContext(), "Fill all info!", Toast.LENGTH_LONG).show()
            }
        }
    }

}