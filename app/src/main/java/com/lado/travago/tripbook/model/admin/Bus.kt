package com.lado.travago.tripbook.model.admin

import com.lado.travago.tripbook.model.booker.Booker

/**
 * A bus is a passenger container. A bus contains a list of [Booker].
 *
 * @property size which is the number of seats in the bus(maximum number of passengers).
 * @property passengers the list of travellers
 */
data class Bus(
    var size: Int = 70,
    var passengers: List<Booker> = listOf(),
){
    /**
     * An inner class for listing all buses in the recycler view of [AgencyJourneyFragment]
     */
    data class BusListInfo(
        val dateInMillis: Long,
        val totalBusNumber: Int
    )

    /**
     * An inner class for listing buses of a day per journey
     */
    data class JourneyBusList(
        val location: String,
        val destination: String,
        val percentageFull: Double
    )

    /**
     * An inner class to contain the info for each passenger of a particular bus
     */
    data class BookerInfo(
        val bookerName: String,
        val seatNumber: Int,
        val photoUrl: String
    )
}
