package com.example.data.model

data class CarOemSpec(
    val brand: String,
    val model: String,
    val yearRange: String,
    val engineOilCapacityLiters: Double,
    val recommendedOilViscosity: String,
    val transFluidType: String,
    val transFluidCapacityLiters: Double,
    val tirePressurePsiFront: Int,
    val tirePressurePsiRear: Int,
    val fuelTankCapacityLiters: Int,
    val sparkPlugGapMm: String,
    val batterySpec: String,
    val oemOilFilterPart: String,
    val oemSparkPlugsPart: String,
    val oemTimingBeltPart: String,
    val oemBrakePadsFrontPart: String
)

data class ObdCode(
    val code: String,
    val titleAr: String,
    val titleEn: String,
    val category: String, // Engine, Transmission, Electrical, Emission
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val symptomsAr: String,
    val solutionAr: String
)

data class Workshop(
    val id: Long,
    val nameAr: String,
    val nameEn: String,
    val rating: Float,
    val reviewCount: Int,
    val specialtyBrands: List<String>,
    val categories: List<String>, // "زيوت وفلاتر", "فتيس ومحرك", "عفشة وفرامل", "كهرباء وتكييف"
    val areaAr: String,
    val phoneNumber: String,
    val isOpenNow: Boolean = true,
    val isVerifiedPartner: Boolean = false,
    val latitude: Double = 30.0444,
    val longitude: Double = 31.2357,
    val mapQuery: String = ""
)

data class CarMaintenanceItemStatus(
    val itemId: Long,
    val itemNameAr: String,
    val itemNameEn: String,
    val category: String,
    val isCritical: Boolean,
    val lastChangeOdometer: Int,
    val lastChangeDateEpoch: Long,
    val nextDueOdometer: Int,
    val nextDueDateEpoch: Long,
    val remainingKm: Int,
    val remainingDays: Int,
    val progressPercent: Float, // 0.0 (new) to 1.0 (overdue)
    val statusLevel: StatusLevel, // GREEN, YELLOW, RED
    val recommendedSpecAr: String
)

enum class StatusLevel {
    GREEN,   // Excellent condition
    YELLOW,  // Due soon (< 1000km or < 30 days)
    RED      // Overdue or immediate critical safety check
}

data class CarHealthSummary(
    val healthScore: Int, // 0 to 100
    val statusTextAr: String,
    val overallLevel: StatusLevel,
    val urgentAlerts: List<CarMaintenanceItemStatus>,
    val itemsByCategory: Map<String, List<CarMaintenanceItemStatus>>
)
