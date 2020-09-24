package com.lado.travago.transpido.model.traveller

import com.lado.travago.transpido.model.admin.Destination
import com.lado.travago.transpido.utils.AdminUtils
import com.lado.travago.transpido.utils.Utils

/**
 * @author Tom Parkert
 * This the main model of Transpido. It is created by an OnlineTransportAgency for the travellers to book
 * @property location is the location from which the journey starts e.g Dschang
 * @property destination is the place where the journey ends e.g Yaounde
 * @see Destination

 * @property VIP is whether the this journey can be done in vip mode or not. This is left to the traveller
 * to decide. If a journey can be vip, the traveller can still turn off the vip and book that journey.
 * @property price is the cost of the journey as stipulated by the agency
 * @property distance is the distance in km between location and destination. This is calculated automatically
 * @property estimatedTimeTaken represents the estimated time needed to cover that distance. It is also calculated automatically
 *
/*  @property journeyID is generated using the traveller
formula: [JourneyId = "Location+destination+TravelDay+TravelTime+*TravellerID*+price+TIMESTAMP"]*/
 * @see Utils.journeyIDGenerator
 */
data class Journey(
    val location: Destination,
    val destination: Destination,
    var VIP: Boolean,
    var price: Double,
) {
//    private val distanceTimePair by lazy {
//        AdminUtils.distanceEvaluator(location.place.name,
//            destination.place.name)
//    }
//    val distance by lazy { distanceTimePair.first }
//    val estimatedTimeTaken by lazy { distanceTimePair.second }
}