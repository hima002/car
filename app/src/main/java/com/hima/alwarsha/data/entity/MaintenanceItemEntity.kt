package com.hima.alwarsha.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_items")
data class MaintenanceItemEntity(
    @PrimaryKey val id: Long,
    val itemNameAr: String,
    val category: String, // OILS_FLUIDS, FILTERS_INTAKE, BELTS_ELEC, SUSPENSION_BRAKES
    val defaultKmInterval: Int,
    val defaultMonthInterval: Int,
    val isCritical: Boolean = false,
    val recommendedSpecAr: String = ""
)
