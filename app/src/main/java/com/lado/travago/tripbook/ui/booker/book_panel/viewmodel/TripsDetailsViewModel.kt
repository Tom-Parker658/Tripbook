package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel


import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.type.DateTime
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.booking.Book
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@ExperimentalCoroutinesApi
class TripsDetailsViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    val authRepo = FirebaseAuthRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //Number of seats paid for
    private val _numberOfBooks = MutableLiveData(1)
    val numberOfBooks: LiveData<Int> get() = _numberOfBooks

    //Begin book creation, for now we don't launch the payment activity before creation
    private val _startCreation = MutableLiveData(false)
    val startCreation: LiveData<Boolean> get() = _startCreation

    private val _bookingComplete = MutableLiveData(false)
    val bookingComplete: LiveData<Boolean> get() = _bookingComplete

    //To notify the user when the trips is not more available during the current config session
    private val _tripHasBeenDeleted = MutableLiveData(false)
    val tripHasBeenDeleted: LiveData<Boolean> get() = _tripHasBeenDeleted

    //Data
    var agencyID = ""
        private set
    var tripID = ""
        private set
    var localityTownName = ""
        private set
    lateinit var tripTime: TimeModel
        private set
    var tripDateInMillis = 0L
        private set
    private val _isVip = MutableLiveData(false)
    val isVip: LiveData<Boolean> get() = _isVip

    private val _agencyDoc = MutableLiveData<DocumentSnapshot>()
    val agencyDoc: LiveData<DocumentSnapshot> get() = _agencyDoc

    private val _tripDoc = MutableLiveData<DocumentSnapshot>()
    val tripDoc: LiveData<DocumentSnapshot> get() = _tripDoc

    fun agencyListener(
        hostActivity: Activity
    ) =/*1- We get the agency document*/
        firestoreRepo.db.document("OnlineTransportAgency/$agencyID")
            .addSnapshotListener(hostActivity) { snapshot, error ->
                _onLoading.value = false
                if (snapshot != null) {
                    if (snapshot.exists()) _agencyDoc.value = snapshot
                    else _tripHasBeenDeleted.value = true
                }
                error?.let {
                    _toastMessage.value = it.handleError { }
                }
            }


    fun tripListener(
        hostActivity: Activity
    ) = /*2- We get the selected trip document from the agency's trips collection */
        firestoreRepo.db.document(
            "OnlineTransportAgency/${
                agencyID
            }/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/$tripID"
        ).addSnapshotListener(hostActivity) { snapshot, error ->
            if (snapshot != null) {
                if (snapshot.exists()) _tripDoc.value = snapshot
                else _tripHasBeenDeleted.value = true
            }
            error?.let {
                _toastMessage.value = it.handleError { }
            }
            _onLoading.value = false
        }


    enum class FieldTags {
        TOAST_MESSAGE, LOCALITY_NAME, NUMBER_OF_BOOKS, ARG_AGENCY_ID, ARG_TRIP_ID, ARG_IS_VIP, ARG_TRIP_TIME, ARG_TRIP_DATE, ON_LOADING, START_CREATION
    }

    fun setField(fieldTag: FieldTags, value: Any) =
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.NUMBER_OF_BOOKS -> _numberOfBooks.value = value as Int
            FieldTags.ARG_AGENCY_ID -> agencyID = value.toString()
            FieldTags.ARG_TRIP_ID -> tripID = value.toString()
            FieldTags.ARG_IS_VIP -> _isVip.value = value as Boolean
            FieldTags.LOCALITY_NAME -> localityTownName = value.toString()
            FieldTags.ARG_TRIP_TIME -> tripTime =
                TimeModel.fromTimeParameter(
                    TimeModel.TimeParameter.MILLISECONDS,
                    (value as Int).toLong()
                )

            FieldTags.ARG_TRIP_DATE -> tripDateInMillis = value as Long
            FieldTags.START_CREATION -> _startCreation.value = value as Boolean
        }

    /**
     * @author Parkert805
     * @since 2021-10-31 UTC 01:19:00.00 AM
     * Creates the book in the bookers doc and the agency doc for that particular departure day
     * Finally it tries to update the counts for the book number in the tripdoc it self
     */
    suspend fun createBookInDB() = flow {
        emit(State.loading())
        firestoreRepo.db.runTransaction {
            val currentBooker =
                it.get(firestoreRepo.db.document("Bookers/${authRepo.currentUser!!.uid}"))
            var count = 1
            while (count <= _numberOfBooks.value!!) {
                val book = Book(
                    tripDoc.value!!,
                    currentBooker,
                    agencyDoc.value!!,
                    localityTownName,
                    isVip.value!!,
                    tripDateInMillis,
                    tripTime,
                    firestoreRepo.db
                )

                //Creates n-books
                val bookerBookRef =
                    firestoreRepo.db.collection("Bookers/${authRepo.currentUser!!.uid}/My_Books")
                        .document()
                val agencyBookDocRef =
                    //We save the book under .../#locality#/to/#destination#/book/bookID
                    firestoreRepo.db.document(
                        "OnlineTransportAgency/$agencyID/Books/${
                            Utils.formatDate(
                                tripDateInMillis,
                                "YYYY-MM-dd"
                            )
                        }/Cameroon/${bookerBookRef.id}"
                    )
                /*firestoreRepo.db.document(
                    "OnlineTransportAgency/$agencyID/Books/${
                        Utils.formatDate(
                            tripDateInMillis,
                            "YYYY-MM-dd"
                        )
                    }/Cameroon/$localityTownName/to/${
                        book.bookMap["destinationName"]
                    }/Books/${bookerBookRef.id}"
                )*/
                it.set(agencyBookDocRef, book.bookMap)
                it.set(bookerBookRef, book.bookMap)
                count++
            }
        }.await()
        emit(State.success("Done"))
    }
        .flowOn(Dispatchers.Main)
        .catch { emit(State.failed(it as Exception)) }
        .collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = "Some thing went wrong"
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    //Increments popularity stats for that particular trip
                    firestoreRepo.incrementField(
                        _numberOfBooks.value!!,
                        "OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/$tripID",
                        "from_$localityTownName"
                    ).collect { numState ->
                        when (numState) {
                            is State.Failed -> {
                                _onLoading.value = false
                                _toastMessage.value = "Couldn't increment"
                            }
                            is State.Loading -> _onLoading.value = true
                            is State.Success -> {
                                _bookingComplete.value = true
                                _onLoading.value = false
                            }
                        }
                    }
                }
            }
        }
}


