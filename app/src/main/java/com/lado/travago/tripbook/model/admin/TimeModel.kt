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

    fun formattedTime(format: TimeFormat): String {
        val fMinutes = if (minutes == 0) "00" else minutes.toString()
        val fMilli = when {
            millisecond == 0 && millisecond != null -> "00"
            millisecond != 0 && millisecond != null -> millisecond.toString()
            else -> null
        }
        return when (format) {
            TimeFormat.FORMAT_12H -> {
                if (hour > 12) "${hour % 12}:$fMinutes:${millisecond ?: ""} ${Meridian.PM}"
                else "$hour : $fMinutes ${millisecond ?: ""} ${Meridian.AM}"
            }
            TimeFormat.FORMAT_24H -> "$hour : $fMinutes ${millisecond ?: ""} "
        }
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

        fun timesDifferenceInMinutes(largerTime: TimeModel, lowerTime: TimeModel): Int? {
            val hourDiff = largerTime.hour - lowerTime.hour
            val minutesDiff = largerTime.minutes - lowerTime.minutes
            return if (hourDiff < 0) null
            else {
                (hourDiff * 60) + minutesDiff
            }
        }

    }
}