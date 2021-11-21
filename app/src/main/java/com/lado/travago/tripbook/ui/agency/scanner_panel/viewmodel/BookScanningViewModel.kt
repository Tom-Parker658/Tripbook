package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Source
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class BookScanningViewModel : ViewModel() {

    val db = FirestoreRepo()

    //While we search for correspondences in the database
    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading


    /**
     * Search if the qr-code is found in the database
     */
    suspend fun activateBook(
        agencyID: String,
        localityName: String,
        destinationName: String,
        failedString: String,
        tripDateInMillis: Long
    ) {
        db.queryCollection(
            "OnlineTransportAgency/$agencyID/Books/${
                Utils.formatDate(
                    tripDateInMillis,
                    "YYYY-MM-dd"
                )
            }/Cameroon/$localityName/to/$destinationName/Books", Source.DEFAULT
        ) {
            it.whereEqualTo(
                "failedString", failedString
            )
        }.collect { state ->
            when (state) {
                is State.Failed -> {
                    //TODO: Something went wrong
                }
                is State.Loading -> _onLoading.value = true

                is State.Success -> {

                }
            }
        }
    }


}
