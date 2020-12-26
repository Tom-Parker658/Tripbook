package com.lado.travago.tripbook.ui.booker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentJourneySearchResultBinding
import com.lado.travago.tripbook.ui.recyclerview.adapters.JourneySearchResultAdapter
import com.lado.travago.tripbook.viewmodel.booker.JourneySearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Search Screen result screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class JourneySearchResultFragment : Fragment() {
    private lateinit var binding: FragmentJourneySearchResultBinding
    private val adapter = JourneySearchResultAdapter()
    private lateinit var viewModel: JourneySearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_journey_search_result, container, false)
        initViewModel()
        setAdapter()
        updateRecyclerView()
        return binding.root
    }

    /**
     * Initialises and sets the recycler view adapter to the defined adapter of [JourneySearchResultAdapter]
     */
    private fun setAdapter() {
        binding.recyclerViewSearchResult.adapter = adapter
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[JourneySearchViewModel::class.java]
    }
    //Adds the newly created scannerInfo to the adapter list to add it ot the recyclerView
    private fun updateRecyclerView() = viewModel.resultsList.observe(viewLifecycleOwner) {
        it?.let {
            adapter.submitList(it)
        }
    }

}