package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

@ExperimentalCoroutinesApi
class TripsConfigViewModel : ViewModel() {
    private val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading get() = _onLoading

    private val _onClose = MutableLiveData(false)
    val onClose get() = _onClose

    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    private val _onNormalPriceForm = MutableLiveData(false)
    val onNormalPriceForm get() = _onNormalPriceForm

    private val _onVipPriceForm = MutableLiveData(false)
    val onVipPriceForm get() = _onVipPriceForm

    private val _startTripSearch = MutableLiveData(false)
    val startTripSearch get() = _startTripSearch

    //Forces the adapter to rebind an object
    private val _onRebindItem = MutableLiveData(false)
    val onRebindItem get() = _onRebindItem

    //Holds the value of the trips for the currently selected town
    private val _tripDocList = MutableLiveData<MutableList<DocumentSnapshot>>()
    val tripDocList: LiveData<MutableList<DocumentSnapshot>> get() = _tripDocList

    /**
     * Syntax in firestore
     * listOf(
     *  {
     *   "tripID" : String,
     *   "vipPrice" : Long?,
     *   "normalPrice" : Long?,
     *   "vip" : Boolean?,
     *   "exempted": Boolean?
     *  },
     *
     *  {
     *   "tripID" : String,
     *   "vipPrice" : Long?,
     *   "normalPrice" : Long?,
     *   "vip" : Boolean?,
     *   "exempted": Boolean?
     *  },
     *
     *  {
     *   "tripID" : String,
     *   "vipPrice" : Long?,
     *   "normalPrice" : Long?,
     *   "vip" : Boolean?,
     *   "exempted": Boolean?
     *  },
     * )
     */

    var sortCheckedItem = 0
        private set

    val tripChangesMapList = mutableListOf<MutableMap<String, Any?>>()
//    var originalChangesCopy = listOf<MutableMap<String, Any?>>()

    val tripNameList = mutableListOf<String>()

    //Do or redo the fetching towns
    private val _retryTrips = MutableLiveData(true)
    val retryTrips get() = _retryTrips

    // Stores the id of the current town for immediate navigation
    var townID = ""
        private set
    var townName = ""
        private set

    //Holds the general price per km
    var pricePerKM = 0.0
        private set
    var vipPricePerKM = 0.0
        private set

    var tripID = ""
        private set

