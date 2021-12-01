package com.lado.travago.tripbook.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.lado.travago.tripbook.model.booking.Book
import com.lado.travago.tripbook.model.users.Booker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Contains a set of utilities for our app_user .
 */
@ExperimentalCoroutinesApi
object Utils {
    private val oneDayInMillis: Long
        get() {
            val today = Calendar.getInstance().timeInMillis
            val tomorrow =
                Calendar.getInstance().apply { this.roll(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
            return tomorrow - today
        }

    /**
     * Gets the number of days between two dates in any order then returns the time difference in days
     * as a decimal number e.g 9.5 Days
     */
    fun getNumberOfDaysBetween(dateInMillis1: Long, dateInMillis2: Long) =
        sqrt(
            ((dateInMillis1 - dateInMillis2).toDouble() / oneDayInMillis.toDouble()).pow(2)
        )

    //We add spaces after every 3 characters except the last character
    fun formatFCFAPrice(price: Long): String {
        var formattedPrice = ""
        price.toString().reversed().forEachIndexed { index, char ->
            formattedPrice += char
            if (index != price.toString().length && (index + 1) % 3 == 0) formattedPrice += " "
        }
        return "${formattedPrice.reversed()} FCFA"
    }


    /**
     * Returns a map with the id as a field
     */
    fun DocumentSnapshot.toMapWithIDField(): MutableMap<String, Any> {
        val map = data!!
        map["id"] = id
        return map
    }

    fun timeTakenCalculator(distance: Double, velocity: Long): String {
        val timeTakenInMinutes = distance / velocity
        val hours = (timeTakenInMinutes / 60).toInt()
        val minutes = (((timeTakenInMinutes / 60) - hours) * 60).toInt()
        return if (hours == 0) "${minutes}M"
        else "${hours}H ${minutes}M"
    }



    /**
     * @author Tom Parkert
     * Subtracts the birthday in millis from the current date in millis, then convert the
     * answer from milliseconds to years
     * @param birthdayInMillis is the birthday in milliseconds
     * @return the age in Whole number
     *
     */
    fun getAge(birthdayInMillis: Long): Int {
        val now = Calendar.getInstance().timeInMillis
        val ageInMillis = now - birthdayInMillis
        return (ageInMillis / (1000 * 3600 * 24 * 365.25)).toInt()
    }


    /**
     * Generate a QR based on the ticket
     * Uses [Utils.qrCodeEncryptor] to encrypt the code
     * @return the qrCode generated is returned as a bitmap image.
     *
     */
    fun bookQRCodeGenerator(qrCodeText: String, requiredHeight: Int = 150, requiredWidth: Int = 150): Bitmap? {
        val result: BitMatrix
        val encryptedSeed = qrCodeEncryptor(qrCodeText)
        //Try to encode the qrSeed to QR code or generate an error
        try {
            result = MultiFormatWriter().encode(
                encryptedSeed,
                BarcodeFormat.QR_CODE, requiredHeight, requiredWidth, null
            )
        } catch (iae: IllegalArgumentException) {
            Log.e("QRCODE", iae.message.toString())
            return null
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) -0x1_000_000 else -0x1
            }
        }
        //Create a bitmap using the qrCode
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        // createImageFile(bitmap)
        return bitmap
    }

    /**
     * CERA
     */
    fun qrCodeEncryptor(qrSeed: String): String {
        val reversedQRSeed = qrSeed.reversed()
        return reversedQRSeed.replace("e", "~", ignoreCase = false)
    }

    /**
     * DERA
     */
    fun qrCodeDecryptor(reversedQrSeed: String): String {
        val qrSeedWithoutE = reversedQrSeed.reversed()
        return qrSeedWithoutE.replace("~", "e", ignoreCase = false)
    }

    /**
     * Determines if a ticket is still valid by parsing the tickets  and getting the
     * travelDay and travelTime and checking if it has already passed or not. If it has passed,
     * it is marked as invalid and QRCode becomes useless.
     * @param book is a Ticket object from which the travelTime and travelDay will be extracted.
     */
    //TODO: Use this to check if a book is still fine
    fun isTicketExpired(book: Book): Boolean {
        val travelDay = book.travelDay

        //Checks if the journeys travelTime and TravelDay is still to come(or not yey passed
        // It true if the travel day is still to come else false thus expired)
        return Calendar.getInstance().before(Date(travelDay))
    }


    /**
     * Formats the date from a milliseconds Long to a nice looking date
     * @param dateInMillis is the time in millis obtained from the calendar selection
     * @param pattern is the format pattern
     * @return the formatted date [String]
     */
    fun formatDate(dateInMillis: Long, pattern: String): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(dateInMillis))

    /**
     * Helper method to create a stream from bitmap
     * @param bitmap is the image we which to use for the conversion
     * @return the Stream
     */
    fun convertBitmapToStream(
        bitmap: Bitmap?,
        format: Bitmap.CompressFormat,
        quality: Int
    ): ByteArrayInputStream {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(format, 0, byteArrayOutputStream)
        return ByteArrayInputStream(byteArrayOutputStream.toByteArray())
    }

    /**
     * A function to remove all spaces
     */
    fun String.removeSpaces() = replace(" ", "")
    fun formatDistance(distance: Long) = "$distance km"


}
