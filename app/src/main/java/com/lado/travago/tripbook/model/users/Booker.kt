package com.lado.travago.tripbook.model.users

import com.google.firebase.Timestamp
import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.enums.SEX
import java.util.*

/**
 * This a [User] which books journeys
 *
 * @author Tom Parkert
 * [Booker] is a model which represents a person who wants to book a ticket.
 *
 * @property name is the name of a booker
 * @property sex is the sex of the booker which can be [SEX.FEMALE], [SEX.MALE] and [SEX.UNKNOWN]
 * @property birthdayInMillis is the place of birth of the booker
 * @property photoUrl is the url link to the face picture of the booker
 * @property occupation is the user's occupation which can be any value among those of the
 *  enum class [OCCUPATION] it is initialise as [OCCUPATION.UNKNOWN]
 * @property phoneNumber is the user's SIM phoneNumber
 */

data class Booker(
    override val uid: String,
    override var name: String,
    override var sex: SEX = SEX.UNKNOWN,
    override var birthdayInMillis: Long,
    override var photoUrl: String,
    override var birthPlace: String,
    override var occupation: OCCUPATION = OCCUPATION.UNKNOWN,
    override var phoneNumber: String,

): User {
    /**
     * An inner class which contains some basic information about the Booker.
     * It inherits from the [User.UserBasicInfo]
     * This class is used to display bookers info on a recyclerView
     */
    data class BookerBasicInfo(
        override val name: String ,
        override val birthdayInMillis: Long,
        override val phoneNumber: String,
        override val photoUrl: String,
        override val occupation: OCCUPATION
    ): User.UserBasicInfo(
        name,
        birthdayInMillis,
        phoneNumber,
        photoUrl,
        occupation
    )

    override val userMap: HashMap<String, Any?> = hashMapOf(
        "uid" to uid,
        "name" to name,
        "sex" to sex,
        "phone" to phoneNumber,
        "birthday" to birthdayInMillis,
        "birthplace" to birthPlace,
        "nationality" to "Cameroon",
        "occupation" to occupation.name,
        "photoUrl" to photoUrl,
        "addedOn" to Timestamp(Date())
    )
}