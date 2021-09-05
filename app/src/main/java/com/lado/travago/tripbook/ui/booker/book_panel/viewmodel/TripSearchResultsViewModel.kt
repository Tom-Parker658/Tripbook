package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchResultsViewModel : ViewModel() {
    var firestoreRepo: FirestoreRepo = FirestoreRepo()

    //These are the id of the localities and destination which is gotten for the search
    var fromID = ""
        private set

    //These are used to fill the header
    var localityName = ""
        private set
    var destinationName = ""
        private set

    var sortCheckedItem = 0

    private val _onNoSuchResults = MutableLiveData(false)
    val onNoSuchResults: LiveData<Boolean> get() = _onNoSuchResults

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    //This is the list of documents for the trips
    private val _tripsDocList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val tripsDocList: LiveData<MutableList<DocumentSnapshot>> get() = _tripsDocList

    private val _agencyIDList = MutableLiveData(mutableListOf<String>())
    val agencyIDList: LiveData<MutableList<String>> get() = _agencyIDList

    //This is the list of documents for all teh agencies which can offer the trip
    private val _agencyDocList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val agencyDocList: LiveData<MutableList<DocumentSnapshot>> get() = _agencyDocList

    //This is a list of pairs containing the list of an agency and its corresponding trip doc
    private val _agencyToTripDocList =
        MutableLiveData(mutableListOf<Pair<DocumentSnapshot, DocumentSnapshot>>())
    val agencyToTripDocList: LiveData<MutableList<Pair<DocumentSnapshot, DocumentSnapshot>>>
        get() = _agencyToTripDocList

    fun setLists(listTag: ListTag, data: Any) = when (listTag) {
        ListTag.TRIPS_DOC -> {
            _agencyDocList.value = data as MutableList<DocumentSnapshot>
        }
        ListTag.AGENCY_IDS -> {
            _agencyIDList.value!! += "$data"
        }
        ListTag.AGENCY_DOC -> {
            _agencyDocList.value = data as MutableList<DocumentSnapshot>
        }
    }

    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.ON_NO_SUCH_RESULTS -> _onNoSuchResults.value = value as Boolean
        FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
        FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
    }

    enum class FieldTags {
        ON_NO_SUCH_RESULTS,
        CHECKED_ITEM,
        ON_LOADING
    }

    fun clearAgencyIDList() = _agencyIDList.value!!.clear()

    //Matches an agency to its trip and create a pair and saves it into the pair list
    fun createPairList() {
        _agencyDocList.value!!.forEach { agencyDoc ->
            _tripsDocList.value!!.forEach { tripDoc ->
                if (agencyDoc.id == tripDoc.getString("agencyID")) {
                    _agencyToTripDocList.value!! += (agencyDoc to tripDoc)
                }
            }
        }
    }

    enum class ListTag {
        TRIPS_DOC, AGENCY_IDS, AGENCY_DOC
    }

    //Setter for the ids
    fun setArguments(
        fromID: String,
        fromName: String,
        toName: String,
    ) {
        this.fromID = fromID
        this.localityName = fromName
        this.destinationName = toName
    }
    fun sortTripsResult(sortTag: SortTags) {
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
    }

    enum class SortTags {
        REPUTATION, PRICES, VIP_PRICES, POPULARITY, AGENCY_NAME
    }

}