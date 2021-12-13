package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripDepartureTimeConfigBinding
import com.lado.travago.tripbook.databinding.LayoutAddTripIntervalBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsDepartureTimeConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsDepartureTimeConfigViewModel.FieldTags
import com.lado.travago.tripbook.ui.recycler_adapters.TripDepartureTimeConfigAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.TimeIntervalClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TripsDepartureTimeConfigFragment : Fragment() {
    private lateinit var binding: FragmentTripDepartureTimeConfigBinding
    private lateinit var adapter: TripDepartureTimeConfigAdapter
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
        handleClicks()
        handleFabVisibilities()
        observeLiveData()

        intervalListenerLauncher()
        return binding.root
    }

    private fun intervalListenerLauncher() = viewModel.intervalListener(
        requireActivity(),
        parentViewModel.bookerDoc.value!!.getString("agencyID")!!
    )

    private fun initViewModels() {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        viewModel = ViewModelProvider(this)[TripsDepartureTimeConfigViewModel::class.java]
    }

    private fun handleClicks() {
        binding.fabAddInterval.setOnClickListener {
            viewModel.setField(FieldTags.SWITCH_ADD_DIALOG_STATE, true)
        }
        binding.fabTimeFormat.setOnClickListener {
            //To switch time format from 24 to 12 or 12 to 24
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.text_pick_time))
                .setSingleChoiceItems(
                    arrayOf(
                        ""//TODO: Time format
                    ),
                    when (viewModel.timeFormat.value!!) {
                        TimeModel.TimeFormat.FORMAT_24H -> 0
                        TimeModel.TimeFormat.FORMAT_12H -> 1
                        else -> 2//TODO: Remove
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
                .show()
        }
        binding.fabSettingsSpan.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.text_items_per_row)
                .setSingleChoiceItems(
                    arrayOf("1", "2", "3", "4", "5", "6"),
                    viewModel.spanSize.value!! - 1
                ) { dialogInterface, index -> // Logically, the index is anyChoice-1 so we directly take index+1 as span
                    viewModel.setField(
                        FieldTags.SPAN_SIZE,
                        index + 1
                    )
                    dialogInterface.cancel()
                }
                .show()
        }
        binding.fabToggleVisibility.setOnClickListener {
            viewModel.invertFabVisibility()
        }
    }

    private fun handleFabVisibilities() {
        // Toggle visibilities fab
        viewModel.fabVisibilityState.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabToggleVisibility.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_off_24,
                        requireActivity().theme
                    )
                )
                binding.fabSettingsSpan.show()
                binding.fabTimeFormat.show()
                binding.fabAddInterval.show()
            } else {
                binding.fabToggleVisibility.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.baseline_visibility_24,
                        requireActivity().theme
                    )
                )
                binding.fabSettingsSpan.hide()
                binding.fabTimeFormat.hide()
                binding.fabAddInterval.hide()
            }
        }
    }

    private fun observeLiveData() {

        viewModel.addDialogState.observe(viewLifecycleOwner) {
            val addDialog = AddTimeIntervalDialogFragment(
                parentViewModel,
                viewModel,
                childFragmentManager
            )
            if (it) {
                //1- Inflate dialog
                addDialog.showNow(
                    childFragmentManager,
                    ""
                )
                //2- Try to load in all data into the fields

            } else {
                viewModel.clearData()
                try {
                    addDialog.dismiss()
                } catch (s: Exception) {
                    //Nothing
                }
            }
        }
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

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it?.isNotBlank() == true) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }

        viewModel.timeFormat.observe(viewLifecycleOwner) {
            intervalListenerLauncher()
        }

        viewModel.spanSize.observe(viewLifecycleOwner) {
            spanSize(it)
        }

        viewModel.agencyIntervals.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter = TripDepartureTimeConfigAdapter(
                    TimeIntervalClickListener { intervalID ->
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.deleteIntervalDoc(
                                parentViewModel.bookerDoc.value!!.getString(
                                    "agencyID"
                                )!!, intervalID
                            )
                        }
                    },
                    viewModel.timeFormat.value!!,
                    resources
                )
                adapter.submitList(it)
                spanSize(viewModel.spanSize.value!!)
            }
        }
    }

    class AddTimeIntervalDialogFragment(
        private val parentViewModel: AgencyConfigViewModel,
        private val viewModel: TripsDepartureTimeConfigViewModel,
        private val manager: FragmentManager,
        private var allFieldValid: Boolean = false
    ) : DialogFragment() {
        private lateinit var creationBinding: LayoutAddTripIntervalBinding

        private fun validateFields() {
            when {
                viewModel.fromTime == null -> {
                    viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Initial Time.")
                    creationBinding.editTextFrom.requestFocus()
                }
                viewModel.toTime == null -> {
                    viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Final Time.")
                    creationBinding.editTextTo.requestFocus()
                }
                viewModel.departureTime == null -> {
                    viewModel.setField(FieldTags.TOAST_MESSAGE, "You must set Departure Time.")
                    creationBinding.editTextDepartureTime.requestFocus()
                }
                viewModel.intervalName.isBlank() -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "You must give this INTERVAL a valid name"
                    )
                    creationBinding.editTextName.requestFocus()
                }
                viewModel.fromTime!! >= viewModel.toTime!! -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Final time should be greater than initial time."
                    )
                    restoreFields()
                    creationBinding.editTextFrom.requestFocus()
                }

                //15*60*1000 is 15 minutes in milliseconds
                viewModel.departureTime!! <= viewModel.toTime!! || viewModel.departureTime!!.absDifference(
                    viewModel.toTime!!
                ) < (15 * 60 * 1000) -> {
                    viewModel.setField(
                        FieldTags.TOAST_MESSAGE,
                        "Please the departure time must be at least 15 minutes greater the the final time"
                    )
                    restoreFields()
                    creationBinding.editTextDepartureTime.requestFocus()
                }
                else -> {
                    allFieldValid = true
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.addIntervalDoc(
                            parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                        )
                    }
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
                    viewModel.setField(
                        FieldTags.FROM_TIME,
                        TimeModel.from24Format(timePicker.hour, timePicker.minute, 0, 0)
                    )
                    timePicker.dismiss()
                    restoreFields()
                }
                timePicker.showNow(manager, "")
            }

            creationBinding.editTextTo.setEndIconOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("Pick the Ending time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()

                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setField(
                        FieldTags.TO_TIME,
                        TimeModel.from24Format(timePicker.hour, timePicker.minute, 0, 0)
                    )
                    timePicker.dismiss()
                    restoreFields()
                }
                timePicker.showNow(manager, "")
            }
            //TODO: Arrange Material dialogs
            creationBinding.editTextDepartureTime.setEndIconOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("What is the departure time for this interval")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()

                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setField(
                        FieldTags.DEPARTURE_TIME,
                        TimeModel.from24Format(timePicker.hour, timePicker.minute, 0, 0)
                    )
                    timePicker.dismiss()
                    restoreFields()
                }
                timePicker.showNow(manager, "")
            }
            creationBinding.editTextName.editText!!.addTextChangedListener {
                viewModel.setField(FieldTags.INTERVAL_NAME, it.toString())
            }
        }

        private fun restoreFields() {
            creationBinding.editTextName.editText!!.setText(viewModel.intervalName)
            if (viewModel.fromTime != null) {
                creationBinding.editTextFrom.editText!!.setText(
                    viewModel.fromTime!!.formattedTime(viewModel.timeFormat.value!!)
                )
            }
            if (viewModel.toTime != null) {
                creationBinding.editTextTo.editText!!.setText(
                    viewModel.toTime!!.formattedTime(viewModel.timeFormat.value!!)
                )
            }
            if (viewModel.departureTime != null) {
                creationBinding.editTextDepartureTime.editText!!.setText(
                    viewModel.departureTime!!.formattedTime(viewModel.timeFormat.value!!)
                )
            }

        }

        override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
            creationBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.layout_add_trip_interval,
                null,
                false
            )
            handleClicks()
            restoreFields()
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create a Trip Interval")
                .setIcon(R.drawable.baseline_add_24)
                .setView(creationBinding.root)
                .setPositiveButton(R.string.text_save) { _, _ ->
                    validateFields()
                }
                .setNegativeButton(R.string.text_cancel) { _, _ ->
                    viewModel.setField(FieldTags.SWITCH_ADD_DIALOG_STATE, false)
                }
                .create()
        }

    }

    private fun spanSize(spanCount: Int) {
        binding.recyclerIntervals.layoutManager = GridLayoutManager(requireContext(), spanCount)
        if (this::adapter.isInitialized) {
            binding.recyclerIntervals.adapter = adapter
            adapter.notifyDataSetChanged() //TODO: Check Utility when Internet becomes available
        }
    }


}