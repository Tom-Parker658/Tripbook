package com.lado.travago.tripbook.ui.agency.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyCreationFinalBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.AgencyConfigActivity
import com.lado.travago.tripbook.utils.contracts.AgencyConfigContract
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreationFinalFragment : Fragment() {
    private lateinit var binding: FragmentAgencyCreationFinalBinding
    private lateinit var viewModel: AgencyCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_agency_creation_final,
            container,
            false
        )
        initViewModel()
        onClickListeners()

    /*    //Restore data to the textFields after any configuration change
        restoreSavedData()
        onFieldChange()
         uiScope.launch {
             onBtnCreateClicked()
         }
        navigateToUserCreation()
*/
        return binding.root
    }

  /*  override fun onSaveInstanceState(outState: Bundle) {
        onSaveRadioValues()
        super.onSaveInstanceState(outState)
    }

    private fun onFieldChange() {
        binding.numVehicles.editText!!.addTextChangedListener {
            viewModel.setField(AgencyCreationViewModel.IntentTags.NUM_VEHICLES, binding.numVehicles.editText!!.text.toString())
        }
        binding.costPerKm.editText!!.addTextChangedListener{
            viewModel.setField(AgencyCreationViewModel.IntentTags.COST_PER_KM,  binding.costPerKm.editText!!.text.toString())
        }
    }
*/
    /*
     * Restore all saved data to their respective views
    private fun restoreSavedData() {
        binding.costPerKm.editText!!.setText(viewModel.costPerKm)
        binding.numVehicles.editText!!.setText(viewModel.vehicleNumberField)
        binding.radioNorth.isChecked = viewModel.regions.contains(Region.NORTH)
        binding.radioEast.isChecked = viewModel.regions.contains(Region.EAST)
        binding.radioWest.isChecked = viewModel.regions.contains(Region.WEST)
        binding.radioSouthWest.isChecked = viewModel.regions.contains(Region.SOUTH_WEST)
        binding.radioSouth.isChecked = viewModel.regions.contains(Region.SOUTH)
        binding.radioLittoral.isChecked = viewModel.regions.contains(Region.LITTORAL)
        binding.radioCentre.isChecked = viewModel.regions.contains(Region.CENTER)
        binding.radioAdamawa.isChecked = viewModel.regions.contains(Region.ADAMAWA)
        binding.radioFarNorth.isChecked = viewModel.regions.contains(Region.EXTREME_NORTH)
        binding.radioNorthWest.isChecked = viewModel.regions.contains(Region.NORTH_WEST)
    }
    */
    /**
     * Adds the region to the list of regions if it's checked
    private fun onSaveRadioValues(){
        if (binding.radioCentre.isChecked) viewModel.regions.add(Region.CENTER)
        else viewModel.regions.remove(Region.CENTER)

        if (binding.radioNorth.isChecked) viewModel.regions.add(Region.NORTH)
        else viewModel.regions.remove(Region.NORTH)

        if (binding.radioEast.isChecked) viewModel.regions.add(Region.EAST)
        else viewModel.regions.remove(Region.EAST)

        if ( binding.radioWest.isChecked) viewModel.regions.add(Region.WEST)
        else viewModel.regions.remove(Region.WEST)

        if (binding.radioSouth.isChecked) viewModel.regions.add(Region.SOUTH)
        else viewModel.regions.remove(Region.SOUTH)

        if (binding.radioNorthWest.isChecked) viewModel.regions.add(Region.NORTH_WEST)
        else viewModel.regions.remove(Region.NORTH_WEST)

        if (binding.radioSouthWest.isChecked) viewModel.regions.add(Region.SOUTH_WEST)
        else viewModel.regions.remove(Region.SOUTH_WEST)

        if (binding.radioAdamawa.isChecked) viewModel.regions.add(Region.ADAMAWA)
        else viewModel.regions.remove(Region.ADAMAWA)

        if (binding.radioLittoral.isChecked) viewModel.regions.add(Region.LITTORAL)
        else viewModel.regions.remove(Region.LITTORAL)

        if (binding.radioFarNorth.isChecked) viewModel.regions.add(Region.EXTREME_NORTH)
        else viewModel.regions.remove(Region.EXTREME_NORTH)
    }*/
/*
    private suspend fun onBtnCreateClicked() = binding.btnCreate.setOnClickListener {
        onSaveRadioValues()
        val anyError = when {
            binding.costPerKm.editText!!.text.isBlank() -> {
                binding.costPerKm.editText!!.requestFocus()
                requiredFieldMessage
            }
            binding.numVehicles.editText!!.text.isBlank() -> {
                binding.numVehicles.editText!!.requestFocus()
                requiredFieldMessage
            }
            viewModel.regions.size==0 -> {
                "Must select at least 1 region"
            }
            else -> null
        }

        if (anyError == null) {
            //A popup to tell the agency if he wants to continue, if yes upload the agency as required
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Continue?")
                .setMessage("Have you chosen ${viewModel.regions.size}/10 regions!")
                .setNegativeButton("Cancel!"){dialog,_ ->
                   dialog.cancel()
                }
                .setPositiveButton("Continue!"){dialog, _ ->
                    dialog.dismiss()
                    uiScope.launch {
                        viewModel.saveAgencyInfo()
                    }
                }
                .show()
            } else//Show snackbar message
                Toast.makeText(requireActivity(), anyError, Toast.LENGTH_LONG).show()

    }

    private fun navigateToUserCreation(){
        viewModel.onOtaCreated.observe(viewLifecycleOwner){
            if(it) {
                startActivity(
                    //Starts the scanner creation activity using the agency name and the agency firestore path
                    Intent(context, BookerCreationActivity::class.java)
                        .putExtra(AgencyCreationActivity.KEY_AGENCY_NAME, viewModel.nameField)
                        .putExtra(AgencyCreationActivity.KEY_OTA_PATH, viewModel.otaPath)
                )
            }
        }
    }*/

    /**Start and return the result from the activity config intent*/
    private val agencyConfigIntent = registerForActivityResult(AgencyConfigContract()){/*Get Results*/}

    private fun onClickListeners(){
        /**
         * Launches for configuration of trips or journeys
         */
        binding.btnTrips.setOnClickListener {
//            val configBundle = Bundle().apply {
//                putString("START_UP_OPTION", AgencyConfigActivity.AgencyConfigResources.StartUpTags.TRIPS_CONFIG.name)
//            }
//            agencyConfigIntent.launch(configBundle)
        }
    }
    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[AgencyCreationViewModel::class.java]
    }
}