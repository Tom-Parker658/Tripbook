package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import android.util.Log
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

@ExperimentalCoroutinesApi
class ScannerPanelViewModel: ViewModel() {
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
            "Bookers/${ authRepo.currentUser!!.uid }",
            Source.SERVER
        ).collect { bookerDocState ->
            when (bookerDocState) {
                is State.Success -> {
                    _bookerDoc.value = bookerDocState.data!!
                    // TODO: Gets the scanner doc
                    /*firestoreRepo.queryCollection(
                        "OnlineTransportAgency/${
                            bookerDocState.data.getString(
                                "agencyID"
                            )!!
                        }/Scanners", Source.DEFAULT
                    ) {
                        it.whereEqualTo("scannerID", bookerDocState.data.id)

                    }.collect { scannerDocState ->
                        when (scannerDocState) {
                            is State.Success -> {
                                _scannerDoc.value = scannerDocState.data.documents.first()
                            }
                            is State.Failed -> {
                                Log.e("AGENCY CONFIG Scanner", "${scannerDocState.exception}")
                            }
                        }
                    }*/
                }
                is State.Failed -> {
                    Log.e("AGENCY CONFIG", "${bookerDocState.exception}")
                }

            }
        }

    }

}