package com.lado.travago.tripbook.model.admin

import com.google.firebase.Timestamp
import com.lado.travago.tripbook.utils.AdminUtils
import java.util.*

data class Journey(
    val location: Destination,
    val destination: Destination
) {
    private val distanceTimePair by lazy {
        AdminUtils.distanceEvaluator(this)
    }
    private val distance by lazy { distanceTimePair.first }
    private val estimatedTimeTaken by lazy { distanceTimePair.second }

    //Data for firestore
    val journeyMap = hashMapOf(
        "fromID" to location.placeMap["id"],
        "toID" to destination.placeMap["id"],
        "name" to "${location.name}-${destination.name}",
        "distance" to distance,
        "timeTaken" to estimatedTimeTaken,
        "addedOn" to Timestamp(Date())
    )

    data class JourneySearchResultInfo(
        val journeyName: String,
        val agencyName: String,
        val agencyCostPerKm: Double,
        val agencyLogoUrl: String,
        val travelDateInMillis: Long,
        val agencyReputation: Int,
        val journeyDistance: Int
    ){
        val journeyCost = agencyCostPerKm * journeyDistance
        val journeyCostFormatted = "$journeyCost FCFA"
    }
}