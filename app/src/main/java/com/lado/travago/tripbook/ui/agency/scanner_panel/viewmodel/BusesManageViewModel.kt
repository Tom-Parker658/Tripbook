package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.google.zxing.Result
import com.journeyapps.barcodescanner.BarcodeResult
import com.lado.travago.tripbook.model.booking.TownsOverview
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

@ExperimentalCoroutinesApi
/**
 * @author Tom Parkert
 * @since 11/30/2021
 */
class BusesManageViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()

    /* MultiPurpose------------------------------------------------------------*/
    //Will be used to stop the listener upon navigating away
    lateinit var registrationObject: ListenerRegistration

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _fabVisibilityState = MutableLiveData(true)
    val fabVisibilityState: LiveData<Boolean> get() = _fabVisibilityState

    /**
     * Holds all books documents fetch from the database
     */
    private val _newBooks = MutableLiveData(mutableListOf<QuerySnapshot>())
    val newBooks: LiveData<MutableList<QuerySnapshot>> get() = _newBooks

    //Tell the adapter to notify all items have changed
    private val _notifyAdapterChanges = MutableLiveData(false)
    val notifyAdapterChanges: LiveData<Boolean> get() = _notifyAdapterChanges

    /*LocalityPage------------------------------------------------*/

    /*
    * Holds the different books from a particular location in a key-value form where bus Overview of
    * the locality is the key to the list of all the books from that locality
    */
    private val _localityOverviews = MutableLiveData(mutableListOf<TownsOverview>())
    val localityOverviews: LiveData<MutableList<TownsOverview>> get() = _localityOverviews

    /**
     * Holds the all books for a particular destination
     */
    private val _destinationOverviews = MutableLiveData(mutableListOf<TownsOverview>())
    val destinationOverviews: LiveData<MutableList<TownsOverview>> get() = _destinationOverviews

    var selectedLocality = ""

    /*
     * Specifies which set of buses to display
     * By default,we show the buses for the current day
     * */
    private val _selectedDate = MutableLiveData(Date().time)
    val selectedDate: LiveData<Long> get() = _selectedDate

    fun formattedTravelDate() =
        Utils.formatDate(
            _selectedDate.value!!,
            "YYYY-MM-dd"
        )

    //SpanSize
    private val _spanSizeLocality = MutableLiveData(2)
    val spanSizeLocality: LiveData<Int> get() = _spanSizeLocality

    //SpanSize
    private val _spanSizeDestination = MutableLiveData(2)
    val spanSizeDestination: LiveData<Int> get() = _spanSizeDestination

    fun invertFabVisibility() {
        _fabVisibilityState.value = !_fabVisibilityState.value!!
    }

    /**
     * All books for the agency for a particular day [_travelDateString]
     */
    fun getAllBooks(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.collection(
            "OnlineTransportAgency/$agencyID/Books/${
                formattedTravelDate()
            }/Cameroon"
        )
            .orderBy("localityName")
            .addSnapshotListener { query, error ->
                _onLoading.value = false
                _toastMessage.value = error?.handleError { }
                if (query != null) {
                    onBooksReceived(query.documentChanges)
                }
            }
            .also { registrationObject = it }//To Stop the listener at will
    }

    private fun onBooksReceived(documentChanges: MutableList<DocumentChange>) {
        val copyLocalityOverviews = _localityOverviews.value!!

        //We make sure we are working with the selected date
        for (changedDoc in documentChanges) {
            if (formattedTravelDate() == Utils.formatDate(
                    changedDoc.document.getLong("travelDateMillis")!!, "YYYY-MM-dd"
                )
            ) {
                //Check if such book overview existed previously for this selected date
                val overview = copyLocalityOverviews.withIndex().find {
                    (it.value.travelDayString == formattedTravelDate())
                            &&
                            (it.value.townName == changedDoc.document.getString("localityName"))
                }

                when (changedDoc.type) {
                    DocumentChange.Type.ADDED -> {
                        if (overview != null) {// A locality overview already existed
                            //We make sure an exact copy of the document is not already found in the list to avoid stats doubling
                            if (overview.value.destinations.find { it.id == changedDoc.document.id } == null) {
                                overview.value.insertNewTown(changedDoc.document)
                                copyLocalityOverviews[overview.index] = overview.value
                            }
                        } else {//We create a new
                            copyLocalityOverviews.add(
                                TownsOverview.newBookOverView(
                                    Utils.formatDate(
                                        changedDoc.document.getLong("travelDateMillis")!!,
                                        "YYYY-MM-dd"
                                    ),
                                    changedDoc.document.getString("localityName")!!,
                                    null,
                                    changedDoc.document
                                )
                            )

                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        overview!!.value.reInsertExistingTown(changedDoc.document)
                        copyLocalityOverviews[overview.index] = overview.value
                    }
//                DocumentChange.Type.REMOVED -> TODO: Rarely occurs
                }
            }
        }

        _localityOverviews.value = copyLocalityOverviews
        _notifyAdapterChanges.value = false
        _notifyAdapterChanges.value = true
    }

    fun onLocalitySelected() {
        val copyDestinationOverviews = _destinationOverviews.value!!
        //1-Get the selected locality overview with all trips docs
        val selectedOverview =
            _localityOverviews.value!!.find { it.townName == selectedLocality && it.travelDayString == formattedTravelDate() }!!
        for (tripDoc in selectedOverview.destinations) {
            val overview = copyDestinationOverviews.withIndex().find {
                it.value.townName == tripDoc.getString("destinationName")
            }

            //We create a new destination overview
            if (overview == null) {
                copyDestinationOverviews.add(
                    TownsOverview.newBookOverView(
                        formattedTravelDate(),
                        tripDoc.getString("destinationName")!!,
                        selectedLocality,
                        tripDoc
                    )
                )
            } else {
                //We check if something has changed or it is still same doc
                //We make sure an exact copy of the document is not already found in the list to avoid stats doubling
                val existingDoc =
                    overview.value.destinations.withIndex().find { it.value.id == tripDoc.id }
                if (existingDoc == null) {
                    overview.value.insertNewTown(tripDoc)
                    copyDestinationOverviews[overview.index] = overview.value
                    //If some parameters(e.g is Scanned) have changed, then we can modify
                } else if (existingDoc.value.getBoolean("isScanned") != tripDoc.getBoolean("isScanned")) {//We modify
                    overview.value.reInsertExistingTown(tripDoc)
                    copyDestinationOverviews[overview.index] = overview.value
                }
            }
        }

        _destinationOverviews.value = copyDestinationOverviews
        _notifyAdapterChanges.value = false
        _notifyAdapterChanges.value = true
    }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.DATE_IN_MILLIS_FROM -> _selectedDate.value = value as Long
            FieldTags.NOTIFY_DATA_CHANGED -> _notifyAdapterChanges.value = value as Boolean
            FieldTags.SPAN_SIZE -> _spanSizeLocality.value = value as Int

            FieldTags.SPAN_SIZE_DESTINATION -> _spanSizeDestination.value = value as Int
            FieldTags.SELECTED_TOWN_NAME -> selectedLocality = value as String
            FieldTags.SELECTED_DESTINATION -> selectedDestination = value as String

            FieldTags.SCAN_RESULT -> _scanResult.value = value as BarcodeResult
            FieldTags.PAUSE_SCAN_VIEW -> _pauseScanView.value = value as Boolean
            FieldTags.TORCH_STATE -> isTorchOn = !isTorchOn
        }
    }

    enum class FieldTags {
        TOAST_MESSAGE, ON_LOADING, DATE_IN_MILLIS_FROM, NOTIFY_DATA_CHANGED, SPAN_SIZE, PAUSE_SCAN_VIEW, SELECTED_TOWN_NAME, TORCH_STATE, SPAN_SIZE_DESTINATION, SCAN_RESULT, SELECTED_DESTINATION
    }

    /**
     * Extracts from all overviews, the overviews for a particular day [dateString]
     */
    fun bookOverviewsOn(dateString: String): List<TownsOverview> {
        val results = _localityOverviews.value!!.filter {
            it.travelDayString == dateString
        }
        return results
    }


    /*------------------------BookScanning--------------------------------*/
    private val _scanResult = MutableLiveData<BarcodeResult>()
    val scanResult: LiveData<BarcodeResult> get() = _scanResult

    private val _pauseScanView = MutableLiveData(false)
    val pauseScanView: LiveData<Boolean> get() = _pauseScanView

    private lateinit var currentTownOveriew: TownsOverview

    var isTorchOn = false
        private set

    var selectedDestination = ""

    //Makes sure we get the latest data before we perform a scan
    fun getLatestData(destOverviews: List<TownsOverview>) {
        currentTownOveriew = destOverviews.find {
            it.townName == selectedDestination
        }!!
    }

    suspend fun onCodeScanned(scannerID: String) {
        _onLoading.value = true

        val scannedBook = currentTownOveriew.destinations.withIndex().find {
            it.value.getString("failed") == _scanResult.value!!.text
        }
        if (scannedBook == null) {
            // Notify the scanner this trip has not been found. May be it is not a correct QR-Code
            _toastMessage.value = "Wrong QR Code"
        }
        // Notify the scanner this trip has not been found. May be it is not a correct QR-Code
        else {
            when {
                scannedBook.value.getBoolean("isScanned") == true -> {
                    //Notify this trips was earlier scanned
                    _toastMessage.value = "Error: Scanned Already"
                }
                scannedBook.value.getBoolean("isExpired") == true -> {
                    //Notify scanner this trip had expired
                    _toastMessage.value = "Error: Expired Already"
                }
                scannedBook.value.getBoolean("isTaken") == true -> {
                    //Notify scanner this trip is already taken
                    _toastMessage.value = "Error: Took trip alrady"
                }
                scannedBook.value.getBoolean("isVip") == true -> {
                    _toastMessage.value = "***VIP**"
                }
                else -> {
                    //Update database
                    firestoreRepo.updateScannedBook(
                        scannedBook.value,
                        scannerID,
                        _scanResult.value!!.timestamp
                    ).collect {
                        when (it) {
                            is State.Failed -> {
                                _onLoading.value = false
                                _toastMessage.value = it.exception.handleError { }
                            }
                            is State.Loading -> _onLoading.value = true
                            is State.Success -> {
                                _onLoading.value = false
                                _toastMessage.value = "OK!"
                            }
                        }
                    }


                }

            }
        }
        _pauseScanView.value = false
        _onLoading.value = false
    }

}