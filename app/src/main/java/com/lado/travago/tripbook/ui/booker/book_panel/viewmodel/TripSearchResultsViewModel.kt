package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.TimeModel
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

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage


    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.ON_NO_SUCH_RESULTS -> _onNoSuchResults.value = value as Boolean
        FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
        FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value as String
    }

    enum class FieldTags {
        ON_NO_SUCH_RESULTS,
        CHECKED_ITEM,
        ON_LOADING,
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

    enum class SortTags {
        REPUTATION, PRICES, VIP_PRICES, POPULARITY, AGENCY_NAME
    }

}