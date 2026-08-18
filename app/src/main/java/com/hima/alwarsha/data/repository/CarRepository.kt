package com.hima.alwarsha.data.repository

import com.hima.alwarsha.data.dao.CarDao
import com.hima.alwarsha.data.database.DefaultMaintenanceCatalog
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.CarMaintenanceConfigEntity
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.entity.TripLogEntity
import com.hima.alwarsha.data.model.BackupData
import com.hima.alwarsha.data.model.CarHealthSummary
import com.hima.alwarsha.data.model.CarMaintenanceItemStatus
import com.hima.alwarsha.data.model.StatusLevel
import com.hima.alwarsha.util.DayEpoch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CarRepository(private val carDao: CarDao) {

    val allCars: Flow<List<CarEntity>> = carDao.getAllCars()
    val selectedCar: Flow<CarEntity?> = carDao.getSelectedCar()
    val maintenanceCatalog: Flow<List<MaintenanceItemEntity>> = carDao.getAllMaintenanceItems()

    fun getServiceLogsForCar(carId: Long): Flow<List<ServiceLogEntity>> = carDao.getServiceLogsForCar(carId)

    fun getFuelLogsForCar(carId: Long): Flow<List<FuelLogEntity>> = carDao.getFuelLogsForCar(carId)

    fun getTripLogsForCar(carId: Long, sinceDaysAgo: Int = 30): Flow<List<TripLogEntity>> =
        carDao.getTripLogsSince(carId, DayEpoch.daysAgo(sinceDaysAgo))

    /**
     * Real measured daily driving average from GPS trip logs (last 30 days), null until at
     * least 3 days of tracked data exist so callers can fall back to a manual estimate.
     * Averaged over calendar days elapsed (not just driving days) so idle days pull the rate down.
     */
    fun getDailyAvgKmFlow(carId: Long): Flow<Int?> {
        return carDao.getTripLogsSince(carId, DayEpoch.daysAgo(30)).map { logs ->
            if (logs.size < 3) {
                null
            } else {
                val totalKm = logs.sumOf { it.distanceKm }
                val earliestDay = logs.minOf { it.dayEpoch }
                val daysSpan = max(logs.size, ((DayEpoch.startOfDay() - earliestDay) / 86_400_000L).toInt() + 1)
                max(1, (totalKm / daysSpan).roundToInt())
            }
        }
    }

    suspend fun recordAutoDrivingDistance(carId: Long, deltaKm: Double) {
        if (deltaKm <= 0.0) return
        carDao.incrementOdometer(carId, deltaKm.roundToInt())
        carDao.upsertTripDistance(carId, DayEpoch.startOfDay(), deltaKm)
    }

    /**
     * Transmission-fluid interval depends on gearbox type, and the engine-oil interval depends on
     * the actual oil type in use (real change intervals track oil chemistry, not odometer bands —
     * see [OilType]) — neither is a single fixed default.
     */
    private fun effectiveIntervalKm(car: CarEntity, item: MaintenanceItemEntity, config: CarMaintenanceConfigEntity): Int {
        config.customKmInterval?.let { return it }
        if (item.id == DefaultMaintenanceCatalog.TRANSMISSION_FLUID_ITEM_ID) {
            return when (car.transmissionType) {
                "CVT", "DCT_DRY", "DCT_WET" -> 40_000
                else -> 60_000
            }
        }
        if (item.id == DefaultMaintenanceCatalog.ENGINE_OIL_ITEM_ID) {
            return runCatching { OilType.valueOf(car.oilType) }.getOrDefault(OilType.FULL_SYNTHETIC).intervalKm
        }
        return item.defaultKmInterval
    }

    /** One-shot snapshot of [getCarHealthSummary], for callers outside Compose (e.g. the tracking service). */
    suspend fun getCarHealthSummarySnapshot(carId: Long): CarHealthSummary? = getCarHealthSummary(carId).first()

    fun getCarHealthSummary(carId: Long): Flow<CarHealthSummary?> {
        return combine(
            carDao.getAllCars(),
            carDao.getAllMaintenanceItems(),
            carDao.getConfigsForCar(carId),
            getDailyAvgKmFlow(carId)
        ) { cars, items, configs, autoDailyAvgKm ->
            val car = cars.find { it.id == carId } ?: return@combine null
            val dailyAvgKm = autoDailyAvgKm ?: max(1, car.dailyAvgKm)
            val itemMap = items.associateBy { it.id }

            val itemStatuses = configs.mapNotNull { config ->
                val item = itemMap[config.itemId] ?: return@mapNotNull null

                var intervalKm = effectiveIntervalKm(car, item, config)
                if (car.isSevereDriving) intervalKm = (intervalKm * 0.85).toInt()

                val lastKm = config.lastChangeOdometer
                val nextKm = lastKm + intervalKm
                val remainingKm = nextKm - car.currentOdometer
                val remainingDays = max(0, remainingKm / dailyAvgKm)

                val kmUsed = car.currentOdometer - lastKm
                val progressPercent = min(1f, max(0f, kmUsed.toFloat() / max(1, intervalKm).toFloat()))

                val statusLevel = when {
                    remainingKm <= 0 || (item.isCritical && remainingKm < 500) -> StatusLevel.RED
                    remainingKm <= 1000 || remainingDays <= 30 -> StatusLevel.YELLOW
                    else -> StatusLevel.GREEN
                }

                CarMaintenanceItemStatus(
                    itemId = item.id,
                    itemNameAr = item.itemNameAr,
                    category = item.category,
                    isCritical = item.isCritical,
                    lastChangeOdometer = lastKm,
                    lastChangeDateEpoch = config.lastChangeDateEpoch,
                    nextDueOdometer = nextKm,
                    remainingKm = remainingKm,
                    remainingDays = remainingDays,
                    progressPercent = progressPercent,
                    statusLevel = statusLevel,
                    recommendedSpecAr = item.recommendedSpecAr
                )
            }

            var score = 100
            for (status in itemStatuses) {
                when (status.statusLevel) {
                    StatusLevel.RED -> score -= if (status.isCritical) 25 else 12
                    StatusLevel.YELLOW -> score -= if (status.isCritical) 10 else 5
                    StatusLevel.GREEN -> {}
                }
            }
            score = max(0, min(100, score))

            val overallLevel = when {
                score >= 80 -> StatusLevel.GREEN
                score >= 50 -> StatusLevel.YELLOW
                else -> StatusLevel.RED
            }
            val statusTextAr = when (overallLevel) {
                StatusLevel.GREEN -> "ممتاز - جميع أجزاء السيارة بحالة جيدة"
                StatusLevel.YELLOW -> "تنبيه - اقتراب موعد صيانات استهلاكية"
                StatusLevel.RED -> "تحذير حرج - تجاوزت حدود الصيانة الدورية"
            }

            CarHealthSummary(
                healthScore = score,
                statusTextAr = statusTextAr,
                overallLevel = overallLevel,
                urgentAlerts = itemStatuses
                    .filter { it.statusLevel == StatusLevel.RED || it.statusLevel == StatusLevel.YELLOW }
                    .sortedBy { it.remainingKm },
                itemsByCategory = itemStatuses.groupBy { it.category }
            )
        }
    }

    /**
     * Registers a new car plus its maintenance baselines. [itemBaselines] maps a catalog item id
     * to the odometer reading it was last actually changed at; items missing from the map are
     * assumed "just done now" (baseline = currentOdometer) rather than guessed as overdue.
     */
    suspend fun addNewVehicle(
        brand: String,
        model: String,
        year: Int,
        transmissionType: String,
        engineCc: String,
        engineVariant: String = "",
        currentOdometer: Int,
        isSevereDriving: Boolean,
        selectedItemIds: Set<Long>,
        itemBaselines: Map<Long, Int>
    ): Long {
        val recommendation = ViscosityEngine.calculate(currentOdometer, "NO_DROP")
        val newCar = CarEntity(
            brand = brand,
            model = model,
            year = year,
            transmissionType = transmissionType,
            engineCc = engineCc,
            engineVariant = engineVariant,
            currentOdometer = currentOdometer,
            recommendedViscosity = recommendation.label,
            oilType = recommendation.oilType.name,
            isSevereDriving = isSevereDriving,
            isSelected = true
        )
        carDao.clearSelectedCars()
        val carId = carDao.insertCar(newCar)

        val configs = selectedItemIds.map { itemId ->
            val lastOdo = itemBaselines[itemId] ?: currentOdometer
            CarMaintenanceConfigEntity(
                carId = carId,
                itemId = itemId,
                lastChangeOdometer = lastOdo,
                lastChangeDateEpoch = System.currentTimeMillis()
            )
        }
        carDao.insertConfigs(configs)
        return carId
    }

    suspend fun selectCar(carId: Long) {
        carDao.clearSelectedCars()
        carDao.setSelectedCarId(carId)
    }

    suspend fun updateOdometer(carId: Long, newOdometer: Int) {
        carDao.updateCarOdometer(carId, newOdometer)
    }

    suspend fun updateViscosityDecision(carId: Long, oilDropStatus: String, recommendation: ViscosityRecommendation) {
        val car = carDao.getCarById(carId) ?: return
        carDao.updateCar(
            car.copy(
                oilLevelDropStatus = oilDropStatus,
                recommendedViscosity = recommendation.label,
                oilType = recommendation.oilType.name
            )
        )
    }

    /** Lets the user override a single item's interval for this car (null clears the override). */
    suspend fun updateCustomInterval(carId: Long, itemId: Long, customKmInterval: Int?) {
        val existing = carDao.getConfig(carId, itemId) ?: return
        carDao.insertConfig(existing.copy(customKmInterval = customKmInterval))
    }

    /** Snapshot of every table for a manual backup file the user saves wherever they choose. */
    suspend fun exportBackup(): BackupData = BackupData(
        exportedAtEpoch = System.currentTimeMillis(),
        cars = carDao.getAllCarsOnce(),
        maintenanceItems = carDao.getAllMaintenanceItemsOnce(),
        configs = carDao.getAllConfigsOnce(),
        serviceLogs = carDao.getAllServiceLogsOnce(),
        fuelLogs = carDao.getAllFuelLogsOnce(),
        tripLogs = carDao.getAllTripLogsOnce()
    )

    /**
     * Wipes the current database and replaces it with the backup's contents. Deleting cars/items
     * first cascades away any dependent configs/logs (FK onDelete=CASCADE), so what follows is a
     * clean insert with no leftover/duplicate rows from before the restore.
     */
    suspend fun importBackup(data: BackupData) {
        carDao.deleteAllCars()
        carDao.deleteAllMaintenanceItems()

        carDao.insertMaintenanceItems(data.maintenanceItems)
        carDao.insertCars(data.cars)
        carDao.insertConfigs(data.configs)
        carDao.insertServiceLogs(data.serviceLogs)
        carDao.insertFuelLogs(data.fuelLogs)
        carDao.insertTripLogs(data.tripLogs)
    }

    suspend fun toggleSevereDriving(carId: Long, isSevere: Boolean) {
        val car = carDao.getCarById(carId) ?: return
        carDao.updateCar(car.copy(isSevereDriving = isSevere))
    }

    suspend fun recordServiceLog(
        carId: Long,
        itemId: Long,
        performedOdometer: Int,
        cost: Double = 0.0,
        partBrand: String = "",
        workshopName: String = "",
        notes: String = ""
    ) {
        carDao.insertServiceLog(
            ServiceLogEntity(
                carId = carId,
                itemId = itemId,
                performedOdometer = performedOdometer,
                performedDateEpoch = System.currentTimeMillis(),
                cost = cost,
                partBrand = partBrand,
                workshopName = workshopName,
                notes = notes
            )
        )

        val existing = carDao.getConfig(carId, itemId)
        carDao.insertConfig(
            CarMaintenanceConfigEntity(
                id = existing?.id ?: 0,
                carId = carId,
                itemId = itemId,
                lastChangeOdometer = performedOdometer,
                lastChangeDateEpoch = System.currentTimeMillis(),
                customKmInterval = existing?.customKmInterval
            )
        )

        val car = carDao.getCarById(carId) ?: return
        if (performedOdometer > car.currentOdometer) {
            carDao.updateCarOdometer(carId, performedOdometer)
        }
    }

    suspend fun recordFuelLog(carId: Long, odometer: Int, liters: Double, totalPrice: Double) {
        carDao.insertFuelLog(
            FuelLogEntity(carId = carId, odometer = odometer, liters = liters, totalPrice = totalPrice)
        )
    }

    suspend fun addCustomMaintenanceItem(
        carId: Long,
        titleAr: String,
        category: String,
        kmInterval: Int,
        monthInterval: Int,
        isCritical: Boolean,
        currentOdometer: Int
    ) {
        val itemId = System.currentTimeMillis()
        carDao.insertMaintenanceItem(
            MaintenanceItemEntity(
                id = itemId,
                itemNameAr = titleAr,
                category = category,
                defaultKmInterval = kmInterval,
                defaultMonthInterval = monthInterval,
                isCritical = isCritical
            )
        )
        carDao.insertConfig(
            CarMaintenanceConfigEntity(
                carId = carId,
                itemId = itemId,
                lastChangeOdometer = currentOdometer,
                lastChangeDateEpoch = System.currentTimeMillis()
            )
        )
    }

}
