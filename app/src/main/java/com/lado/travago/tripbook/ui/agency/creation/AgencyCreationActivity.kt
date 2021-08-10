package com.lado.travago.tripbook.ui.agency.creation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityAgencyCreationBinding
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationViewModel.*
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AgencyCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyCreationBinding
    lateinit var viewModel: AgencyCreationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_creation)

        viewModel = ViewModelProvider(this)[AgencyCreationViewModel::class.java]

        observeLiveData()
        //setups action bar with the back button
        val navController = findNavController(R.id.my_booker_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }


    private fun observeLiveData() {
        viewModel.loading.observe(this) {
            if (it == true) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }
        viewModel.toastMessage.observe(this){
            if(it.isNotBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.saveInfo.observe(this){
            if(it){
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.setField(FieldTags.SAVE_INFO, false)
                    viewModel.saveAgencyInfo()
                }
            }
        }
        viewModel.onInfoSaved.observe(this){
            if(it){
                try {
                    findNavController(binding.myAgencyNavHostFragment.id).navigate(R.id.action_agencyCreation1Fragment_to_agencyCreationFinalFragment)
                }catch(exception: Exception){
                    //Incase we are already in that screen
                }
            }
        }
    }

    /**
     * Navigate up in the stack when the back button is clicked
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(binding.myAgencyNavHostFragment.id)
        return navController.navigateUp()
    }


}