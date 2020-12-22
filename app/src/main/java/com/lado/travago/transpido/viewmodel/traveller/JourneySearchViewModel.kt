package com.lado.travago.transpido.viewmodel.traveller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.libraries.places.api.net.PlacesClient
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.places.PlacesRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

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

class JourneySearchViewModel(application: Application, private val placesClient: PlacesClient) :
    AndroidViewModel(application) {
    //Our job for all coroutine processes
    private var viewModelJob = Job()

    //We want our coroutine t work background in the uiScope as it will need to update the UI. So we use Dispatcher.Main
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        //Cancelling all coroutine process when the viewModel is destroyed
        viewModelJob.cancel()
    }


    /**
     * Searches Journey
     */
    suspend fun searchMyJourney(location: String, destination: String){
        val placeRepo = PlacesRepo(null)

        placeRepo.searchJourney(location, destination).collect {
            when(it){
                is State.Loading -> Log.i("Journey Search", "Loading ...")
                is State.Success -> Log.i("Journey Search", it.data.data["name"].toString())
                is State.Failed -> Log.e("Journey Search", it.message)
            }
        }
    }
}