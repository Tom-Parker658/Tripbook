package com.lado.travago.tripbook.ui.administrator

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Source
import com.lado.travago.tripbook.model.enums.DataResources
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.HashMap

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AdminFunctionViewModel : ViewModel() {
    private val db = FirestoreRepo()

    /**
     * Administrator utility
     * Introduces journeys to the TranSpeed database
     * It run through all combination of journeys found in [DataResources.journeyDistanceList] and uploads each to the database
     */
    /*@Deprecated("Very Old and inneficient!")
    suspend fun uploadJourney() {
        db.getAllDocuments("Destinations").collect { queryState ->
            when (queryState) {
                is State.Loading -> {
                    Log.i("Transfer Response", "Getting Documents")
                }
                is State.Failed -> {
                    Log.i("Transfer Response", "Failed to get Doc")
                }
                is State.Success -> {
                    Log.i("Transfer Response", "${queryState.data.documents.size}")

                    */
    /**
     * Go through all the possible journey combination list and for each combination, uploads it to the database
     * based on their corresponding regions! e,g Dschang-Yaounde will be stored under West-Centre collection
     *//*
                    DataResources.journeyDistanceList.trimIndent().reader().buffered()
                        .forEachLine { journeyName ->
                            val names: List<String> = journeyName.split("+")
                            Log.i("Transfer Response", names.size.toString())
                            val location1 = names.first()
                            val destination1 = names.last().trim().reversed()
                                .subSequence(4 until names.last().trim().length).reversed().trim()
                                .toString()

                            Log.i("Transfer Response", location1)
                            Log.i("Transfer Response", destination1)

                            queryState.data.documents.forEach { location ->//1
                                when (location["name"].toString().toLowerCase(Locale.getDefault())
                                    .trim().replace(" ", "-")) {
                                    location1.toLowerCase(Locale.getDefault()).trim() -> {
                                        queryState.data.documents.forEach { destination ->//1
                                            when (destination["name"].toString()
                                                .toLowerCase(Locale.getDefault()).trim()
                                                .replace(" ", "-")) {
                                                destination1.toLowerCase(Locale.getDefault())
                                                    .trim() -> {
                                                    //Uploading
                                                    Log.i(
                                                        "Transfer Response",
                                                        "UploadingL: ${location["name"]} - ${destination["name"]}"
                                                    )
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        placeRepo.journeyUploader(
                                                            location,
                                                            destination
                                                        )
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
        }


    }
*/
    private val _loading = MutableLiveData(false)
    val loading get() = _loading

    private val _toastMessage = MutableLiveData("")
    val toastMessage get() = _toastMessage

    private val _text = MutableLiveData("")
    val text get() = _text

    suspend fun addTowns() {
        val townsList = DataResources.regionList.trimIndent().reader().buffered().readLines()
        for ((index, town) in townsList.withIndex()) {
            val percentage = ((index.toDouble()) / townsList.size).toInt() * 100
            _text.value =
                "${town.split("+").first()} |----| $percentage"//The name of the town
            val townMap = hashMapOf<String, Any?>(
                "name" to town.split("+").first(),
                "region" to town.split("+").last(),
            )
            db.addDocument(townMap, "Planets/Earth/Continents/Africa/Cameroon")
                .collect { state ->
                    when (state) {
                        is State.Loading -> _loading.value = true
                        is State.Failed -> {
                            _loading.value = false
                            _toastMessage.value =
                                state.exception.handleError { /**TODO: Handle Error lambda*/ }
                        }
                        is State.Success -> {
                            _toastMessage.value = "${townMap["name"]}"
                        }
                    }
                }
        }
        _loading.value = false
        _text.value = "100%"
    }

    suspend fun addJourneys() {
        val tripsAndDistance = DataResources.journeyDistanceList.trimIndent().reader().buffered()
            .readLines()//listOf("town1 + town2 + distance", "town1 + tow3 + distance", ..)
        val total = tripsAndDistance.size
        for ((count, trip) in tripsAndDistance.withIndex()) {
            val (text1, text2, text3) = trip.split("+")//stores town1, town2 , distance respectively
            val town1 = text1.replace("-", " ").trim() //e.g Abong-Mbang to Abong Mbang
            val town2 = text2.replace("-", " ").trim()
            val distance = text3.trim()

            _text.value =
                "Trip: $town1 - $town2 & $distance: ${
                    count.toDouble().toInt()
                }/${total} \n ${(count.toDouble() / total * 100).toInt()}%"
            //We query to get a town which is same as that town1 or town2
            db.queryCollection("Planets/Earth/Continents/Africa/Cameroon", Source.DEFAULT) {
                it.whereIn("name", listOf(town1, town2))
            }.collect { queryState ->
                when (queryState) {
                    is State.Loading -> _loading.value = true
                    is State.Failed -> {
                        _loading.value = false
                        _toastMessage.value =
                            "For $town1-$town2 ${queryState.exception.handleError { /*TODO: Handle Error lambda*/ }}"
                    }
                    is State.Success -> {
                        val documents = queryState.data
                        val sortedNames = listOf(
                            documents.first().getString("name")!!,
                            documents.last().getString("name")!!
                        ).sorted()
                        //When we find the 2 towns, in each of the towns document, we add a sub-collection {Trips} and store the other town + distance separating them
                        val tripMap = hashMapOf<String, Any?>(
                            //[townNamesList] is for general detection to get all trips which contain a location
                            "townNamesList" to listOf(
                                sortedNames.first(),
                                sortedNames.last()
                            ),//[townNames] is for trip search
                            "townNames" to mapOf(
                                "town1" to sortedNames.first(),
                                "town2" to sortedNames.last()
                            ),
                            "townIDs" to listOf(
                                documents.first().id,
                                documents.last().id
                            ),
                            "distance" to distance.toLong(),
                        )
                        //Now we upload to ~/{town}/Trips/{otherTown}
                        db.addDocument(
                            tripMap,
                            "/Planets/Earth/Continents/Africa/Cameroon/all/Trips"
                        ).collect {
                            when (it) {
                                is State.Loading -> {
                                }
                                is State.Failed -> {
                                    _loading.value = false
                                    _toastMessage.value =
                                        "${documents.first()["name"]} - ${documents.last()["name"]} Failed! ${it.exception.handleError { /**TODO: Handle Error lambda*/ }}"
                                }
                                is State.Success -> {
                                    _toastMessage.value = "Success!"
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}


