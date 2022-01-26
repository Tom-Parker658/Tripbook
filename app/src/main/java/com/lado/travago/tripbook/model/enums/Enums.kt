package com.lado.travago.tripbook.model.enums

import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.enums.OCCUPATION.*
import com.lado.travago.tripbook.model.enums.SEX.*
import com.lado.travago.tripbook.ui.notification.ImageViewerFragment


/**
 * @author Tom Parkert
 */

/**
 * Contains options for the sex type of travellers
 * @property MALE for M
 * @property FEMALE for F
 * @property UNKNOWN for initialising sex variables
 */
enum class SEX {

    MALE {
        override fun toString() = "M"
    },
    FEMALE {
        override fun toString() = "F"
    },
    UNKNOWN;

    companion object {
        fun String.toSEX() =
            when {
                this == "Male" || this == "M" || this == "MALE" -> MALE
                this == "Female" || this == "F" || this == "FEMALE" -> FEMALE
                else -> UNKNOWN
            }
    }
}

enum class SignUpCaller {
    PHONE_CHANGE, USER, OTHER_ACTIVITY,
}


/**
 * Contains options for occupations of travellers
 *
 * @property PUPIL any person from nursery to class 6
 * @property STUDENT any person from secondary school & university
 * @property EMPLOYEE any person who has a job
 * @property WORKER any person auto employed
 * @property OTHER any other occupation apart from the above
 * @property UNKNOWN for initialising occupation variables
 */
enum class OCCUPATION {
    PUPIL, STUDENT, EMPLOYEE, WORKER, SCANNER, OTHER
}

/**
 * Contains all operations firestore can carry out
 */
enum class DbOperations {
    SET, DELETE, UPDATE, GET
}

/**
 * This tells us which placeholders to use if the image does not load successfully and during loading also
 *
 * @see ImageViewerFragment
 */
enum class PlaceHolder {
    PERSON, AGENCY, ANY;

    fun placeholderResID(): Int {
        return when (this) {
            PERSON -> R.drawable.baseline_person_24
            AGENCY -> R.drawable.outline_agency_profile_24
            ANY ->  R.drawable.baseline_insert_photo_24
        }
    }
}