package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchResultsViewModel: ViewModel() {
    var firestoreRepo: FirestoreRepo = FirestoreRepo()
    var storageUrl: StorageRepo = StorageRepo()

    //These are the id of the localities and destination which is gotten for the search
    var fromID = ""
        private set
    var toID = ""
        private set
    //These are used to fill the header
    var localityName = ""
        private set
    var destinationName = ""
        private set
    var distance = 0L
        private set

    //Setter for the ids
    fun setArguments(fromID: String, toID: String, fromName: String, toName:String, distance: Long){
        this.toID = toID
        this.fromID = toID

        this.localityName = fromName
        this.destinationName = toName
        this.distance = distance
    }

}