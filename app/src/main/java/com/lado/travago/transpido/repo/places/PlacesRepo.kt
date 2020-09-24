package com.lado.travago.transpido.repo.places

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.lado.travago.transpido.repo.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Performs all queries like places autocomplete and Locate-Me functions.
 */
@ExperimentalCoroutinesApi
class PlacesRepo(val placesClient: PlacesClient) {

    fun autoComplete(query: String) = flow {
        emit(State.loading())
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
        val response = placesClient.findAutocompletePredictions(request)
        //Autocomplete succeeded
        emit(State.success(response))
    }.catch {
        //Autocomplete failed
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.Main)
}