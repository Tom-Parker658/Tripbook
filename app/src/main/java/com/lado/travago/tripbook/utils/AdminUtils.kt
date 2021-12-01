package com.lado.travago.tripbook.utils


import com.lado.travago.tripbook.model.enums.DataResources
//import com.lado.travago.tripbook.model.enums.Region
import java.util.*
import java.util.Collections.*


/**
 * contains utilities for the admin panel
 */
object AdminUtils {
    const val LOCAL_SERVER_FIREBASE_IP = "192.168.236.114"
//    Link-local IPv6 address:	fe80::3898:c413:1dcb:e4d7%2
//    IPv4 address:	192.168.236.114
//    IPv4 DNS servers:	192.168.236.52
//    Manufacturer:	Microsoft
//    Description:	Remote NDIS based Internet Sharing Device
//    Driver version:	10.0.18362.1
//    Physical address (MAC):	36-0F-2E-FC-1F-E4

    //GITHUB token = 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
//    Link-local IPv6 address:	fe80::ddc8:2129:1732:5645%2
//    IPv4 address:	192.168.236.200
//    IPv4 DNS servers:	192.168.236.52
//    Manufacturer:	Microsoft
//    Description:	Remote NDIS based Internet Sharing Device
//    Driver version:	10.0.18362.1
//    Physical address (MAC):	BA-EF-49-3C-EB-53

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
}


