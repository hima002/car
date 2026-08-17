package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CarEntity
import com.example.data.entity.CarMaintenanceConfigEntity
import com.example.data.entity.FuelLogEntity
import com.example.data.entity.MaintenanceItemEntity
import com.example.data.entity.ServiceLogEntity
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

    @Delete
    suspend fun deleteCar(car: CarEntity)

    @Query("UPDATE cars SET isSelected = 0")
    suspend fun clearSelectedCars()

    @Query("UPDATE cars SET isSelected = 1 WHERE id = :carId")
    suspend fun setSelectedCarId(carId: Long)

    @Query("UPDATE cars SET currentOdometer = :newOdometer WHERE id = :carId")
    suspend fun updateCarOdometer(carId: Long, newOdometer: Int)

    // Maintenance Items
    @Query("SELECT * FROM maintenance_items")
    fun getAllMaintenanceItems(): Flow<List<MaintenanceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceItem(item: MaintenanceItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceItems(items: List<MaintenanceItemEntity>)

    // Car Maintenance Configurations
    @Query("SELECT * FROM car_maintenance_configs WHERE carId = :carId")
    fun getConfigsForCar(carId: Long): Flow<List<CarMaintenanceConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: CarMaintenanceConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<CarMaintenanceConfigEntity>)

    @Update
    suspend fun updateConfig(config: CarMaintenanceConfigEntity)

    // Service Logs
    @Query("SELECT * FROM service_logs WHERE carId = :carId ORDER BY performedOdometer DESC, performedDateEpoch DESC")
    fun getServiceLogsForCar(carId: Long): Flow<List<ServiceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceLog(log: ServiceLogEntity): Long

    @Query("DELETE FROM service_logs WHERE id = :logId")
    suspend fun deleteServiceLog(logId: Long)

    // Fuel Logs
    @Query("SELECT * FROM fuel_logs WHERE carId = :carId ORDER BY odometer DESC")
    fun getFuelLogsForCar(carId: Long): Flow<List<FuelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLog(log: FuelLogEntity): Long
}
