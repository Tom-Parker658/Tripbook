package com.lado.travago.transpido.model.admin

import com.google.android.libraries.places.api.model.Place
import com.google.firebase.Timestamp
import com.lado.travago.transpido.model.enums.Region
import java.util.*

data class Destination(val place: Place, val region: Region) {
    val placeMap: HashMap<String, Any?> = hashMapOf(
        "id" to place.id,
        "name" to place.name,
        "address" to place.address,
        "country" to "Cameroon",
        "latitude" to place.latLng?.latitude,
        "longitude" to place.latLng?.longitude,
        "region" to region,
        "addedOn" to Timestamp(Date())
    )
}