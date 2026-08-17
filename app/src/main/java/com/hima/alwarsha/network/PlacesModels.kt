package com.hima.alwarsha.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NearbySearchResponse(
    @Json(name = "results") val results: List<PlaceResult> = emptyList(),
    @Json(name = "status") val status: String = "",
    @Json(name = "error_message") val errorMessage: String? = null
)

@JsonClass(generateAdapter = true)
data class PlaceResult(
    @Json(name = "place_id") val placeId: String,
    @Json(name = "name") val name: String,
    @Json(name = "vicinity") val vicinity: String? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "user_ratings_total") val userRatingsTotal: Int? = null,
    @Json(name = "geometry") val geometry: Geometry? = null,
    @Json(name = "business_status") val businessStatus: String? = null
)

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "location") val location: LatLngDto? = null
)

@JsonClass(generateAdapter = true)
data class LatLngDto(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)
