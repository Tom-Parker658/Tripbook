package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchResultBinding
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel
import com.lado.travago.tripbook.ui.recyclerview.adapters.TripSearchResultsAdapter
import com.lado.travago.tripbook.ui.recyclerview.adapters.TripSearchResultsClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Search Screen result screen fragment
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TripSearchResultsFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchResultBinding
    private lateinit var adapter: TripSearchResultsAdapter
    private lateinit var viewModel: TripSearchResultsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_trip_search_result, container, false)
        initViewModel()

        val agenciesQuery = viewModel.firestoreRepo.db.collection("Cameroon/${viewModel}")
            .limit(10)


        return binding.root
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[TripSearchResultsViewModel::class.java]
        TripSearchResultsFragmentArgs.fromBundle(requireArguments()).let {
            viewModel.setArguments(
                fromID = it.fromID,
                toID = it.toID,
                fromName = it.localityName,
                toName = it.destinationName,
                distance = it.distance
            )
        }
    }

}