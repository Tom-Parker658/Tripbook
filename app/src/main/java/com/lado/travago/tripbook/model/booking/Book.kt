package com.lado.travago.tripbook.model.booking

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lado.travago.tripbook.model.admin.TimeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.random.Random
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
        "tripLocalityID" to localityID,
        "tripDestinationID" to destinationID,
        "tripLocalityName" to localityName,
        "tripDestinationName" to destinationName,
        "isVip" to isVip,
        "price" to price,
        "isExpired" to false,
        "isScanned" to false,
        "failed" to failed,
        "distance" to tripDoc["distance"],
        "taken" to false,
        "travelDateMillis" to travelDay,
        "tripTimeInMillis" to departureTime.fullTimeInMillis,
        "generatedOn" to Timestamp.now()
    )

}

/**
 * @property fromLocality is to be called only for destinations overviews
 */
data class TownsOverview(
    var travelDayString: String,
    var townName: String,
//    val regionName: String,
    var busCounts: Int,
    var bookersCount: Int,
    var scansCount: Int,
    var fromLocality: String? = null,
    val destinations: MutableList<DocumentSnapshot> = emptyList<DocumentSnapshot>() as MutableList<DocumentSnapshot>
) {
    companion object {
        //Creates a new overview
        fun newBookOverView(
            travelDay: String,
            townName: String,
            from: String?,
            townDoc: DocumentSnapshot
        ): TownsOverview {
            val newBusCount = 1
            val newScanCounts = if (townDoc.getBoolean("isScanned")!!) 1 else 0
            val newDestinations = mutableListOf(townDoc)
            return TownsOverview(
                travelDayString = travelDay,
                townName = townName,
                busCounts = newBusCount,
                bookersCount = 1,
                scansCount = newScanCounts,
                fromLocality = from,
                destinations = newDestinations
            )

        }
    }

    override fun hashCode(): Int {
        var result = townName.hashCode()
        result = 31 * result + bookersCount
        result = 31 * result + scansCount
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TownsOverview

        //We only want to compare localityNames
        if (townName != other.townName) return false

        return true
    }

    fun insertNewTown(townDoc: DocumentSnapshot) {
        bookersCount += 1
        if (townDoc.getBoolean("isScanned")!!) scansCount += 1
        //We check if this is the first time the destination of this book is appearing so that we can increment destination count
        //Also we want to do this only for localities overviews
        if (destinations.find { it.getString("tripDestinationName") == townDoc.getString("tripDestinationName") } == null && fromLocality == null)
            busCounts += 1
        destinations.add(townDoc)

    }

    fun reInsertExistingTown(townDoc: DocumentSnapshot) {
        // In case the modification in the database was a scan
        if (townDoc.getBoolean("isScanned")!!) scansCount += 1
        else scansCount -= 1
        destinations.withIndex().find {
            it.value.id == townDoc.id
        }?.let {
            destinations[it.index] = townDoc
        }
    }
}

//TODO Info screen