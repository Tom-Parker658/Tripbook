package com.lado.travago.tripbook.ui.agency.config_panel.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
//import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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

    //Tell the adapter to notify all items have changed
    private val _notifyAdapterChanges = MutableLiveData(false)
    val notifyAdapterChanges: LiveData<Boolean> get() = _notifyAdapterChanges

    //Span size
    private val _spanSize = MutableLiveData(2)
    val spanSize: LiveData<Int> get() = _spanSize

    //Visibility of the Fabs
    private val _fabVisibilityState = MutableLiveData(true)
    val fabVisibilityState: LiveData<Boolean> get() = _fabVisibilityState
    fun invertFabVisibility() {
        _fabVisibilityState.value = !_fabVisibilityState.value!!
    }

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
    private val _agencyTownsList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val agencyTownsList: LiveData<MutableList<DocumentSnapshot>> get() = _agencyTownsList

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
                        _originalTownsList.value = townsListState.data.documents
                        addTownsNames()
                        _onLoading.value = false
                    }

                }
            }
    }

    fun agencyTownsListener(
        hostActivity: Activity,
        agencyID: String
    ): ListenerRegistration =
        firestoreRepo.db.collection(
            "OnlineTransportAgency/${
                agencyID
            }/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/"
        ).addSnapshotListener(hostActivity) { snapshot, error ->
            _toastMessage.value = error?.handleError { }
            if (snapshot != null) {
                if (!snapshot.isEmpty) {
                    _agencyTownsList.value = snapshot.documents
                    addTownsNames()
                } else {
                    _toastMessage.value = hostActivity.getString(R.string.text_empty_content)
                    //We clear the list completely if it is empty inorder to clear off recycler
                    _agencyTownsList.value?.clear()
                    //It may be because the admin scanner has removed all towns so we notify adapter
                    //to clear all data
                    _notifyAdapterChanges.value = true
                }
            }

        }

    /**
     * This will be use to set the list of towns so that the scanner can add them to his agency collection
     * This modifies the [townSimpleInfoMap] list
     */
    private fun addTownsNames() {
        townSimpleInfoMap.clear()
        originalTownsList.value?.forEach { originalTownDoc ->
            val found = _agencyTownsList.value?.find {
                it["name"] == originalTownDoc.getString("name")
            }
            if (found == null) //We add to "TO-ADD-LIST" iff it is not already part of the agency collection of towns
                townSimpleInfoMap.add(
                    hashMapOf(
                        "id" to originalTownDoc.id,
                        "name" to originalTownDoc.getString("name")!!
                    )
                )
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
        val idListToBeRemovedLater =
            mutableListOf<String>()
        // This is to avoid iterating through a list and modifying it also
        _toDeleteIDList.value!!.forEach { id ->
            firestoreRepo.queryCollection(
                "OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency"
            ) {
                it.whereArrayContains("townIDs", id)
            }.collect { queryState ->
                when (queryState) {
                    is State.Loading -> _onLoading.value = true
                    is State.Failed -> {
                        _onLoading.value = false
                        _toastMessage.value = queryState.exception.handleError { }
                    }
                    is State.Success -> {
                        flow {
                            emit(State.loading())
                            firestoreRepo.db.runBatch { batch ->
                                //We get all trips associated to the town
                                for (tripDoc in queryState.data.documents) {
                                    batch.delete(firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/${tripDoc.id}"))
                                }
                                //We delete the townDocument
                                batch.delete(firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/$id"))
                                //This delete all towns associated with the trips
                            }.await()
                            emit(State.success(Unit))
                        }.apply {
                            catch {
                                emit(State.failed(it as Exception))
                            }
                            flowOn(Dispatchers.IO)
                            collect {
                                when (it) {
                                    is State.Failed -> {
                                        _toastMessage.value = it.exception.handleError { }
                                    }
                                    is State.Loading -> _onLoading.value = false
                                    is State.Success -> idListToBeRemovedLater += id
                                }
                            }
                        }

                    }
                }
            }
        }
        //We then delete all ids which succeeded to be delete
        _toDeleteIDList.value!!.removeAll(idListToBeRemovedLater)
    }

    /**
     * Fn1: Adds the selected towns to the database
     * Fn2: Intertwine towns by connecting them inorder to create trips
     */
    suspend fun commitToAddList(agencyID: String) = flow {
        emit(State.loading())
        val toAddTripDocList = mutableListOf<DocumentSnapshot>()

        // 1- Get a list of all trips with involves any 2 of the selected towns (We add all trips concerned by intertwining)
        _toAddIDList.value!!.forEach { id1 ->
            //1.1 Get the first town document
            val firstTownDoc = _originalTownsList.value!!.find {
                it.id == id1
            }!!
            _toAddIDList.value!!.forEach { id2 ->
                if (id1 != id2) {
                    //1.2 Get the second town document
                    val secondTownDoc = _originalTownsList.value!!.find {
                        it.id == id2
                    }!!
                    //1.3 We get a trip document which contains the 2 towns using their town names,
                    // i.e the "townNames.town1" is always the alphabetical less between the 2 towns
                    // The town1 is the alphabetically lesser than town2 e.g town1 = Ambam, town2 = Buea
                    val sortedTownNameList = listOf(
                        firstTownDoc.getString("name")!!,
                        secondTownDoc.getString("name")!!
                    ).sorted()
                    // We get the original trip document and stores it in the list if everything goes find
                    //this list will be added to db later since reads comes before writes
                    CoroutineScope(Dispatchers.Main).launch {
                        firestoreRepo.db.collection("/Planets/Earth/Continents/Africa/Cameroon/all/Trips")
                            .whereEqualTo("townNames.town1", sortedTownNameList.first())
                            .whereEqualTo("townNames.town2", sortedTownNameList.last())
                            .get().await().documents.first().let {
                                toAddTripDocList += it
                            }
                    }
                }
            }
        }

        /*WRITES*/
        // We want to intertwine among all towns and create a combination of trips btn these agency
        firestoreRepo.db.runTransaction { transaction ->
            val agencyDoc = transaction.get(
                firestoreRepo.db.document("OnlineTransportAgency/$agencyID")
            )
            //3- Writes the trips doc to database
            for (originalTripDoc in toAddTripDocList) {
                transaction.set(
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/${originalTripDoc.id}"),
                    hashMapOf(
                        "townNames" to mapOf(
                            "town1" to (originalTripDoc["townNames"]!! as Map<String, String>)["town1"],
                            "town2" to (originalTripDoc["townNames"]!! as Map<String, String>)["town2"]
                        ),
                        /*"busTypes" to mapOf(//TODO: Take care to highlight these names
                                    "seaterSeventy" to false,
                                    "seaterCoaster" to false,
                                    "seaterNormal" to false
                                ),*/
                        "townIDs" to originalTripDoc["townIDs"]!! as List<String>,
                        "distance" to originalTripDoc.getLong("distance")!!,
                        "isVip" to true,
                        "agencyID" to agencyID,
                        "flagVipPriceFromDistance" to true,
                        "flagNormalPriceFromDistance" to true,
                        "normalPrice" to (agencyDoc.getDouble("pricePerKm")
                            ?: 11.0) * originalTripDoc.getLong("distance")!!,
                        "vipPrice" to (agencyDoc.getDouble("pricePerKm")
                            ?: 12.0) * originalTripDoc.getLong("distance")!!,
                    )
                )
            }

            //Final: We add the selected towns into the database
            _toAddIDList.value!!.forEach { id ->
                val doc = _originalTownsList.value!!.find {
                    it.id == id
                }!!
                transaction.set(
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/$id"),
                    doc.data!!.toMap().apply {
                        "agencyID" to agencyID
                    }
                )
            }

        }.await()
        emit(State.success(Unit))
    }.apply {
        catch {
            emit(State.failed(it as Exception))
        }
        flowOn(Dispatchers.IO)
        collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value = it.exception.handleError { }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _toAddIDList.value!!.clear()
                    _onLoading.value = false
                }
            }
        }
    }


    enum class FieldTags {
        TOAST_MESSAGE, START_TOWN_SEARCH, TOWN_ID, TOWN_NAME, ON_CLOSE, CHECKED_ITEM, REMOVE_MAP, TOWN_NAME_LIST, ON_SHOW_ADD, SPAN_SIZE, NOTIFY_DATA_CHANGED
    }

    enum class SortTags { TOWN_NAMES, REGIONS }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.ON_SHOW_ADD -> _onShowAddTrip.value = value as Boolean
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.START_TOWN_SEARCH -> _startTownSearch.value = value as Boolean
            FieldTags.TOWN_ID -> townId = value.toString()
            FieldTags.TOWN_NAME -> townName = value.toString()
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
            FieldTags.SPAN_SIZE -> _spanSize.value = value as Int

            FieldTags.NOTIFY_DATA_CHANGED -> _notifyAdapterChanges.value = value as Boolean
//            FieldTags.RETRY_TOWNS -> _retryTowns.value = value as Boolean

            //Removes a town which is already part of that agencies document so that it can't be added again
            FieldTags.REMOVE_MAP -> townSimpleInfoMap.remove(value as HashMap<String, String>)
            FieldTags.TOWN_NAME_LIST -> townNamesList.add(value.toString())
        }
    }

    /**
     * A function to search for a specific town name from the doc and return its index in the list
     */
    fun searchTown(townName: String): Int? = _agencyTownsList.value?.indexOf(
        _agencyTownsList.value!!.find { townDoc ->
            townDoc["name"] == townName
        }
    )

    fun sortResult(sortTag: SortTags) = when (sortTag) {
        SortTags.TOWN_NAMES -> _agencyTownsList.value?.sortBy {
            it.getString("name")
        }
        SortTags.REGIONS -> _agencyTownsList.value?.sortBy {
            it.getString("region")
        }
    }

    //Clear town names list
    fun townNamesListClear() = townNamesList.clear()

}


