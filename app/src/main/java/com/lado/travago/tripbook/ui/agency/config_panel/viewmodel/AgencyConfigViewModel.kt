package com.lado.travago.tripbook.ui.agency.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * Config Activity to control Master-Slave mechanism e.g Country-Journey, Continent-Country
 */
@ExperimentalCoroutinesApi
class AgencyConfigViewModel : ViewModel() {
    private var firestoreRepo: FirestoreRepo = FirestoreRepo()
    var authRepo: FirebaseAuthRepo = FirebaseAuthRepo()

    private val _bookerDoc = MutableLiveData<DocumentSnapshot>()
    val bookerDoc: LiveData<DocumentSnapshot> get() = _bookerDoc

    private val _scannerDoc = MutableLiveData<DocumentSnapshot>()
    val scannerDoc: LiveData<DocumentSnapshot> get() = _scannerDoc

    private val _retry = MutableLiveData(true)
    val retry: LiveData<Boolean> get() = _retry

    /**
     * Get the current booker document
     */
    suspend fun getCurrentBooker() {
        _retry.value = false
        //TODO: CHANGE ${authRepo.currentUser?.uid}, TO ${authRepo.currentUser!!.uid}", it must throw an exception
        //TODO: For testing
        firestoreRepo.getDocument(
            "Bookers/ptUDtYNmuTZNjdXBAfDcLFQ7Z6aq",
            Source.SERVER
        ).collect { bookerDoc ->
            when (bookerDoc) {
                is State.Success -> {
                    // Gets the scanner doc
                    firestoreRepo.queryCollection(
                        "OnlineTransportAgency/${
                            this.bookerDoc.value!!.getString(
                                "agencyID"
                            )!!
                        }/Scanners", Source.DEFAULT
                    ) {
                        it.whereEqualTo("scannerID", this.bookerDoc.value!!.id)

                    }.collect { scannerDoc ->
                        when (scannerDoc) {
                            is State.Success -> {
                                _bookerDoc.value = bookerDoc.data!!
                                _scannerDoc.value = scannerDoc.data.documents.first()
                            }
                        }
                    }
                }
            }
        }

    }


}