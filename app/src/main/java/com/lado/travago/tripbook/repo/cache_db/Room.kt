package com.lado.travago.tripbook.repo.cache_db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.lado.travago.tripbook.repo.osm_services.BusStationEntity
import com.lado.travago.tripbook.repo.osm_services.TownEntity

@Dao
interface OsmDao {
    @Query("select * from town_table")
    fun getAllTowns(): LiveData<List<TownEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTowns(townEntities: Array<TownEntity>)

    @Query("select * from bus_station_table")
    fun getAllBusStations(): LiveData<List<BusStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBusStations(vararg busStationEntities: BusStationEntity)
}

@Database(
    entities = [TownEntity::class, BusStationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OsmDatabase : RoomDatabase() {
    abstract val osmDao: OsmDao
}

@Volatile
private lateinit var INSTANCE: OsmDatabase

fun getDatabase(context: Context): OsmDatabase {
    if (!::INSTANCE.isInitialized) {
        INSTANCE =
            Room.databaseBuilder(
                context.applicationContext,
                OsmDatabase::class.java,
                "osm_database")
                .build()
    }
    return INSTANCE
}