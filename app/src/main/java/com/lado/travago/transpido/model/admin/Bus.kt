package com.lado.travago.transpido.model.admin

import com.lado.travago.transpido.model.traveller.Traveller

/**
 * A bus is a passenger container. A bus contains a list of [Traveller].
 *
 * @property size which is the number of seats in the bus(maximum number of passengers).
 * @property passengers the list of travellers
 */
data class Bus(
    var size: Int = 70,
    var passengers: List<Traveller> = listOf(),
)
