package com.lado.travago.tripbook.repo.osm_services

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://overpass-api.de/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface OverpassApiService {
    /**
     * Gets the list of cities or towns or even roads from the OpenStreetMap through the OverpassAPI
     */
    @GET("api/interpreter")
    fun getItemsAsync(@Query("data") data: String): Deferred<TownEntityList>
}

/**
 * Single instance of the service
 */
object OverpassApi {
    val overpassService: OverpassApiService by lazy {
        retrofit.create(OverpassApiService::class.java)
    }
}