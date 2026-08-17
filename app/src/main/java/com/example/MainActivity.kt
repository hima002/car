package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Settings
import com.example.ui.screens.AddVehicleScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MaintenanceCatalogScreen
import com.example.ui.screens.ObdDictionaryScreen
import com.example.ui.screens.ResaleReportScreen
import com.example.ui.screens.ServiceLogsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ViscosityWizardScreen
import com.example.ui.screens.WorkshopFinderScreen
import androidx.compose.runtime.collectAsState
import com.example.ui.components.ThemeAndLanguageSelectorDialog
import com.example.ui.theme.AppStrings
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CarViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentTheme by viewModel.currentTheme.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()

            MyApplicationTheme(
                appTheme = currentTheme,
                appLanguage = currentLanguage
            ) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

enum class ScreenRoute {
    DASHBOARD,
    CATALOG,
    SERVICE_LOGS,
    WORKSHOPS,
    SETTINGS,
    VISCOSITY_WIZARD,
    RESALE_REPORT,
    OBD_SCANNER,
    ADD_VEHICLE
}

@Composable
fun MainAppContent(viewModel: CarViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenRoute.DASHBOARD) }

    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val showThemeDialog by viewModel.showThemeDialog.collectAsState()
    val themeStyle = LocalThemeStyle.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = themeStyle.canvasBg,
        bottomBar = {
            if (currentScreen in listOf(
                    ScreenRoute.DASHBOARD,
                    ScreenRoute.CATALOG,
                    ScreenRoute.SERVICE_LOGS,
                    ScreenRoute.WORKSHOPS,
                    ScreenRoute.SETTINGS
                )
            ) {
                NavigationBar(
                    containerColor = themeStyle.navBg,
                    contentColor = themeStyle.primaryColor,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.DASHBOARD,
                        onClick = { currentScreen = ScreenRoute.DASHBOARD },
                        icon = { Icon(Icons.Default.DirectionsCar, contentDescription = AppStrings.navDashboard(currentLanguage)) },
                        label = { Text(AppStrings.navDashboard(currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = themeStyle.primaryColor,
                            selectedTextColor = themeStyle.primaryColor,
                            indicatorColor = themeStyle.cardBg,
                            unselectedIconColor = themeStyle.textSecondary,
                            unselectedTextColor = themeStyle.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.CATALOG,
                        onClick = { currentScreen = ScreenRoute.CATALOG },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = AppStrings.navCatalog(currentLanguage)) },
                        label = { Text(AppStrings.navCatalog(currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = themeStyle.primaryColor,
                            selectedTextColor = themeStyle.primaryColor,
                            indicatorColor = themeStyle.cardBg,
                            unselectedIconColor = themeStyle.textSecondary,
                            unselectedTextColor = themeStyle.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_catalog")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.SERVICE_LOGS,
                        onClick = { currentScreen = ScreenRoute.SERVICE_LOGS },
                        icon = { Icon(Icons.Default.Build, contentDescription = AppStrings.navExpenses(currentLanguage)) },
                        label = { Text(AppStrings.navExpenses(currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = themeStyle.primaryColor,
                            selectedTextColor = themeStyle.primaryColor,
                            indicatorColor = themeStyle.cardBg,
                            unselectedIconColor = themeStyle.textSecondary,
                            unselectedTextColor = themeStyle.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_service_logs")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.WORKSHOPS,
                        onClick = { currentScreen = ScreenRoute.WORKSHOPS },
                        icon = { Icon(Icons.Default.Store, contentDescription = AppStrings.navWorkshops(currentLanguage)) },
                        label = { Text(AppStrings.navWorkshops(currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = themeStyle.primaryColor,
                            selectedTextColor = themeStyle.primaryColor,
                            indicatorColor = themeStyle.cardBg,
                            unselectedIconColor = themeStyle.textSecondary,
                            unselectedTextColor = themeStyle.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_workshops")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenRoute.SETTINGS,
                        onClick = { currentScreen = ScreenRoute.SETTINGS },
                        icon = { Icon(Icons.Default.Settings, contentDescription = AppStrings.navSettings(currentLanguage)) },
                        label = { Text(AppStrings.navSettings(currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = themeStyle.primaryColor,
                            selectedTextColor = themeStyle.primaryColor,
                            indicatorColor = themeStyle.cardBg,
                            unselectedIconColor = themeStyle.textSecondary,
                            unselectedTextColor = themeStyle.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenRoute.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToViscosityWizard = { currentScreen = ScreenRoute.VISCOSITY_WIZARD },
                        onNavigateToCatalog = { currentScreen = ScreenRoute.CATALOG },
                        onNavigateToResaleReport = { currentScreen = ScreenRoute.RESALE_REPORT },
                        onNavigateToObd = { currentScreen = ScreenRoute.OBD_SCANNER },
                        onNavigateToAddVehicle = { currentScreen = ScreenRoute.ADD_VEHICLE }
                    )
                }

                ScreenRoute.CATALOG -> {
                    MaintenanceCatalogScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.SERVICE_LOGS -> {
                    ServiceLogsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.WORKSHOPS -> {
                    WorkshopFinderScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.SETTINGS -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.VISCOSITY_WIZARD -> {
                    ViscosityWizardScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.RESALE_REPORT -> {
                    ResaleReportScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.OBD_SCANNER -> {
                    ObdDictionaryScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }

                ScreenRoute.ADD_VEHICLE -> {
                    AddVehicleScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = ScreenRoute.DASHBOARD }
                    )
                }
            }

            if (showThemeDialog) {
                ThemeAndLanguageSelectorDialog(
                    currentTheme = currentTheme,
                    currentLanguage = currentLanguage,
                    onSelectTheme = { viewModel.setTheme(it) },
                    onSelectLanguage = { viewModel.setLanguage(it) },
                    onDismiss = { viewModel.closeThemeDialog() }
                )
            }
        }
    }
}


