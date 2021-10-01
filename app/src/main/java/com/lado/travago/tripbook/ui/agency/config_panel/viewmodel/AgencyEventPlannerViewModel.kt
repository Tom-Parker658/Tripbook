package com.lado.travago.tripbook.ui.agency.config_panel.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.sql.Time
import java.util.*


@ExperimentalCoroutinesApi
class AgencyEventPlannerViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(false)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //creation data
    var eventReason = ""
        private set
    var eventType = ""
        private set
    var eventDateInMillis = 0L
        private set
    var eventTime: TimeModel? = null
        private set
    private var eventDateAndTime: Timestamp? = null

    enum class FieldTags { ON_LOADING, TOAST_MESSAGE, EVENT_REASON, EVENT_DATE, EVENT_TIME, EVENT_TYPE }

    /**
     * @property LIVE is when the planned event is taking place now and so can be stopped or end
     * @property NOT_LIVE is when the planned event is still to come and so can still be deleted
     */
    enum class EventStates { LIVE, NOT_LIVE }

    fun setField(fieldTag: FieldTags, value: Any) =
        when (fieldTag) {
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.EVENT_REASON -> eventReason = value.toString()
            FieldTags.EVENT_DATE -> eventDateInMillis = value as Long
            FieldTags.EVENT_TIME -> eventTime = value as TimeModel
            FieldTags.EVENT_TYPE -> eventType = value.toString()
        }

    suspend fun deleteEvent(agencyID: String, eventID: String, currentEventDate: Long) {
        //We first delete the event from the agency main document
        firestoreRepo.getDocument("OnlineTransportAgency/$agencyID")
            .collect { agencyDoc ->
                when (agencyDoc) {
                    is State.Failed -> {
                        _toastMessage.value = agencyDoc.exception.handleError {}
                        _onLoading.value = false
                    }
                    is State.Loading -> _onLoading.value = true
                    is State.Success -> {
                        //We remove the date of the current event from the list of old events and store
                        val eventList = agencyDoc.data.get("eventDateList") as MutableList<Long>
                        eventList.remove(currentEventDate)
                        eventList.toSet().toList()
                        //We add this event date to the eventDateList in the agency document
                        firestoreRepo.setDocument(
                            hashMapOf("eventDateList" to eventList),
                            "OnlineTransportAgency/$agencyID"
                        ).collect { state ->
                            when (state) {
                                is State.Failed -> {
                                    _toastMessage.value = state.exception.handleError {}
                                    _onLoading.value = false
                                }
                                is State.Loading -> _onLoading.value = true
                                is State.Success -> {
                                    _onLoading.value = false
                                    _toastMessage.value = "Done"
                                    //Remove the actual event document from the database
                                    firestoreRepo.deleteDocument("OnlineTransportAgency/$agencyID/Events/$eventID")
                                        .collect {
                                            when (it) {
                                                is State.Failed -> {
                                                    _toastMessage.value =
                                                        it.exception.handleError { }
                                                    _onLoading.value = false
                                                }
                                                is State.Loading -> _onLoading.value = true
                                                is State.Success -> {
                                                    _onLoading.value = false
                                                    _toastMessage.value = "Successfully cancelled"
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


    suspend fun stopEvent(agencyID: String, eventID: String, currentEventDate: Long) {
        //We now add this date to the list of events in Original Agency Document
        firestoreRepo.getDocument("OnlineTransportAgency/$agencyID")
            .collect { agencyDoc ->
                when (agencyDoc) {
                    is State.Failed -> {
                        _toastMessage.value = agencyDoc.exception.handleError {}
                        _onLoading.value = false
                    }
                    is State.Loading -> _onLoading.value = true
                    is State.Success -> {
                        //We ad the date of the new event o the list of old events and store
                        val eventList = agencyDoc.data.get("eventDateList") as MutableList<Long>
                        eventList.remove(currentEventDate)
                        eventList.toSet().toList()
                        //We add this event date to the eventDateList in the agency documents
                        firestoreRepo.setDocument(
                            hashMapOf("eventDateList" to eventList),
                            "OnlineTransportAgency/$agencyID"
                        ).collect { state ->
                            when (state) {
                                is State.Failed -> {
                                    _toastMessage.value = state.exception.handleError {}
                                    _onLoading.value = false
                                }
                                is State.Loading -> _onLoading.value = true
                                is State.Success -> {
                                    val dataMap = hashMapOf<String, Any?>(
                                        "isExpired" to true
                                    )
                                    firestoreRepo.setDocument(
                                        dataMap,
                                        "OnlineTransportAgency/$agencyID/Events/$eventID"
                                    )
                                        .collect {
                                            when (it) {
                                                is State.Failed -> {
                                                    _toastMessage.value =
                                                        it.exception.handleError {}
                                                    _onLoading.value = false
                                                }
                                                is State.Loading -> _onLoading.value = true
                                                is State.Success -> {
                                                    _onLoading.value = false
                                                    _toastMessage.value =
                                                        "The event has been stopped!"
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
     * @param scannerID is gotten from the current booker and admin scanner who planned the event
     */
    suspend fun createEvent(agencyID: String, scannerID: String) {
        //We create a time with the selected date an the selected time
        eventDateAndTime = Timestamp(
            Date(
                Time.UTC(
                    Date(eventDateInMillis).year,
                    Date(eventDateInMillis).month,
                    Date(eventDateInMillis).date,
                    eventTime!!.hour,
                    eventTime!!.minutes,
                    0
                )
            )
        )
        val eventData = hashMapOf<String, Any?>(
            "eventReason" to eventReason,
            "eventType" to eventType,
            "eventDate" to eventDateAndTime,
            "isExpired" to false,
            "scannerID" to scannerID
        )

        // TODO: Make sure No booker has booked a ticket from the agency and need to travel the day of the maintenance
        firestoreRepo.addDocument(eventData, "OnlineTransportAgency/$agencyID/Events/")
            .collect {
                when (it) {
                    is State.Failed -> {
                        _toastMessage.value = it.exception.handleError {}
                        _onLoading.value = false
                    }
                    is State.Loading -> _onLoading.value = true
                    is State.Success -> {
                        _onLoading.value = false
                        //We now add this date to the list of events in Original Agency Document
                        firestoreRepo.getDocument("OnlineTransportAgency/$agencyID")
                            .collect { agencyDoc ->
                                when (agencyDoc) {
                                    is State.Failed -> {
                                        _toastMessage.value = agencyDoc.exception.handleError {}
                                        _onLoading.value = false
                                    }
                                    is State.Loading -> _onLoading.value = true
                                    is State.Success -> {
                                        //We add the date of the new event to the list of old events and store
                                        val eventList =
                                            agencyDoc.data.get("eventDateList") as List<Long>?
                                        val newEventList = mutableListOf(
                                            eventDateAndTime!!.toDate().time,
                                        )
                                        eventList?.let { newEventList += eventList }
                                        newEventList.toSet().toList()

                                        //We add this event date to the eventDateList in the agency documents
                                        firestoreRepo.setDocument(
                                            hashMapOf<String, Any?>("eventDateList" to newEventList),
                                            "OnlineTransportAgency/$agencyID"
                                        )
                                            .collect { state ->
                                                when (state) {
                                                    is State.Failed -> {
                                                        _toastMessage.value =
                                                            state.exception.handleError {}
                                                        _onLoading.value = false
                                                    }
                                                    is State.Loading -> _onLoading.value = true
                                                    is State.Success -> {
                                                        _onLoading.value = false
                                                        _toastMessage.value =
                                                            "The event has created succesfully!"
                                                        clearData()
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


    companion object {
        fun AgencyEventPlannerViewModel.clearData() {
            eventDateInMillis = 0L
            eventReason = ""
            eventTime = null
            eventReason = ""
            eventType = ""
        }
    }
}