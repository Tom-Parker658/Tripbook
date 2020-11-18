package com.lado.travago.transpido.repo.places

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.lado.travago.transpido.model.admin.Destination
import com.lado.travago.transpido.model.admin.Journey
import com.lado.travago.transpido.model.enums.DataResources
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.utils.AdminUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/**
 * Performs all queries like places autocomplete and Locate-Me functions.
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class PlacesRepo(private val placesClient: PlacesClient?) {
    private val db = FirestoreRepo()

    /**
     * Used to find a [Place] from its name
     * @return a list of places
     */
    fun findPlace(placeName: String) = flow {
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
            .setQuery(placeName)// Sets the user input query to the request
            .build()
        val response = placesClient?.findAutocompletePredictions(request)?.await()
        //Autocomplete succeeded
        emit(State.success(response?.autocompletePredictions))
    }.catch {
        //Autocomplete failed
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.Main)

    fun fetchPlace(placeId: String, fields: List<Place.Field>) = flow {
        emit(State.loading())
        val request = FetchPlaceRequest.builder(placeId, fields).build()
        val place = placesClient?.fetchPlace(request)?.await()
        emit(State.success(place?.place))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.Main)

    /**
     * Administrator utility
     * Introduces journeys to the TranSpeed database
     * It run through all combination of journeys found in [DataResources.journeyDistanceList] and uploads each to the database
     */
    fun addJourneys() = flow {
        emit(State.loading())
        val allPlaces = mutableListOf<DocumentSnapshot>()
        //Gets all the destinations places found the database
        allPlaces.run {
            db.getAllDocuments("Destination").collect { queryState ->
                when (queryState) {
                    is State.Success -> {
                        this += queryState.data.documents
                    }
                    is State.Failed -> {
                        this += listOf()
                    }
                }
            }
        }
        /**
         * Go through all the possible journey combination list and for each combination, uploads it to the database
         * based on their corresponding regions! e,g Dschang-Yaounde will be stored under West-Centre collection
         */
        for (journeyName in DataResources.journeyDistanceList.split("\n")) {
            val (location1, destination1) = journeyName.split(" ")
            val location = allPlaces.find { doc ->
                doc["name"] == location1
            }!!
            val destination = allPlaces.find { doc ->
                doc["name"] == destination1
            }!!

            val locationPlace = Destination(
                AdminUtils.parseRegionFromString(location["region"].toString()),
                location.id,
                location["name"].toString(),
                latLng = GeoPoint(
                    location["latitude"].toString().toDouble(),
                    location["longitude"].toString().toDouble(),
                )
            )
            val destinationPlace = Destination(
                AdminUtils.parseRegionFromString(destination["region"].toString()),
                destination.id,
                destination["name"].toString(),
                latLng = GeoPoint(
                    destination["latitude"].toString().toDouble(),
                    destination["longitude"].toString().toDouble(),
                )
            )
            //The journey to be uploaded
            val journey = Journey(
                locationPlace,
                destinationPlace
            )

            //Adds the journey to firestore database under the path Journey/locationRegion-finalRegion/locationIDdestinationID
            val firestoreRepo = FirestoreRepo()
            val journeyPath = "Journey/Cameroon/${locationPlace.region}-${destinationPlace.region}/${locationPlace.id}${destinationPlace.id}"
            firestoreRepo.setDocument(
                journey.journeyMap,
                journeyPath
            )
            /*//A  pair which contains the location region and destination region
            val regionToRegion = locationPlace.region to destinationPlace.region

            //Checks for the pair of regions to which the journey belongs to
            for (pairOfRegions in DataResources.regionPairs) {
                val requiredRegions =
                    AdminUtils.parseRegionFromString(pairOfRegions.first) to AdminUtils.parseRegionFromString(
                        pairOfRegions.second
                    )
                if (requiredRegions == regionToRegion) {

                }
            }*/
        }

        emit(State.success("Done"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}