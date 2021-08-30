package com.lado.travago.tripbook.model.enums

import com.lado.travago.tripbook.model.enums.OCCUPATION.*
import com.lado.travago.tripbook.model.enums.SEX.*


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
    MALE, FEMALE, UNKNOWN
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

