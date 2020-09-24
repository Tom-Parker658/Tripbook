package com.lado.travago.transpido.model.admin


import com.google.android.libraries.places.api.model.Place

/**
 * This is a model of a travelling destination with the following properties:
 * @property place represent a Place object or any town
 * @property destinationName is the name of the Place
 *
 */
data class Destination(
    var place: Place,
) {
    var destinationName: String? = place.name
}
