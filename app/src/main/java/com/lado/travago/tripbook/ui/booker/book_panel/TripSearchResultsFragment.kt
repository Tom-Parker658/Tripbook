package com.lado.travago.tripbook.ui.booker.book_panel

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchResultBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel.*
import com.lado.travago.tripbook.ui.recycler_adapters.TripSearchResultsAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.TripSearchResultsClickListener
import com.lado.travago.tripbook.utils.contracts.BookerSignUpContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Search Screen result screen fragment
 * planned which will occur before this trip
 * @see AgencyEventPlannerViewModel
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TripSearchResultsFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchResultBinding
    private lateinit var adapter: TripSearchResultsAdapter
    private lateinit var viewModel: TripSearchResultsViewModel

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
        searchListenerLauncher()
        liveDataObserver()

        return binding.root
    }


    private fun searchListenerLauncher() {
        viewModel.tripsListener(requireActivity())
        viewModel.agencyIDList?.let {
            viewModel.agenciesListener(requireActivity(), it)
            viewModel.departureTimeListener(requireActivity(), it)
        }
    }

    private fun removeListeners() {
        viewModel.tripsListener(requireActivity()).remove()
        viewModel.agencyIDList?.let {
            viewModel.agenciesListener(requireActivity(), it).remove()
            viewModel.departureTimeListener(requireActivity(), it).remove()
        }
    }

    private fun initViewModelWithArgs() {
        viewModel = ViewModelProvider(this)[TripSearchResultsViewModel::class.java]
        TripSearchResultsFragmentArgs.fromBundle(requireArguments()).let {
            viewModel.setArguments(
                fromName = it.localityName,
                toName = it.destinationName,
                dateInMillis = it.tripDateInMillis,
                tripHour = it.tripHour,
                tripMinutes = it.tripMinutes
            )
        }
    }

    //To start the booker signUp or LogIn if the user hasn't logIn already
    private val bookerSignUpContract =
        registerForActivityResult(BookerSignUpContract()) { content ->
            viewModel.setField(
                FieldTags.TOAST_MESSAGE, when (content) {
                    Activity.RESULT_OK -> {
                        "Welcome! Dear Booker, select your trip"
                        removeListeners()
                    }
                    else -> "Something went wrong, try again now or later"
                }
            )

        }

    private fun setRecycler(resultList: MutableList<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>) {

        adapter = TripSearchResultsAdapter(
            TripSearchResultsClickListener { agencyID, tripDoc, tripTime, isVip ->
                //We check if he has an account or not
                if (viewModel.authRepo.currentUser == null) {
                    //Navigate to creation
                    bookerSignUpContract.launch(
                        Bundle.EMPTY
                    )
                } else {
                    //Navigate to config and stop all snapshot listeners
                    removeListeners()
                    findNavController().navigate(
                        TripSearchResultsFragmentDirections.actionTripSearchResultsFragmentToTripDetailsFragment(
                            tripDoc.id,
                            agencyID,
                            isVip,
                            tripTime.hour,
                            tripTime.minutes,
                            viewModel.tripDateInMillis,
                            viewModel.localityName
                        )
                    )
                }
            }
        )
        adapter.notifyDataSetChanged()
        adapter.submitList(resultList)
        binding.recyclerSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResult.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun liveDataObserver() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.searchResultProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
//                requireActivity().window.setFlags(
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                )
            } else {
                binding.searchResultProgressBar.visibility = View.GONE
//                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onNoSuchResults.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("NO RESULT", "NOT FOUND")
            }
        }

        viewModel.searchResultsTripleList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setRecycler(it)
            }
        }
        viewModel.allTripsResultsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.agencyIDList?.let { list ->
                    viewModel.agenciesListener(requireActivity(), list)
                }
            }
        }
        viewModel.allAgenciesResultList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.agencyIDList?.let { list ->
                    viewModel.departureTimeListener(requireActivity(), list)
                }
            }
        }
        viewModel.allDepartureResultList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.prepareForDisplay()
            }
        }
//}
/*
binding.fabSortResults.setOnClickListener{
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
}
*/

        /* private val tripsQueryListener: ListenerRegistration
        get() {
            //We sort the town names alphabetically
            val sortedTownList = listOf(viewModel.localityName, viewModel.destinationName).sorted()
            val town1 = sortedTownList.first()
            val town2 = sortedTownList.last()
            viewModel.setField(FieldTags.ON_LOADING, true)
            return viewModel.firestoreRepo.db.collectionGroup("Trips_agency")
                .whereEqualTo("townNames.town1", town1)
                .whereEqualTo("townNames.town2", town2)
                .limit(10L)
                .addSnapshotListener(requireActivity()) { tripSnapshot, error ->
                    if (tripSnapshot != null) {
                        viewModel.setField(FieldTags.ON_LOADING, false)
                        if (!tripSnapshot.isEmpty) {
    //                            Log.d("DO IT", tripSnapshot.documents.toString())
                            viewModel.setLists(ListTag.TRIPS_DOC, tripSnapshot.documents)
                        } else {
                            viewModel.setField(FieldTags.ON_NO_SUCH_RESULTS, true)
                        }
                    }
                    if (error != null) {
                        Log.e("ERROR", "FAILURE: ${error.message.toString()}")
                        viewModel.setField(FieldTags.ON_LOADING, false)
                    }
                }
        }

    private val agencyQueryListener
        get() = viewModel.firestoreRepo.db.collection("OnlineTransportAgency")
    //            .whereIn("id", viewModel.agencyIDList.value!!)
    //            .orderBy("reputation")
            .addSnapshotListener(requireActivity()) { agencySnapshot, error ->
                if (agencySnapshot != null) {
                    Log.d("DO IT KNOW", agencySnapshot.documents.toString())
                    viewModel.setLists(ListTag.AGENCY_DOC, agencySnapshot.documents)
                }
                if (error != null) viewModel.setField(FieldTags.ON_LOADING, false)
            }*/
    }
}
