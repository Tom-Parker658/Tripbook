package com.lado.travago.tripbook.model.booker

import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.enums.SEX
import com.lado.travago.tripbook.utils.Utils

/**
 * @author Tom Parkert
 * [Booker] is a model which represents a person who wants to book a ticket.
 *
 * @property name is the name of a booker
 * @property age is calculated lazily from the [birthday]
 * @property sex is the sex of the booker which can be [SEX.FEMALE], [SEX.MALE] and [SEX.UNKNOWN]
 * @property birthday is the place of birth of the booker
 * @property picture is the url link to the face picture of the booker
 * @property occupation is the user's occupation which can be any value among those of the
 *  enum class [OCCUPATION] it is initialise as [OCCUPATION.UNKNOWN]
 * @property phoneNumber is the user's SIM phoneNumber
 *
 * @property travellerDescription is a string which contains all info about a booker which has the
 * following template "{label:value,label:value}" e.g "{travellerName:lado,travellerSex:M}"
 */

data class Booker(
    var name: String,
    var sex: SEX = SEX.UNKNOWN,
    var birthdayInMillis: Long,
    var pictureUrl: String,
    var occupation: OCCUPATION = OCCUPATION.UNKNOWN,
    var birthplace: String,
    var phoneNumber: String,
    var email: String? = null,
) {
    val age by lazy { Utils.getAge(birthdayInMillis) }
//    val travellerDescription
//        get() = "{travellerName:${name}," +
//                "travellerSex:${name}," +
//                "travellerAge:${age}," +
//                "travellerOccupation:${occupation}," +
//                "travellerPhoneNumber:${phoneNumber}," +
//                "travellerEmail:${email}}"
}