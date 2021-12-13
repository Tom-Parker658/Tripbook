package com.lado.travago.tripbook.model.enums

/**
 * For error and notifications display
 */
enum class NotificationType {
    ACCOUNT_NOT_FOUND {
        override fun toString(): String {
            return "My Tripbook account"
        }
    },
    BOOKING_COMPLETE{
        override fun toString(): String {
            return "Complete"
        }
    },
    EMPTY_RESULTS,  NOT_PERMITTED_SCANNER, AGENCY_PROFILE_NOT_FOUND,
}