package com.hima.alwarsha.data.model

data class Workshop(
    val placeId: String,
    val name: String,
    val address: String,
    val rating: Double?,
    val userRatingsTotal: Int,
    val distanceKm: Double,
    val lat: Double,
    val lng: Double,
    val isOpen: Boolean
)
