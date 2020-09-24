package com.lado.travago.transpido.model.admin

/**
 * @author Tom Parkert
 * @constructor The constructor of this can only be invoked by an [OnlineTravelAgency] and cannot be called directly
 * So an [OnlineTravelAgencyPark] must be attributed to an OnlineTransportAgency
 */
data class OnlineTravelAgencyPark(
    val location: Destination? = null,
    val OTA: OnlineTravelAgency,
    var destinations: List<Destination> = listOf(),
) {
    val name: String = "${OTA.agencyName} ${location?.destinationName}"
}

