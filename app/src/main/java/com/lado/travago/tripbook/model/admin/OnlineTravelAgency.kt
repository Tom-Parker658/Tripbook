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
 *  @property logoUrl is the logo that represents that agency and will be shared by all OTAPs created
 *  @property motto is the motto of this OnlineTransportAgency
 *  @property bankNumber is a bank account number which the travellers can send money to during payment
 *  @property mtnMoneyNumber is the MTN Mobile Money numbers which travellers can send money to during payment
 *  @property orangeMoneyNumber is the Orange Mobile Money numbers which travellers can send money to during payment
 *  @property reputation is the average ratio of likes to dislike received by the OnlineTransportAgency by travellers reviews. It is
 *  a double /10 e.g 5.6/10
 *  @property likes represent the number of likes(good reviews by travellers)
 *  @property dislikes represent the number of dislikes(bad reviews by travellers)
 *  @property modifiedOn tells if we are updating or else crearing the agency
 */


data class OnlineTravelAgency(
    val agencyName: String,
    var logoUrl: String = "",
    val motto: String,
    val nameCEO: String,
    val creationYear: Int,
    val creationDecree: String,
    val bankNumber: Int,
    val mtnMoneyNumber: String,
    val orangeMoneyNumber: String,
    val supportEmail: String,
    val supportPhone1: String,
    val supportPhone2: String,
    val supportCountryCode1: String, //e.g 237
    val supportCountryCode2: String, //e.g 237
    val phone1: String,//e.g 677889955
    val phone2: String,//e.g 677889955
    val isSuspended: Boolean = false,
    val modifiedOn: Timestamp?
) {
    //    @DocumentId
//    var uid: String =""
    var costPerKm: Double = 10.0

    @ServerTimestamp
    var addedOn: Timestamp = Timestamp.now()
    var likes: Int = 1
    var dislikes: Int = 1
    val reputation: Double = (likes / (likes + dislikes)) * 10.0

    val otaMap =
        if (modifiedOn == null) {//In this case, we are creating the agency and we instead set addedOn tag
            hashMapOf<String, Any?>(
                "agencyName" to agencyName,
                "logoUrl" to logoUrl,
                "nameCEO" to nameCEO,
                "creationYear" to creationYear,
                "pricePerKm" to costPerKm,
                "motto" to motto,
                "bankNumber" to bankNumber,
                "mtnMoneyNumber" to mtnMoneyNumber,
                "orangeMoneyNumber" to orangeMoneyNumber,
                "supportEmail" to supportEmail,
                "supportPhone1" to supportPhone1,
                "supportPhone2" to supportPhone2,
                "phoneCode1" to supportCountryCode1,
                "phoneCode2" to supportCountryCode2,
                "phone1" to phone1,
                "phone2" to phone2,
                "creationDecree" to creationDecree,

                "likes" to likes,
                "dislikes" to dislikes,
                "reputation" to reputation,
                "addedOn" to addedOn,
                "modifiedOn" to addedOn,
                "isSuspended" to isSuspended,
            )
        } else {//We are updating agency info instead of creating
            hashMapOf<String, Any?>(
                "agencyName" to agencyName,
                "logoUrl" to logoUrl,
                "pricePerKm" to costPerKm,
                "motto" to motto,
                "bankNumber" to bankNumber,
                "mtnMoneyNumber" to mtnMoneyNumber,
                "orangeMoneyNumber" to orangeMoneyNumber,
                "supportEmail" to supportEmail,
                "supportPhone1" to supportPhone1,
                "supportPhone2" to supportPhone2,
                "creationDecree" to creationDecree,
                "modifiedOn" to modifiedOn,
            )
        }
}

