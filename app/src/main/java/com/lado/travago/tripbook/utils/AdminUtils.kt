package com.lado.travago.tripbook.utils


import com.lado.travago.tripbook.model.admin.Destination
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.model.enums.DataResources
import com.lado.travago.tripbook.model.enums.Region
import java.util.*


/**
 * contains utilities for the admin panel
 */
object AdminUtils {


    /**
     * Returns a list of places and their respective regions. the format of the list is as follows.
     * Place1+RegionX, Place2+RegionX ...Place59+RegionX e.g Dschang+West
     */
    fun findPlaceRegion()=
        //Parse the text file and returns a list based on each line
        DataResources.regionList.trimIndent().reader().buffered().readLines()

    /**
     * Removes a particular predicate from all the list
     */
    fun Collection<String>.removePredicate(vararg predicate: String): Collection<String>{
        var newList = emptyList<String>() as Collection<String>
        this.forEach {
            if (it !in predicate) newList+=it
        }
        return newList
    }

    /**
     * Removes a predicate
     */
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
        val journeysDistanceList = DataResources.journeyDistanceListOriginal.trimIndent().reader().buffered().readLines()
        val journeyString = journeysDistanceList.find {
            it.contains(from.placeMap["name"].toString().replace(" ", "-"), true) && it.contains(to.placeMap["name"].toString().replace(" ", "-"), true)
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