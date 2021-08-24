package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class TripsConfigViewModel : ViewModel() {
    private val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    private val _onPriceForm = MutableLiveData(false)
    val onPriceForm get() = _onPriceForm

    private val _startTripSearch = MutableLiveData(false)
    val startTripSearch get() = _startTripSearch

    //Holds the value of the trips for the currently selected town
    private val _tripDocList = MutableLiveData<List<DocumentSnapshot>>()
    val tripDocList get() = _tripDocList

    val tripNameList = mutableListOf<String>()
    //A list of maps containing price config for a particular trip
    var optionMapList = mutableListOf<Map<String, Any>>()

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

    //Fields to hold prices for a vip and a normal trip
    var normalPrice = 0L
        private set
    var vipPrice = 0L
        private set

    //Holds all the configurations about a particular town and its trip
    lateinit var configsDoc: DocumentSnapshot
    var exemptedTripList = mutableListOf<String>() //and that's agency's exception list for a particular trip
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
                        _toastMessage.value = tripsListState.message
                        _onLoading.value = false
                    }
                    is State.Success -> {
                        //We get that agency document to get the pricePerKM and vipPricePerKM
                        firestoreRepo.getDocument("OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0").collect{agencyState ->
                            when(agencyState) {
                                is State.Loading -> _onLoading.value = true
                                is State.Failed -> {
                                    _toastMessage.value = agencyState.message
                                    _onLoading.value = false
                                }
                                is State.Success ->{
                                    //_tripDocList.value = tripsListState.data.documents
                                    //Now we get the configurations about the trips from this town e.g Exemptions, vip prices, normal prices
                                    firestoreRepo.getDocument("OnlineTransportAgency/Bh7XGjKv5AlUMoDQFpv0/Configs/Cameroon/Towns/$townID")
                                        .collect { configsDocState ->
                                            when (configsDocState) {
                                                is State.Loading -> _onLoading.value = true
                                                is State.Failed -> {
                                                    _toastMessage.value = configsDocState.message
                                                    _onLoading.value = false
                                                    //TODO: Handle failures inorder to retry
                                                }
                                                is State.Success -> {
                                                    pricePerKM = agencyState.data.getDouble("pricePerKM")!!
                                                    vipPricePerKM = agencyState.data.getDouble("vipPricePerKM")!!
                                                    configsDoc = configsDocState.data

                                                    //Lastly we try to get
                                                    configsDoc.getField<MutableList<String>>("tripList")?.let {
                                                        exemptedTripList.addAll(it)
                                                    }
                                                    configsDoc.getField< List< Map<String, Any> > >("optionMapList")?.let {
                                                        optionMapList.addAll(it)
                                                    }
                                                    //We get the list of destinations for the current town -> A trip
                                                    _tripDocList.value =
                                                        tripsListState.data.documents.sortedBy {
                                                            it["destination"].toString().lowercase()
                                                        }

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
    fun exemptTrip(tripId: String) =
        if (exemptedTripList.contains(tripId)) exemptedTripList.remove(townID)
        else exemptedTripList.add(tripId)

    fun configureTrip(tripId: String) {
        if(optionMapList.find{
            it[tripId]
            })
    }

    enum class FieldTag {
        NORMAL_PRICE, VIP_PRICE, TOAST_MESSAGE, TOWN_ID, TRIP_ID, TOWN_NAME, ON_PRICE_FORM, START_TRIP_SEARCH
    }

    /**
     * Tags used to know what the user has clicked on a recycler view item
     */
    enum class TripButtonTags {
        TRIPS_CHECK_VIP, TRIPS_SWITCH_ACTIVATE, TRIPS_BUTTON_PRICES
    }

    fun setField(fieldTag: FieldTag, value: Any) {
        when (fieldTag) {
            FieldTag.TOWN_NAME -> townName = value.toString()
            FieldTag.NORMAL_PRICE -> normalPrice = value.toString().let {
                if (it.isBlank()) 0
                else it.toLong()
            }
            FieldTag.VIP_PRICE -> vipPrice = value.toString().let {
                if (it.isBlank()) 0
                else it.toLong()
            }
            FieldTag.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTag.TOWN_ID -> townID = value.toString()
            FieldTag.TRIP_ID -> tripID = value.toString()
            FieldTag.ON_PRICE_FORM -> _onPriceForm.value = value as Boolean
            FieldTag.START_TRIP_SEARCH -> _startTripSearch.value = value as Boolean
        }
    }

    /**
     * A function to search for a specific trip destination from the doc and return its index in the list
     */
    fun searchTrip(tripName: String): Int =
        tripDocList.value!!.indexOf(
            tripDocList.value!!.find { tripDoc ->
                tripDoc["destination"] == tripName
            }
        )

}