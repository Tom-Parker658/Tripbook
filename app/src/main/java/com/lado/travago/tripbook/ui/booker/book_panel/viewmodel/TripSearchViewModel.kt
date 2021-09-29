package com.lado.travago.tripbook.ui.booker.book_panel.viewmodel

import android.content.Intent
import android.telephony.CarrierConfigManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

/**
 * This is the viewModel to manage the search screen of the app -> [TripSearchViewModel]
 *
 * @property placesClient is passed as argument by the fragment to the [TripSearchViewModel] for
 * building [TripSearchViewModel]. The
 * viewModel need this [placesClient] to deal with place autocompletion & user's current location
 * which can be fed to our layout autoCompleteTextViews to display
 *
 */
@ExperimentalCoroutinesApi
class TripSearchViewModel : ViewModel() {
    val firestoreRepo = FirestoreRepo()
    var townNames = mutableListOf<String>()
        private set

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> get() = _toastMessage

    //We set the date to today
    var locality = ""
        private set
    var destination = ""
        private set
    var tripTime : TimeModel? =null
        private set
    var tripDateInMillis = Date().time
        private set

    /**
     * Contains different identifiers for the fields in our searching form
     */
    enum class FieldTags {
        LOCALITY, DESTINATION, TOWNS_NAMES, TOAST_MESSAGE, TRIP_DATE, TRIP_TIME
    }

    /**
     * A function to set the value the fields from the searching form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setField(fieldTag: FieldTags, value: Any) = when (fieldTag) {
        FieldTags.LOCALITY -> locality = value.toString()
        FieldTags.DESTINATION -> destination = value.toString()
        FieldTags.TOWNS_NAMES -> townNames = value as MutableList<String>
        FieldTags.TOAST_MESSAGE -> _toastMessage.value = value as String
        FieldTags.TRIP_DATE -> tripDateInMillis = value as Long
        FieldTags.TRIP_TIME -> tripTime = value as TimeModel
    }



}