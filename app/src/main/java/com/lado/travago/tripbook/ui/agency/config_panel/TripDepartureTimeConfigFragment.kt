package com.lado.travago.tripbook.ui.agency.config_panel

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripDepartureTimeConfigBinding
import com.lado.travago.tripbook.databinding.LayoutAddTripIntervalBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsDepartureTimeConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsDepartureTimeConfigViewModel.*
import com.lado.travago.tripbook.ui.recycler_adapters.TimeIntervalAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.TimeIntervalClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TripDepartureTimeConfigFragment : Fragment() {
    private lateinit var binding: FragmentTripDepartureTimeConfigBinding
    private lateinit var adapter: TimeIntervalAdapter
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var viewModel: TripsDepartureTimeConfigViewModel

    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_trip_departure_time_config,
            container,
            false
        )
        initViewModels()
        intervalListener
        handleClicks()
        handleFabVisibilities()
        observeLiveData()

        return binding.root
    }

    private fun initViewModels() {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        viewModel = ViewModelProvider(this)[TripsDepartureTimeConfigViewModel::class.java]
    }

    private fun handleClicks() {
        binding.fabAddInterval.setOnClickListener {
            AddTimeIntervalDialogFragment(parentViewModel, viewModel).showNow(
                childFragmentManager,
                ""
            )
        }
        binding.fabTimeFormat.setOnClickListener {
            //To switch time format from 24 to 12 or 12 to 24
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Time Format")
                .setSingleChoiceItems(
                    arrayOf("24 Hour Format", "12 Hour Format"),
                    when (viewModel.timeFormat.value!!) {
                        TimeModel.TimeFormat.FORMAT_24H -> 0
                        TimeModel.TimeFormat.FORMAT_12H -> 1
                    }
                ) { dialogInterface, index ->
                    when (index) {
                        0 -> {
                            if (viewModel.timeFormat.value!! == TimeModel.TimeFormat.FORMAT_12H)
                                viewModel.setField(
                                    FieldTags.TIME_FORMAT,
                                    TimeModel.TimeFormat.FORMAT_24H
                                )
                        }
                        1 -> {
                            if (viewModel.timeFormat.value!! == TimeModel.TimeFormat.FORMAT_24H)
                                viewModel.setField(
                                    FieldTags.TIME_FORMAT,
                                    TimeModel.TimeFormat.FORMAT_12H
                                )
                        }
                    }
                    dialogInterface.cancel()
                }
        }
        binding.fabSettingsSpan.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("How many items per row?")
                .setSingleChoiceItems(
                    arrayOf("1", "2", "3", "4", "5", "6"),
                    viewModel.spanSize.value!!
                ) { dialogInterface, index -> // Logically, the index is anyChoice-1 so we directly take index+1 as span
                    viewModel.setField(
                        FieldTags.SPAN_SIZE,
                        index + 1
                    )
                    dialogInterface.cancel()
                }
        }

    }

    private fun handleFabVisibilities() {
        // Toggle visibilities fab
        binding.fabToggleVisibility.setOnClickListener { toggleFab ->
            binding.fabAddInterval.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabSettingsSpan.let {
                if (it.isShown) it.hide() else it.show()
            }
            binding.fabAddInterval.let {
                if (it.isShown) {
                    it.hide()
                    (toggleFab as FloatingActionButton).setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.baseline_visibility_24,
                            requireActivity().theme
                        )
                    )
                } else {
                    (toggleFab as FloatingActionButton).setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.baseline_visibility_off_24,
                            requireActivity().theme
                        )
                    )
                    it.show()
                }
            }
            binding.fabTimeFormat.let {
                if (it.isShown) it.hide() else it.show()
            }
        }
    }

    private fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBarInterval.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBarInterval.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner)
        {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.timeFormat.observe(viewLifecycleOwner)
        {
            intervalListener
        }
        viewModel.spanSize.observe(viewLifecycleOwner)
        {
            try {
                initRecycler(it)
            } catch (e: Exception) {
                //
            }
        }
    }

    class AddTimeIntervalDialogFragment(
        private val parentViewModel: AgencyConfigViewModel,
        private val viewModel: TripsDepartureTimeConfigViewModel
    ) : DialogFragment() {
        private val creationBinding = DataBindingUtil.inflate<LayoutAddTripIntervalBinding>(
            layoutInflater,
            R.layout.layout_add_trip_interval,
            null,
            false
        )

        private fun validateFields() {
            if (viewModel.fromHour == null || viewModel.fromMinutes == null) {
                viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Initial Time.")
                creationBinding.editTextFrom.requestFocus()
            } else if (viewModel.toHour == null || viewModel.toMinutes == null) {
                viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Final Time.")
                creationBinding.editTextTo.requestFocus()
            } else if (viewModel.departureHour == null && viewModel.departureMinutes == null) {
                viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Departure Time.")
                creationBinding.editTextDepartureTime.requestFocus()
            } else if (viewModel.intervalName.isBlank()) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    "You must give this INTERVAL a valid name"
                )
                creationBinding.editTextName.requestFocus()
            } else if (
                TimeModel.timesDifferenceInMinutes(
                    TimeModel.from24Format(viewModel.toHour!!, viewModel.toMinutes!!, null),
                    TimeModel.from24Format(viewModel.fromHour!!, viewModel.fromMinutes!!, null),
                ) == null
            ) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    "Logically Initial Time can't be greater than final right?"
                )
                creationBinding.editTextFrom.requestFocus()
            } else if (TimeModel.timesDifferenceInMinutes(
                    TimeModel.from24Format(
                        viewModel.departureHour!!,
                        viewModel.departureHour!!,
                        null
                    ),
                    TimeModel.from24Format(viewModel.toHour!!, viewModel.toMinutes!!, null),
                ) == null
            ) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    "Please the departure time must be greater the the final time"
                )
                creationBinding.editTextDepartureTime.requestFocus()
            } else if (TimeModel.timesDifferenceInMinutes(
                    TimeModel.from24Format(
                        viewModel.departureHour!!,
                        viewModel.departureHour!!,
                        null
                    ),
                    TimeModel.from24Format(viewModel.toHour!!, viewModel.toMinutes!!, null),
                )!! < 15
            ) {
                viewModel.setField(
                    FieldTags.TOAST_MESSAGE,
                    "Please the departure time must be at least 15 minutes after the final Time!"
                )
                creationBinding.editTextDepartureTime.requestFocus()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.addIntervalDoc(
                        parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                    )
                }
            }
        }

        private fun handleClicks() {
            creationBinding.editTextFrom.setEndIconOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("Pick the Starting time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()
                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setField(FieldTags.FROM_HOUR, timePicker.hour)
                    viewModel.setField(FieldTags.FROM_MINUTES, timePicker.minute)
                    timePicker.dismiss()
                }
            }

            creationBinding.editTextTo.setEndIconOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("Pick the Ending time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()
                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setField(FieldTags.TO_HOUR, timePicker.hour)
                    viewModel.setField(FieldTags.TO_MINUTES, timePicker.minute)
                    timePicker.dismiss()
                }
            }

            creationBinding.editTextDepartureTime.setEndIconOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("What is the departure time for this interval")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()
                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setField(FieldTags.DEPARTURE_HOUR, timePicker.hour)
                    viewModel.setField(FieldTags.DEPARTURE_MINUTES, timePicker.minute)
                    timePicker.dismiss()
                }
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
            handleClicks()
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create a Time Interval")
                .setIcon(R.drawable.baseline_add_24)
                .setMessage("Fill in the following Information")
                .setView(creationBinding.root)
                .setPositiveButton("Create") { dialog, _ ->
                    validateFields()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            dialog.cancel()
        }
    }

    private fun initRecycler(spanCount: Int) {
        binding.recyclerIntervals.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerIntervals.adapter = adapter
        adapter.notifyDataSetChanged() //TODO: Check Utility when Internet becomes available
    }

    private val intervalListener
        @SuppressLint("NotifyDataSetChanged")
        get() =
            viewModel.firestoreRepo.db.collection(
                "OnlineTransportAgency/${parentViewModel.bookerDoc.value!!.getString("agencyID")!!}/Time_Intervals"
            ).addSnapshotListener(requireActivity()) { snapshot, error ->
                if (snapshot != null) {
                    viewModel.setField(FieldTags.ON_LOADING, false)
                    if (!snapshot.isEmpty) {
                        adapter = TimeIntervalAdapter(
                            TimeIntervalClickListener { intervalID ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    viewModel.deleteIntervalDoc(
                                        parentViewModel.bookerDoc.value!!.getString(
                                            "agencyID"
                                        )!!, intervalID
                                    )
                                }
                            },
                            viewModel.timeFormat.value!!
                        )
                        adapter.submitList(snapshot.documents)
                        adapter.notifyDataSetChanged()
                        initRecycler(viewModel.spanSize.value!!)
                    } else {
                        try {
                            adapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            //Incase the list has been cleared completely
                        }
                    }
                } else error?.handleError { }?.let {
                    viewModel.setField(FieldTags.ON_LOADING, false)
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        it
                    )
                }
            }

}