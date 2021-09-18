package com.lado.travago.tripbook.model.admin

import com.lado.travago.tripbook.model.users.Booker

/**
 * A bus is a matrix of cells with certain constraints called "paths". they help construct the bus. Some of them include
 * @property corridorPath this is the path of seats to make invisible to indicate the passage or small path or road in the bus
 * @property driverSeatPath path to make invisible to indicate the drivers + 'boite a vitesse'
 * @property firstDoorPath path to make invisible to indicate the entrance or exit from the first door of the bus
 * @property lastDoorPath path to make invisible to indicate the entrance or exit from the last door of the bus if any else and empty list
 * @property busSize Is the total number of actual seats that particular bus can contain excluding driver seat
 * @property matrixSize is the total number of boxes the bus contains which some can be excluded
 * used by passengers to move from one end of the bus to another
 */
data class BusMatrix(
    val busType: BusType,
) {
    val busSize: Int
    val width: Int
    val length: Int
    val driverSeatPath: List<Int>
    val corridorPath: List<Int>
    val firstDoorPath: List<Int>
    val lastDoorPath: List<Int>

    init {
        when (busType) {
            BusType.SEVENTY_SEATER -> {
                busSize = 69
                width = 6
                length = 15
                driverSeatPath = listOf(1, 2, 3)
                corridorPath = listOf(4, 10, 16, 22, 28, 34, 40, 46, 52, 58, 64, 70, 76, 82, 88)
                firstDoorPath = listOf(23, 24)
                lastDoorPath = listOf(77, 78)
            }
            BusType.COASTER_SEATER -> {//TODO: Repair these values
                busSize = 49
                width = 6
                length = 15
                driverSeatPath = listOf(1, 2, 3)
                corridorPath = listOf(4, 10, 16, 22, 28, 34, 40, 46, 52, 58, 64, 70)
                firstDoorPath = listOf(23, 24)
                lastDoorPath = listOf(54, 53)
            }
            BusType.THIRTY_SEATER -> {//TODO: Repair these values
                busSize = 35
                width = 6
                length = 15
                driverSeatPath = listOf(1, 2, 3)
                corridorPath = listOf(4, 10, 16, 22, 28, 34, 40, 46, 52, 58, 64, 70)
                firstDoorPath = listOf(23, 24)
                lastDoorPath = listOf(54, 53)
            }
        }
    }

    enum class BusType { SEVENTY_SEATER, COASTER_SEATER, THIRTY_SEATER }
}
