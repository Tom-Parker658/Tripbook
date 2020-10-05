package com.lado.travago.transpido.utils


import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.lado.travago.transpido.model.admin.Destination
import com.lado.travago.transpido.model.admin.Journey
import com.lado.travago.transpido.model.enums.DataResources
import com.lado.travago.transpido.model.enums.Region
import java.util.*


/**
 * contains utilities for the admin panel
 */
object AdminUtils {

    /**
     * Initiates the scanning process for the QR code. It call the camera to do the scan then stores the
     * content as part of an intent which can gotten by any Activity or Fragment using the [onActivityResult]
     * callback method.
     * @param fragment is the fragment which will call this utility in other to actually scan the QR code.
     */
    fun qRCodeScannerInitiator(fragment: Fragment) {
        //A scannerIntentIntegrator to scan the qr
        IntentIntegrator.forSupportFragment(fragment)
            //Specifies only QR CODES and not bar codes should be scanned
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            //Specifies the activity to capture the intent
            .setCaptureActivity(fragment.activity?.javaClass)
            .setBeepEnabled(true)//Enables the beep sound during scanning
            .setOrientationLocked(false)//Unlocks phone orientation during scanning
            .setPrompt("Scan QR Ticket!")// Text to be displayed to tell the admin to scan
            .setRequestCode(5)//Set the scan intent request code which will be used to extract this intent when called by [OnActivityResult]
            .setTorchEnabled(true)//Enables the torch on the camera to make it more visible
            .initiateScan()//Initiates the scan process

    }

    /**
     * Returns a list of places and their respective regions. the format of the list is as follows.
     * Place1+RegionX, Place2+RegionX ...Place59+RegionX e.g Dschang+West
     */
    fun findPlaceRegion()=
        //Parse the text file and returns a list based on each line
        DataResources.regionList.trimIndent().reader().buffered().readLines()

    /**
     * Takes in a string and returns the corresponding [Region]
     */
    fun parseRegionFromString(regionInString: String) =
        when (regionInString.toLowerCase(Locale.ROOT)){
            "north" -> Region.NORTH
            "west" -> Region.WEST
            "east" -> Region.EAST
            "northwest" -> Region.NORTH_WEST
            "southwest" -> Region.SOUTH_WEST
            "adamawa" -> Region.ADAMAWA
            "centre" -> Region.CENTER
            "littoral" -> Region.LITTORAL
            "extremenorth" -> Region.EXTREME_NORTH
            "farnorth" -> Region.EXTREME_NORTH
            "south" -> Region.SOUTH
            else -> Region.UNKNOWN
        }



    /**
     *
     * @author Tom Parkert
     *
     * This utility method takes in 2 locations names and return the actual road distance separating them.
        It uses the string resource [DataResources.journeyDistanceList] table which contains all distances between cities in Cameroon.
     * Finally we get the distance and from there using the 60km/h standard sped we can get the
     * time taken
     *
     * @param currentJourney is the journey object which we wish to get the distance and time taken
     * @param from is the [Destination] Place location of origin
     * @param to is the [Destination] Place final destination from the origin
     * @returns Pair<Float, Long> which is made up of the distance and travel time. Distance in Kms
     * and time in Minutes
     */
    @Suppress("KDocUnresolvedReference")
    fun distanceEvaluator(currentJourney: Journey): Pair<Int, Double>{
        val from = currentJourney.location
        val to = currentJourney.destination

        //Average speed a bus can move
        val averageCarSpeed = 60.0
        //We query to find the parameter locations from the list if not, return "0"
        val journeysDistanceList = DataResources.journeyDistanceList.trimIndent().reader().buffered().readLines()
        val journeyString = journeysDistanceList.find {
            it.contains(from.placeMap["name"].toString(), true) && it.contains(to.placeMap["name"].toString(), true)
        }
        //Finally we get the distance in km part of the journey and convert it to an integer and return that
        var distanceString = ""
        journeyString?.forEach {
        if ("0123456789".contains("$it")) distanceString += it
        }

        val distance = distanceString.toInt()
        //We get the average time taken in hours and convert into minutes
        val timeTakenInMinutes = distance/averageCarSpeed
        return Pair(distance, timeTakenInMinutes)
    }
}