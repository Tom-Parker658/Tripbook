package com.lado.travago.tripbook.model.admin

import com.google.android.libraries.places.api.model.Place

/**
 * @author Tom Parkert
 * @constructor The constructor of this can only be invoked by an [OnlineTravelAgency] and cannot be called directly
 * So an [OnlineTravelAgencyPark] must be attributed to an OnlineTransportAgency
 */
data class OnlineTravelAgencyPark(
    val location: Place? = null,
    val OTA: OnlineTravelAgency,
    var destinations: List<Place> = listOf(),
) {
    val name: String = OTA.agencyName
}

