package com.lado.travago.tripbook.utils

import android.graphics.Bitmap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.lado.travago.tripbook.model.booking.Ticket
import com.lado.travago.tripbook.model.users.Booker
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Contains a set of utilities for our app_user .
 */
object Utils {

    /**
     * @author Tom Parkert
     * Subtracts the birthday in millis from the current date in millis, then convert the
     * answer from milliseconds to years
     * @param birthdayInMillis is the birthday in milliseconds
     * @return the age in Whole number
     *
     */
    fun getAge(birthdayInMillis: Long): Int{
        val now = Calendar.getInstance().timeInMillis
        val ageInMillis = now - birthdayInMillis
        return (ageInMillis/(1000*3600*24*365.25)).toInt()
    }

    /**
     * This utility functions will be used to query all model descriptions e.g [Booker.travellerDescription]
     * @see Booker.travellerDescription
     * @see Ticket.ticketDescription
     * Descriptions are of format {label:value, label:value} ans so on. e.g {travellerName:Tom Parker}
     * This query utility is used to search for specific values. So you pass your label as query and
     * the description to search for the value of that query.
     * NOTE: Labels must be exact as shown by the class description variable
     *
     * @param description is the required description string we want to search in
     * @param query is the label for which we want to get the value
     * @return the value of the query else null
     */
    fun queryDescription(description: String, query: String): String? {
        var cleanedDescString = ""
        //We remove all curly braces from the description and add it to cleanedDescString
        description.forEach {
            if (it != '{' && it != '}') cleanedDescString += "$it"
        }
        /*
         *We then split it into a list with delimeter , from which we choose the string containing our label(query)
         *Lastly, we split the result with delimeter : from which a list is returned and we get the last member
         *which is always the value
         */
        return run {
            cleanedDescString.split(",").find {
                it.contains(query)
            }?.split(":")?.last()
        }
    }

    /**
     * Generate a QR based on the ticket
     * @param ticket is a ticket instance which provides a description string for the journey
     * Uses [Utils.qrCodeEncryptor] to encrypt the code
     * @return the qrCode generated is returned as a bitmap image.
     *
     */
    fun ticketQRCodeGenerator(ticket: Ticket): Bitmap? {
        val result: BitMatrix
        val requiredHeight = 300
        val requiredWidth = 300
        val encryptedSeed = qrCodeEncryptor(ticket.qrSeed)
        //Try to encode the qrSeed to QR code or generate an error
        try {
            result = MultiFormatWriter().encode(encryptedSeed,
                BarcodeFormat.QR_CODE, requiredHeight, requiredWidth, null)
        } catch (iae: IllegalArgumentException) {
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
    fun qrCodeEncryptor(qrSeed: String): String{
        val reversedQRSeed = qrSeed.reversed()
        return reversedQRSeed.replace("e", "~",ignoreCase = false)
    }

    /**
     * DERA
     */
    fun qrCodeDecryptor(reversedQrSeed: String): String{
        val qrSeedWithoutE = reversedQrSeed.reversed()
        return qrSeedWithoutE.replace("~", "e",ignoreCase = false)
    }

    /**
     * Determines if a ticket is still valid by parsing the tickets  and getting the
     * travelDay and travelTime and checking if it has already passed or not. If it has passed,
     * it is marked as invalid and QRCode becomes useless.
     * @param ticket is a Ticket object from which the travelTime and travelDay will be extracted.
     */
    fun isTicketExpired(ticket: Ticket): Boolean {
        val travelDay = ticket.travelDay

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
    fun formatDate(dateInMillis: Long, pattern: String): String = SimpleDateFormat(pattern, Locale.getDefault()).format(Date(dateInMillis))

    /**
     * Helper method to create a stream from bitmap
     * @param bitmap is the image we which to use for the conversion
     * @return the Stream
     */
    fun convertBitmapToStream(bitmap: Bitmap?, format: Bitmap.CompressFormat, quality: Int): ByteArrayInputStream {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(format, 0, byteArrayOutputStream)
        return ByteArrayInputStream(byteArrayOutputStream.toByteArray())
    }

}
