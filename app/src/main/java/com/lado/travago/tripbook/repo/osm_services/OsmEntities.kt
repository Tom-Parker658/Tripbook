package com.lado.travago.tripbook.repo.osm_services

import androidx.room.*
import com.squareup.moshi.Json


/**
 * This is an entity for the database which is based on the [TownEntity] class
 *
 * @param tags is the encoded version of the [TownEntity.tags] variable but now as a String which can later be decoded into a [HashMap<String, String>]
 *
 */
@Entity(tableName = "town_table")
@TypeConverters(MapStringConverter::class)
data class TownEntity(
    @PrimaryKey
    val id: Long,
    val type: String,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>,
)

class TownEntityList(
    @Json(name = "elements") val towns: List<com.lado.travago.tripbook.repo.osm_services.TownEntity>,
)

/**
 * See the [TownEntity] documentation,
 */
@Entity(tableName = "bus_station_table")
@TypeConverters(MapStringConverter::class)
data class BusStationEntity(
    @PrimaryKey
    val id: Long,
    val type: String,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>,
)


class MapStringConverter{
    /**
     * Helps convert from [HashMap<String, String>] to String
     */
    @TypeConverter
    fun fromMapToString(map: Map<String, String>): String {
        var strMap = ""
        map.forEach { (key, value) ->
            strMap += "$key::$value\n"
        }
        return strMap
    }

    /**
     * Helps convert from String to [HashMap<String, String>]
     */
    @TypeConverter
    fun fromStringToMap(strMap: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        strMap.reader().buffered().readLines().forEach { pair ->
            pair.split("::").run {
                map += first() to last()
            }
        }
        return map
    }
}

