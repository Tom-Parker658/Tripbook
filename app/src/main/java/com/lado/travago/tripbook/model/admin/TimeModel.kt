package com.lado.travago.tripbook.model.admin

import java.util.*


/**
 * @author Tom parkert
 * @property from12Format is used to create this class from the 12H format basis
 * @property from24Format is used to create this class from the 24H format basis
 * @property timesDifferenceInMinutes is used to get the difference in minutes between 2 [TimeModel] object
 *
 * NOTE: ALL TIMES ARE STORED AS 24 HOUR FORMAT
 */
class TimeModel private constructor(
    val hour: Int,
    val minutes: Int,
    val millisecond: Int?,
) {
    //A number formatted timeModel
    val timeInSeconds = (hour * 3600) + (minutes * 60)

    //Adds a zero before single numbers

    fun Int.toStringF() = if (this < 10) "0$this" else this

    fun formattedTime(format: TimeFormat) =
        when (format) {
            TimeFormat.FORMAT_12H -> {
                if (hour > 12) "${(hour % 12).toStringF()}:${minutes.toStringF()} ${millisecond?.toStringF() ?: ""} ${Meridian.PM}"
                else "$hour : ${minutes.toStringF()} ${millisecond?.toStringF() ?: ""} ${Meridian.AM}"
            }
            TimeFormat.FORMAT_24H -> "${hour.toStringF()} : ${minutes.toStringF()} ${millisecond?.toStringF() ?: ""}"
        }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeModel

        if (hour != other.hour) return false
        if (minutes != other.minutes) return false
        if (millisecond != other.millisecond) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minutes
        result = 31 * result + (millisecond ?: 0)
        return result
    }

    enum class TimeFormat { FORMAT_12H, FORMAT_24H }
    enum class Meridian { AM, PM }
    companion object {
        fun from12Format(hour: Int, minutes: Int, millisecond: Int?, meridian: Meridian) =
            if (meridian == Meridian.AM) TimeModel(hour, minutes, millisecond)
            else TimeModel(hour + 12, minutes, millisecond)

        fun from24Format(hour: Int, minutes: Int, millisecond: Int? = null) =
            TimeModel(hour, minutes, millisecond)

        fun fromSeconds(inSeconds: Int): TimeModel {
            val minutes = (inSeconds / 60)
            val hours = (inSeconds / 3600)
            return TimeModel(
                hours, minutes, null
            )
        }

        fun timesDifferenceInMinutes(largerTime: TimeModel, lowerTime: TimeModel): Int? {
            val secondsDiff = largerTime.timeInSeconds - lowerTime.timeInSeconds
            return if (secondsDiff < 0) null
            else {
                secondsDiff / 60
            }
        }

    }
}