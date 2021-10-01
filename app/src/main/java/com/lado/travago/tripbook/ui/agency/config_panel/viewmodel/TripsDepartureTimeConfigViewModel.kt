package com.lado.travago.tripbook.ui.agency.config_panel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class TripsDepartureTimeConfigViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //We start with a loading screen before stopping when results are available or failed to be gotten
    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    //24H or 12H Format: initially at 24H
    private val _timeFormat = MutableLiveData(TimeModel.TimeFormat.FORMAT_24H)
    val timeFormat: LiveData<TimeModel.TimeFormat> get() = _timeFormat
    private val _spanSize = MutableLiveData(3)
    val spanSize: LiveData<Int> get() = _spanSize

    //Addition Data
    var fromHour: Int? = null
        private set
    var toHour: Int? = null
        private set
    var departureHour: Int? = null
        private set
    var fromMinutes: Int? = null
        private set
    var toMinutes: Int? = null
        private set
    var departureMinutes: Int? = null
        private set

    var intervalName = ""
        private set

    enum class FieldTags {
        FROM_HOUR, FROM_MINUTES, TO_HOUR, TO_MINUTES, DEPARTURE_HOUR, DEPARTURE_MINUTES, INTERVAL_NAME, TOAST_MESSAGE, ON_LOADING, TIME_FORMAT, SPAN_SIZE
    }

    fun setField(fieldTags: FieldTags, value: Any) =
        when (fieldTags) {
            FieldTags.FROM_HOUR -> fromHour = value as Int
            FieldTags.FROM_MINUTES -> fromMinutes = value as Int
            FieldTags.TO_HOUR -> toHour = value as Int
            FieldTags.TO_MINUTES -> toMinutes = value as Int
            FieldTags.DEPARTURE_HOUR -> departureHour = value as Int
            FieldTags.DEPARTURE_MINUTES -> departureMinutes = value as Int
            FieldTags.INTERVAL_NAME -> intervalName = value.toString()
            FieldTags.TOAST_MESSAGE -> _toastMessage.value = value.toString()
            FieldTags.ON_LOADING -> _onLoading.value = value as Boolean
            FieldTags.TIME_FORMAT -> _timeFormat.value = value as TimeModel.TimeFormat
            FieldTags.SPAN_SIZE -> _spanSize.value = value as Int
        }

    fun addIntervalDoc(agencyID: String) {
        val intervalMap = hashMapOf<String, Any?>(
            "intervalName" to intervalName,
            "agencyID" to agencyID,
            "fromHour" to fromHour,
            "fromMinutes" to fromMinutes,
            "toHour" to toHour,
            "toMinutes" to toMinutes,
            "departureHour" to departureHour,
            "departureMinutes" to departureMinutes
        )
        firestoreRepo.addDocument(intervalMap, "OnlineTransportAgency/$agencyID/Departure_Intervals")
            .collect {
                when (it) {
                    is State.Failed -> {
                        _toastMessage.value = it.exception.handleError { }
                        _onLoading.value = false
                    }
                    is State.Loading -> _onLoading.value = true
                    is State.Success -> {
                        _onLoading.value = true
                        _toastMessage.value = "Successfully created: $intervalName"
                        //We re-init all variables
                        clearData()
                    }
                }
            }
    }

    suspend fun deleteIntervalDoc(agencyID: String, intervalID: String) {
        firestoreRepo.deleteDocument(
            "OnlineTransportAgency/$agencyID/Time_Intervals/$intervalID"
        ).collect {
            when (it) {
                is State.Failed -> {
                    _toastMessage.value = it.exception.handleError { }
                    _onLoading.value = false
                }
                is State.Loading -> _onLoading.value = true
                is State.Success -> {
                    _onLoading.value = true
                    _toastMessage.value = "Successfully Deleted!"
                }
            }
        }
    }

    private fun clearData() {
        intervalName = ""
        fromHour = null
        fromMinutes = null
        toHour = null
        toMinutes = null
        departureHour = null
        departureMinutes = null
    }
}
