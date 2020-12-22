package com.lado.travago.transpido.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lado.travago.transpido.model.enums.DataResources
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.repo.places.PlacesRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AdminFunctionViewModel : ViewModel() {
    private val db = FirestoreRepo()
    private val placeRepo = PlacesRepo(null)

    /**
     * Administrator utility
     * Introduces journeys to the TranSpeed database
     * It run through all combination of journeys found in [DataResources.journeyDistanceList] and uploads each to the database
     */
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

                    /**
                     * Go through all the possible journey combination list and for each combination, uploads it to the database
                     * based on their corresponding regions! e,g Dschang-Yaounde will be stored under West-Centre collection
                     */
                    DataResources.journeyDistanceList.
                    trimIndent().reader().buffered().forEachLine { journeyName ->
                        val names:List<String> = journeyName.split("+")
                        Log.i("Transfer Response", names.size.toString())
                        val location1 = names.first()
                        val destination1 = names.last().trim().reversed().subSequence(4 until names.last().trim().length ).reversed().trim().toString()

                        Log.i("Transfer Response", location1)
                        Log.i("Transfer Response", destination1)

                        queryState.data.documents.forEach { location ->//1
                            when(location["name"].toString().toLowerCase(Locale.getDefault()).trim().replace(" ", "-")) {
                                location1.toLowerCase(Locale.getDefault()).trim() -> {
                                    queryState.data.documents.forEach { destination ->//1
                                        when(destination["name"].toString().toLowerCase(Locale.getDefault()).trim().replace(" ", "-")) {
                                            destination1.toLowerCase(Locale.getDefault()).trim() -> {
                                                //Uploading
                                                Log.i("Transfer Response", "UploadingL: ${location["name"]} - ${destination["name"]}")
                                                CoroutineScope(Dispatchers.IO) .launch{
                                                    placeRepo.journeyUploader(location, destination)
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

}