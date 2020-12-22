package com.lado.travago.transpido.repo

/**
 * A class which contains tags which shall be used as folder names to hold data about those specific
 * user types
 */
enum class FirestoreTags {
    Scanner, Bookers, Record, Ticket, Journey, OnlineTransportAgency, Destinations
}

/**
 * Represents different image categories used in fireStorage as folder names
 */
enum class StorageTags {
    PROFILE, LOGO, PHOTOS, QR_CODE
}