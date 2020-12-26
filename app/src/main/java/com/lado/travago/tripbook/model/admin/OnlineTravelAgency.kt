package com.lado.travago.tripbook.model.admin

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


/**
 * @author Tom Parkert
 *  An OnlineTransportAgency(Online Travel Agency). This is an OnlineTransportAgency which represents an agency. They have OTAP located
 *  in all their destinations
 *  They have the ability to create subAgencies([OnlineTravelAgencyPark]) and they are created by any person who wishes
 *  to have people book a journey online from their agencies.
 *
 *  @property agencyName is the name of the agency which will be displayed on to users. It will also
 *  constitute the name of every OTAP created by this OnlineTransportAgency.
 *  @pricePerKm is the price per km covered which will determine the prices of journeys
 *  @property agencyLogo is the logo that represents that agency and will be shared by all OTAPs created
 *  @property motto is the motto of this OnlineTransportAgency
 *  @property numberOfBuses is the number of buses this company owns in all its locations.
 *  These are represented by [Bus] objects with size.
 *  can transport to
 *  the OnlineTransportAgency settings
 *  @see Scanner class for more information
 *
 *  @property bankAccountNumber is a bank account number which the travellers can send money to during payment
 *  @property mtnMoMoAccount is the MTN Mobile Money numbers which travellers can send money to during payment
 *  @property orangeMoneyAccount is the Orange Mobile Money numbers which travellers can send money to during payment
 *  @property reputation is the average ratio of likes to dislike received by the OnlineTransportAgency by travellers reviews. It is
 *  a double /10 e.g 5.6/10
 *  @property likes represent the number of likes(good reviews by travellers)
 *  @property dislikes represent the number of dislikes(bad reviews by travellers)
 *
 */


data class OnlineTravelAgency(
    val agencyName: String,
    val agencyLogo: String = "",
    val motto: String,
    val costPerKm: Double = 10.0,
    val numberOfBuses: Int,
    val bankAccountNumber: String,
    val mtnMoMoAccount: String,
    val orangeMoneyAccount: String,
    val supportEmail: String = "",
    val supportContact: String = "",
) {
//    @DocumentId
//    var uid: String =""

    @ServerTimestamp
    var addedOn: Timestamp? = null
    var likes: Int = 1
    var dislikes: Int = 1
    val reputation: Double = (likes / (likes + dislikes))*10.0

    val otaMap:HashMap<String, Any?> by lazy {
        hashMapOf(
            "agencyName" to agencyName,
            "agencyLogo" to agencyLogo,
            "pricePerKm" to costPerKm,
            "motto" to motto,
            "numberOfBuses" to numberOfBuses,
            "bankAccountNumber" to bankAccountNumber,
            "mtnMoMoAccount" to mtnMoMoAccount,
            "orangeMoneyAccount" to orangeMoneyAccount,
            "supportEmail" to supportEmail,
            "supportContact" to supportContact,
            "likes" to likes,
            "dislikes" to dislikes,
            "reputation" to reputation,
            "addedOn" to Timestamp(Date())
        )
    }

    companion object{
        fun otaScannerMap(scanner: Scanner): HashMap<String, Any?> =
            hashMapOf(
                "birthdayInMillis" to scanner.birthdayInMillis,
                "sex" to scanner.sex.name,
                "photoUrl" to scanner.profilePhoto
            )
    }

}