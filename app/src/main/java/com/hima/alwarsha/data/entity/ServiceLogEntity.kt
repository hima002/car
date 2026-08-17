package com.hima.alwarsha.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val cost: Double = 0.0,
    val partBrand: String = "",
    val workshopName: String = "",
    val notes: String = ""
)
