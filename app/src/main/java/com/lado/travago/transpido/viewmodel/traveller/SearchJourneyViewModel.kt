package com.lado.travago.transpido.viewmodel.traveller


import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.*

/**
 * This is the viewModel to manage the search screen of the app -> [SearchJourneyViewModel]
 *
 * @property placesClient is passed as argument by the fragment to the [SearchJourneyViewModel] for
 * building [SearchJourneyViewModel]. The
 * viewModel need this [placesClient] to deal with place autocompletion & user's current location
 * which can be fed to our layout autoCompleteTextViews to display
 *
 */
class SearchJourneyViewModel(application: Application, private val placesClient: PlacesClient) :
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
     * Performs the Place autocomplete as the user enters his location and destinations
     * @param query is the user input from the editText
     * @return a list of likelihood(like correct places) prediction about the place being typed
     */
    fun autoComplete(query: String): List<String> {
        //This is a list which contains predictions about the users location or destination searches
        //It is returned. We use run here to avoid variable capture
        return mutableListOf<String>().run {
            //Does all requesting off main thread
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    this@run.requestAutocomplete(query)
                }
            }
            this//Returns the list
        }
    }

    /**
     * Helper method to do the request autocompletion
     */
    private fun MutableList<String>.requestAutocomplete(query: String) {
        //A session token for each autocomplete session. Helps in billing and is required
        val token = AutocompleteSessionToken.newInstance()

        //Restrictions for our autocomplete for Cameroon only using the Cameroon ISO alpha code(ISO 3166-2:CM)
        val cameroonCode = "CM"

        //A request which uses the user's query to request possible autocomplete locations
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountry(cameroonCode)// Restrict the results to Cameroon only
            .setTypeFilter(TypeFilter.CITIES)// Restrict the result to be only made up of cities
            .setSessionToken(token)// Set the session token for each autocomplete session
            .setQuery(query)// Sets the user input query to the request
            .build()

        //The response from the passed in request
        placesClient.findAutocompletePredictions(request)
            //In case autocomplete options were found for the request
            .addOnSuccessListener { response ->
                //Iterates over the predictions and fills the autocompleteList with each prediction
                //name which are cities e.g Yaounde

                response.autocompletePredictions.map { prediction ->
                    this += prediction.getFullText(null).toString()
                    Log.i("SearchJourneyViewModel", "${prediction.getFullText(null)}")
                }
            }
            //In case no options were found for the request
            .addOnFailureListener { exception ->
                //Logs an error with the error message
                Log.e("SearchJourneyViewModel", "$exception ${exception.message}")
            }
    }


    /**
     * Performs the process to locating(city) the traveller geographical location
     * @param requestLocation is a function which request user's location permission if not granted
     * @return a list of likelihood places which the user's device is located
     */
    fun locateTraveller(requestLocation: () -> Unit): List<String> {
        //This is a list which contains predictions about the device present location and will be returned
        return mutableListOf<String>().run {
            //Does all the requesting off main thread
            uiScope.launch {
                //Runs all the request on IO thread
                withContext(Dispatchers.IO) {
                    this@run.requestLocateTraveller(requestLocation)
                }
            }
            this
        }
    }

    /**
     * Helper method to do the request Location
     */
    private fun MutableList<String>.requestLocateTraveller(requestLocation: () -> Unit) {
        this += "lado Saha"
        this += "lado Sahaf"
        //Check if the permission has been granted to locate the device
        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            //Creates a request to find current place NAME!
            val request =
                FindCurrentPlaceRequest.newInstance((mutableListOf(Place.Field.NAME)))

            // Response for the request
            placesClient.findCurrentPlace(request)
                //In case request succeeds
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //get the response from as the task result
                        val response = task.result
                        //Iterates over the predictions and fills the autocompleteList with each prediction
                        //name which are cities e.g Yaounde
                        response?.placeLikelihoods?.map { likelyLocation ->
                            //Adds the likelihood to the list  before adding it to the list
                            this += likelyLocation.place.name!!
                            Log.i("SearchJourneyViewModel",
                                "User Location: ${likelyLocation.place.name!!}")

                        }
                        //If request fails
                    } else {
                        //Logs an error with the error message
                        val exception = task.exception
                        if (exception is ApiException)
                            Log.e("SearchJourneyViewModel",
                                "Place not found: ${exception.statusCode} ${exception.message}")
                    }
                }


        } else
            requestLocation()
    }
}