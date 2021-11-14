package com.lado.travago.tripbook.model.booking

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.Document
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.crypto.EncryptedPrivateKeyInfo
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.random.nextULong

/**
 * @author Tom Parkert
 * A ticket is created by a [Booker] and is mainly made up of a booked journey and the Booker
 * This state is determined my the travelTime and travelDay encoded in the journeyID. If it ticked-QR is not
 * scanned by tranSpeed admin before the takeoff time, it becomes expired and can't be used anymore
 * @property journeyID This is the id gotten from booking a ticket which has the booker's id and travel time
 * encoded in it.
 * @property isExpired as seen below manages the validity of the ticket
 * @property qrCode is scan-able qr which can be scanned by the [Scanner] and if valid, before the
 * booker can enter the bus. It contains all info about the journey and booker
 * @property isScanned Checks whether the booker made the journey or not(When the qr is
 * scanned by the [Scanner], this is made true)
 */
@ExperimentalCoroutinesApi
data class Book(
    val tripDoc: DocumentSnapshot,
    val bookerDoc: DocumentSnapshot,
    val agencyDoc: DocumentSnapshot,
    val localityName: String,
    val isVip: Boolean,
    val travelDay: Long,
    val departureTime: TimeModel,
    val db: FirebaseFirestore
) {
    private var destinationID = ""
    private var destinationName = ""
//    var qrSeed = "" +
//            "${journey.location.id}+${journey.destination.id}+$travellerUid+${Timestamp(Date())}"

    //    private val qrCode by lazy {
//        Utils.ticketQRCodeGenerator(this)
//    }
    val townNames = tripDoc["townNames"] as Map<String, String>
    private val townsIDList = tripDoc["townIDs"] as List<String>

    private val localityID by lazy {
        if (townNames["town1"] == localityName) {
            destinationName = townNames["town2"].toString()
            destinationID = townsIDList.last()
            return@lazy townsIDList.first()
        } else {
            destinationName = townNames["town1"].toString()
            destinationID = townsIDList.first()
            return@lazy townsIDList.last()
        }
    }
    val price = if (isVip) tripDoc.getLong("vipPrice")!! else tripDoc.getLong("normalPrice")!!

    /*
        *Failed is the QR-Code
        *
    */
    @ExperimentalCoroutinesApi
    val failed =
        "${
            db.collection("Bookings").document().id
        }${
            Random.nextULong()
        }${
            Timestamp.now().seconds
        }${
            db.collection("Booki").document().id
        }"

    val bookMap = hashMapOf(
        "bookerID" to bookerDoc.id,
        "bookerName" to bookerDoc.getString("name"),
        "agencyName" to agencyDoc.getString("agencyName"),
        "tripID" to tripDoc.id,
        "localityID" to localityID,
        "destinationID" to destinationID,
        "localityName" to localityName,
        "destinationName" to destinationName,
        "isVip" to isVip,
        "price" to price,
        "isExpired" to false,
        "isScanned" to false,
        "failed" to failed,
        "distance" to tripDoc["distance"],
        "taken" to false,
        "travelDateMillis" to travelDay,
        "departureTime" to departureTime.timeInSeconds,
        "generatedOn" to Timestamp.now()
    )
}

//TODO Info screen