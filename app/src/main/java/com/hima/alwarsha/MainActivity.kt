package com.hima.alwarsha

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.hima.alwarsha.service.DrivingTrackingService
import com.hima.alwarsha.ui.screens.AddVehicleScreen
import com.hima.alwarsha.ui.screens.DashboardScreen
import com.hima.alwarsha.ui.screens.MaintenanceCatalogScreen
import com.hima.alwarsha.ui.screens.DiagnosticsScreen
import com.hima.alwarsha.ui.screens.ResaleReportScreen
import com.hima.alwarsha.ui.screens.ServiceLogsScreen
import com.hima.alwarsha.ui.screens.SettingsScreen
import com.hima.alwarsha.ui.screens.TrackingSettingsScreen
import com.hima.alwarsha.ui.screens.ViscosityWizardScreen
import com.hima.alwarsha.ui.screens.WorkshopFinderScreen
import com.hima.alwarsha.ui.theme.AlWarshaTheme
import com.hima.alwarsha.util.DayEpoch
import com.hima.alwarsha.util.TrackingPreferences
import com.hima.alwarsha.viewmodel.CarViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CarViewModel by viewModels()

    private var onForegroundPermissionsResult: (() -> Unit)? = null
    private var onBackgroundLocationResult: (() -> Unit)? = null

    private val backgroundLocationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        onBackgroundLocationResult?.invoke()
    }

    private val foregroundPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onForegroundPermissionsResult?.invoke()
    }

    private val locationOnlyLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun hasFineLocationPermission() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun requestLocationOnlyPermission() {
        locationOnlyLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun startTrackingFlow() {
        val foregroundPermissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) foregroundPermissions += Manifest.permission.ACTIVITY_RECOGNITION

        onForegroundPermissionsResult = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                onBackgroundLocationResult = { activateTracking() }
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                activateTracking()
            }
        }
        foregroundPermissionsLauncher.launch(foregroundPermissions.toTypedArray())
    }

    private fun activateTracking() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        TrackingPreferences.setEnabled(this, true)
        val intent = Intent(this, DrivingTrackingService::class.java).setAction(DrivingTrackingService.ACTION_START_TRACKING)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopTrackingFlow() {
        TrackingPreferences.setEnabled(this, false)
        val intent = Intent(this, DrivingTrackingService::class.java).setAction(DrivingTrackingService.ACTION_STOP_TRACKING)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun requestBatteryOptimizationExemption() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) return
        startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName")))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AlWarshaTheme {
                MainAppContent(
                    viewModel = viewModel,
                    isTrackingEnabled = TrackingPreferences.isEnabled(this@MainActivity),
                    onToggleTracking = { enabled -> if (enabled) startTrackingFlow() else stopTrackingFlow() },
                    onRequestBatteryOptimizationExemption = { requestBatteryOptimizationExemption() },
                    hasLocationPermission = hasFineLocationPermission(),
                    onRequestLocationPermission = { requestLocationOnlyPermission() }
                )
            }
        }
    }
}

private enum class ScreenRoute {
    DASHBOARD, CATALOG, SERVICE_LOGS, SETTINGS,
    VISCOSITY_WIZARD, ADD_VEHICLE, WORKSHOP_FINDER, TRACKING_SETTINGS, OBD_DICTIONARY, RESALE_REPORT
}

@Composable
private fun MainAppContent(
    viewModel: CarViewModel,
    isTrackingEnabled: Boolean,
    onToggleTracking: (Boolean) -> Unit,
    onRequestBatteryOptimizationExemption: () -> Unit,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(ScreenRoute.DASHBOARD) }
    var trackingEnabledState by remember { mutableStateOf(isTrackingEnabled) }
    val selectedCar by viewModel.selectedCar.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen in listOf(ScreenRoute.DASHBOARD, ScreenRoute.CATALOG, ScreenRoute.SERVICE_LOGS, ScreenRoute.SETTINGS)) {
                NavigationBar(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.DASHBOARD,
                        onClick = { currentScreen = ScreenRoute.DASHBOARD },
                        icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.CATALOG,
                        onClick = { currentScreen = ScreenRoute.CATALOG },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "الكتالوج") },
                        label = { Text("الكتالوج", fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.SERVICE_LOGS,
                        onClick = { currentScreen = ScreenRoute.SERVICE_LOGS },
                        icon = { Icon(Icons.Default.Build, contentDescription = "السجل") },
                        label = { Text("السجل", fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.SETTINGS,
                        onClick = { currentScreen = ScreenRoute.SETTINGS },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                        label = { Text("الإعدادات", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                ScreenRoute.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToViscosityWizard = { currentScreen = ScreenRoute.VISCOSITY_WIZARD },
                    onNavigateToCatalog = { currentScreen = ScreenRoute.CATALOG },
                    onNavigateToWorkshops = { currentScreen = ScreenRoute.WORKSHOP_FINDER },
                    onNavigateToObd = { currentScreen = ScreenRoute.OBD_DICTIONARY },
                    onNavigateToResaleReport = { currentScreen = ScreenRoute.RESALE_REPORT },
                    onNavigateToTracking = { currentScreen = ScreenRoute.TRACKING_SETTINGS },
                    onNavigateToAddVehicle = { currentScreen = ScreenRoute.ADD_VEHICLE }
                )

                ScreenRoute.CATALOG -> MaintenanceCatalogScreen(viewModel = viewModel, onBack = { currentScreen = ScreenRoute.DASHBOARD })

                ScreenRoute.SERVICE_LOGS -> ServiceLogsScreen(viewModel = viewModel, onBack = { currentScreen = ScreenRoute.DASHBOARD })

                ScreenRoute.SETTINGS -> SettingsScreen(onNavigateToTracking = { currentScreen = ScreenRoute.TRACKING_SETTINGS })

                ScreenRoute.VISCOSITY_WIZARD -> ViscosityWizardScreen(viewModel = viewModel, onBack = { currentScreen = ScreenRoute.DASHBOARD })

                ScreenRoute.ADD_VEHICLE -> AddVehicleScreen(viewModel = viewModel, onBack = { currentScreen = ScreenRoute.DASHBOARD })

                ScreenRoute.WORKSHOP_FINDER -> WorkshopFinderScreen(
                    carBrand = selectedCar?.brand,
                    hasLocationPermission = hasLocationPermission,
                    onRequestLocationPermission = onRequestLocationPermission,
                    onBack = { currentScreen = ScreenRoute.DASHBOARD }
                )

                ScreenRoute.TRACKING_SETTINGS -> {
                    val recentTripLogs by viewModel.recentTripLogs.collectAsState()
                    val todayEpoch = DayEpoch.startOfDay()
                    val todayKm = recentTripLogs.filter { it.dayEpoch == todayEpoch }.sumOf { it.distanceKm }
                    val weekKm = recentTripLogs.sumOf { it.distanceKm }

                    TrackingSettingsScreen(
                        onBack = { currentScreen = ScreenRoute.DASHBOARD },
                        isTrackingEnabled = trackingEnabledState,
                        onToggleTracking = { enabled -> trackingEnabledState = enabled; onToggleTracking(enabled) },
                        onRequestBatteryOptimizationExemption = onRequestBatteryOptimizationExemption,
                        todayKm = todayKm,
                        weekKm = weekKm
                    )
                }

                ScreenRoute.OBD_DICTIONARY -> DiagnosticsScreen(
                    carViewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.DASHBOARD }
                )

                ScreenRoute.RESALE_REPORT -> ResaleReportScreen(viewModel = viewModel, onBack = { currentScreen = ScreenRoute.DASHBOARD })
            }
        }
    }
}
