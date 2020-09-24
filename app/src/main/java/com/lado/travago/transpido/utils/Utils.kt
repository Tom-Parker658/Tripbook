package com.lado.travago.transpido.utils

import android.graphics.Bitmap
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.lado.travago.transpido.model.traveller.Ticket
import com.lado.travago.transpido.model.traveller.Traveller
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Contains a set of utilities for our app_user .
 */
object Utils {

    /**
     * Used to calculate the age of a Traveller [Traveller]
     * @param birthday it must in the format dd/mm/yy
     * It extract the year from the [birthday] and subtracts it from the current year to get the age
     */
    fun getAge(birthday: String): Int {
        val birthDate = Calendar.getInstance()
        birthDate.set(
            birthday.subSequence(0, 1).toString().toInt(),
            birthday.subSequence(3, 4).toString().toInt(),
            birthday.subSequence(6, 9).toString().toInt()
        )
        val now = Calendar.getInstance().timeInMillis
        val age = Calendar.getInstance()
        age.timeInMillis = now - birthDate.timeInMillis
        return age[Calendar.DAY_OF_YEAR] % 365
    }

    /**
     * This utility functions will be used to query all model descriptions e.g [Traveller.travellerDescription]
     * @see Traveller.travellerDescription
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
     * @return the qrCode generated is returned as a bitmap image.
     *
     */
    fun ticketQRCodeGenerator(ticket: Ticket): Bitmap? {
        val result: BitMatrix
        val requiredHeight = 300
        val requiredWidth = 300
        //Try to encode the QR code or generate an error
        try {
            result = MultiFormatWriter().encode("ticket",
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
     * PickDate Utility
     */
    fun getDatePicker(
        startDate: Long,
        endDate: Long,
        titleText: String,
    ): MaterialDatePicker<Long> {
        //We create constraint so that the user can only select dates between a particular interval
        val bounds = CalendarConstraints.Builder()
            .setStart(startDate)//Smallest date which can be selected
            .setEnd(endDate)//Furthest day which can be selected
            .build()

        //We create our date picker which the user will use to enter his travel day
        //Showing the created date picker onScreen
        return MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(bounds)//Constrain the possible dates
            .setTitleText(titleText)//Set the Title of the Picker
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
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
