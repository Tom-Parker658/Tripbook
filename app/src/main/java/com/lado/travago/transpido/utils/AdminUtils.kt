package com.lado.travago.transpido.utils


import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator

//import org.apache.tika.metadata.Metadata
//import org.apache.tika.parser.ParseContext
//import org.apache.tika.parser.microsoft.ooxml.OOXMLParser
//import org.apache.tika.sax.BodyContentHandler


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
     *
     * @author Tom Parkert
     *
     * This utility method takes in 2 locations names and return the actual road distance separating them.
    It uses an excel table which contains all distances between cities in Cameroon.
     * @see [Interurban Distances.xlsx]
     * We use [OOXMLParser] from [org.apache.tika] library to parse this excel file into a string.
     * @see OOXMLParser
     * Finally we get the distance and from there using the 60km/h standard sped we can get the
     * time taken
     *
     * @param from is the name of the PLace location of origin
     * @param to is the name of the Place final destination from the origin
     * @returns Pair<Float, Long> which is made up of the distance and travel time. Distance in Kms
     * and time in Minutes
     * /
    fun distanceEvaluator(from: String?, to: String?): Pair<Int, Double>{

    //Average speed a bus can move
    val averageCarSpeed = 60.0

    //Convert all french "é" from the locations to normal "e"
    val fromString = from?.replace("é","e",true).toString()
    val toString = to?.replace("é", "e", true).toString()

    //Parse the excel file into a string stored in handler
    val handler = BodyContentHandler()
    val inputStream = FileInputStream(File("C:\\Users\\FAMILIAL\\Desktop\\Interurban Distances.xlsx"))
    val msOfficeParser = OOXMLParser()
    msOfficeParser.parse(inputStream, handler, Metadata(), ParseContext())//Actual parsing process

    //We create a list of journeys by splitting the excel file by newline as each newline is a journey
    //The format of the journey ==> {Location1 Location2 distance}
    val journeyList = handler.toString().split("\n")

    //We query to find the parameter locations from the list if not, return "0"
    val journey = journeyList.find {
    it.contains(fromString, true) && it.contains(toString , true)
    }?:"0"

    //Finally we get the distance in km part of the journey and convert it to an integer and return that
    var distanceString = ""
    journey.forEach {
    if ("0123456789".contains("$it"))
    distanceString += it
    }

    val distance = distanceString.toInt()
    //We get the average time taken in hours and convert into minutes
    val timeTakenInMinutes = distance/averageCarSpeed

    return Pair(distance, timeTakenInMinutes)
    }
     */
}