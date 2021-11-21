package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.booking.BusOverview
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyEventPlannerViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class BusOverviewViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    //Will be used to stop the listener upon navigating away
    lateinit var registrationObject: ListenerRegistration

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _fabVisibilityState = MutableLiveData(true)
    val fabVisibilityState: LiveData<Boolean> get() = _fabVisibilityState

//    private val _destinationCounts = MutableLiveData(mutableMapOf<String, Int>())
//    val destinationCounts: LiveData<MutableMap<String, Int>> get() = _destinationCounts

    //Holds all the books for that agency for that day
    private val _allBooks = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val allBooks: LiveData<MutableList<DocumentSnapshot>> get() = _allBooks

    private val _busOverviewList = MutableLiveData(mutableListOf<BusOverview>())
    val busOverviewList: LiveData<MutableList<BusOverview>> get() = _busOverviewList

    //Tell the adapter to notify all items have changed
    private val _notifyAdapterChanges = MutableLiveData(false)
    val notifyAdapterChanges: LiveData<Boolean> get() = _notifyAdapterChanges

    /*
     * Specifies which set of buses to display
     * By default,we show the buses for the current day
     * */
    private val _dateInMillis = MutableLiveData(Date().time)
    val dateInMillis: LiveData<Long> get() = _dateInMillis

    //SpanSize
    private val _spanSize = MutableLiveData(2)
    val spanSize: LiveData<Int> get() = _spanSize

    fun invertFabVisibility() {
        _fabVisibilityState.value = !_fabVisibilityState.value!!
    }

    /**
     * All books for the agency for a particular day [_dateInMillis]
     */
    fun getAllBooks(agencyID: String, hostActivity: Activity) {
        _onLoading.value = true
        firestoreRepo.db.collection(
            "OnlineTransportAgency/$agencyID/Books/${
                Utils.formatDate(
                    dateInMillis.value!!,
                    "YYYY-MM-dd"
                )
            }/Cameroon"
        ).addSnapshotListener(hostActivity) { snapshots, error ->
            _onLoading.value = false
            _toastMessage.value = error?.handleError { }
            if (snapshots != null) {
                if (!snapshots.isEmpty) {
                    _allBooks.value = snapshots.documents
                    //TODO: addDestinationTownNames()
                } else {
                    _toastMessage.value = hostActivity.getString(R.string.text_dialog_empty_content)
                    _notifyAdapterChanges.value = true
                    //TODO: _agencyTripList.value?.clear()
                }
            }
        }.also { registrationObject = it }//To Stop the listener at will
    }

    /**
     * Analyse sort and classify books into sections and sub categories
     */
    suspend fun fromTownInterpreter() {
        _onLoading.value = true

        //Just for definition sake
        val tempBusOverViewList: MutableList<BusOverview> = _busOverviewList.value!!

        tempBusOverViewList.clear()
        _allBooks.value?.forEach { book ->
            val busOverview = tempBusOverViewList.withIndex().find {
                it.value.townName == book.getString("localityName")!!
            }

            if (busOverview != null) {
                var scansCount = busOverview.value.scansCount
                if (book.getBoolean("isScanned")!!) scansCount += 1
                tempBusOverViewList[busOverview.index] = BusOverview(
                    busOverview.value.townName,
                    busOverview.value.regionName,
                    busOverview.value.busCounts,
                    busOverview.value.bookersCount,
                    scansCount
                )
            } else
                tempBusOverViewList.add(
                    BusOverview(
                        book.getString("localityName")!!,
                        book.getString("region")!!,
                        0,
                        0,
                        0
                    )
                )

        }

        _busOverviewList.value = tempBusOverViewList
        _onLoading.value = false

    }


    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.DATE_IN_MILLIS -> _dateInMillis.value = value as Long
            FieldTags.NOTIFY_DATA_CHANGED -> _notifyAdapterChanges.value = value as Boolean
            FieldTags.SPAN_SIZE -> _spanSize.value = value as Int
        }
    }

    enum class FieldTags {
        TOAST_MESSAGE, ON_LOADING, DATE_IN_MILLIS, NOTIFY_DATA_CHANGED, SPAN_SIZE
    }

}