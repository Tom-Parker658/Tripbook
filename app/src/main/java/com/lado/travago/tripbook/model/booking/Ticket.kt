package com.lado.travago.tripbook.model.booking

import com.google.firebase.Timestamp
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.utils.Utils
import java.util.*

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
data class Ticket(
    val journey: Journey,
    val travellerUid: String,
    var travelTime: Long,
    var travelDay: Long,
) {
    var isExpired = Utils.isTicketExpired(this)
    var isScanned = false
    var qrSeed = "" +
            "${journey.location.id}+${journey.destination.id}+$travellerUid+${Timestamp(Date())}"

    private val qrCode by lazy {
        Utils.ticketQRCodeGenerator(this)
    }
}

//TODO Info screen