package com.lado.travago.tripbook.repo.places

import android.util.Log
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.getField
import com.lado.travago.tripbook.model.admin.Destination
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.repo.FirestoreTags
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.AdminUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Performs all queries like places autocomplete and Locate-Me functions.
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class PlacesRepo {
    private val db = FirestoreRepo()

   /**
     * Used to find a [Place] from its name
     * @return a list of places
     */
   /**
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
*/

    /**
     * Upload a journey
     */
    suspend fun journeyUploader(
        location: DocumentSnapshot,
        destination: DocumentSnapshot,
    ) {
        val locationPlace = Destination(
            AdminUtils.parseRegionFromString(location["region"].toString()),
            location.id,
            location["name"].toString(),
            latLng = null
        )
        val destinationPlace = Destination(
            AdminUtils.parseRegionFromString(destination["region"].toString()),
            destination.id,
            destination["name"].toString(),
            latLng = null
        )

        //The journey to be uploaded
        val journey = Journey(
            locationPlace,
            destinationPlace
        )

        //Adds the journey to firestore database under the path Journey/locationRegion-finalRegion/locationIDdestinationID
        val firestoreRepo = FirestoreRepo()
        val journeyPath =
            "Journeys/Cameroon/${locationPlace.region}-${destinationPlace.region}/${locationPlace.id}${destinationPlace.id}"

        firestoreRepo.setDocument(
            journey.journeyMap,
            journeyPath
        ).collect {
            when (it) {
                is State.Loading -> Log.i(
                    "TransferResponse1",
                    "Transferring ${journey.journeyMap["name"]}"
                )
                is State.Failed -> Log.e("TransferResponse1", it.message)
                is State.Success -> {
                    Log.i("TransferResponse1", "Done")
                }
            }
        }
    }

    /**
     * Searches journeys from the database
     * @locationName is the name of the location of the user
     * @destinationName is the name of the destination to where the user is going to
     */
    fun searchJourney(locationName: String, destinationName: String) = flow {
        emit(State.loading())

        db.queryCollection("Destinations") {
            it.whereEqualTo("name", locationName)
        }.collect { locationState ->
            when (locationState) {
                is State.Loading -> Log.i("JourneySearch", "Getting the journey")

                is State.Success -> {
                    val location = locationState.data.documents.first()
                    //Searches for destination id
                    db.queryCollection("Destinations") {
                        it.whereEqualTo("name", destinationName)
                    }.collect { destinationState ->
                        when (destinationState) {
                            is State.Success -> {
                                val destination = destinationState.data.documents.first()

                                //Returns the correct journey
                                val journeyId = "${location.id}${destination.id}"
                                val regionToRegion = "${
                                    location["region"].toString()
                                        .toUpperCase()
                                }-${
                                    destination["region"].toString().toUpperCase()
                                }"
                                // We get the concernerd document for the journey
                                db.getDocument("Journey/Cameroon/$regionToRegion/$journeyId")
                                    .collect { journeyState ->
                                        when (journeyState) {
                                           is State.Success ->{
                                               //We get all OTA from the db
                                               db.getAllDocuments(
                                                   FirestoreTags.OnlineTransportAgency.name
                                               ).collect{
                                                   when(it){
                                                       is State.Success -> {
                                                           // Checks for all document each document which contains the required regions in their snapshot
                                                           val listOfAgencies = mutableListOf<DocumentSnapshot>()
                                                           val fieldPath = FieldPath.of("Cameroon", "Regions", "list")
                                                           for(agency in it.data.documents){
                                                               if(agency.getField<List<String>>(fieldPath)?.containsAll(listOf(destination["region"].toString().toUpperCase(), location["region"].toString().toUpperCase()))!!
                                                               ) {
                                                                   listOfAgencies += agency
                                                               }

                                                           }
                                                           //Then we emit the result as a pair of the journey snapshot and a list of all agencies snapshots

                                                               emit(
                                                                   State.success(
                                                                       Pair(
                                                                           journeyState.data,
                                                                           listOfAgencies.toList()
                                                                       )
                                                                   )
                                                               )
                                                       }
                                                       is State.Failed -> {
                                                            Log.e("Journey Search", "Couldn't find the agenciees")
                                                       }

                                                       else -> {}
                                                   }
                                               }
                                           }
                                            is State.Failed -> Log.e("Journey Search", journeyState.message)
                                        }
                                    }
                            }
                            is State.Loading -> TODO()
                            is State.Failed -> TODO()
                        }
                    }
                }
                is State.Failed -> Log.i("JourneySearch", "Failed gettting journey")
            }
        }

    }

}