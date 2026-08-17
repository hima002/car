package com.hima.alwarsha.data.repository

/**
 * Oil category, used both to display a recommendation and to drive the actual change interval
 * (real change intervals track oil type — full synthetic tolerates far more km than mineral oil —
 * not an arbitrary odometer curve). Manufacturer-typical km bands per type:
 * full synthetic ~10,000km, semi-synthetic ~7,500km, high-mileage/mineral ~5,000km. These are
 * generic manufacturer-typical figures, not a substitute for your car's actual manual spec.
 */
enum class OilType(val intervalKm: Int) {
    FULL_SYNTHETIC(10_000),
    SEMI_SYNTHETIC(7_500),
    HIGH_MILEAGE(5_000)
}

data class ViscosityRecommendation(val label: String, val oilType: OilType)

object ViscosityEngine {
    fun calculate(currentOdometer: Int, oilLevelDropStatus: String): ViscosityRecommendation {
        return when {
            currentOdometer < 100_000 -> ViscosityRecommendation(
                "5W-30 تخليقي بالكامل (Fully Synthetic)", OilType.FULL_SYNTHETIC
            )
            currentOdometer in 100_000..180_000 -> when (oilLevelDropStatus) {
                "SLIGHT_DROP" -> ViscosityRecommendation("5W-40 / 10W-40 نصف تخليقي", OilType.SEMI_SYNTHETIC)
                "HEAVY_DROP" -> ViscosityRecommendation("10W-40 / 15W-50 لمحركات عالية الكيلومترات", OilType.HIGH_MILEAGE)
                else -> ViscosityRecommendation("5W-30 / 5W-40 تخليقي (استمرار)", OilType.FULL_SYNTHETIC)
            }
            else -> if (oilLevelDropStatus == "NO_DROP") {
                ViscosityRecommendation("5W-40 تخليقي (لحماية البساتم)", OilType.FULL_SYNTHETIC)
            } else {
                ViscosityRecommendation("10W-40 / 15W-50 لمحركات عالية الكيلومترات", OilType.HIGH_MILEAGE)
            }
        }
    }
}
