package com.lado.travago.tripbook.repo

/**
 * A class which contains tags which shall be used as folder names to hold data about those specific
 * user types!
 */
enum class FirestoreTags {
    Scanners, Bookers, Records, Tickets, Journeys, OnlineTransportAgency, Destinations, Regions, Users
}

/**
 * Represents different image categories used in fireStorage as folder names!
 */
enum class StorageTags {
    PROFILE, LOGO, PHOTOS, QR_CODE

}