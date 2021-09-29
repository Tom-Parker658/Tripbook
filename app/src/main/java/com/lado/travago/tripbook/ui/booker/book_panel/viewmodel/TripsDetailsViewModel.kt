package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripsDetailsViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()

    private val _onLoading = MutableLiveData(true)
    val onLoading: LiveData<Boolean> get() = _onLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //Data
    var agencyID = ""
        private set
    var tripID = ""
        private set
    var destinationTownName = ""
        private set
    var numberOfBooks = 1
        private set
    var isVip = false //Very dummy value
        private set
    lateinit var tripTime: TimeModel
        private set
    var tripDateInMillis = 0L
        private set

    lateinit var agencyDoc: DocumentSnapshot
        private set
    lateinit var tripDoc: DocumentSnapshot
        private set


    enum class FieldTags {
        ON_LOADING, TOAST_MESSAGE, AGENCY_DOC, TRIP_DOC, DESTINATION_NAME, NUMBER_OF_BOOKS, ARG_AGENCY_ID, ARG_TRIP_ID, ARG_IS_VIP, ARG_TRIP_TIME, ARG_TRIP_DATE
    }

    fun setField(fieldTag: FieldTags, value: Any) =
        when (fieldTag) {
            FieldTags.ON_LOADING -> _toastMessage.value = value.toString()
            FieldTags.TOAST_MESSAGE -> _onLoading.value = value as Boolean
            FieldTags.AGENCY_DOC -> agencyDoc = value as DocumentSnapshot
            FieldTags.TRIP_DOC -> tripDoc = value as DocumentSnapshot
            FieldTags.NUMBER_OF_BOOKS -> numberOfBooks = value as Int
            FieldTags.ARG_AGENCY_ID -> agencyID = value.toString()
            FieldTags.ARG_TRIP_ID -> tripID = value.toString()
            FieldTags.ARG_IS_VIP -> isVip = value as Boolean
            FieldTags.DESTINATION_NAME -> destinationTownName = value.toString()
            FieldTags.ARG_TRIP_TIME -> (value as Pair<Int, Int>).let {
                tripTime = TimeModel.from24Format(it.first, it.second)
            }
            FieldTags.ARG_TRIP_DATE -> tripDateInMillis = value as Long
        }

}
