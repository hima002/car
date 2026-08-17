package com.hima.alwarsha.data.repository

/** Generic engine-oil viscosity guidance by mileage band and observed oil consumption. */
object ViscosityEngine {
    fun calculate(currentOdometer: Int, oilLevelDropStatus: String): String {
        return when {
            currentOdometer < 100_000 -> "5W-30 تخليقي بالكامل (Fully Synthetic)"
            currentOdometer in 100_000..180_000 -> when (oilLevelDropStatus) {
                "SLIGHT_DROP" -> "5W-40 / 10W-40 نصف تخليقي"
                "HEAVY_DROP" -> "10W-40 / 15W-50 لمحركات عالية الكيلومترات"
                else -> "5W-30 / 5W-40 تخليقي (استمرار)"
            }
            else -> if (oilLevelDropStatus == "NO_DROP") {
                "5W-40 تخليقي (لحماية البساتم)"
            } else {
                "10W-40 / 15W-50 لمحركات عالية الكيلومترات"
            }
        }
    }
}
