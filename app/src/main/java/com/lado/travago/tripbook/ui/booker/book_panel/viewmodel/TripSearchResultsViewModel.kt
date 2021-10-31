package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @property tripDateInMillis is the combined TripDate+TripTime to helps us avoid all events
 */

@ExperimentalCoroutinesApi
class TripSearchResultsViewModel : ViewModel() {
    var firestoreRepo: FirestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()

    //These are used to fill the header
    var localityName = ""
        private set
    var destinationName = ""
        private set

    lateinit var tripTime: TimeModel
    var tripDateInMillis = 0L

    var sortCheckedItem = 0

    private val _onNoSuchResults = MutableLiveData(false)
    val onNoSuchResults: LiveData<Boolean> get() = _onNoSuchResults

//    private val _agencyIDList = MutableLiveData(mutableListOf<String>())
//    val agencyIDList: LiveData<MutableList<String>> get() = _agencyIDList

    //All the live documents
    private val _allTripsResultsList = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val allTripsResultsList: LiveData<MutableList<DocumentSnapshot>> get() = _allTripsResultsList

    private val _allAgenciesResultList = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val allAgenciesResultList: LiveData<MutableList<DocumentSnapshot>> get() = _allAgenciesResultList

    private val _allDepartureTimeResultList = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val allDepartureResultList: LiveData<MutableList<DocumentSnapshot>> get() = _allDepartureTimeResultList

    //Triple to load on the recycler view
    private val _searchResultsTripleList =
        MutableLiveData(mutableListOf<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>())
    val searchResultsTripleList: LiveData<MutableList<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>> get() = _searchResultsTripleList


    //This a snapshot to know from where in the DB to start retrieving new documents when the user reaches the bottom of th screen
    private val startAfterSnapshot: DocumentSnapshot? = null

    // The field to order results by and if ASCENDING or DESC..
    private val _sortBy = MutableLiveData("")
    val sortBy: LiveData<String> get() = _sortBy

    // This is to rebind data
    private val _adaptResults = MutableLiveData(false)
    val adaptResults: LiveData<Boolean> get() = _adaptResults

    val sortDirection: Query.Direction? = null

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.ON_NO_SUCH_RESULTS -> _onNoSuchResults.value = value as Boolean
        FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int

        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value as String
    }

    enum class FieldTags {
        ON_NO_SUCH_RESULTS,
        CHECKED_ITEM,
        TOAST_MESSAGE
    }

    //Setter for the ids
    fun setArguments(
        fromName: String,
        toName: String,
        dateInMillis: Long,
        tripHour: Int,
        tripMinutes: Int
    ) {
        localityName = fromName
        destinationName = toName
        tripDateInMillis = dateInMillis
        tripTime = TimeModel.from24Format(tripHour, tripMinutes, null)
    }


    /**
     *
     */
    private fun sortedTripSearchQuery(): Query {
        _onLoading.value = true
        val sortedNames = listOf(localityName, destinationName).sorted()
        return firestoreRepo.db.collectionGroup("Trips_agency")
            .whereEqualTo("townNames.town1", sortedNames.first())
            .whereEqualTo("townNames.town2", sortedNames.last())
//            startAfterSnapshot?.let {
//                query.startAfter(it)
//            }
//            _sortBy.value?.let {
//                if (it.isNotBlank()) query.orderBy(it)
//            }
            .limit(10L)
    }

    fun tripsListener(
        hostActivity: Activity
    ) = sortedTripSearchQuery()
        .addSnapshotListener(hostActivity) { querySnapshot, error ->
            _onLoading.value = false
            if (querySnapshot != null)
                if (!querySnapshot.isEmpty) {
                    _allTripsResultsList.value = querySnapshot.documents
                } else {
                    _onNoSuchResults.value = true
                    agencyIDList?.let {
                        agenciesListener(hostActivity, it)
                    }
                    _toastMessage.value = "No Trips found from $localityName to $destinationName"
                }

            error?.let {
                _toastMessage.value = it.handleError { }
            }
        }

    //Contents all the agency selected from the trip search
    val agencyIDList: List<String>?
        get() {
            val tempIDList = emptyList<String>().toMutableList()
            _allTripsResultsList.value?.forEach {
                tempIDList += it.getString("agencyID")!!
            }
            return if (tempIDList.isEmpty()) null else tempIDList
        }

    fun agenciesListener(
        hostActivity: Activity,
        agencyIDList: List<String>
    ) = firestoreRepo.db.collection("OnlineTransportAgency")
        .whereIn("id", agencyIDList)
        .orderBy("reputation")
        .addSnapshotListener(hostActivity) { querySnapshot, error ->
            _onLoading.value = false
            if (querySnapshot != null) {
                _allAgenciesResultList.value = querySnapshot.documents
                departureTimeListener(hostActivity, agencyIDList)
            }
            error?.let {
                _toastMessage.value = it.handleError { }
            }
        }

    fun departureTimeListener(
        hostActivity: Activity,
        agencyIDList: List<String>
    ) = firestoreRepo.db.collectionGroup("Departure_Intervals")
        //Assert only intervals from selected agencies are gotten
        .whereIn("agencyID", agencyIDList)
//        .whereGreaterThanOrEqualTo("fromInSeconds", tripTime.timeInSeconds)
//        .whereLessThan("toInSeconds", tripTime.timeInSeconds)
        .addSnapshotListener(hostActivity) { querySnapshot, error ->
            _onLoading.value = false
            if (querySnapshot != null) {
                if (!querySnapshot.isEmpty) {
                    _allDepartureTimeResultList.value = querySnapshot.documents
                    prepareForDisplay()
                } else {
                    _toastMessage.value = "No departure time found"
                }
            }
            error?.let {
                _toastMessage.value = it.handleError { }
            }
        }


    fun prepareForDisplay() {
        _searchResultsTripleList.value?.clear()
        val tempTriple = mutableListOf<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>()
        _allAgenciesResultList.value?.forEach { agencyDoc ->
            val currentAgencyDepartureTimeDoc =
                _allDepartureTimeResultList.value?.find {
                    it["agencyID"] == agencyDoc.id
                }
            val eventsList = (agencyDoc["eventDateList"] as? List<Long>
                ?: emptyList())
            val hinderingEvent =
                eventsList.find { tripDateInMillis > it }
            if (hinderingEvent == null/*No hindering event found*/ && currentAgencyDepartureTimeDoc != null) {
                val correspondingTripDoc =
                    _allTripsResultsList.value!!.find { it["agencyID"] == agencyDoc.id }!!
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
                tempTriple += Triple(
                    agencyDoc,
                    correspondingTripDoc,
                    departureTime
                )
            }
        }
        _searchResultsTripleList.value = tempTriple
    }

/*fun sortTripsResult(sortTag: SortTags) {
    when (sortTag) {
        SortTags.REPUTATION -> _agencyDocList.value?.sortBy {
            it["reputation"] as String
        }
        SortTags.PRICES -> _tripsDocList.value?.sortBy {
            (it["normalPrice"]!! as Double).toLong()
        }
        SortTags.VIP_PRICES -> _tripsDocList.value?.sortBy {
            (it["vipPrice"]!! as Double).toLong()
        }
        SortTags.AGENCY_NAME -> _agencyDocList.value?.sortBy {
            (it["name"] as Double).toLong()
        }
        SortTags.POPULARITY -> {
            //TODO: Popularity sort
        }
    }
}*/

//    enum class SortTags {
//        REPUTATION, PRICES, VIP_PRICES, POPULARITY, AGENCY_NAME
//    }

}