    suspend fun getTrips() {
        _retryTrips.value = false
        firestoreRepo.getCollection("Planets/Earth/Continents/Africa/Cameroon/$townID/Trips")
            .collect { tripsListState ->
                when (tripsListState) {
                    is State.Loading -> _onLoading.value = true
                    is State.Failed -> {
                        _toastMessage.value =
                            tripsListState.exception.handleError { /**TODO: Handle Error lambda*/ }
                        _onLoading.value = false
                    }
                    is State.Success -> {
                        //We get that agency document to get the pricePerKM and vipPricePerKM
                        firestoreRepo.getDocument("OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0")
                            .collect { agencyState ->
                                when (agencyState) {
                                    is State.Loading -> _onLoading.value = true
                                    is State.Failed -> {
                                        _toastMessage.value =
                                            agencyState.exception.handleError { /**TODO: Handle Error lambda*/ }
                                        _onLoading.value = false
                                    }
                                    is State.Success -> {
                                        //_tripDocList.value = tripsListState.data.documents
                                        //Now we get the configurations about the trips from this town e.g Exemptions, vip prices, normal prices
                                        firestoreRepo.getDocument("OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0/Configs/Cameroon/Towns/$townID")
                                            .collect { configsDocState ->
                                                when (configsDocState) {
                                                    is State.Loading -> _onLoading.value = true
                                                    is State.Failed -> {
                                                        _toastMessage.value =
                                                            configsDocState.exception.handleError { /**TODO: Handle Error lambda*/ }
                                                        _onLoading.value = false
                                                        //TODO: Handle failures inorder to retry
                                                    }
                                                    is State.Success -> {
                                                        //We get the changes list if it exist already
                                                        configsDocState.data.get(
                                                            "tripChangesList"
                                                        )?.let {
                                                            tripChangesMapList.plusAssign(it as MutableList<MutableMap<String, Any?>>)
//                                                            originalChangesCopy = tripChangesMapList
                                                        }

                                                        pricePerKM = 11.0
//                                                            agencyState.data.getDouble("pricePerKM")!!
                                                        vipPricePerKM = 12.0
//                                                            agencyState.data.getDouble("vipPricePerKM")!!

                                                        //We get the list of destinations for the current town -> A trip
                                                        _tripDocList.value =
                                                            tripsListState.data.documents
                                                        //We get the names of the destinations
                                                        _tripDocList.value!!.forEach {
                                                            tripNameList += it["destination"]!!.toString()
                                                        }
                                                        _onLoading.value = false
                                                    }
                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
            }
    }

    /**
     * This get the trip which has been clicked to exempt it if not already found in exemption list of
     * add him if not found
     */
    fun exemptTrip(tripId: String) {
        val tripData = tripChangesMapList.find {
            it["tripID"] == tripId
        }
        val index = tripChangesMapList.indexOf(tripData)
        if (index != -1) {
            val exempted = tripChangesMapList[index]["exempted"]
            if (exempted == null) tripChangesMapList[index]["exempted"] = true
            else tripChangesMapList[index].remove("exempted")
        } else {
            tripChangesMapList.plusAssign(
                hashMapOf(
                    "tripID" to tripId,
                    "exempted" to true
                )
            )
        }
    }

    /**
     * Adds or remove a trip from the vip exemption list
     * It first check to see if this trip has been configured i.e it has a map present in the [tripChangesMapList], if yes we try to get the vip-field and incase it exists, we delete it else we set it to true
     * In the case the trip has never been config before, we create a new map with its details and add it [tripChangesMapList]
     */
    fun exemptVIP(tripId: String) {
        val tripData = tripChangesMapList.find {
            it["tripID"] == tripId
        }
        val index = tripChangesMapList.indexOf(tripData)
        if (index != -1) {
            val vip = tripChangesMapList[index]["vip"]
            if (vip == null) tripChangesMapList[index]["vip"] = true
            else tripChangesMapList[index].remove("vip")
        } else {
            tripChangesMapList.plusAssign(
                hashMapOf(
                    "tripID" to tripId,
                    "vip" to true
                )
            )
        }
    }

    /**
     * Changes the vip price for another particular price
     */
    fun changeVIPPrice(tripId: String, newVIPPrice: Long) {
        val oldPriceMap = tripChangesMapList.find {
            it["tripID"] == tripId
        }

        if (oldPriceMap.isNullOrEmpty() && newVIPPrice != 0L) {
            tripChangesMapList.plusAssign(
                hashMapOf(
                    "tripID" to tripID,
                    "vipPrice" to newVIPPrice
                )
            )
        } else {//In case it exist already
            val index = tripChangesMapList.indexOf(oldPriceMap)
            try {
                if (newVIPPrice != 0L) tripChangesMapList[index]["vipPrice"] = newVIPPrice
                else tripChangesMapList[index].remove("vipPrice")
            } catch (e: Exception) {/*TODO: Handle it*/
            }
        }

    }

    /**
     * Changes the normal price for another particular price
     */
    fun changeNormalPrice(tripId: String, newNormalPrice: Long) {
        val oldPriceMap = tripChangesMapList.find {
            it["tripID"] == tripId
        }
        if (oldPriceMap.isNullOrEmpty() && newNormalPrice != 0L)
            tripChangesMapList.plusAssign(
                hashMapOf(
                    "tripID" to tripID,
                    "normalPrice" to newNormalPrice
                )
            )
        else {//In case it exist already
            val index = tripChangesMapList.indexOf(oldPriceMap)
            if (newNormalPrice != 0L) tripChangesMapList[index]["normalPrice"] = newNormalPrice
            else tripChangesMapList[index].remove("normalPrice")
        }
    }


    enum class FieldTags {
        TOAST_MESSAGE, TOWN_ID, TRIP_ID, TOWN_NAME, ON_NORMAL_PRICE_FORM, START_TRIP_SEARCH, ON_VIP_PRICE_FORM, REBIND_ITEM, ON_CLOSE, CHECKED_ITEM
    }

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TripButtonTags {
        TRIPS_CHECK_VIP, TRIPS_SWITCH_ACTIVATE, TRIPS_BUTTON_NORMAL_PRICE, TRIPS_BUTTON_VIP_PRICE
    }

    enum class SortTags { TRIP_NAMES, TRIP_PRICES, TRIP_VIP_PRICES, DISTANCE }

    fun setField(fieldTag: FieldTags, value: Any) {
        when (fieldTag) {
            FieldTags.TOWN_NAME -> townName = value.toString()
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.TOWN_ID -> townID = value.toString()
            FieldTags.TRIP_ID -> tripID = value.toString()
            FieldTags.REBIND_ITEM -> _onRebindItem.value = value as Boolean
            FieldTags.ON_NORMAL_PRICE_FORM -> _onNormalPriceForm.value = value as Boolean
            FieldTags.ON_VIP_PRICE_FORM -> _onVipPriceForm.value = value as Boolean
            FieldTags.START_TRIP_SEARCH -> _startTripSearch.value = value as Boolean
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
        }
    }

    /**
     * A function to search for a specific trip destination from the doc and return its index in the list
     */
    fun searchTrip(tripName: String) =
        tripDocList.value?.indexOf(
            tripDocList.value!!.find { tripDoc ->
                tripDoc["destination"] == tripName
            }
        )

    /*fun clearProperties() {
        tripChangesMapList.clear()
        _tripDocList.value!!.clear()
        tripNameList.clear()
    }*/

    /**
     * Uploads the alterations to db
     */
    suspend fun uploadTripChanges() {
        val newTripList = mutableListOf<MutableMap<String, Any?>>()
        for (it in tripChangesMapList) {
            if (!(it["vip"] == null && it["vipPrice"] == null && it["normalPrice"] == null && it["exempted"] == null))
                newTripList += it
        }
        val tripChanges = hashMapOf<String, Any?>(
            "tripChangesList" to newTripList
        )
        firestoreRepo.setDocument(
            data = tripChanges,
            "OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0/Configs/Cameroon/Towns/$townID"
        ).collect {
            when (it) {
                is State.Loading -> _onLoading.value = true
                is State.Failed -> {
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                    _onLoading.value = false
                    //TODO: Something
                }
                is State.Success -> {
                    _onLoading.value = false

                    //We end this fragment and navigate back to the towns fragment
                    _onClose.value = true
                }
            }
        }
    }

    fun sortTripsResult(sortTags: SortTags) {
        when (sortTags) {
            SortTags.TRIP_NAMES -> _tripDocList.value!!.sortBy {
                    it.getString("destination")
                }
            SortTags.TRIP_PRICES -> _tripDocList.value!!.sortBy {
                it.getLong("distance")!! * pricePerKM
            }
            SortTags.TRIP_VIP_PRICES -> _tripDocList.value!!.sortBy {
                it.getLong("distance")!! * vipPricePerKM
            }
            SortTags.DISTANCE -> _tripDocList.value!!.sortBy {
                    it.getLong("distance")
                }
        }
    }

}