package com.lado.travago.tripbook.repo.osm_services

import androidx.lifecycle.LiveData
import com.lado.travago.tripbook.repo.cache_db.OsmDatabase
import com.lado.travago.tripbook.repo.State
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class TownsRepository(private val database: OsmDatabase) {

    val towns: LiveData<List<TownEntity>> = database.osmDao.getAllTowns()

    suspend fun refreshTowns() = flow {
        emit(State.loading())

        val townEntityList =
            OverpassApi.overpassService.getItemsAsync(QUERY_ALL_TOWNS).await().towns
        database.osmDao.insertAllTowns(townEntityList.toTypedArray())
        emit(State.success(townEntityList.size))

    }.catch {
        emit(State.failed(it as Exception))
    }.flowOn(Dispatchers.IO)


    companion object {
        val QUERY_ALL_TOWNS =
            """[out:json][timeout:25];
                area["name:en"="Cameroon"]->.cam;
                (
                node["place"="town"](area.cam);
                node["place"="city"](area.cam);
                );
                out body;""".trimIndent()

    }
}