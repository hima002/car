package com.example.data.repository

import com.example.data.dao.CarDao
import com.example.data.database.PrepopulateData
import com.example.data.entity.CarEntity
import com.example.data.entity.CarMaintenanceConfigEntity
import com.example.data.entity.FuelLogEntity
import com.example.data.entity.MaintenanceItemEntity
import com.example.data.entity.ServiceLogEntity
import com.example.data.entity.TripLogEntity
import com.example.data.model.CarHealthSummary
import com.example.data.model.CarMaintenanceItemStatus
import com.example.data.model.CarOemSpec
import com.example.data.model.ObdCode
import com.example.data.model.StatusLevel
import com.example.data.model.Workshop
import com.example.util.DayEpoch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CarRepository(private val carDao: CarDao) {

    val allCars: Flow<List<CarEntity>> = carDao.getAllCars()
    val selectedCar: Flow<CarEntity?> = carDao.getSelectedCar()
    val maintenanceCatalog: Flow<List<MaintenanceItemEntity>> = carDao.getAllMaintenanceItems()

    fun getServiceLogsForCar(carId: Long): Flow<List<ServiceLogEntity>> {
        return carDao.getServiceLogsForCar(carId)
    }

    fun getFuelLogsForCar(carId: Long): Flow<List<FuelLogEntity>> {
        return carDao.getFuelLogsForCar(carId)
    }

    fun getTripLogsForCar(carId: Long, sinceDaysAgo: Int = 30): Flow<List<TripLogEntity>> {
        return carDao.getTripLogsSince(carId, DayEpoch.daysAgo(sinceDaysAgo))
    }

    /**
     * Real measured daily driving average from GPS trip logs (last 30 days),
     * falling back to the manually-set [CarEntity.dailyAvgKm] until at least
     * 3 days of tracked data exist. Averaged over calendar days elapsed (not
     * just driving days) so weekends/idle days correctly pull the rate down.
     */
    fun getDailyAvgKmFlow(carId: Long): Flow<Int?> {
        return carDao.getTripLogsSince(carId, DayEpoch.daysAgo(30)).map { logs ->
            if (logs.size < 3) {
                null
            } else {
                val totalKm = logs.sumOf { it.distanceKm }
                val earliestDay = logs.minOf { it.dayEpoch }
                val daysSpan = max(
                    logs.size,
                    ((DayEpoch.startOfDay() - earliestDay) / 86_400_000L).toInt() + 1
                )
                max(1, (totalKm / daysSpan).roundToInt())
            }
        }
    }

    suspend fun recordAutoDrivingDistance(carId: Long, deltaKm: Double) {
        if (deltaKm <= 0.0) return
        carDao.incrementOdometer(carId, deltaKm.roundToInt())
        carDao.upsertTripDistance(carId, DayEpoch.startOfDay(), deltaKm)
    }

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

            val itemStatuses = mutableListOf<CarMaintenanceItemStatus>()

            for (config in configs) {
                val item = itemMap[config.itemId] ?: continue

                var effectiveIntervalKm = config.customKmInterval ?: item.defaultKmInterval
                // Severe driving factor: 15% reduction
                if (car.isSevereDriving) {
                    effectiveIntervalKm = (effectiveIntervalKm * 0.85).toInt()
                }

                val lastKm = config.lastChangeOdometer
                val nextKm = lastKm + effectiveIntervalKm
                val remainingKm = nextKm - car.currentOdometer

                val remainingDays = max(0, remainingKm / max(1, dailyAvgKm))

                val kmUsed = car.currentOdometer - lastKm
                val progressPercent = min(1.0f, max(0.0f, kmUsed.toFloat() / max(1, effectiveIntervalKm).toFloat()))

                val statusLevel = when {
                    remainingKm <= 0 || (item.isCritical && remainingKm < 500) -> StatusLevel.RED
                    remainingKm <= 1000 || remainingDays <= 30 -> StatusLevel.YELLOW
                    else -> StatusLevel.GREEN
                }

                itemStatuses.add(
                    CarMaintenanceItemStatus(
                        itemId = item.id,
                        itemNameAr = item.itemNameAr,
                        itemNameEn = item.itemNameEn,
                        category = item.category,
                        isCritical = item.isCritical,
                        lastChangeOdometer = lastKm,
                        lastChangeDateEpoch = config.lastChangeDateEpoch,
                        nextDueOdometer = nextKm,
                        nextDueDateEpoch = config.nextDueDateEpoch,
                        remainingKm = remainingKm,
                        remainingDays = remainingDays,
                        progressPercent = progressPercent,
                        statusLevel = statusLevel,
                        recommendedSpecAr = item.recommendedSpecAr
                    )
                )
            }

            // Calculate overall health score (100 = perfect, lower for overdue items)
            var score = 100
            for (status in itemStatuses) {
                if (status.statusLevel == StatusLevel.RED) {
                    score -= if (status.isCritical) 25 else 12
                } else if (status.statusLevel == StatusLevel.YELLOW) {
                    score -= if (status.isCritical) 10 else 5
                }
            }
            score = max(0, min(100, score))

            val overallLevel = when {
                score >= 80 -> StatusLevel.GREEN
                score >= 50 -> StatusLevel.YELLOW
                else -> StatusLevel.RED
            }

            val statusTextAr = when (overallLevel) {
                StatusLevel.GREEN -> "ممتاز - جميع أجزاء السيارة بحالة جيدة جداً"
                StatusLevel.YELLOW -> "تنبيه - اقتراب موعد صيانات استهلاكية"
                StatusLevel.RED -> "تحذير حرج - تجاوزت حدود الصيانة الدورية!"
            }

            val urgentAlerts = itemStatuses
                .filter { it.statusLevel == StatusLevel.RED || it.statusLevel == StatusLevel.YELLOW }
                .sortedBy { it.remainingKm }

            val itemsByCategory = itemStatuses.groupBy { it.category }

            CarHealthSummary(
                healthScore = score,
                statusTextAr = statusTextAr,
                overallLevel = overallLevel,
                urgentAlerts = urgentAlerts,
                itemsByCategory = itemsByCategory
            )
        }
    }

    suspend fun addNewVehicle(
        brand: String,
        model: String,
        year: Int,
        fuelType: String,
        transmissionType: String,
        engineCc: String,
        chassisVin: String,
        currentOdometer: Int,
        isZeroKm: Boolean,
        isSevereDriving: Boolean,
        safetyResetAll: Boolean
    ): Long {

        val initialViscosity = calculateViscosityRecommendation(currentOdometer, "NO_DROP")

        val newCar = CarEntity(
            brand = brand,
            model = model,
            year = year,
            fuelType = fuelType,
            transmissionType = transmissionType,
            engineCc = engineCc,
            chassisVin = chassisVin,
            currentOdometer = currentOdometer,
            recommendedViscosity = initialViscosity,
            isSevereDriving = isSevereDriving,
            dailyAvgKm = 35,
            oilLevelDropStatus = "NO_DROP",
            isZeroKm = isZeroKm,
            isSelected = true
        )

        carDao.clearSelectedCars()
        val carId = carDao.insertCar(newCar)

        // Setup maintenance configurations based on items catalog
        val defaultItems = PrepopulateData.defaultMaintenanceItems
        val newConfigs = mutableListOf<CarMaintenanceConfigEntity>()

        for (item in defaultItems) {
            val lastOdo = if (isZeroKm) {
                0
            } else if (safetyResetAll) {
                // Safety Baseline Reset: mark critical parts as needing immediate check/change
                currentOdometer - item.defaultKmInterval
            } else {
                currentOdometer
            }

            val nextDueOdo = lastOdo + item.defaultKmInterval

            newConfigs.add(
                CarMaintenanceConfigEntity(
                    carId = carId,
                    itemId = item.id,
                    lastChangeOdometer = lastOdo,
                    lastChangeDateEpoch = System.currentTimeMillis(),
                    customKmInterval = null,
                    nextDueOdometer = nextDueOdo,
                    nextDueDateEpoch = System.currentTimeMillis() + (item.defaultMonthInterval * 30 * 86400000L)
                )
            )
        }

        carDao.insertConfigs(newConfigs)
        return carId
    }

    suspend fun selectCar(carId: Long) {
        carDao.clearSelectedCars()
        carDao.setSelectedCarId(carId)
    }

    suspend fun updateOdometer(carId: Long, newOdometer: Int) {
        carDao.updateCarOdometer(carId, newOdometer)
    }

    suspend fun updateViscosityDecision(carId: Long, oilDropStatus: String, chosenViscosity: String) {
        val car = carDao.getCarById(carId) ?: return
        val updated = car.copy(
            oilLevelDropStatus = oilDropStatus,
            recommendedViscosity = chosenViscosity
        )
        carDao.updateCar(updated)
    }

    suspend fun toggleSevereDriving(carId: Long, isSevere: Boolean) {
        val car = carDao.getCarById(carId) ?: return
        carDao.updateCar(car.copy(isSevereDriving = isSevere))
    }

    suspend fun recordServiceLog(
        carId: Long,
        itemId: Long,
        performedOdometer: Int,
        cost: Double,
        partBrand: String,
        viscosityUsed: String,
        workshopName: String,
        notes: String
    ) {
        val log = ServiceLogEntity(
            carId = carId,
            itemId = itemId,
            performedOdometer = performedOdometer,
            performedDateEpoch = System.currentTimeMillis(),
            cost = cost,
            partBrand = partBrand,
            viscosityUsed = viscosityUsed,
            workshopName = workshopName,
            notes = notes
        )
        carDao.insertServiceLog(log)

        // Also update the car's maintenance config for this item
        val car = carDao.getCarById(carId) ?: return
        val items = PrepopulateData.defaultMaintenanceItems.associateBy { it.id }
        val item = items[itemId]
        val intervalKm = item?.defaultKmInterval ?: 10000

        val nextDueOdo = performedOdometer + intervalKm

        // Fetch existing config and update or insert
        val existingConfig = CarMaintenanceConfigEntity(
            carId = carId,
            itemId = itemId,
            lastChangeOdometer = performedOdometer,
            lastChangeDateEpoch = System.currentTimeMillis(),
            nextDueOdometer = nextDueOdo,
            nextDueDateEpoch = System.currentTimeMillis() + ((item?.defaultMonthInterval ?: 12) * 30 * 86400000L)
        )
        carDao.insertConfig(existingConfig)

        // If performed odometer is higher than current car odometer, update car odometer
        if (performedOdometer > car.currentOdometer) {
            carDao.updateCarOdometer(carId, performedOdometer)
        }
    }

    suspend fun recordFuelLog(
        carId: Long,
        odometer: Int,
        liters: Double,
        totalPrice: Double,
        fuelType: String
    ) {
        val log = FuelLogEntity(
            carId = carId,
            dateEpoch = System.currentTimeMillis(),
            odometer = odometer,
            liters = liters,
            totalPrice = totalPrice,
            fuelType = fuelType
        )
        carDao.insertFuelLog(log)
    }

    fun calculateViscosityRecommendation(currentOdometer: Int, oilLevelDropStatus: String): String {
        return when {
            currentOdometer < 100000 -> {
                "5W-30 Fully Synthetic (تخليقي بالكامل)"
            }
            currentOdometer in 100000..180000 -> {
                if (oilLevelDropStatus == "SLIGHT_DROP") {
                    "5W-40 / 10W-40 Semi-Synthetic (نصف تخليقي)"
                } else if (oilLevelDropStatus == "HEAVY_DROP") {
                    "10W-40 / 15W-50 High Mileage (محركات عالية الكيلومترات)"
                } else {
                    "5W-30 / 5W-40 Synthetic (استمرار اللزوجة التخليقية)"
                }
            }
            else -> {
                if (oilLevelDropStatus == "NO_DROP") {
                    "5W-40 Synthetic (لحماية البساتن مع الاحتفاظ بالزوجة التخليقية)"
                } else {
                    "10W-40 / 15W-50 High Mileage Oils (زيوت المحركات المرتفعة لحماية الحشوات وتقليل الصوت)"
                }
            }
        }
    }

    fun getOemSpecsForCar(brand: String, model: String): CarOemSpec? {
        return PrepopulateData.oemSpecs.find {
            it.brand.equals(brand, ignoreCase = true) && it.model.contains(model, ignoreCase = true)
        } ?: PrepopulateData.oemSpecs.firstOrNull()
    }

    fun getObdCodes(): List<ObdCode> = PrepopulateData.obdCodes

    fun getWorkshops(): List<Workshop> = PrepopulateData.workshops

    suspend fun addCustomMaintenanceReminder(
        carId: Long,
        titleAr: String,
        category: String,
        targetOdometer: Int,
        daysAhead: Int,
        isCritical: Boolean,
        notes: String
    ): Long {
        val itemId = System.currentTimeMillis()
        val newItem = MaintenanceItemEntity(
            id = itemId,
            itemNameAr = titleAr,
            itemNameEn = titleAr,
            category = category,
            defaultKmInterval = 10000,
            defaultMonthInterval = 6,
            isCritical = isCritical,
            descriptionAr = notes,
            recommendedSpecAr = if (notes.isNotBlank()) notes else "تذكير صيانة مخصص تم إنشاؤه يدويًا"
        )
        carDao.insertMaintenanceItem(newItem)

        val car = carDao.getCarById(carId)
        val currentOdo = car?.currentOdometer ?: 0
        val intervalKm = maxOf(1000, targetOdometer - currentOdo)
        val lastChangeOdo = maxOf(0, targetOdometer - intervalKm)

        val nextDueDate = System.currentTimeMillis() + (daysAhead * 86400000L)

        val config = CarMaintenanceConfigEntity(
            carId = carId,
            itemId = itemId,
            lastChangeOdometer = lastChangeOdo,
            lastChangeDateEpoch = System.currentTimeMillis(),
            customKmInterval = intervalKm,
            nextDueOdometer = targetOdometer,
            nextDueDateEpoch = nextDueDate
        )
        carDao.insertConfig(config)
        return itemId
    }
}
