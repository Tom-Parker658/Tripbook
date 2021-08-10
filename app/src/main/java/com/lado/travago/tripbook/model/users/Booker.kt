package com.lado.travago.tripbook.model.users

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.lado.travago.tripbook.model.enums.SEX
import java.util.*

/**
 * This a [User] which books journeys
 *
 * @author Tom Parkert
 * [Booker] is a model which represents a person who wants to book a ticket.
 *
 * @property name is the name of a booker
 * @property sex is the sex of the booker which can be [SEX.FEMALE], [SEX.MALE]
 * @property birthdayInMillis is the place of birth of the booker
 * @property photoUrl is the url link to the face picture of the booker
 * @property occupation is the user's occupation which can be any value among those of the
 * @property nationality is the country from which the user comes from
 * @property recoveryPhoneNumber is any SIM phoneNumber which can be used to recover a lost account. It must be different from the
 */

data class Booker(
    val name: String,
    var sex: SEX,
    val birthdayInMillis: Long,
    val photoUrl: String,
    val nationality: String,
    var occupation: String,
    var recoveryPhoneNumber: String,
    ){
    @ServerTimestamp
    var addedOn: Timestamp = Timestamp.now()

    val bookerMap: HashMap<String, Any?> = hashMapOf(
        "name" to name,
        "sex" to sex,
        "recoveryPhoneNumber" to recoveryPhoneNumber,
        "birthday" to birthdayInMillis,
        "nationality" to nationality,
        "occupation" to occupation,
        "photoUrl" to photoUrl,
        "addedOn" to addedOn
    )
}