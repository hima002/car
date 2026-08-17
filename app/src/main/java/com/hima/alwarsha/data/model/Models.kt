package com.hima.alwarsha.data.model

enum class StatusLevel { GREEN, YELLOW, RED }

data class CarMaintenanceItemStatus(
    val itemId: Long,
    val itemNameAr: String,
    val category: String,
    val isCritical: Boolean,
    val lastChangeOdometer: Int,
    val lastChangeDateEpoch: Long,
    val nextDueOdometer: Int,
    val remainingKm: Int,
    val remainingDays: Int,
    val progressPercent: Float,
    val statusLevel: StatusLevel,
    val recommendedSpecAr: String
)

data class CarHealthSummary(
    val healthScore: Int,
    val statusTextAr: String,
    val overallLevel: StatusLevel,
    val urgentAlerts: List<CarMaintenanceItemStatus>,
    val itemsByCategory: Map<String, List<CarMaintenanceItemStatus>>
)
