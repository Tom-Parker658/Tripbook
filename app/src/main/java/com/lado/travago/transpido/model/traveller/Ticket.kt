package com.lado.travago.transpido.model.traveller

import com.lado.travago.transpido.model.admin.Journey
import com.lado.travago.transpido.utils.Utils

/**
 * @author Tom Parkert
 * A ticket is created by a [Traveller] and is mainly made up of a booked journey and the Traveller
 * This state is determined my the travelTime and travelDay encoded in the journeyID. If it ticked-QR is not
 * scanned by transpido admin before the takeoff time, it becomes expired and can't be used anymore
 * @property journeyID This is the id gotten from booking a ticket which has the traveller's id and travel time
 * encoded in it.
 * @property isExpired as seen below manages the validity of the ticket
 * @property qrCode is scan-able qr which can be scanned by the [Scanner] and if valid, before the
 * traveller can enter the bus. It contains all info about the journey and traveller
 * @property isScanned Checks whether the traveller made the journey or not(When the qr is
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

    private val qrCode by lazy {
        Utils.ticketQRCodeGenerator(this)
    }

}
