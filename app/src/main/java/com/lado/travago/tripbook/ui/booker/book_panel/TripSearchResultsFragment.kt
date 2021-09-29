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
import com.google.firebase.firestore.ListenerRegistration
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchResultBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
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
        tripsListener
        liveDataObserver()

        return binding.root
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
                        tripsListener
                    }
                    else -> "Something went wrong, try again now or later"
                }
            )

        }

    /**
     * We get all agency generated trips with the characteristics(locality, destination) searched by the booker
     * Since these characteristics like prices can be changing, we put a snapshot listener
     */
    private val tripsListener: ListenerRegistration
        get() {
            viewModel.setField(FieldTags.ON_LOADING, true)
            val sortedNames = listOf(viewModel.localityName, viewModel.destinationName).sorted()
            return viewModel.firestoreRepo.db.collectionGroup("Trips_agency")
                .limit(10)
                .whereEqualTo("townNames.town1", sortedNames.first())
                .whereEqualTo("townNames.town2", sortedNames.last())
                .addSnapshotListener(requireActivity()) { tripsSnapshot, error ->
                    if (tripsSnapshot != null) {
                        when {
                            !tripsSnapshot.isEmpty -> {
                                /*1- We extract all ids of agencies which can offer this trip */
                                val agencyIDList = mutableListOf<String>()
                                tripsSnapshot.forEach {
                                    agencyIDList += it.getString("agencyID")!!
                                }
                                /*2- We get all the documents of the agencies whose id appears in the list*/
                                /*NB: We also only pick agencies which don't contain an event on that day and time*/
                                viewModel.firestoreRepo.db.collection("OnlineTransportAgency")
                                    .whereIn("id", agencyIDList.toList())
                                    .addSnapshotListener(requireActivity()) { agencySnapshot, agencyError ->
                                        if (agencySnapshot != null) {

                                            /*3- We get all the departure times of agencies which met requirements for this trip*/
                                            viewModel.firestoreRepo.db.collectionGroup("Departure_Intervals")
                                                .whereIn(
                                                    "agencyID",
                                                    agencyIDList
                                                )//Assert only intervals from selected agencies are gotten
                                                .whereGreaterThanOrEqualTo(
                                                    "fromHour",
                                                    viewModel.tripTime.hour
                                                )
                                                .whereGreaterThanOrEqualTo(
                                                    "fromMinute",
                                                    viewModel.tripTime.minutes
                                                )
                                                .whereLessThanOrEqualTo(
                                                    "toHour",
                                                    viewModel.tripTime.hour
                                                )
                                                .whereLessThanOrEqualTo(
                                                    "toMinute",
                                                    viewModel.tripTime.minutes
                                                )
                                                .addSnapshotListener(requireActivity()) { departureTimesSnapshots, departureError ->
                                                    if (departureTimesSnapshots != null) {
                                                        /*4- We then create a triple list to inflate the adapter with information*/
                                                        val agencyToTripPairList =
                                                            mutableListOf<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>()
                                                        //At this stage, we can be sure the snapshot can't be empty
                                                        /*
                                                        Actually we find a trip document of each agency and pair it with the agency
                                                       */
                                                        agencySnapshot.forEach { agencyDoc ->
                                                            val currentAgencyDepartureTimeDoc =
                                                                departureTimesSnapshots.find {
                                                                    it["agencyID"] == agencyDoc.id
                                                                }
                                                            val eventsList =
                                                                (agencyDoc["eventDateList"]
                                                                    ?: emptyList<Long>()) as List<Long>
                                                            /*
                                                            this is any event that is planned to happen before the tripDate and time
                                                            E.g if trip date and time is 22/10/2021 at 14:25,
                                                            A hindering event will be one planned for the 21/10/2021 at 14:25, or even 22/10/2021 at 14:00,
                                                            */
                                                            val hinderingEvent = eventsList.find {
                                                                viewModel.tripDateInMillis > it
                                                            }
                                                            if (hinderingEvent == null/*No hindering event found*/ && currentAgencyDepartureTimeDoc != null) {
                                                                val correspondingTripDoc =
                                                                    tripsSnapshot.find { it["agencyID"] == agencyDoc.id }!!
                                                                val departureTime =
                                                                    TimeModel.from24Format(
                                                                        currentAgencyDepartureTimeDoc.getLong(
                                                                            "departureHour"
                                                                        )!!.toInt(),
                                                                        currentAgencyDepartureTimeDoc.getLong(
                                                                            "departureMinutes"
                                                                        )!!.toInt(),
                                                                        null
                                                                    )
                                                                agencyToTripPairList += Triple(
                                                                    agencyDoc,
                                                                    correspondingTripDoc,
                                                                    departureTime
                                                                )
                                                            }
                                                        }
                                                        /*4- We inflate the adapter and stop loading*/
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
                                                                    tripsListener.remove()
                                                                    findNavController().navigate(
                                                                        TripSearchResultsFragmentDirections.actionTripSearchResultsFragmentToTripDetailsFragment(
                                                                            tripDoc.id,
                                                                            agencyID,
                                                                            isVip,
                                                                            tripTime.hour,
                                                                            tripTime.minutes,
                                                                            viewModel.tripDateInMillis
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        ).also {
                                                            it.submitList(agencyToTripPairList)
                                                            setRecycler(it)
                                                        }
                                                        viewModel.setField(
                                                            FieldTags.ON_LOADING,
                                                            false
                                                        )

                                                    } else {
                                                        viewModel.setField(
                                                            FieldTags.TOAST_MESSAGE,
                                                            departureError?.handleError { }
                                                                .toString()
                                                        )
                                                    }
                                                }
                                        }
                                        agencyError?.let {
                                            viewModel.setField(FieldTags.TOAST_MESSAGE,
                                                it.handleError { }
                                            )
                                        }
                                    }
                            }
                            else -> viewModel.setField(FieldTags.ON_NO_SUCH_RESULTS, true)
                        }
                    }
                    error?.let {
                        viewModel.setField(FieldTags.TOAST_MESSAGE,
                            it.handleError { }
                        )
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
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d(this.tag, it)
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onNoSuchResults.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("NO REASULT", "NOT FOUND")
            }
        }
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
