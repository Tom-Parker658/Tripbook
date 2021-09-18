package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils.toMapWithIDField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class TripsConfigViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _onClose = MutableLiveData(false)
    val onClose: LiveData<Boolean> get() = _onClose

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _onNormalPriceForm = MutableLiveData(false)
    val onNormalPriceForm: LiveData<Boolean> get() = _onNormalPriceForm

    private val _onVipPriceForm = MutableLiveData(false)
    val onVipPriceForm: MutableLiveData<Boolean> get() = _onVipPriceForm

    private val _startTripSearch = MutableLiveData(false)
    val startTripSearch: LiveData<Boolean> get() = _startTripSearch

    //Controls when to show the add trip view
    private val _onShowAddTrip = MutableLiveData(false)
    val onShowAddTrip: LiveData<Boolean> get() = _onShowAddTrip

    //Forces the adapter to rebind an object
    private val _onRebindItem = MutableLiveData(false)
    val onRebindItem: LiveData<Boolean> get() = _onRebindItem

    //Hold the ids of trips to be deleted and that's agency's exception list
    private val _toDeleteIDList = MutableLiveData(mutableListOf<String>())
    val toDeleteIDList: LiveData<MutableList<String>> get() = _toDeleteIDList

    //Contains the changes made by the use locally by the user
    private val _localChangesMapList = MutableLiveData(mutableListOf<MutableMap<String, Any>>())
    val localChangesMapList: LiveData<MutableList<MutableMap<String, Any>>> get() = _localChangesMapList

    //Contains the snapshot result
    private val _currentTripsList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val currentTripsList: LiveData<MutableList<DocumentSnapshot>> = _currentTripsList

    //Holds the list of the ids to be added and that's agency's exception list
    private val _toAddIDList = MutableLiveData(mutableListOf<String>())
    val toAddIDList: LiveData<MutableList<String>> = _toAddIDList

    //Contains the basic info for the addition, for teh simple recycler
    val tripsSimpleInfoMap = mutableListOf<HashMap<String, String>>()

    //The list of all trips from the reference Planet/Earth/.. Path
    private val _originalTripsList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val originalTripsList: LiveData<MutableList<DocumentSnapshot>> get() = _originalTripsList

    var sortCheckedItem = 0
        private set

    val tripNamesList = mutableListOf<String>()

    //Do or redo the fetching towns
    private val _retryTrips = MutableLiveData(true)
    val retryTrips get() = _retryTrips

    //SpanSize
    private val _spanSize = MutableLiveData(2)
    val spanSize: LiveData<Int> get() = _spanSize

    // Stores the id of the current town for immediate navigation
    var townID = ""
        private set
    var currentTownName = ""
        private set

    //Holds the general price per km
    var pricePerKM = 11.0
        private set
    var vipPricePerKM = 12.5
        private set
    var tripID = ""
        private set

    suspend fun getOriginalTrips() {
        _toastMessage.value = "$townID , $currentTownName"
        _retryTrips.value = false
        //We convert ge all trips associated to that document
        firestoreRepo.queryCollection("/Planets/Earth/Continents/Africa/Cameroon/all/Trips") {
            it.whereArrayContains("townIDs", townID)
//            it.whereArrayContains("townNames", currentTownName)
        }.collect { tripsListState ->
            when (tripsListState) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _toastMessage.value =
                        tripsListState.exception.handleError { /**TODO: Handle Error lambda*/ }
                    _onLoading.value = false
                }
                is State.Success -> {
//                        pricePerKM = 11.0
//                        // agencyState.data.getDouble("pricePerKM")!!
//                        vipPricePerKM = 12.0
//                       //agencyState.data.getDouble("vipPricePerKM")!!

                    tripsListState.data.documents.forEach {
                        //We get the name of the town that is not same as
                        val otherTownName =
                            if ((it.get("townNames")!! as HashMap<String, String>)["town1"] == currentTownName)
                                (it.get("townNames")!! as HashMap<String, String>)["town2"]!!
                            else (it.get("townNames")!! as HashMap<String, String>)["town1"]!!

                        tripsSimpleInfoMap.add(
                            hashMapOf(
                                "id" to it.id,
                                "name" to otherTownName,
                                "distance" to it.getLong("distance").toString()
                            )
                        )
                    }
                    _originalTripsList.value = tripsListState.data.documents
                    _onLoading.value = false
                }
            }
        }
    }

    /**
     * Do background job
     * This works in the background to make sure this current town can be a locality to anther destination
     */
    /*fun doBackGroundJob(currentList: List<DocumentSnapshot>, agencyID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            currentList.forEach { doc ->
                (doc["from"] as Map<String, Any>).let {
                    if (it[townID] == null && it[townName] == null) {
                        val updatedMap = mutableMapOf<String, Any>(townID to true, townName to true)
                        updatedMap.putAll(it)
                        firestoreRepo.db.document("OnlineTransportAgency/${agencyID}/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/${doc.id}")
                            .update("to", updatedMap.toMap()).await()
                    }
                }
            }
        }
    }*/

    //Adds a trip into the toDeleteList
    fun removeTrip(townId: String) =
        if (_toDeleteIDList.value!!.contains(townId)) _toDeleteIDList.value!!.remove(townId)
        else _toDeleteIDList.value!!.add(townId)

    //Adds a trip into the toAddList
    fun addTrip(townId: String) =
        if (_toAddIDList.value!!.contains(townId)) _toAddIDList.value!!.remove(townId)
        else _toAddIDList.value!!.add(townId)

    //Does the deletion of the list from the database
    suspend fun commitToDeleteList(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.runBatch { batch ->
            _toDeleteIDList.value!!.forEach { id ->
                batch.delete(
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/$id")
                )
            }
        }.apply {
            _onLoading.value = true
        }.addOnCompleteListener { task ->
            _onLoading.value = false
            if (task.isSuccessful) {
                //We make sure we clear the modification list of deleted content
                _toDeleteIDList.value!!.forEach { id ->
                    val index =
                        _localChangesMapList.value!!.withIndex().find { it.value["id"] == id }
                    index?.index?.let { _localChangesMapList.value!!.removeAt(it) }
                }
                _toDeleteIDList.value!!.clear()
                _onLoading.value = false
            } else {
                _onLoading.value = false
                _toastMessage.value = task.exception!!.handleError { }
            }
        }.await()
    }

    //Does the addition of the list to database
    suspend fun commitToAddList(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.runBatch { batch ->
            _toAddIDList.value!!.forEach { id ->
                val document = _originalTripsList.value!!.find {
                    it.id == id
                }!!

                /**
                 * In the trip document, we store the 2 towns alphabetically in a map called townNames
                 * where the town1 corresponds to the lowest in alphabetical order while the town2
                 * corresponds to the highest in alphabetical order
                 * e.g town1: Ambam, town2: Yaounde
                 */
                batch.set(
                    /**
                     * the from map indicates that we can leave from the the current townID and also current townName to the other
                     */
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/$id"),
                    hashMapOf(
                        "townNames" to mapOf(
                            "town1" to (document["townNames"]!! as HashMap<String, String>)["town1"]!!,
                            "town2" to (document["townNames"]!! as HashMap<String, String>)["town2"]!!
                        ),
                        /*"busTypes" to mapOf(//TODO: Take care to highlight these names
                            "seaterSeventy" to false,
                            "seaterCoaster" to false,
                            "seaterNormal" to false
                        ),*/
                        "townIDs" to document["townIDs"]!! as List<String>,
                        "distance" to document.getLong("distance")!!,
                        "isVip" to true,
                        "agencyID" to agencyID,
                        "flagVipPriceFromDistance" to true,
                        "flagNormalPriceFromDistance" to true,
                        "normalPrice" to pricePerKM * document.getLong("distance")!!,
                        "vipPrice" to vipPricePerKM * document.getLong("distance")!!,
                    )
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

    /**
     * When the save button saved
     */
    suspend fun commitAllChangesToDataBase(agencyID: String) {
        _onLoading.value = true
        firestoreRepo.db.runBatch { batch ->
            _localChangesMapList.value!!.forEachIndexed { index, tripMap ->
                //We remove the id field from the document
                val id = _localChangesMapList.value!![index].remove("id").toString()
                batch.update(
                    firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/$id"),
                    tripMap
                )
            }
        }.apply {
            if (!isComplete) _onLoading.value = true
        }.addOnCompleteListener {
            _onLoading.value = false
            if (it.isSuccessful) {
                _onClose.value = true
            } else {
                _onLoading.value = false
                _toastMessage.value = it.exception!!.handleError { }
            }
        }.await()
    }

    /**
     * Tags "isVip" as positive or as negative by inversing
     * It first check to see if this trip has been configured i.e it has a map present in the [tripChangesMapList], if yes we try to get the vip-field and incase it exists, we delete it else we set it to true
     * In the case the trip has never been config before, we create a new map with its details and add it [tripChangesMapList]
     */
    fun exemptVIP(tripId: String) {
        val tripDoc = _currentTripsList.value!!.withIndex().find {
            it.value.id == tripId
        }!!
        val existingChangeMap = _localChangesMapList.value!!.withIndex().find {
            it.value["id"] == tripId
        }
        if (existingChangeMap != null) {
            //If it was already true we make it false OR the reverse
            _localChangesMapList.value!![existingChangeMap.index]["isVip"] =
                !(_currentTripsList.value!![tripDoc.index]["isVip"]!! as Boolean)
        } else {
            //In case the user hasn't made any change to this document, we create a new map with the different vip option
            val changeMap = tripDoc.value.toMapWithIDField()
            changeMap["isVip"] = !_currentTripsList.value!![tripDoc.index].getBoolean("isVip")!!
            _localChangesMapList.value!! += changeMap
        }
    }

    /**
     * Adds the bus types the local cache database
     * if this document has already been modified, it means its surly stored in the cache [_localChangesMapList]
     * but if it is not existing, we add a change map with the values of the busTypes as true or false
     */
    fun configBusTypes(tripId: String, busType: BusTypes, newValue: Boolean) {
        val tripDoc = _currentTripsList.value!!.withIndex().find {
            it.value.id == tripId
        }!!
        val existingChangeMap = _localChangesMapList.value!!.withIndex().find {
            it.value["id"] == tripId
        }
        if (existingChangeMap != null) {
            when (busType) {
                BusTypes.SEATER_SEVENTY ->
                    (_localChangesMapList.value!![existingChangeMap.index]["busTypes"] as MutableMap<String, Any>)["seaterSeventy"] =
                        newValue
                BusTypes.SEATER_COASTER ->
                    (_localChangesMapList.value!![existingChangeMap.index]["busTypes"] as MutableMap<String, Any>)["seaterCoaster"] =
                        newValue
                BusTypes.SEATER_NORMAL ->
                    (_localChangesMapList.value!![existingChangeMap.index]["busTypes"] as MutableMap<String, Any>)["seaterNormal"] =
                        newValue
            }
        } else {
            val changeMap = tripDoc.value.toMapWithIDField()
            when (busType) {
                BusTypes.SEATER_SEVENTY ->
                    (changeMap["busTypes"] as MutableMap<String, Any>)["seaterSeventy"] = newValue
                BusTypes.SEATER_COASTER ->
                    (changeMap["busTypes"] as MutableMap<String, Any>)["seaterCoaster"] = newValue
                BusTypes.SEATER_NORMAL ->
                    (changeMap["busTypes"] as MutableMap<String, Any>)["seaterNormal"] = newValue
            }
            _localChangesMapList.value!!.add(changeMap)
        }

    }

    enum class BusTypes { SEATER_SEVENTY, SEATER_COASTER, SEATER_NORMAL }

    /**
     * Changes the normal price for another particular price, if the price is not more calculated, we set the "flagNormalPriceFromDistance" to false else true
     */
    fun changeNormalPrice(tripId: String, newNormalPrice: Long) {
        val tripDoc = _currentTripsList.value!!.withIndex().find {
            it.value.id == tripId
        }!!
        val existingChangeMap = _localChangesMapList.value!!.withIndex().find {
            it.value["id"] == tripId
        }
        if (existingChangeMap != null) {
            //If it was already true we make it false OR the reverse
            _localChangesMapList.value!![existingChangeMap.index]["normalPrice"] = newNormalPrice
        } else {
            val changeMap = tripDoc.value.toMapWithIDField()
            changeMap["normalPrice"] = newNormalPrice
            /*
                 We check to find out if the new price is same as the calculated price, in that case, true else false
              We check if the new price is equal to calculated price from the pricePerKm to set the flag
            */
            changeMap["flagNormalPriceFromDistance"] =
                (_currentTripsList.value!![tripDoc.index].getLong("distance")!! * pricePerKM).toLong() == newNormalPrice
            _localChangesMapList.value!! += changeMap
        }
    }

    /**
     * Same concept as [changeNormalPrice], but here we change instead "flagVipPriceFromDistance"
     */
    fun changeVIPPrice(tripId: String, newVIPPrice: Long) {
        val tripDoc = _currentTripsList.value!!.withIndex().find {
            it.value.id == tripId
        }!!
        val existingChangeMap = _localChangesMapList.value!!.withIndex().find {
            it.value["id"] == tripId
        }
        if (existingChangeMap != null) {
            //If it was already true we make it false OR the reverse
            _localChangesMapList.value!![existingChangeMap.index]["vipPrice"] = newVIPPrice
        } else {
            val changeMap = tripDoc.value.toMapWithIDField()
            changeMap["vipPrice"] = newVIPPrice
            /*
                 We check to find out if the new price is same as the calculated price, in that case, true else false
              We check if the new price is equal to calculated price from the pricePerKm to set the flag
            */
            changeMap["flagVipPriceFromDistance"] =
                (_currentTripsList.value!![tripDoc.index].getLong("distance")!! * pricePerKM).toLong() == newVIPPrice
            _localChangesMapList.value!! += changeMap
        }
    }

    enum class FieldTags {
        TOAST_MESSAGE,
        TOWN_ID, SHOW_ADD_TRIP,
        TRIP_ID,
        TOWN_NAME,
        ON_NORMAL_PRICE_FORM,
        REMOVE_MAP,
        START_TRIP_SEARCH,
        ON_VIP_PRICE_FORM,
        REBIND_ITEM,
        ON_CLOSE,
        CHECKED_ITEM,
        TRIP_NAME_LIST,
        CURRENT_TRIPS,
        SPAN_SIZE,
    }

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TripButtonTags {
        TRIPS_CHECK_VIP, TRIP_CHECK_TO_DELETE, TRIPS_BUTTON_NORMAL_PRICE, TRIPS_BUTTON_VIP_PRICE, //TRIPS_BUTTON_BUS_TYPES
    }

    enum class SortTags { TRIP_NAMES, TRIP_PRICES, TRIP_VIP_PRICES, DISTANCE }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.TOWN_NAME -> currentTownName = value.toString()
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.TOWN_ID -> townID = value.toString()
            FieldTags.TRIP_ID -> tripID = value.toString()
            FieldTags.REBIND_ITEM -> _onRebindItem.value = value as Boolean

            FieldTags.ON_NORMAL_PRICE_FORM -> _onNormalPriceForm.value = value as Boolean
            FieldTags.ON_VIP_PRICE_FORM -> _onVipPriceForm.value = value as Boolean
            FieldTags.START_TRIP_SEARCH -> _startTripSearch.value = value as Boolean
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
            FieldTags.SPAN_SIZE -> _spanSize.value = value as Int

            FieldTags.REMOVE_MAP -> tripsSimpleInfoMap.remove(value)
            FieldTags.SHOW_ADD_TRIP -> _onShowAddTrip.value = value as Boolean
            FieldTags.TRIP_NAME_LIST -> tripNamesList.add(value.toString())
            FieldTags.CURRENT_TRIPS -> _currentTripsList.value =
                value as MutableList<DocumentSnapshot>
        }
    }

    /**
     * A function to search for a specific trip destination from the doc and return its index in the list
     */
    fun searchTrip(destination: String): Int? {
        return _currentTripsList.value?.indexOf(
            _currentTripsList.value!!.find { tripMap ->
                (tripMap["townNames"] as Map<String, String>).containsValue(destination)
            }
        )
    }

    fun sortTripsResult(sortTags: SortTags) {
        when (sortTags) {
            SortTags.TRIP_NAMES -> _currentTripsList.value!!.sortBy {
                //Here we are simply sorting by the names but we must get the name of other towns
                if ((it["townNames"] as Map<String, String>)["town1"] == currentTownName)
                    (it["townNames"] as Map<String, String>)["town2"].toString()
                else
                    (it["townNames"] as Map<String, String>)["town1"].toString()
            }
            SortTags.TRIP_PRICES -> _currentTripsList.value!!.sortBy {
                (it["normalPrice"]!! as Double).toLong()
            }
            SortTags.TRIP_VIP_PRICES -> _currentTripsList.value!!.sortBy {
                (it["vipPrice"]!! as Double).toLong()
            }
            SortTags.DISTANCE -> _currentTripsList.value!!.sortBy {
                (it["distance"] as Double).toLong()
            }
        }
    }
}