package com.hima.alwarsha.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hima.alwarsha.data.database.AppDatabase
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.entity.TripLogEntity
import com.hima.alwarsha.data.model.CarHealthSummary
import com.hima.alwarsha.data.repository.CarRepository
import com.hima.alwarsha.data.repository.ViscosityRecommendation
import com.hima.alwarsha.util.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CarRepository(database.carDao())

    val allCars: StateFlow<List<CarEntity>> = repository.allCars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCar: StateFlow<CarEntity?> = repository.selectedCar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val maintenanceCatalog: StateFlow<List<MaintenanceItemEntity>> = repository.maintenanceCatalog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val carHealthSummary: StateFlow<CarHealthSummary?> = selectedCar
        .flatMapLatest { car -> if (car != null) repository.getCarHealthSummary(car.id) else flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val serviceLogs: StateFlow<List<ServiceLogEntity>> = selectedCar
        .flatMapLatest { car -> if (car != null) repository.getServiceLogsForCar(car.id) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelLogs: StateFlow<List<FuelLogEntity>> = selectedCar
        .flatMapLatest { car -> if (car != null) repository.getFuelLogsForCar(car.id) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentTripLogs: StateFlow<List<TripLogEntity>> = selectedCar
        .flatMapLatest { car -> if (car != null) repository.getTripLogsForCar(car.id, sinceDaysAgo = 7) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog visibility state
    private val _showUpdateOdometerDialog = MutableStateFlow(false)
    val showUpdateOdometerDialog: StateFlow<Boolean> = _showUpdateOdometerDialog.asStateFlow()

    private val _showLogServiceDialog = MutableStateFlow(false)
    val showLogServiceDialog: StateFlow<Boolean> = _showLogServiceDialog.asStateFlow()

    private val _showLogFuelDialog = MutableStateFlow(false)
    val showLogFuelDialog: StateFlow<Boolean> = _showLogFuelDialog.asStateFlow()

    private val _selectedItemIdForLog = MutableStateFlow<Long?>(null)
    val selectedItemIdForLog: StateFlow<Long?> = _selectedItemIdForLog.asStateFlow()

    fun openUpdateOdometerDialog() { _showUpdateOdometerDialog.value = true }
    fun closeUpdateOdometerDialog() { _showUpdateOdometerDialog.value = false }

    fun openLogServiceDialog(itemId: Long? = null) {
        _selectedItemIdForLog.value = itemId
        _showLogServiceDialog.value = true
    }
    fun closeLogServiceDialog() { _showLogServiceDialog.value = false }

    fun openLogFuelDialog() { _showLogFuelDialog.value = true }
    fun closeLogFuelDialog() { _showLogFuelDialog.value = false }

    fun selectCar(carId: Long) {
        viewModelScope.launch { repository.selectCar(carId) }
    }

    fun updateOdometer(newOdometer: Int, context: Context? = null) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.updateOdometer(car.id, newOdometer)
            closeUpdateOdometerDialog()
            context?.let { checkNotificationsNow(it) }
        }
    }

    fun checkNotificationsNow(context: Context) {
        val car = selectedCar.value ?: return
        NotificationHelper.checkAndNotifyMaintenance(context, carHealthSummary.value, "${car.brand} ${car.model}")
    }

    fun updateCustomInterval(itemId: Long, customKmInterval: Int?) {
        val car = selectedCar.value ?: return
        viewModelScope.launch { repository.updateCustomInterval(car.id, itemId, customKmInterval) }
    }

    fun toggleSevereDriving(isSevere: Boolean) {
        val car = selectedCar.value ?: return
        viewModelScope.launch { repository.toggleSevereDriving(car.id, isSevere) }
    }

    fun applyViscosityDecision(oilDropStatus: String, recommendation: ViscosityRecommendation) {
        val car = selectedCar.value ?: return
        viewModelScope.launch { repository.updateViscosityDecision(car.id, oilDropStatus, recommendation) }
    }

    fun recordServiceLog(itemId: Long, performedOdometer: Int, cost: Double, partBrand: String, workshopName: String, notes: String) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.recordServiceLog(car.id, itemId, performedOdometer, cost, partBrand, workshopName, notes)
            closeLogServiceDialog()
        }
    }

    /** One-tap logging for the dashboard hero card: uses the current odometer, no extra details. */
    fun recordServiceLogQuick(itemId: Long) {
        val car = selectedCar.value ?: return
        viewModelScope.launch { repository.recordServiceLog(car.id, itemId, car.currentOdometer) }
    }

    fun recordFuelLog(odometer: Int, liters: Double, totalPrice: Double) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.recordFuelLog(car.id, odometer, liters, totalPrice)
            closeLogFuelDialog()
        }
    }

    fun addNewVehicle(
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
    ) {
        viewModelScope.launch {
            repository.addNewVehicle(
                brand = brand, model = model, year = year, transmissionType = transmissionType,
                engineCc = engineCc, engineVariant = engineVariant, currentOdometer = currentOdometer,
                isSevereDriving = isSevereDriving, selectedItemIds = selectedItemIds, itemBaselines = itemBaselines
            )
        }
    }

    fun addCustomMaintenanceItem(titleAr: String, category: String, kmInterval: Int, monthInterval: Int, isCritical: Boolean) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.addCustomMaintenanceItem(car.id, titleAr, category, kmInterval, monthInterval, isCritical, car.currentOdometer)
        }
    }
}
