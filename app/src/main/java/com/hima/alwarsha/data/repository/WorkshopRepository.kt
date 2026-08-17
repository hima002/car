package com.hima.alwarsha.data.repository

import android.location.Location
import com.hima.alwarsha.BuildConfig
import com.hima.alwarsha.data.model.Workshop
import com.hima.alwarsha.network.PlacesApiClient
import com.hima.alwarsha.network.PlaceResult

class WorkshopRepository {

    /**
     * Real nearby car-repair workshops from Google Places (Nearby Search), within [radiusKm]
     * (capped at 50km by the API), optionally biased towards [carBrand] so brand-relevant
     * service centers surface first. Sorted by rating then by review count — never fabricated.
     */
    suspend fun findNearbyWorkshops(
        userLat: Double,
        userLng: Double,
        carBrand: String?,
        radiusKm: Int = 50
    ): List<Workshop> {
        val apiKey = BuildConfig.PLACES_API_KEY
        if (apiKey.isBlank()) return emptyList()

        val response = PlacesApiClient.service.nearbySearch(
            location = "$userLat,$userLng",
            radiusMeters = (radiusKm * 1000).coerceAtMost(50_000),
            type = "car_repair",
            keyword = carBrand,
            apiKey = apiKey
        )

        return response.results
            .mapNotNull { it.toWorkshop(userLat, userLng) }
            .sortedWith(
                compareByDescending<Workshop> { it.rating ?: 0.0 }
                    .thenByDescending { it.userRatingsTotal }
                    .thenBy { it.distanceKm }
            )
    }

    private fun PlaceResult.toWorkshop(userLat: Double, userLng: Double): Workshop? {
        val location = geometry?.location ?: return null
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLng, location.lat, location.lng, results)
        return Workshop(
            placeId = placeId,
            name = name,
            address = vicinity ?: "",
            rating = rating,
            userRatingsTotal = userRatingsTotal ?: 0,
            distanceKm = results[0] / 1000.0,
            lat = location.lat,
            lng = location.lng,
            isOpen = businessStatus == null || businessStatus == "OPERATIONAL"
        )
    }
}
