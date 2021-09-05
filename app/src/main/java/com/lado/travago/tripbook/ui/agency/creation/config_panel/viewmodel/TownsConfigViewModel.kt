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
import kotlinx.coroutines.tasks.await
import kotlin.collections.HashMap

@ExperimentalCoroutinesApi
class TownsConfigViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //To know when to start searching when the search fab is tapped
    private val _startTownSearch = MutableLiveData(false)
    val startTownSearch: LiveData<Boolean> get() = _startTownSearch

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TownButtonTags { TOWN_CHECK_TO_DELETE, TOWN_BUTTON_TRIPS }

    //The list of all towns from the reference Planet/Earth/.. Path
    private val _originalTownsList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val originalTownsList: LiveData<MutableList<DocumentSnapshot>> get() = _originalTownsList

    private val _onShowAddTrip = MutableLiveData(false)
    val onShowAddTrip: LiveData<Boolean> get() = _onShowAddTrip

    //Hold the ids of towns to be deleted
    private val _toDeleteIDList =
        MutableLiveData(mutableListOf<String>()) //and that's agency's exception list
    val toDeleteIDList: LiveData<MutableList<String>> = _toDeleteIDList

    //Contains the snapshot result
    private val _currentTownsList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val currentTownsList: LiveData<MutableList<DocumentSnapshot>> get() = _currentTownsList

    //Holds the list of the ids to be added
    private val _toAddIDList =
        MutableLiveData(mutableListOf<String>()) //and that's agency's exception list
    val toAddIDList: LiveData<MutableList<String>> = _toAddIDList

    //for the simple recycler
    val townSimpleInfoMap = mutableListOf<HashMap<String, String>>()

    //Stores the names of the all towns for autocompletion during search
    val townNamesList = mutableListOf<String>()

    //The current index item to be selected when the sort button is tapped
    var sortCheckedItem = 0
        private set

    //To navigate away
    private val _onClose = MutableLiveData(false)
    val onClose: LiveData<Boolean> get() = _onClose

    //This is the variable to hold arguments to navigate to trips
    var townId = ""
    var townName = ""

    //Do or redo the fetching towns
    private val _retryTowns = MutableLiveData(true)
    val retryTowns get() = _retryTowns

    /**
     * We get all the towns from the database and the document containing the list towns
     * Launched when the add fab is tapped
     */
    suspend fun getOriginalTowns() {
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
                        townsListState.data.documents.forEach { townDoc ->
                            townSimpleInfoMap.add(
                                hashMapOf(
                                    "id" to townDoc.id,
                                    "name" to townDoc.getString("name")!!
                                )
                            )
                        }
                        _originalTownsList.value = townsListState.data.documents
                        _onLoading.value = false
                    }

                }
            }
    }

    //Adds a town into the toDeleteList
    fun removeTown(townId: String) =
        if (_toDeleteIDList.value!!.contains(townId)) _toDeleteIDList.value!!.remove(townId)
        else _toDeleteIDList.value!!.add(townId)

    //Adds a town into the toAddList
    fun addTown(townId: String) =
        if (_toAddIDList.value!!.contains(townId)) _toAddIDList.value!!.remove(townId)
        else _toAddIDList.value!!.add(townId)

    //Does the deletion of the list from the database
    suspend fun commitToDeleteList(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.runBatch { batch ->
            _toDeleteIDList.value!!.forEach { id ->
                batch.delete(firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/$id"))
            }
        }.apply {
            _onLoading.value = true
        }.addOnCompleteListener {
            _onLoading.value = false
            if (it.isSuccessful) {
                _toDeleteIDList.value!!.clear()
                _onLoading.value = false
            } else {
                _onLoading.value = false
                _toastMessage.value = it.exception!!.handleError { }
            }
        }.await()
    }

    //Does the addition of the list to database
    suspend fun commitToAddList(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.runBatch { batch ->
            _toAddIDList.value!!.forEach { id ->
                val doc = _originalTownsList.value!!.find {
                    it.id == id
                }!!
                batch.set(
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/$id"),
                    doc.data!!.toMap().apply {
                        "agencyID" to agencyID
                    }
                )
            }
        }.apply {
            if (!isComplete) _onLoading.value = true
        }.addOnCompleteListener {
            _onLoading.value = false
            if (it.isSuccessful) {
                _toAddIDList.value!!.clear()
                _onLoading.value = false
            } else {
                _onLoading.value = false
                _toastMessage.value = it.exception!!.handleError { }
            }
        }.await()
    }

    enum class FieldTags {
        TOAST_MESSAGE, START_TOWN_SEARCH, TOWN_ID, TOWN_NAME, ON_CLOSE, CHECKED_ITEM, RETRY_TOWNS, CURRENT_TOWNS, REMOVE_MAP, TOWN_NAME_LIST, ON_SHOW_ADD
    }

    enum class SortTags { TOWN_NAMES, REGIONS }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.ON_SHOW_ADD -> _onShowAddTrip.value= value  as Boolean
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.START_TOWN_SEARCH -> _startTownSearch.value = value as Boolean
            FieldTags.TOWN_ID -> townId = value.toString()
            FieldTags.TOWN_NAME -> townName = value.toString()
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
//            FieldTags.RETRY_TOWNS -> _retryTowns.value = value as Boolean
            FieldTags.CURRENT_TOWNS -> _currentTownsList.value =
                value as MutableList<DocumentSnapshot>
            //Removes a town which is already part of that agencies document so that it can't be added again
            FieldTags.REMOVE_MAP -> townSimpleInfoMap.remove(value as HashMap<String, String>)
            FieldTags.TOWN_NAME_LIST -> townNamesList.add(value.toString())
        }
    }

    /**
     * A function to search for a specific town name from the doc and return its index in the list
     */
    fun searchTown(townName: String): Int? = _currentTownsList.value?.indexOf(
        _currentTownsList.value!!.find { townDoc ->
            townDoc["name"] == townName
        }
    )

    fun sortResult(sortTag: SortTags) = when (sortTag) {
        SortTags.TOWN_NAMES -> _currentTownsList.value?.sortBy {
            it.getString("name")
        }
        SortTags.REGIONS -> _currentTownsList.value?.sortBy {
            it.getString("region")
        }
    }

}
