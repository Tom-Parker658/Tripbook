package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.HashMap

/**
 * This is the viewModel to manage the search screen of the app -> [TripSearchViewModel]
 *
 * @property placesClient is passed as argument by the fragment to the [TripSearchViewModel] for
 * building [TripSearchViewModel]. The
 * viewModel need this [placesClient] to deal with place autocompletion & user's current location
 * which can be fed to our layout autoCompleteTextViews to display
 *
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi

class TripSearchViewModel : ViewModel() {
    private var firestoreRepo: FirestoreRepo = FirestoreRepo()
    private var storageUrl: FirestoreRepo = FirestoreRepo()

    //Knows when the locality document have been found
    private val _onLocalityResultsFound = MutableLiveData(false)
    val onLocalityResultsFound: LiveData<Boolean> get() = _onLocalityResultsFound

    //Livedata to know when to start searching
    private val _retrySearch = MutableLiveData(false)
    val retrySearch: LiveData<Boolean> get() = _retrySearch

    // This stores the locality firestore document
    private lateinit var localityDoc: DocumentSnapshot

    // This is a list which stores the list of all possible destinations from that locality
    private var destinationDocList = listOf<DocumentSnapshot>()

    private var _onLoading = MutableLiveData(false)
    val onLoading get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //We set the date to today
    val destinationNameList = mutableListOf<String>()
    var locality = ""
        private set
    var destination = ""
        private set
    var vip = false
        private set
    var distance = 0L
        private set

    /**
     * Gets the trip detail doc
     */
    suspend fun searchLocalityTowns() {
        _retrySearch.value = false
        _onLocalityResultsFound.value = false
        destinationNameList.clear()
        firestoreRepo.queryCollection(
            "/Planets/Earth/Continents/Africa/Cameroon"
        ) {
            it.whereEqualTo("name", locality)
        }.collect { localityState ->
            when (localityState) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = localityState.exception.handleError { }
                }
                is State.Success -> {
                    firestoreRepo.getCollection(
                        "/Planets/Earth/Continents/Africa/Cameroon/${localityState.data.documents.first().id}/Trips"
                    ).collect { destinationState ->
                        when (destinationState) {
                            is State.Loading -> _onLoading.value = true
                            is State.Failed -> {
                                _onLoading.value = false
                                _toastMessage.value = destinationState.exception.handleError { }
                            }
                            is State.Success -> {
                                destinationState.data.documents.forEach {
                                    destinationNameList += it.getString("destination")!!
                                }
                                localityDoc = localityState.data.documents.first()
                                destinationDocList = destinationState.data.documents
                                _onLocalityResultsFound.value = true
                                _onLoading.value = false
                            }
                        }

                    }
                }
            }
        }

        // As you see
        fun setDistance() {
            val destinationDoc = destinationDocList.find {
                it["destination"] == destination
            }
            val index = destinationDocList.indexOf(destinationDoc)
            distance = destinationDocList[index].getLong("distance")!!
        }

//    /**
//     * Get the destination info
//     */
//     fun getDestinationInfo(){
//    }
        //For the adapter
    }


    /**
     * Contains different identifiers for the fields in our searching form
     */
    enum class FieldTags {
        LOCALITY, DESTINATION, VIP, RETRY_SEARCH
    }

    /**
     * A function to set the value the fields from the searching form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setFields(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.LOCALITY -> locality = value.toString()
        FieldTags.DESTINATION -> destination = value.toString()
        FieldTags.VIP -> vip = value as Boolean
        FieldTags.RETRY_SEARCH -> _retrySearch.value = true
    }

}