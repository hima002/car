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

    /**
     * The odometer alone always sets a baseline tier automatically — a high-mileage engine needs
     * a shorter interval regardless of whether the driver has ever reported noticing an oil drop.
     * A reported drop can only escalate to a *shorter* interval on top of that baseline, never
     * relax it back to a longer one — it's a manual "extra caution" signal, not the sole driver.
     */
    fun calculate(currentOdometer: Int, oilLevelDropStatus: String): ViscosityRecommendation {
        val baseline = when {
            currentOdometer > 200_000 -> OilType.HIGH_MILEAGE
            currentOdometer > 100_000 -> OilType.SEMI_SYNTHETIC
            else -> OilType.FULL_SYNTHETIC
        }

        // Enum declaration order is increasing severity (shorter interval), so ordinal comparison
        // picks whichever of baseline/escalation-target is stricter — never relaxes the baseline.
        val escalationTarget = when (oilLevelDropStatus) {
            "HEAVY_DROP" -> OilType.HIGH_MILEAGE
            "SLIGHT_DROP" -> OilType.SEMI_SYNTHETIC
            else -> baseline
        }
        val oilType = if (escalationTarget.ordinal > baseline.ordinal) escalationTarget else baseline

        val label = when (oilType) {
            OilType.FULL_SYNTHETIC -> "5W-30 تخليقي بالكامل (Fully Synthetic)"
            OilType.SEMI_SYNTHETIC -> "5W-40 / 10W-40 نصف تخليقي (Semi-Synthetic)"
            OilType.HIGH_MILEAGE -> "10W-40 / 15W-50 لمحركات عالية الكيلومترات (High Mileage)"
        }
        return ViscosityRecommendation(label, oilType)
    }
}
