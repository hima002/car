package com.hima.alwarsha.network

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    /** Google Places "Nearby Search" (legacy JSON) — radius is capped at 50,000m by the API. */
    @GET("maps/api/place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String, // "lat,lng"
        @Query("radius") radiusMeters: Int,
        @Query("type") type: String,
        @Query("keyword") keyword: String?,
        @Query("key") apiKey: String
    ): NearbySearchResponse
}
