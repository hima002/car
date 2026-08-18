package com.hima.alwarsha.data.model

import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.CarMaintenanceConfigEntity
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.entity.TripLogEntity

/**
 * A full snapshot of the local database, exported as one JSON file the user saves anywhere they
 * choose (Google Drive, internal storage, etc.) via the system file picker — no account linking
 * or backend needed. [version] lets a future import path handle older backup files if the schema
 * changes later.
 */
data class BackupData(
    val version: Int = 1,
    val exportedAtEpoch: Long,
    val cars: List<CarEntity>,
    val maintenanceItems: List<MaintenanceItemEntity>,
    val configs: List<CarMaintenanceConfigEntity>,
    val serviceLogs: List<ServiceLogEntity>,
    val fuelLogs: List<FuelLogEntity>,
    val tripLogs: List<TripLogEntity>
)
