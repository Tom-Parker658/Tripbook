package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchResultBinding
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel.*
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
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_trip_search_result,
            container,
            false
        )
        initViewModelWithArgs()
        tripsQueryListener
        liveDataObserver()

        return binding.root
    }

    private fun liveDataObserver() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.searchResultProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.searchResultProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        binding.fabSortResults.setOnClickListener {
            //1-We create a spinner with options
            MaterialAlertDialogBuilder(requireContext()).apply {
                setIcon(R.drawable.baseline_sort_24)
                setTitle("Sort By?")
                setSingleChoiceItems(
                    arrayOf(
                        "Agency Reputation",
                        "Agency Name",
                        "Trip Price",
                        "Trip VIP Price",
                        "Agency Popularity"
                    ),
                    viewModel.sortCheckedItem
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            viewModel.sortTripsResult(SortTags.REPUTATION)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 0)
                        }
                        1 -> {
                            viewModel.sortTripsResult(SortTags.AGENCY_NAME)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortTripsResult(SortTags.PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 2)
                        }
                        3 -> {
                            viewModel.sortTripsResult(SortTags.VIP_PRICES)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 3)
                        }
                        4 -> {
                            viewModel.sortTripsResult(SortTags.POPULARITY)
                            viewModel.setField(FieldTags.CHECKED_ITEM, 4)
                        }
                    }
                    try {
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {

                    }
                    dialog.dismiss()
                }

            }.create().show()
        }
        //Fills in the agency ids
        viewModel.tripsDocList.observe(viewLifecycleOwner)
        { tripDocList ->
            viewModel =
                ViewModelProvider(this@TripSearchResultsFragment)[TripSearchResultsViewModel::class.java]
            if (tripDocList.isNotEmpty()) {
                viewModel.clearAgencyIDList()
                tripDocList.forEach { tripDoc ->
                    viewModel.setLists(ListTag.AGENCY_IDS, tripDoc.getString("agencyID")!!)
                }
            }
        }
        //We launch the agency query search when the agency id list are full
        viewModel.agencyIDList.observe(viewLifecycleOwner)
        {
            if (it.isNotEmpty()) {
                agencyQueryListener
            }
        }
        //We launch the pairing process when the agencyDoc list is full
        viewModel.tripsDocList.observe(viewLifecycleOwner)
        {
            if (it.isNotEmpty()) {
                viewModel.createPairList()
            }
        }
        //Finally, we inflate the adapter with the results
        viewModel.agencyToTripDocList.observe(viewLifecycleOwner)
        { pairList ->
            if (pairList.isNotEmpty()) {
                viewModel.setField(FieldTags.ON_LOADING, false)
                adapter = TripSearchResultsAdapter(
                    TripSearchResultsClickListener {
                        //TODO: Onclick
                    }
                )
                adapter.submitList(pairList)
                setRecycler(adapter)
            }
        }
        // Incase we have no results
        viewModel.onNoSuchResults.observe(viewLifecycleOwner)
        {
            if (it) {
                Log.e("DO IT", "No results at all")
            }
        }
    }

    /**
     * Configures the results recycler
     */
    private fun setRecycler(adapter: TripSearchResultsAdapter) {
        val recyclerManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResult.layoutManager = recyclerManager
        binding.recyclerViewSearchResult.adapter = adapter
    }

    private fun initViewModelWithArgs() {
        viewModel = ViewModelProvider(this)[TripSearchResultsViewModel::class.java]
        TripSearchResultsFragmentArgs.fromBundle(requireArguments()).let {
            viewModel.setArguments(
                fromID = it.fromID,
                fromName = it.localityName,
                toName = it.destinationName,
            )
        }
    }

    private val tripsQueryListener: ListenerRegistration
        get() {
            Log.d(
                "Data",
                "fromID = ${viewModel.fromID}, fromName = ${viewModel.destinationName}, name = ${viewModel.destinationName}"
            )
            viewModel.setField(FieldTags.ON_LOADING, true)
            return viewModel.firestoreRepo.db.collection("Trips")
                .addSnapshotListener(requireActivity()) { tripSnapshot, error ->
                    if (tripSnapshot != null) {
                        viewModel.setField(FieldTags.ON_LOADING, false)
                        if (!tripSnapshot.isEmpty) {
                            Log.d("GOOD BOY", "Size: ${tripSnapshot.size()}, e.g${tripSnapshot?.first()}")
                            viewModel.setLists(ListTag.TRIPS_DOC, tripSnapshot.documents)
                        } else {
                            viewModel.setField(FieldTags.ON_NO_SUCH_RESULTS, true)
                        }
                    }
                    if (error != null) {
                        viewModel.setField(FieldTags.ON_LOADING, false)
                    }
                }
        }

    private val agencyQueryListener
        get() = viewModel.firestoreRepo.db.collection("OnlineTransportAgency")
            .whereIn("id", viewModel.agencyIDList.value!!)
            .orderBy("reputation")
            .addSnapshotListener(requireActivity()) { agencySnapshot, error ->
                if (agencySnapshot != null) {
                    viewModel.setLists(ListTag.AGENCY_DOC, agencySnapshot.documents)
                }
                if (error != null) viewModel.setField(FieldTags.ON_LOADING, false)
            }

}