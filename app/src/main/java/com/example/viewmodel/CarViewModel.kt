package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.CarEntity
import com.example.data.entity.FuelLogEntity
import com.example.data.entity.MaintenanceItemEntity
import com.example.data.entity.TripLogEntity
import android.content.Context
import com.example.data.entity.ServiceLogEntity
import com.example.data.model.CarHealthSummary
import com.example.data.model.CarOemSpec
import com.example.data.model.ObdCode
import com.example.data.model.Workshop
import com.example.data.repository.CarRepository
import com.example.util.NotificationHelper
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppLanguage
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

    private val prefs = application.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    // App Theme and Language States with SharedPreferences persistence
    private val _currentTheme = MutableStateFlow(loadSavedTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow(loadSavedLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()

    private fun loadSavedTheme(): AppTheme {
        val savedThemeId = prefs.getString("selected_app_theme", AppTheme.CYBER_NEON.id)
        return AppTheme.values().find { it.id == savedThemeId } ?: AppTheme.CYBER_NEON
    }

    private fun loadSavedLanguage(): AppLanguage {
        val savedLangName = prefs.getString("selected_app_language", AppLanguage.AR.name)
        return try {
            AppLanguage.valueOf(savedLangName ?: "AR")
        } catch (e: Exception) {
            AppLanguage.AR
        }
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        prefs.edit().putString("selected_app_theme", theme.id).apply()
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("selected_app_language", language.name).apply()
    }

    fun openThemeDialog() { _showThemeDialog.value = true }
    fun closeThemeDialog() { _showThemeDialog.value = false }


    val allCars: StateFlow<List<CarEntity>> = repository.allCars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCar: StateFlow<CarEntity?> = repository.selectedCar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val maintenanceCatalog: StateFlow<List<MaintenanceItemEntity>> = repository.maintenanceCatalog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val carHealthSummary: StateFlow<CarHealthSummary?> = selectedCar
        .flatMapLatest { car ->
            if (car != null) repository.getCarHealthSummary(car.id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val serviceLogs: StateFlow<List<ServiceLogEntity>> = selectedCar
        .flatMapLatest { car ->
            if (car != null) repository.getServiceLogsForCar(car.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelLogs: StateFlow<List<FuelLogEntity>> = selectedCar
        .flatMapLatest { car ->
            if (car != null) repository.getFuelLogsForCar(car.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentTripLogs: StateFlow<List<TripLogEntity>> = selectedCar
        .flatMapLatest { car ->
            if (car != null) repository.getTripLogsForCar(car.id, sinceDaysAgo = 7)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Dialog States
    private val _showUpdateOdometerDialog = MutableStateFlow(false)
    val showUpdateOdometerDialog: StateFlow<Boolean> = _showUpdateOdometerDialog.asStateFlow()

    private val _showLogServiceDialog = MutableStateFlow(false)
    val showLogServiceDialog: StateFlow<Boolean> = _showLogServiceDialog.asStateFlow()

    private val _showLogFuelDialog = MutableStateFlow(false)
    val showLogFuelDialog: StateFlow<Boolean> = _showLogFuelDialog.asStateFlow()

    private val _showAddCustomScheduleDialog = MutableStateFlow(false)
    val showAddCustomScheduleDialog: StateFlow<Boolean> = _showAddCustomScheduleDialog.asStateFlow()

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

    fun openAddCustomScheduleDialog() { _showAddCustomScheduleDialog.value = true }
    fun closeAddCustomScheduleDialog() { _showAddCustomScheduleDialog.value = false }

    fun addCustomMaintenanceReminder(
        titleAr: String,
        category: String,
        targetOdometer: Int,
        daysAhead: Int,
        isCritical: Boolean,
        notes: String,
        context: Context
    ) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.addCustomMaintenanceReminder(
                carId = car.id,
                titleAr = titleAr,
                category = category,
                targetOdometer = targetOdometer,
                daysAhead = daysAhead,
                isCritical = isCritical,
                notes = notes
            )
            closeAddCustomScheduleDialog()
            NotificationHelper.sendNotification(
                context = context,
                title = "📅 تم جدولة تذكير صيانة جديد",
                message = "تم حفظ تذكير صيانة ($titleAr) لسيارتك ${car.brand} عند عداد $targetOdometer كم.",
                notificationId = 4001
            )
            checkNotificationsNow(context)
        }
    }

    fun selectCar(carId: Long) {
        viewModelScope.launch {
            repository.selectCar(carId)
        }
    }

    fun updateOdometer(newOdometer: Int, context: Context? = null) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.updateOdometer(car.id, newOdometer)
            closeUpdateOdometerDialog()
            context?.let { ctx ->
                checkNotificationsNow(ctx)
            }
        }
    }

    fun checkNotificationsNow(context: Context) {
        val car = selectedCar.value ?: return
        val summary = carHealthSummary.value
        NotificationHelper.checkAndNotifyMaintenance(
            context = context,
            summary = summary,
            carName = "${car.brand} ${car.model}"
        )
    }

    fun toggleSevereDriving(isSevere: Boolean) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.toggleSevereDriving(car.id, isSevere)
        }
    }

    fun applyViscosityDecision(oilDropStatus: String, chosenViscosity: String) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.updateViscosityDecision(car.id, oilDropStatus, chosenViscosity)
        }
    }

    fun recordServiceLog(
        itemId: Long,
        performedOdometer: Int,
        cost: Double,
        partBrand: String,
        viscosityUsed: String,
        workshopName: String,
        notes: String
    ) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.recordServiceLog(
                carId = car.id,
                itemId = itemId,
                performedOdometer = performedOdometer,
                cost = cost,
                partBrand = partBrand,
                viscosityUsed = viscosityUsed,
                workshopName = workshopName,
                notes = notes
            )
            closeLogServiceDialog()
        }
    }

    /** One-tap logging for the dashboard hero card: uses the current odometer, no extra details. */
    fun recordServiceLogQuick(itemId: Long) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.recordServiceLog(
                carId = car.id,
                itemId = itemId,
                performedOdometer = car.currentOdometer,
                cost = 0.0,
                partBrand = "",
                viscosityUsed = "",
                workshopName = "",
                notes = ""
            )
        }
    }

    fun recordFuelLog(
        odometer: Int,
        liters: Double,
        totalPrice: Double,
        fuelType: String
    ) {
        val car = selectedCar.value ?: return
        viewModelScope.launch {
            repository.recordFuelLog(
                carId = car.id,
                odometer = odometer,
                liters = liters,
                totalPrice = totalPrice,
                fuelType = fuelType
            )
            closeLogFuelDialog()
        }
    }

    fun addNewVehicle(
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
    ) {
        viewModelScope.launch {
            repository.addNewVehicle(
                brand = brand,
                model = model,
                year = year,
                fuelType = fuelType,
                transmissionType = transmissionType,
                engineCc = engineCc,
                chassisVin = chassisVin,
                currentOdometer = currentOdometer,
                isZeroKm = isZeroKm,
                isSevereDriving = isSevereDriving,
                safetyResetAll = safetyResetAll
            )
        }
    }

    fun getOemSpecs(): CarOemSpec? {
        val car = selectedCar.value ?: return repository.getOemSpecsForCar("Chery", "Arrizo 6 GT")
        return repository.getOemSpecsForCar(car.brand, car.model)
    }

    fun getObdCodes(): List<ObdCode> = repository.getObdCodes()

    fun getWorkshops(): List<Workshop> = repository.getWorkshops()
}
