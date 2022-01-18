package com.lado.travago.tripbook.model.admin

import android.os.CountDownTimer
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lado.travago.tripbook.model.admin.TimeModel.Companion.from12Format
import com.lado.travago.tripbook.model.admin.TimeModel.Companion.from24Format
import com.lado.travago.tripbook.model.admin.TimeModel.Companion.fromTimeParameter
import java.util.*
import kotlin.math.absoluteValue


/**
 * @author Tom parkert
 * @property from12Format is used to create this class from the 12H format basis
 * @property from24Format is used to create this class from the 24H format basis
 *
 * NOTE: ALL TIMES ARE STORED AS 24 HOUR FORMAT
 */
class TimeModel private constructor(
    val hour: Int,
    val minutes: Int,
    val seconds: Int,
    val millisecond: Int,
) {
    //Adds a zero before single numbers
    private fun Int.toStringF() = if (this < 10) "0$this" else this

    //Full time in milliseconds for storing in database
    val fullTimeInMillis: Int
        get() = (hour * 3_600_000) + (minutes * 60_000) + (seconds * 1_000) + millisecond

    fun formattedTime(format: TimeFormat) = when (format) {
        TimeFormat.FORMAT_12H -> {
            if (hour > 12) "${(hour % 12).toStringF()}:${minutes.toStringF()} ${Meridian.PM}"
            else "${hour.toStringF()}:${minutes.toStringF()} ${Meridian.AM}"
        }
        TimeFormat.FORMAT_24H -> "${hour.toStringF()}:${minutes.toStringF()}"
        TimeFormat.FORMAT_FULL_TIME -> "${hour.toStringF()}:${minutes.toStringF()}:${seconds.toStringF()}.${millisecond.toStringF()}"
        TimeFormat.FORMAT_SECONDS -> "${seconds}s"
    }

    /**
     * We want to return the default format
     */
    fun localTimeFormat() = when (Locale.getDefault().language) {
        "en" -> formattedTime(TimeFormat.FORMAT_12H)
        "fr" -> formattedTime(TimeFormat.FORMAT_24H)
        else -> formattedTime(TimeFormat.FORMAT_FULL_TIME)

    }

    /**
     * Compares this time value with another(other)
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    operator fun compareTo(other: TimeModel): Int {
        val thisMinusOther = difference(this, other)
        return when {
            thisMinusOther == 0 -> 1
            thisMinusOther < 0 -> -1
            else -> 1
        }
    }

    /**
     * Returns the absolute value of the difference between this "time" and the "other" time
     */
    fun absDifference(other: TimeModel) = difference(this, other).absoluteValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeModel

        if (hour != other.hour) return false
        if (minutes != other.minutes) return false
        if (seconds != other.seconds) return false
        if (millisecond != other.millisecond) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minutes
        result = 31 * result + seconds
        result = 31 * result + millisecond
        return result
    }

    enum class TimeFormat { FORMAT_12H, FORMAT_24H, FORMAT_FULL_TIME, FORMAT_SECONDS }
    enum class Meridian { AM, PM }
    enum class TimeParameter { HOUR, MINUTE, SECONDS, MILLISECONDS }

    companion object {
        //Denotes the maximums values in which 1Day can be represented in hour, minutes, seconds and milliseconds
        const val MAX_HOURS_DAY = 24L
        const val MAX_MINUTES_DAY = 1_440L
        const val MAX_SECONDS_DAY = 86_400L
        const val MAX_MILLISECONDS_DAY = 864_000_000L

        fun from12Format(
            @IntRange(from = 0, to = 11) hour: Int,
            @IntRange(from = 0, to = 60) minutes: Int,
            @IntRange(from = 0, to = 60) second: Int = 0,
            @IntRange(from = 0, to = 1000) millisecond: Int = 0,
            meridian: Meridian
        ) =
            if (meridian == Meridian.AM) TimeModel(hour, minutes, second, millisecond)
            else TimeModel(hour + 12, minutes, second, millisecond)

        fun from24Format(
            @IntRange(from = 0, to = 23) hour: Int,
            @IntRange(from = 0, to = 60) minutes: Int,
            @IntRange(from = 0, to = 60) second: Int = 0,
            @IntRange(from = 0, to = 1000) millisecond: Int = 0,
        ) =
            TimeModel(hour, minutes, second, millisecond)

        /**
         * Takes a number given as a particular time parameter [TimeParameter] and convert it into a TimeModel
         * with different level of accuracy depending on the time parameter
         * @param parameterType is the [TimeParameter]
         * @param parameterValue should be less than or equals to the its corresponding MAX_VALUE
         */
        fun fromTimeParameter(parameterType: TimeParameter, parameterValue: Long): TimeModel {
            /* This is to make sure the input is not more than the MAX_..._DAY for each category,
             so we take the modulo of the input with the corresponding max value for that given parameter */
            val moduloForm: Int
            val hour: Int
            val minutes: Int
            val seconds: Int
            val millisecond: Int
            return when (parameterType) {
                TimeParameter.HOUR -> {
                    moduloForm = (parameterValue % MAX_HOURS_DAY).toInt()

                    TimeModel(moduloForm, 0, 0, 0)
                }
                TimeParameter.MINUTE -> {
                    moduloForm = (parameterValue % MAX_MINUTES_DAY).toInt()
                    hour = moduloForm / 60
                    minutes = moduloForm % 60

                    TimeModel(hour, minutes, 0, 0)
                }
                TimeParameter.SECONDS -> {
                    moduloForm = (parameterValue % MAX_SECONDS_DAY).toInt()
                    hour = moduloForm / 3600
                    minutes = (moduloForm % 3600) / 60
                    seconds = (moduloForm % 3600) % 60

                    TimeModel(hour, minutes, seconds, 0)
                }
                TimeParameter.MILLISECONDS -> {
                    moduloForm = (parameterValue % MAX_MILLISECONDS_DAY).toInt()
                    hour = ((moduloForm / 3600_000) % 24) + 1
                    minutes = (moduloForm % 3600_000) / 60_000
                    seconds = ((moduloForm % 3600_000) % 60_000) / 1000
                    millisecond = ((moduloForm % 3600_000) % 60_000) % 1000

                    TimeModel(hour, minutes, seconds, millisecond)
                }
            }

        }

        /**
         * @return the time difference in milliseconds with its sign
         */
        private fun difference(time1: TimeModel, time2: TimeModel): Int {
            val hourDiff = time1.hour - time2.hour
            val minuteDiff = time1.minutes - time2.minutes
            val secondDiff = time1.seconds - time2.seconds
            val millisecondDiff = time1.millisecond - time2.millisecond

            return (hourDiff * 3_600_000) + (minuteDiff * 60_000) + (secondDiff * 1_000) + millisecondDiff
        }

        /**
         * Returns the current time as a Time model. It takes a date in millis, and put in the base of the
         * [MAX_MILLISECONDS_DAY]. i.e it Gets the milliseconds for a single day, and use it to create the
         * [TimeModel]
         */
        fun now() = fromTimeParameter(TimeParameter.MILLISECONDS, Date().time)
    }

    open class CountDown(
        val toInMillis: Long,
        val countInterval: Long = 1_000L//1 second timer
    ) {
        private lateinit var currentTick: TimeModel

        private val _left = MutableLiveData("")
        val left: LiveData<String> get() = _left

        private val _leftInMillis = MutableLiveData(0L)
        val leftInMillis: LiveData<Long> get() = _leftInMillis

        private val _isRunning = MutableLiveData(false)
        val isRunning: LiveData<Boolean> get() = _isRunning

        private val _isEnded = MutableLiveData(false)
        val isEnded: LiveData<Boolean> get() = _isEnded


        private val timer = object : CountDownTimer(toInMillis, countInterval) {
            override fun onTick(p0: Long) {
                currentTick = fromTimeParameter(
                    TimeParameter.MILLISECONDS,
                    p0
                )
                _leftInMillis.value = currentTick.seconds.toLong()
                _left.value = "${currentTick.seconds}s"
            }

            override fun onFinish() {
                stop()
            }

        }


        fun start(): CountDown {
            timer.start()
            _isRunning.value = true
            _isEnded.value = false
            return this
        }


        //Make sure you set isEnded before isRunning inorder to allow observers to listen to the last tick
        fun stop():CountDown {
            timer.cancel()
            _isEnded.value = true
            _isRunning.value = false
            return this
        }

    }
}