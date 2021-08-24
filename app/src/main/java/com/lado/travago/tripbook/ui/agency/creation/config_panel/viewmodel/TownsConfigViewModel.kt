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
    private val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    private val _navigateToTrip = MutableLiveData(false)
    val onNavigateToTrip get() = _navigateToTrip

    //To know when to start searching
    private val _startTownSearch = MutableLiveData(false)
    val startTownSearch get() = _startTownSearch

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TownButtonTags {
        //Town Tags
        TOWN_SWITCH_ACTIVATE, TOWN_BUTTON_TRIPS,
    }

    var exemptedTownList = mutableListOf<String>() //and that's agency's exception list
        private set

    //Stores the list of a hashmap of a town document
    //we also store an original copy to compare later
    private var _originalTownDoc = listOf<DocumentSnapshot>()

    private val _townDocList = MutableLiveData<List<DocumentSnapshot>>()
    val townDocList get() = _townDocList

    //Stores the names of the all towns for autocompletion during search
    val townNamesList = mutableListOf<String>()

    //This is the variable to hold arguments to navigate to trips
    var townId = ""
    var townName = ""

    //Do or redo the fetching towns
    private val _retryTowns = MutableLiveData(true)
    val retryTowns get() = _retryTowns

    /**
     * We get all the towns from the database and the document containing the list of exempted towns
     */
    suspend fun getTownsData() {
        _retryTowns.value = false
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
                                        exemptedDocState.data
                                        exemptedTownList =
                                            (exemptedDocState.data["townList"] as List<String>).toMutableList()
                                        //We sort ascending order
                                        _townDocList.value =
                                            townsListState.data.documents.sortedBy {
                                                it["name"].toString().lowercase()
                                            }
                                        // we save an original copy of the document
                                        _originalTownDoc = _townDocList.value!!
                                        //We then get all the names
                                        _townDocList.value!!.forEach {
                                            townNamesList += it["name"]!!.toString()
                                        }
                                        _onLoading.value = false
                                    }
                                }
                            }
                    }
                }
            }
    }

    /**
     * This get the town which has been clicked to exempt it if not already found in exemption list of
     * add him if not found
     */
    fun exemptTown(townId: String) =
        if (exemptedTownList.contains(townId)) exemptedTownList.remove(townId)
        else exemptedTownList.add(townId)

    enum class FieldTag {
        TOAST_MESSAGE, START_TOWN_SEARCH, NAVIGATE_TO_TRIP, TOWN_ID, TOWN_NAME
    }

    fun setField(fieldTag: FieldTag, value: Any) {
        when (fieldTag) {
            FieldTag.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTag.START_TOWN_SEARCH -> _startTownSearch.value = value as Boolean
            FieldTag.NAVIGATE_TO_TRIP -> _navigateToTrip.value = value as Boolean
            FieldTag.TOWN_ID -> townId = value.toString()
            FieldTag.TOWN_NAME -> townName =  value.toString()
        }
    }

    /**
     * A function to search for a specific town name from the doc and return its index in the list
     */
    fun searchTown(townName: String): Int =
        townDocList.value!!.indexOf(
            townDocList.value!!.find { townDoc ->
                townDoc["name"] == townName
            }
        )
}
