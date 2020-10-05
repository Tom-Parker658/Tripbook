package com.lado.travago.transpido.ui.administrator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.lado.travago.transpido.R
import com.lado.travago.transpido.model.admin.Destination
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.repo.places.PlacesRepo
import com.lado.travago.transpido.utils.AdminUtils
import kotlinx.android.synthetic.main.activity_admin_function.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class AdminFunctionActivity : AppCompatActivity() {
    private lateinit var placeRepo: PlacesRepo
    private val firestoreRepo = FirestoreRepo()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_function)
        button.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                addPlacesToDB()
            }
        }
        //Initialises the Places API and set a client for us to use if not already initialized
        placeRepo = if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key))
            PlacesRepo(Places.createClient(this))
        } else PlacesRepo(Places.createClient(this))

    }

    private suspend fun addPlacesToDB() {
        val placesAndRegionList = AdminUtils.findPlaceRegion()
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )

        for (placeAndRegion in placesAndRegionList) {
            val (placeName, region) = placeAndRegion.split("+")
            predictPlace(placeName, placeFields, region)
        }
    }

    private suspend fun predictPlace(
        placeName: String,
        placeFields: List<Place.Field>,
        region: String
    ) {
        placeRepo.findPlace(placeName).collect { predictionState ->
            when (predictionState) {
                is State.Failed -> Log.e("PLACE", predictionState.message)
                is State.Success -> {
                    fetchPlace(predictionState, placeFields, region)
                }
            }
        }
    }

    private suspend fun fetchPlace(
        predictionState: State.Success<MutableList<AutocompletePrediction>>,
        placeFields: List<Place.Field>,
        region: String
    ) {
        placeRepo.fetchPlace(
            predictionState.data.first().placeId,
            placeFields
        ).collect { fetchState ->
            when (fetchState) {
                is State.Failed -> Log.e("PLACE Build", fetchState.message)
                is State.Success -> {
                    val destinationMap = Destination(
                        fetchState.data,
                        AdminUtils.parseRegionFromString(region)
                    ).placeMap
                    firestoreRepo.setDocument(
                        destinationMap,
                        "Destinations/${destinationMap["id"]}"
                    ).collect {
                        when (it) {
                            is State.Failed -> Log.e("PLACE Build", it.message)
                            is State.Success -> Log.i(
                                "AdminFunction",
                                "Added ${destinationMap["name"]}"
                            )
                        }
                    }
                }
            }
        }
    }
}