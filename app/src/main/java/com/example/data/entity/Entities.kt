package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val fuelType: String,
    val transmissionType: String, // CVT, DCT_DRY, DCT_WET, TORQUE_CONVERTER, MANUAL
    val engineCc: String,
    val chassisVin: String = "",
    val currentOdometer: Int,
    val recommendedViscosity: String = "5W-30",
    val isSevereDriving: Boolean = false,
    val dailyAvgKm: Int = 35,
    val oilLevelDropStatus: String = "NO_DROP", // NO_DROP, SLIGHT_DROP, HEAVY_DROP
    val isZeroKm: Boolean = false,
    val isSelected: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "maintenance_items")
data class MaintenanceItemEntity(
    @PrimaryKey val id: Long,
    val itemNameAr: String,
    val itemNameEn: String,
    val category: String, // OILS_FLUIDS, BELTS_ELEC, SUSPENSION_BRAKES, FILTERS_INTAKE
    val defaultKmInterval: Int,
    val defaultMonthInterval: Int,
    val isCritical: Boolean = false,
    val descriptionAr: String = "",
    val recommendedSpecAr: String = ""
)

@Entity(
    tableName = "car_maintenance_configs",
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaintenanceItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId"), Index("itemId")]
)
data class CarMaintenanceConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carId: Long,
    val itemId: Long,
    val lastChangeOdometer: Int,
    val lastChangeDateEpoch: Long,
    val customKmInterval: Int? = null,
    val nextDueOdometer: Int,
    val nextDueDateEpoch: Long
)

@Entity(
    tableName = "service_logs",
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId")]
)
data class ServiceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carId: Long,
    val itemId: Long,
    val performedOdometer: Int,
    val performedDateEpoch: Long,
    val cost: Double,
    val partBrand: String = "",
    val viscosityUsed: String = "",
    val workshopName: String = "",
    val notes: String = "",
    val invoiceImageUrl: String? = null
)

@Entity(
    tableName = "fuel_logs",
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId")]
)
data class FuelLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carId: Long,
    val dateEpoch: Long = System.currentTimeMillis(),
    val odometer: Int,
    val liters: Double,
    val totalPrice: Double,
    val fuelType: String = "92"
)
