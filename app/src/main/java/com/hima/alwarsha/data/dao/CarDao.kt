package com.hima.alwarsha.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.CarMaintenanceConfigEntity
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.entity.TripLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    // Cars
    @Query("SELECT * FROM cars ORDER BY createdAtEpoch DESC")
    fun getAllCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE isSelected = 1 LIMIT 1")
    fun getSelectedCar(): Flow<CarEntity?>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Long): CarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity): Long

    @Update
    suspend fun updateCar(car: CarEntity)

    @Query("UPDATE cars SET isSelected = 0")
    suspend fun clearSelectedCars()

    @Query("UPDATE cars SET isSelected = 1 WHERE id = :carId")
    suspend fun setSelectedCarId(carId: Long)

    @Query("UPDATE cars SET currentOdometer = :newOdometer WHERE id = :carId")
    suspend fun updateCarOdometer(carId: Long, newOdometer: Int)

    @Query("UPDATE cars SET currentOdometer = currentOdometer + :deltaKm WHERE id = :carId")
    suspend fun incrementOdometer(carId: Long, deltaKm: Int)

    // Maintenance Items catalog
    @Query("SELECT * FROM maintenance_items")
    fun getAllMaintenanceItems(): Flow<List<MaintenanceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceItems(items: List<MaintenanceItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceItem(item: MaintenanceItemEntity)

    // Car <-> maintenance item configuration (per-car last/next tracking)
    @Query("SELECT * FROM car_maintenance_configs WHERE carId = :carId")
    fun getConfigsForCar(carId: Long): Flow<List<CarMaintenanceConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: CarMaintenanceConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<CarMaintenanceConfigEntity>)

    @Query("SELECT * FROM car_maintenance_configs WHERE carId = :carId AND itemId = :itemId LIMIT 1")
    suspend fun getConfig(carId: Long, itemId: Long): CarMaintenanceConfigEntity?

    // Service logs
    @Query("SELECT * FROM service_logs WHERE carId = :carId ORDER BY performedOdometer DESC, performedDateEpoch DESC")
    fun getServiceLogsForCar(carId: Long): Flow<List<ServiceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceLog(log: ServiceLogEntity): Long

    // Fuel logs
    @Query("SELECT * FROM fuel_logs WHERE carId = :carId ORDER BY odometer DESC")
    fun getFuelLogsForCar(carId: Long): Flow<List<FuelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLog(log: FuelLogEntity): Long

    // Trip logs (auto GPS tracking)
    @Query(
        """
        INSERT INTO trip_logs (carId, dayEpoch, distanceKm) VALUES (:carId, :dayEpoch, :deltaKm)
        ON CONFLICT(carId, dayEpoch) DO UPDATE SET distanceKm = distanceKm + :deltaKm
        """
    )
    suspend fun upsertTripDistance(carId: Long, dayEpoch: Long, deltaKm: Double)

    @Query("SELECT * FROM trip_logs WHERE carId = :carId AND dayEpoch >= :sinceEpoch ORDER BY dayEpoch DESC")
    fun getTripLogsSince(carId: Long, sinceEpoch: Long): Flow<List<TripLogEntity>>

    // Full-database snapshot for backup/restore (export/import), independent of the selected car.
    @Query("SELECT * FROM cars")
    suspend fun getAllCarsOnce(): List<CarEntity>

    @Query("SELECT * FROM maintenance_items")
    suspend fun getAllMaintenanceItemsOnce(): List<MaintenanceItemEntity>

    @Query("SELECT * FROM car_maintenance_configs")
    suspend fun getAllConfigsOnce(): List<CarMaintenanceConfigEntity>

    @Query("SELECT * FROM service_logs")
    suspend fun getAllServiceLogsOnce(): List<ServiceLogEntity>

    @Query("SELECT * FROM fuel_logs")
    suspend fun getAllFuelLogsOnce(): List<FuelLogEntity>

    @Query("SELECT * FROM trip_logs")
    suspend fun getAllTripLogsOnce(): List<TripLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceLogs(logs: List<ServiceLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLogs(logs: List<FuelLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripLogs(logs: List<TripLogEntity>)

    // Deleting cars/maintenance items cascades to their dependent configs/logs (see FK onDelete=CASCADE).
    @Query("DELETE FROM cars")
    suspend fun deleteAllCars()

    @Query("DELETE FROM maintenance_items")
    suspend fun deleteAllMaintenanceItems()
}
