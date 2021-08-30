package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
//import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

@ExperimentalCoroutinesApi
class TownsConfigViewModel : ViewModel() {
    private val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //To know when to start searching
    private val _startTownSearch = MutableLiveData(false)
    val startTownSearch: LiveData<Boolean> get() = _startTownSearch

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TownButtonTags { TOWN_SWITCH_ACTIVATE, TOWN_BUTTON_TRIPS }

    var exemptedTownList = mutableListOf<String>() //and that's agency's exception list
        private set

    var sortCheckedItem = 0
        private set

    //Stores the list of a hashmap of a town document
    //we also store an original copy to compare later
    private val _townDocList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val townDocList: LiveData<MutableList<DocumentSnapshot>> get() = _townDocList

    private val _onClose = MutableLiveData(false)
    val onClose: LiveData<Boolean> get() = _onClose

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
    suspend fun getTownsData(agencyID: String) {
        _retryTowns.value = false
        firestoreRepo.getCollection("Planets/Earth/Continents/Africa/Cameroon")
            .collect { townsListState ->
                when (townsListState) {
                    is State.Loading -> _onLoading.value = true
                    is State.Failed -> {
                        _toastMessage.value =
                            townsListState.exception.handleError { /**TODO: Handle Error lambda*/ }
                        _onLoading.value = false
                    }
                    is State.Success -> {
                        //TODO: Get agency id from the current user
                        firestoreRepo.getDocument("OnlineTransportAgency/${agencyID}/Configs/Cameroon/Towns/exemption")
                            .collect { exemptedDocState ->
                                when (exemptedDocState) {
                                    is State.Loading -> _onLoading.value = true
                                    is State.Failed -> {
                                        _toastMessage.value =
                                            exemptedDocState.exception.handleError { /**TODO: Handle Error lambda*/ }
                                        _onLoading.value = false
                                    }
                                    is State.Success -> {
                                        (exemptedDocState.data["exemptedTownList"] as List<String>?)?.let {
                                            exemptedTownList.addAll(it)
                                        }
                                        //We sort ascending order
                                        _townDocList.value = townsListState.data.documents

                                        //We then get all the names for the auto-complete
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

    enum class FieldTags {
        TOAST_MESSAGE, START_TOWN_SEARCH, TOWN_ID, TOWN_NAME, ON_CLOSE, CHECKED_ITEM, RETRY_TOWNS
    }

    enum class SortTags { TOWN_NAMES, REGIONS }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.START_TOWN_SEARCH -> _startTownSearch.value = value as Boolean
            FieldTags.TOWN_ID -> townId = value.toString()
            FieldTags.TOWN_NAME -> townName = value.toString()
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
            FieldTags.RETRY_TOWNS -> _retryTowns.value = value as Boolean
        }
    }

    /**
     * A function to search for a specific town name from the doc and return its index in the list
     */
    fun searchTown(townName: String): Int? = townDocList.value?.indexOf(
        townDocList.value!!.find { townDoc ->
            townDoc["name"] == townName
        }
    )

    suspend fun uploadTownChanges(agencyID: String) {
        firestoreRepo.setDocument(
            hashMapOf(
                "exemptedTownList" to exemptedTownList
            ),
            "OnlineTransportAgency/${agencyID}/Configs/Cameroon/Towns/exemption"
        ).collect {
            when (it) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Success -> {
                    _onLoading.value = false
                    //We navigate to the original lobby
                    _onClose.value = true
                }
            }
        }
    }

    fun sortResult(sortTag: SortTags) = when (sortTag) {
        SortTags.TOWN_NAMES -> _townDocList.value!!.sortBy {
            it.getString("name")
        }
        SortTags.REGIONS -> _townDocList.value!!.sortBy {
            it.getString("region")
        }
    }

}
