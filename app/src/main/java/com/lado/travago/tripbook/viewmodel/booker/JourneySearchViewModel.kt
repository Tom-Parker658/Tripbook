package com.lado.travago.tripbook.viewmodel.booker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.model.enums.TravelTime
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.places.PlacesRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

/**
 * This is the viewModel to manage the search screen of the app -> [JourneySearchViewModel]
 *
 * @property placesClient is passed as argument by the fragment to the [JourneySearchViewModel] for
 * building [JourneySearchViewModel]. The
 * viewModel need this [placesClient] to deal with place autocompletion & user's current location
 * which can be fed to our layout autoCompleteTextViews to display
 *
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi

class JourneySearchViewModel : ViewModel() {

    //LiveData to check if the search results are ready and contains thr agencies info
    private val _resultsReady = MutableLiveData(mutableListOf<Journey.JourneySearchResultInfo>())
    val resultsList: LiveData<MutableList<Journey.JourneySearchResultInfo>> get() = _resultsReady

    private var _loading = MutableLiveData(false)
    val loading get() = _loading
    //Data variables
    var location = ""
        private set
    var destination= ""
        private set
    //We set the date to today
    var dateInMillis = Calendar.getInstance().timeInMillis
        private set
    var travelTime = TravelTime.UNKNOWN
        private set
    var vip = false
        private set
//    //All the agencies which were found able to offer the journey
//    var agencies = mutableListOf<DocumentSnapshot>()
//        private set
    //The journey which the user is looking for
    lateinit var journey: DocumentSnapshot
        private set
    //We want our coroutine t work background in the uiScope as it will need to update the UI. So we use Dispatcher.Main
    private val uiScope = CoroutineScope(Dispatchers.Main)


    /**
     * Searches Journey and all the agencies which can offer the journey
     */
    suspend fun searchMyJourney(){
        val placeRepo = PlacesRepo()

        placeRepo.searchJourney(location, destination).collect{ pairState ->

            when(pairState){
                is State.Loading ->{
                    Log.i("Journey Search", "Loading ...")
                    _loading.value = true
                }
                is State.Success -> {
                    journey = pairState.data.first
                    Log.i("Journey Search", "Got It: $journey")
                    Log.i("Journey Search", "Got It 2:  ${pairState.data.second.size}")
                    pairState.data.second.forEach {
                        _resultsReady.value?.add(
                            Journey.JourneySearchResultInfo(
                                journey["name"].toString(),
                                it["name"].toString(),
                                it["pricePerKm"].toString().toDouble(),
                                it["agencyLogo"].toString(),
                                dateInMillis,
                                it["reputation"].toString().toInt(),
                                journey["distance"].toString().toInt()
                            )
                        )?:Log.i("Journey Search", "Got It: Livedata null")
                    }
                    _loading.value = false
                }
                is State.Failed -> {
                    Log.e("Journey Search", pairState.message)
                    _loading.value = false
                }
            }
        }
    }

    /**
     * Contains different identifiers for the fields in our searching form
     */
    enum class FieldTags {
        LOCATION, DESTINATION, VIP, TRAVEL_TIME, TRAVEL_DAY
    }

    /**
     * A function to set the value the fields from the searching form
     * @param fieldTag is the identifier used to specify the field you wish to set or change
     * @param value is the new value you wish to assign to the field
     */
    fun setFields(fieldTag: FieldTags, value: Any) = when(fieldTag){
        FieldTags.LOCATION -> location = value.toString()
        FieldTags.DESTINATION -> destination = value.toString()
        FieldTags.VIP -> vip = value as Boolean
        FieldTags.TRAVEL_DAY -> dateInMillis= value as Long
        FieldTags.TRAVEL_TIME -> travelTime = value as TravelTime
    }
    companion object

}