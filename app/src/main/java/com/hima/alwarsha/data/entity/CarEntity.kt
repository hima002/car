package com.hima.alwarsha.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val transmissionType: String, // CVT, DCT_DRY, DCT_WET, TORQUE_CONVERTER, MANUAL
    val engineCc: String = "",
    val currentOdometer: Int,
    val dailyAvgKm: Int = 35,
    val isSevereDriving: Boolean = false,
    val recommendedViscosity: String = "5W-30 تخليقي بالكامل",
    val oilType: String = "FULL_SYNTHETIC", // FULL_SYNTHETIC, SEMI_SYNTHETIC, HIGH_MILEAGE — drives the oil-change interval
    val oilLevelDropStatus: String = "NO_DROP", // NO_DROP, SLIGHT_DROP, HEAVY_DROP
    val isSelected: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
