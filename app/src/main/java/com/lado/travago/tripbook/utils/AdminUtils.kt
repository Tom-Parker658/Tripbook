package com.lado.travago.tripbook.utils

import com.google.firebase.firestore.DocumentSnapshot

object AdminUtils {
    const val LOCAL_SERVER_FIREBASE_IP = "192.168.58.159"



    /**
     * Takes a list of document snapshots and sort them accordingly to the parameter[SortOption]
     */
    fun MutableList<DocumentSnapshot>.sortDocuments(
        byOption: SortOption
    ): MutableList<DocumentSnapshot> {
        val fieldName = byOption.fieldName
        return when (byOption.sortingParam) {
            //Asc
            SortingParams.NAMES_ASC -> this.sortedBy { it.getString(fieldName) }
            SortingParams.NAMES_DESC -> this.sortedByDescending { it.getString(fieldName) }
            SortingParams.PRICE_DESC -> this.sortedByDescending { it.getDouble(fieldName) }
            SortingParams.PRICE_ASC -> this.sortedBy { it.getDouble(fieldName) }
            SortingParams.REGION_ASC -> this.sortedBy { it.getString(fieldName) }
            SortingParams.REGION_DESC -> this.sortedByDescending { it.getString(fieldName) }
            SortingParams.VIP_FIRST -> this.sortedBy { it.getBoolean(fieldName) }
            SortingParams.VIP_LAST -> this.sortedByDescending { it.getBoolean(fieldName) }

            SortingParams.POPULARITY_ASC -> this.sortedBy { it.getLong(fieldName) }
            SortingParams.POPULARITY_DESC -> this.sortedBy { it.getLong(fieldName) }
            SortingParams.REPUTATION_ASC -> this.sortedBy { it.getDouble(fieldName) }
            SortingParams.REPUTATION_DESC -> this.sortedBy { it.getDouble(fieldName) }
            SortingParams.TAKEN_ALREADY -> this.sortedBy { it.getBoolean(fieldName) }
            SortingParams.EXPIRY_FIRST -> this.sortedBy { it.getBoolean(fieldName) }
        }.toMutableList().apply {
            //We make sure we mark the current param as selected
            byOption.selected = true
        }
    }

    enum class SortingParams {
        POPULARITY_ASC,
        POPULARITY_DESC,
        REPUTATION_ASC,
        REPUTATION_DESC,
        NAMES_ASC,
        NAMES_DESC,
        PRICE_ASC,
        PRICE_DESC,
        REGION_ASC,
        REGION_DESC,
        VIP_FIRST,
        VIP_LAST,
        TAKEN_ALREADY,
        EXPIRY_FIRST,
    }

    data class SortOption constructor(
        val sortingParam: SortingParams,
        val fieldName: String
    ) {
        var selected = false
        var ascSelected: Boolean? = null
        var descSelected: Boolean? = null
    }
}

