package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.repo.State
//import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class TownsConfigViewModel : ViewModel() {
    //    private val authRepo = FirebaseAuthRepo()
    private val firestoreRepo = FirestoreRepo()
    lateinit var exemptedTownList: MutableList<String>//and that's agency's exception list

    //Stores the list of a hashmap of a town document
    //we also store an original copy to compare later
    private var _originalTownDoc = listOf<DocumentSnapshot>()

    private val _townDocList = MutableLiveData<List<DocumentSnapshot>>()
    val townDocList get() = _townDocList

    private val _onLoading = MutableLiveData(true)
    val onLoading get() = _onLoading

    //Do or redo the fetching
    private val _retry = MutableLiveData(true)
    val retry get() = _retry
    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    /**
     * We get all the towns from the database and the document containing the list of exempted towns
     */
    suspend fun getData() {
        _retry.value = false
        firestoreRepo.getCollection("Planets/Earth/Continents/Africa/Cameroon")
            .collect { townsListState ->
                when (townsListState) {
                    is State.Loading -> _onLoading.value = true
                    is State.Failed -> {
                        _toastMessage.value = townsListState.message
                    }
                    is State.Success -> {
                        //TODO: Get agency id from the current user
                        firestoreRepo.getDocument("OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0/Exemptions/town")
                            .collect { exemptedDocState ->
                                when (exemptedDocState) {
                                    is State.Loading -> _onLoading.value = true
                                    is State.Failed -> {
                                        _toastMessage.value = exemptedDocState.message
                                    }
                                    is State.Success -> {
                                        //
                                        exemptedTownList = (exemptedDocState.data["townList"] as List<String>).toMutableList()
                                        _townDocList.value  = townsListState.data.documents

                                        // we save an original copy of the document
                                        _originalTownDoc = _townDocList.value!!
                                        _onLoading.value = false
                                    }

                                }
                            }
                    }
                }
            }
    }

    /**
     * This get the town which has been clicked to exempt it
     */
    fun exemptTown(townId: String) = exemptedTownList.add(townId)

    enum class ButtonTags{
        BUTTON_SWITCH_ACTIVATE, BUTTON_PARKS, BUTTON_TRIPS
    }
}
