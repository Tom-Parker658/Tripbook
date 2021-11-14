package com.lado.travago.tripbook.utils


import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.model.admin.Destination
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.model.enums.DataResources
//import com.lado.travago.tripbook.model.enums.Region
import java.util.*
import java.util.Collections.*


/**
 * contains utilities for the admin panel
 */
object AdminUtils {
    const val LOCAL_SERVER_FIREBASE_IP = "192.168.108.190"


//    Link-local IPv6 address:	fe80::1c0c:b74e:2833:c83a%2
//    IPv4 address:	192.168.110.167
//    IPv4 DNS servers:	192.168.110.35
//    Manufacturer:	Microsoft
//    Description:	Remote NDIS based Internet Sharing Device
//    Driver version:	10.0.18362.1
//    Physical address (MAC):	16-1D-85-B5-76-21
//    Link-local IPv6 address:	fe80::862:222e:7832:a3f7%2

//    Link-local IPv6 address:	fe80::eda8:f167:7dac:7cd0%2
//    IPv4 address:	192.168.167.15Link-local IPv6 address:	fe80::862:222e:7832:a3f7%2
//IPv4 address:	192.168.246.6
//IPv4 DNS servers:	192.168.246.145
//Manufacturer:	Microsoft
//Description:	Remote NDIS based Internet Sharing Device
//Driver version:	10.0.18362.1
//Physical address (MAC):	3A-C1-49-EE-2E-A6
//    IPv4 DNS servers:	192.168.167.23
//    Manufacturer:	Microsoft
//    Description:	Remote NDIS based Internet Sharing Device
//    Driver version:	10.0.18362.1
//    Physical address (MAC):	2E-06-9C-27-DC-3C


    /**
     * Removes a particular predicate from all the list
     */
    fun Collection<String>.removePredicate(vararg predicate: String): Collection<String> {
        var newList = emptyList<String>() as Collection<String>
        this.forEach {
            if (it !in predicate) newList += it
        }
        return newList
    }

    /**
     * Removes a predicate
     */
    /*   */
    /**
     * Takes in a string and returns the corresponding [Region]
     *//*
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
*/


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
    fun distanceEvaluator(currentJourney: Journey): Pair<Int, Double> {
        val from = currentJourney.location
        val to = currentJourney.destination

        //Average speed a bus can move
        val averageCarSpeed = 60.0
        //We query to find the parameter locations from the list if not, return "0"
        val journeysDistanceList =
            DataResources.journeyDistanceListOriginal.trimIndent().reader().buffered().readLines()
        val journeyString = journeysDistanceList.find {
            it.contains(
                from.placeMap["name"].toString().replace(" ", "-"),
                true
            ) && it.contains(to.placeMap["name"].toString().replace(" ", "-"), true)
        }
        //Finally we get the distance in km part of the journey and convert it to an integer and return that
        var distanceString = ""
        journeyString?.forEach {
            if ("0123456789".contains("$it")) distanceString += it
        }

        val distance = distanceString.toInt()
        //We get the average time taken in hours and convert into minutes
        val timeTakenInMinutes = distance / averageCarSpeed
        return Pair(distance, timeTakenInMinutes)
    }
}

