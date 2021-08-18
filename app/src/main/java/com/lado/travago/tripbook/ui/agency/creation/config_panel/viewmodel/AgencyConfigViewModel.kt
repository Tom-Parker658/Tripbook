package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.OfflineCollection
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * Config Activity to control Master-Slave mechanism e.g Country-Journey, Continent-Country
 */
@ExperimentalCoroutinesApi
class AgencyConfigViewModel(
//    var startUpOption: AgencyConfigResources.StartUpTags
) : ViewModel() {
    //Ids  of the currently clicked in the recycler views
    private val _masterItemID = MutableLiveData("")
    private val _slaveItemID = MutableLiveData("")
    val masterItemID get() = _masterItemID
    val slaveItemID get() = _slaveItemID

    private lateinit var firestoreRepo: FirestoreRepo
    private lateinit var authRepo: FirebaseAuthRepo

    /*An offline collection to make modification and directly upload it the firestore when needed*/
    private lateinit var offlineCollection: OfflineCollection

    //Livedata to hold the list of master items
    private val _masterDataList = MutableLiveData(mutableListOf<Any>())
    val masterDataList get() = _masterDataList

    //Livedata to hold the list of slave items
    private val _slaveDataList = MutableLiveData(mutableListOf<Any>())
    val slaveDataList get() = _slaveDataList

    //LVD for loading bars
    private val _masterLoading = MutableLiveData(false)
    val masterLoading get() = _masterLoading
    private val _slaveLoading = MutableLiveData(false)
    val slaveLoading get() = _slaveLoading

    //LVD to know when and operation has failed and the reason
    private val _onMasterFailed = MutableLiveData("")
    val onMasterFailed get() = _onMasterFailed
    private val _onSlaveFailed = MutableLiveData("")
    val onSlaveFailed get() = _onSlaveFailed


    //Gets data for master then populates the list of items
//    suspend fun masterData() {
//        when (startUpOption.name) {
//            /**
//             * In this case, the master data is a list of countries from the {Planets/Earth/Continents/Africa} doc and specifically
//             * from the {countryList} Array.
//             */
//            AgencyConfigResources.StartUpTags.TRIPS_CONFIG.name -> {
//                firestoreRepo.getDocument("Planets/Earth/Continents/Africa").collect {
//                    when (it) {
//                        is State.Loading -> _masterLoading.value = true
//                        is State.Failed -> {
//                            _masterLoading.value = false
//                            _onMasterFailed.value = it.message
//                        }
//                        is State.Success -> {
//                            _masterLoading.value = false
//                            //We populate the list of items using the country List
//                            _masterDataList.value = it.data["countryList"]!! as List<String> as MutableList<Any>
//                        }
//                    }
//                }
//            }
//            else -> {
//            }
//        }
//    }

    //Gets data for slave then populates the list of items
    suspend fun slaveData(collectionPath: String) {
        firestoreRepo.getAllDocuments(collectionPath).collect {
            when (it) {
                is State.Loading -> _slaveLoading.value = true
                is State.Failed -> {
                    _slaveLoading.value = false
                    _onSlaveFailed.value = it.message
                }
                is State.Success -> {
                    _slaveLoading.value = false

                    //We populate the list of items List
//                    _slaveDataList.value = it.data.documents
                }
            }
        }
    }

    //Specific method to add scanners
    /**
     * 1- We check if there exist any booker with such a phone number and make sure he is not already a scanner{"isScanner: false"}
     * 2- We add he/she to our agency/Scanner sub-collection
     * 3- Sends the booker an invitation message
     */
    /**
    suspend fun addScanner(phoneNumber: String) {
        firestoreRepo.queryCollection("Bookers/") {
            it.whereEqualTo("phone", phoneNumber).whereEqualTo("isScanner", false)
        }.collect { bookerState ->
            when (bookerState) {
                is State.Loading -> {
                    _masterLoading.value = true
                }
                is State.Failed -> {
                    _masterLoading.value = false
                    _onMasterFailed.value = "step1:${bookerState.message}"
                }
                is State.Success -> {
                    if (!bookerState.data.isEmpty) {
                        //Th active field will be made true when the user accepts this invitation
                        val scannerMap = hashMapOf<String, Any?>(
                            "name" to bookerState.data.first().getString("name"),
                            "phone" to bookerState.data.first().getString("name"),
                            "photoUrl" to bookerState.data.first().getString("photoUrl"),
                            "isAdmin" to false,
                            "active" to false,
                            "scansNumber" to 0,
                            "addedOn" to Timestamp.now(),
                        )
                        firestoreRepo.setDocument(
                            scannerMap,
                            "OnlineTransportAgency/${agencyID}/Scanners/${bookerState.data.first().id}"
                        ).collect { state1 ->
                            when (state1) {
                                is State.Loading -> {/*Nothing since we are still in loading phase*/
                                }
                                is State.Failed -> {
                                    _masterLoading.value = false
                                    _onMasterFailed.value = "step2:${state1.message}"
                                }
                                is State.Success -> {
                                    //Finally we send an invitation to the booker
                                    firestoreRepo.addDocument(
                                        Booker.InvitationMessage(
                                            agencyID,
                                            agencyName,
                                            logoUrl,
                                            Timestamp.now()
                                        ).messageMap,
                                        "Bookers/${bookerState.data.first().id}/Messages"
                                    ).collect {
                                        when (it) {
                                            is State.Loading -> {/*Nothing*/
                                            }
                                            is State.Failed -> {
                                                _masterLoading.value = false
                                                _onMasterFailed.value =
                                                    "Invitation: Resnd invitation later${it.message}"
                                            }
                                            is State.Success -> {
                                                _masterLoading.value = false
                                                Log.d("Invitation", "Success: ${it.data}")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        _masterLoading.value = false
                        _onMasterFailed.value =
                            "This booker is either unavailable or is already a Scanner! You can invite him to join the app."
                    }

                }
            }
        }
    }
    */
    /**
     * A sandbox to configure a master to slave situation
     */
    fun addCountries() {

    }

    /**
     * To set Ids of clicked items
     */
    fun setClickedItemID(fieldTags: FieldTags, id: String) {
        when (fieldTags) {
            FieldTags.MASTER_ID -> _masterItemID.value = id
            FieldTags.SLAVE_ID -> _slaveItemID.value = id
        }
    }

    enum class FieldTags() { MASTER_ID, SLAVE_ID, NAME }

}