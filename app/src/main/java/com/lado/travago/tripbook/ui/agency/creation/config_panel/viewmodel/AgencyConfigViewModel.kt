package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

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
    private lateinit var firestoreRepo: FirestoreRepo
    private lateinit var authRepo: FirebaseAuthRepo

    private val _bookerDoc = MutableLiveData<DocumentSnapshot>()
    val bookerDoc: LiveData<DocumentSnapshot> get() = _bookerDoc

    /**
     * Get the current booker document
     */
    suspend fun getCurrentBooker(){
        firestoreRepo.getDocument(
            "Bookers/${authRepo.currentUser!!.uid}",
            Source.DEFAULT
        ).collect{
            when(it){
                is State.Success -> {
                    _bookerDoc.value = it.data!!
                }
            }
        }
    }

}