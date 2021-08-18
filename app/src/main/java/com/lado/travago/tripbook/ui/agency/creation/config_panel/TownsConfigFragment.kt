package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTownsConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel
import com.lado.travago.tripbook.ui.recyclerview.adapters.TownConfigAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


/**
 * A fragment used by agency admins to add and subtract towns and access trips from that town.
 */
@ExperimentalCoroutinesApi
class TownsConfigFragment : Fragment() {
    private lateinit var viewModel: TownsConfigViewModel
    private lateinit var binding: FragmentTownsConfigBinding
    private lateinit var adapter: TownConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_towns_config, container, false)
        viewModel = ViewModelProvider(this)[TownsConfigViewModel::class.java]
        observeLiveData()
        try {
            setRecycler()
        }catch (e: Exception){
            //TODO: LOad list first
        }
        return binding.root
    }

    /**
     * Configures the towns recycler
     */
    private fun setRecycler(){
        var recyclerManager = GridLayoutManager(context, 2)
        binding.recyclerTowns.layoutManager = recyclerManager
        binding.recyclerTowns.adapter = adapter
    }

    private fun observeLiveData(){
        viewModel.retry.observe(viewLifecycleOwner){
            if (it) CoroutineScope(Dispatchers.Main).launch {  viewModel.getData() }
        }
        //Submit list to inflate recycler view
        viewModel.townDocList.observe(viewLifecycleOwner){
            adapter = TownConfigAdapter(exemptedTownsList = viewModel.exemptedTownList)
            setRecycler()
            adapter.submitList(it)
        }
        viewModel.onLoading.observe(viewLifecycleOwner){
            if(it) binding.townProgressBar.visibility = View.VISIBLE
            else  binding.townProgressBar.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner){
            if(it.isNotBlank()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG)
                Log.d("TownsConfig", it)
            }
        }
    }

}