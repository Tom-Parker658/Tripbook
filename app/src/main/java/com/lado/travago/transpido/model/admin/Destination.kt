package com.lado.travago.transpido.model.admin

import com.google.firebase.firestore.GeoPoint
import com.lado.travago.transpido.model.enums.Region
import java.util.*

data class Destination(
    val region: Region,
    val id: String,
    val name: String,
    val country: String,
    val latLng: GeoPoint,
) {
    val placeMap: HashMap<String, Any?> = hashMapOf(
        "id" to id,
        "name" to name,
        "address" to "$country, $region $name",
        "country" to "Cameroon",
        "latitude" to latLng.latitude,
        "longitude" to latLng.longitude,
        "region" to region
    )
}