package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyEventPlannerBinding
import com.lado.travago.tripbook.databinding.LayoutCreateEventBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyEventPlannerViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyEventPlannerViewModel.FieldTags
import com.lado.travago.tripbook.ui.recycler_adapters.AgencyEventPlannerAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.AgencyEventPlannerClickListener
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.*
import java.util.*

/**
 * This is a fragment to permit Agencies to plan a maintenance/repair/deletion beforehand, This helps users avoid
 * booking tickets from an agency which will go off before the booker takes the trip or to AdminScanners
 * configuring and changing sensitive information about the agency while bookers are booking with outdated
 * information. UseCases
 * TRIP/TOWNS DELETION(S)
 * AGENCY_DELETION
 * PARKS_TRANSFER (changing GPS location of a park)
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyEventPlannerFragment : Fragment() {
    private lateinit var viewModel: AgencyEventPlannerViewModel
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentAgencyEventPlannerBinding
    private lateinit var adapter: AgencyEventPlannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_event_planner,
            null,
            false
        )
        initViewModel()

        eventsListener
        observeLiveData()

        return binding.root
    }


    private fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            viewModel.onLoading.observe(viewLifecycleOwner) {
                if (it) {
                    binding.eventProgressBar.visibility = View.VISIBLE
                    //Makes the screen untouchable
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    )
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                    )
                } else {
                    binding.eventProgressBar.visibility = View.GONE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                }
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        binding.fabPlanEvent.setOnClickListener {
            showAddDialog()
        }
        binding.fabHelpEventPlanner.setOnClickListener {
            showHelpSnackBar().show()
        }
    }

    private fun showHelpSnackBar() =
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.baseline_help_24)
            .setMessage(R.string.text_help_event_planner)
            .setPositiveButton(getString(R.string.text_btn_event_planner_add)) { dialog, _ ->
                showAddDialog()
                dialog.dismiss()
            }
            .create()


    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

    private fun showAddDialog() {
        val eventBinding = DataBindingUtil.inflate<LayoutCreateEventBinding>(
            layoutInflater,
            R.layout.layout_create_event,
            null,
            false
        )

        fun datePicker() {
            val titleText = "Event Date"
            val calendar = Calendar.getInstance()//An instance of the current Calendar
            val minDate = calendar.timeInMillis // The current date(today) in millis

            //We create constraint so that the user can only select dates between a particular interval
            val bounds = CalendarConstraints.Builder()
                .setStart(minDate)//Smallest date which can be selected
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
                viewModel.setField(FieldTags.EVENT_DATE, it)
                eventBinding.editTextDate.editText!!.setText(
                    Utils.formatDate(it, "EEEE dd MMMM YYYY")
                )
            }
            datePicker.showNow(childFragmentManager, "")
        }

        fun timePicker() {
            val titleText = "At what time?"
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText(titleText)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                viewModel.setField(
                    FieldTags.EVENT_TIME,
                    TimeModel.from24Format(
                        timePicker.hour, timePicker.minute, null
                    )
                )
                eventBinding.editTextTime.editText!!.setText(
                    viewModel.eventTime!!.formattedTime(
                        TimeModel.TimeFormat.FORMAT_24H
                    )
                )
            }
            timePicker.showNow(childFragmentManager, "")
        }

        fun clickListeners() {
            eventBinding.editTextDate.setOnClickListener {
                datePicker()
            }
            eventBinding.editTextDate.setEndIconOnClickListener {
                datePicker()
            }
            eventBinding.editTextTime.setOnClickListener {
                timePicker()
            }
            eventBinding.editTextTime.setEndIconOnClickListener {
                timePicker()
            }
            when (eventBinding.chipGroupEventTypes.checkedChipId) {
                eventBinding.chipDeletion.id -> viewModel.setField(
                    FieldTags.EVENT_TYPE, "DELETION"
                )
                eventBinding.chipMaintenance.id -> viewModel.setField(
                    FieldTags.EVENT_TYPE,
                    "MAINTENANCE"
                )
            }
        }
        clickListeners()
        eventBinding.apply {
            editTextReason.editText!!.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            editTextDate.editText!!.inputType = InputType.TYPE_NULL
            editTextTime.editText!!.inputType = InputType.TYPE_NULL
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create an Event")
            .setView(eventBinding.root)
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .setPositiveButton("Create") { dialogInterface, _ ->
                if (eventBinding.editTextReason.editText!!.text.isBlank() || viewModel.eventTime == null || viewModel.eventDateInMillis == 0L)
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Give all information required!"
                    )
                else
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.createEvent(
                            parentViewModel.bookerDoc.value!!.getString("agencyID")!!,
                            parentViewModel.bookerDoc.value!!.id
                        )
                    }
                dialogInterface.cancel()
            }
            .create().show()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AgencyEventPlannerViewModel::class.java]
        parentViewModel =
            ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
    }

    private val eventsListener
        get() = viewModel.firestoreRepo.db.collection(
            "OnlineTransportAgency/${
                parentViewModel.bookerDoc.value?.getString(
                    "agencyID"
                ) ?: "dsfsd" //TODO: Remove this 
            }/Events"
        ).addSnapshotListener(requireActivity()) { snapShot, error ->
            if (snapShot != null) {
                if (!snapShot.isEmpty) {
                    AgencyEventPlannerAdapter(
                        clickListener = AgencyEventPlannerClickListener { eventID ->
                            CoroutineScope(Dispatchers.Main).launch {
                                val currentEventDate =
                                    snapShot.documents.find { it.id == eventID }!!
                                        .getTimestamp("eventDate")!!
                                if (currentEventDate < Timestamp.now()
                                ) {
                                    // We can delete event
                                    viewModel.deleteEvent(
                                        parentViewModel.bookerDoc.value!!.getString(
                                            "agencyID"
                                        )!!, eventID,
                                        currentEventDate.toDate().time
                                    )

                                } else {
                                    //We can stop event
                                    viewModel.stopEvent(
                                        parentViewModel.bookerDoc.value!!.getString(
                                            "agencyID"
                                        )!!, eventID,
                                        currentEventDate.toDate().time
                                    )
                                }
                            }
                        }
                    )
                    try {
                        binding.recyclerEvents.layoutManager = LinearLayoutManager(requireContext())
                        adapter.submitList(snapShot.documents)
                        binding.recyclerEvents.adapter = adapter
                    } catch (e: Exception) {
                        //
                    }
                } else {
                    try {
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        //When list has been cleared
                    }
                }
            } else {
                error?.handleError { }?.let { viewModel.setField(FieldTags.TOAST_MESSAGE, it) }
            }
        }

}