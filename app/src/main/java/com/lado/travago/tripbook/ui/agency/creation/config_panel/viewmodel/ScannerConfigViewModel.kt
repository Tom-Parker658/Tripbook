package com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.model.Document
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.model.users.Booker
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerConfigViewModel : ViewModel() {
    private val firestoreRepo = FirestoreRepo()

    /**New scanner addition variables*/
    private val _onSearchLoading = MutableLiveData(false)
    val onSearchLoading: LiveData<Boolean> get() = _onSearchLoading

    var countryCode = 237
        private set
    var scannerPhone = ""
        private set
    var sortCheckedItem = 0
        private set

    private val _newScannerSearch = MutableLiveData(false)
    val newScannerSearch: LiveData<Boolean> get() = _newScannerSearch

    private val _newScannerDoc = MutableLiveData<DocumentSnapshot>()
    val newScannerDoc: LiveData<DocumentSnapshot> get() = _newScannerDoc

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _onLoading = MutableLiveData(false)
    val onLoading get() = _onLoading

    private val _retrySearch = MutableLiveData(true)
    val retrySearch get() = _retrySearch

    //Navigate away
    private val _onClose = MutableLiveData(false)
    val onClose: LiveData<Boolean> get() = _onClose

    //When the search for a new scanner is not found
    private val _onNoResult = MutableLiveData(false)
    val onNoResult: LiveData<Boolean> get() = _onNoResult

    //Show the dialog to add scanner
    private val _onAddScannerDialog = MutableLiveData(false)
    val onAddScannerDialog: LiveData<Boolean> get() = _onAddScannerDialog

    //Contains the list of scanners which have been made admin
    val adminIDList = mutableListOf<MutableMap<String, Any>>()

    private val _onScannerFired = MutableLiveData<Int?>()
    val onScannerFired get() = _onScannerFired

    // Stores the list of that agency
    private val _myScannerList = MutableLiveData(mutableListOf<DocumentSnapshot>())
    val myScannerList get() = _myScannerList

    //Stores the original list
    private var originalScannerList = listOf<DocumentSnapshot>()

    suspend fun getScannerListData(agencyID: String) {
        firestoreRepo.getAllDocuments(
            "OnlineTransportAgency/$agencyID/Scanners"
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {

                    //We get the list and store the copy of the original
                    it.data.run {
                        _myScannerList.value = documents
                        originalScannerList = documents
                    }

                    _onLoading.value = false
                    _retrySearch.value = false
                }
            }
        }
    }

    //Makes the scanner admin when the  check is pressed
    fun makeScannerAdmin(scannerId: String) {
        //If the current scanner already exist we delete him else we add him and make him admin
        val scannerMap = adminIDList.find {
            it["id"].toString() == scannerId
        }
        val scannerDoc = _myScannerList.value!!.find {
            it.id == scannerId
        }!!
        if (scannerMap == null) {
            adminIDList.add(
                hashMapOf(
                    "id" to scannerDoc.id,
                    "isAdmin" to !scannerDoc.getBoolean("isAdmin")!!
                )
            )
        } else adminIDList[adminIDList.indexOf(scannerMap)]["isAdmin"] =
            scannerMap["isAdmin"] != true //We just interchange boolean values

    }

    //delete a scanner and also removes it from the list
    suspend fun fireScanner(scannerId: String, agencyID: String) {
        firestoreRepo.deleteDocument("OnlineTransportAgency/$agencyID/Scanners/$scannerId")
            .collect {
                when (it) {
                    is State.Failed -> {
                        _onLoading.value = false
                        _toastMessage.value =
                            it.exception.handleError { /**TODO: Handle Error lambda*/ }
                    }
                    is State.Loading -> _onLoading.value = true
                    is State.Success -> {
                        firestoreRepo.addDocument(
                            hashMapOf(
//                                "agencyID" to
                                "message" to "You were fired from your agency on ${
                                    Utils.formatDate(
                                        Date().time,
                                        "dddd MMMM yyyy ,HH:mm:ss"
                                    )
                                } .We are Sorry "
                            ),
                            "Bookers/$scannerId/Messages"
                        ).collect { messageState ->
                            when (messageState) {
                                is State.Loading -> {
                                    _onLoading.value = true
                                }
                                is State.Failed -> {
                                    _onLoading.value = false
                                    _toastMessage.value =
                                        messageState.exception.handleError { /*TODO: Handle Error lambda*/ }
                                }
                                is State.Success -> {
                                    //We now remove it from the list
                                    val firedScannerDoc = _myScannerList.value!!.find { doc ->
                                        doc.id == scannerId
                                    }
                                    val adminMap = adminIDList.find { doc ->
                                        doc["id"] == scannerId
                                    }
                                    adminIDList.remove(adminMap)
                                    val index = _myScannerList.value!!.indexOf(firedScannerDoc)
                                    _myScannerList.value!!.remove(firedScannerDoc)
                                    _onScannerFired.value = index//To notify listener
                                    _onLoading.value = false
                                }
                            }

                        }

                    }
                }
            }
    }

    fun setField(fieldTags: FieldTags, value: Any?) {
        when (fieldTags) {
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.ON_SCANNER_FIRED -> _onScannerFired.value = value as Int?
            FieldTags.ON_SEARCH_LOADING -> _onSearchLoading.value = value as Boolean
            FieldTags.COUNTRY_CODE -> value.toString().toInt()
            FieldTags.NOT_FOUND -> _onNoResult.value = value as Boolean
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.CHECKED_ITEM -> sortCheckedItem = value as Int
            FieldTags.SCANNER_PHONE -> scannerPhone = value.toString()
            FieldTags.ADD_SCANNER_DIALOG -> _onAddScannerDialog.value = value as Boolean
            FieldTags.ON_CLOSE -> _onClose.value = value as Boolean
            FieldTags.RETRY_SEARCH -> _retrySearch.value = value as Boolean
        }
    }

    /**
     * Update the ['isAdmin'] field in the database information
     */
    suspend fun uploadAdmin(agencyID: String) {
        _onLoading.value = true
        for (map in adminIDList) {
            firestoreRepo.db.document("OnlineTransportAgency/$agencyID/Scanners/${map["id"]}")
                .update(
                    "isAdmin",
                    map["isAdmin"]
                ).addOnCompleteListener {
                    if (!it.isSuccessful) _toastMessage.value = "${it.exception.message}"
                }.await()
        }
        _onLoading.value = false
    }

    /**
     * Searches through the booker collection and returns its information
     * @param phoneNumber should be a live data which is true if the phone field is valid
     */
    suspend fun searchNewScanner(phoneNumber: String) {
        firestoreRepo.queryCollection(
            "Bookers",
            query = {
                it.whereEqualTo("phone", phoneNumber)
            }
        ).collect {
            _onNoResult.value = false
            when (it) {
                is State.Failed -> {
                    _onSearchLoading.value = false
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Loading -> _onSearchLoading.value = true
                is State.Success -> {
                    it.data.documents.let { doc ->
                        if (doc.isNotEmpty()) _newScannerDoc.value = doc.first()
                        else _onNoResult.value = true
                    }
                    _onSearchLoading.value = false
                }
            }
        }
    }

    /**
     * Sort a collection using specified tag
     */
    fun sortResult(sortTags: SortTags) {
        when (sortTags) {
            SortTags.SCANNER_NAME ->
                _myScannerList.value!!.sortBy {
                    it.getString("name")
                }
            SortTags.NUM_SCANS ->
                _myScannerList.value!!.sortBy {
                    it.getLong("numberScans")
                }
            SortTags.ADDED_ON ->
                _myScannerList.value!!.sortBy {
                    it.getDate("addedOn")
                }
            SortTags.IS_ADMIN ->
                _myScannerList.value!!.sortBy {
                    it.getBoolean("isAdmin")
                }
        }
    }

    enum class SortTags { SCANNER_NAME, NUM_SCANS, ADDED_ON, IS_ADMIN }

    /**
     * Recruits a booker into your agency from the phone search
     */
    suspend fun recruitScanner(bookerDoc: DocumentSnapshot, agencyID: String) {
        val scannerMap = hashMapOf<String, Any?>(
            "name" to bookerDoc.getString("name"),
            "phone" to bookerDoc.getString("phone"),
            "photoUrl" to bookerDoc.getString("photoUrl"),
            "isAdmin" to false,
            "active" to false,
            "scansNumber" to 0,
            "addedOn" to Timestamp.now(),
        )
        val message = "An agency has sent you and invitation on ${
            Utils.formatDate(
                Timestamp.now().toDate().time,
                "MM, dd yyyy"
            )
        } at ${
            Utils.formatDate(
                Timestamp.now().toDate().time,
                "HH:mm:ss"
            )
        }. They wish to get you as their personalScanner.\n NB: You can be a scanner for only one agency! \n If you don't want, donot accept"

        val messageMap = hashMapOf<String, Any?>(
            "agencyID" to "Bh7XGjKv5AlUMoDQFpv0",
            "message" to message
        )

        firestoreRepo.setDocument(
            scannerMap,
            "OnlineTransportAgency/${agencyID}/Scanners/${bookerDoc.id}"
        ).collect {
            when (it) {
                is State.Failed -> {
                    _onLoading.value = false
                    _toastMessage.value =
                        it.exception.handleError { /**TODO: Handle Error lambda*/ }
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    firestoreRepo.addDocument(
                        messageMap,
                        "Bookers/${bookerDoc.id}/Messages"
                    ).collect { messageState ->
                        when (messageState) {
                            is State.Failed -> {
                                _onLoading.value = false
                                _toastMessage.value =
                                    messageState.exception.handleError { /**TODO: Handle Error lambda*/ }
                            }
                            is State.Loading -> _onLoading.value = true
                            is State.Success -> {
                                _toastMessage.value = "You have a new scanner"
                                _onLoading.value = false
                                _retrySearch.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    enum class FieldTags { ON_LOADING, ON_SCANNER_FIRED, ON_SEARCH_LOADING, ON_CLOSE, COUNTRY_CODE, NOT_FOUND, TOAST_MESSAGE, CHECKED_ITEM, SCANNER_PHONE, ADD_SCANNER_DIALOG, RETRY_SEARCH }
    enum class ScannerButtonTags {
        BUTTON_IS_ADMIN, BUTTON_FIRE_SCANNER
    }
}