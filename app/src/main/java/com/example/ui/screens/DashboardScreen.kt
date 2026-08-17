package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.AppStrings
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.CarEntity
import com.example.data.model.CarHealthSummary
import com.example.data.model.CarMaintenanceItemStatus
import com.example.data.model.StatusLevel
import com.example.ui.components.HealthGaugeCard
import com.example.ui.components.AddCustomScheduleDialog
import com.example.ui.components.LogFuelDialog
import com.example.ui.components.LogServiceDialog
import com.example.ui.components.UpdateOdometerDialog
import com.example.ui.components.VehicleSwitcherDialog
import com.example.ui.theme.EditorialBg
import com.example.ui.theme.EditorialCardBg
import com.example.ui.theme.EditorialCardBorder
import com.example.ui.theme.EditorialNavBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialPrimaryDark
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.theme.EditorialVehicleCardBg
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.util.DayEpoch
import com.example.viewmodel.CarViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: CarViewModel,
    onNavigateToViscosityWizard: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToResaleReport: () -> Unit,
    onNavigateToObd: () -> Unit,
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToTracking: () -> Unit
) {
    val selectedCar by viewModel.selectedCar.collectAsState()
    val allCars by viewModel.allCars.collectAsState()
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    val maintenanceCatalog by viewModel.maintenanceCatalog.collectAsState()
    val recentTripLogs by viewModel.recentTripLogs.collectAsState()

    val showOdometerDialog by viewModel.showUpdateOdometerDialog.collectAsState()
    val showLogServiceDialog by viewModel.showLogServiceDialog.collectAsState()
    val showLogFuelDialog by viewModel.showLogFuelDialog.collectAsState()
    val showAddCustomScheduleDialog by viewModel.showAddCustomScheduleDialog.collectAsState()
    val preselectedItemId by viewModel.selectedItemIdForLog.collectAsState()

    val context = LocalContext.current
    var selectedCategoryTab by remember { mutableStateOf("ALL") }
    var showVehicleSwitcherDialog by remember { mutableStateOf(false) }

    if (selectedCar == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EditorialBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = EditorialPrimary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "مرحباً بك في منظومة AutoKeep 🚘",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "لا توجد أية بيانات أو سيارات مسجلة حالياً.\nإبدأ بإضافة سيارتك الخاصة لتجربة وتتبع كافة صياناتك ووقودك بنفسك.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = onNavigateToAddVehicle,
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("empty_state_add_car_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة سيارتك الأولى الآن ➕", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
        return
    }

    val car = selectedCar!!
    val themeStyle = LocalThemeStyle.current
    val lang = LocalAppLanguage.current
    val isAr = lang == com.example.ui.theme.AppLanguage.AR

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStyle.canvasBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 0. PROMINENT TOP BAR WITH LOGO AND THEME SWITCHER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AutoKeep 🚘",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.primaryColor
                        )
                        Text(
                            text = if (isAr) "إدارة وصيانة المركبات الذكية" else "Smart Vehicle Maintenance",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeStyle.textSecondary
                        )
                    }

                    // PROMINENT THEME & LANGUAGE BUTTON
                    Button(
                        onClick = { viewModel.openThemeDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeStyle.primaryColor.copy(alpha = 0.18f),
                            contentColor = themeStyle.primaryColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, themeStyle.primaryColor),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_theme_dialog_prominent_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = themeStyle.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "🎨 الثيمات (12)" else "🎨 Themes (12)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = themeStyle.primaryColor
                        )
                    }
                }
            }

            // 1. TOP CAR HEADER BAR
            item {
                CarHeaderCard(
                    car = car,
                    allCars = allCars,
                    onSelectCar = { viewModel.selectCar(it) },
                    onUpdateOdometerClick = { viewModel.openUpdateOdometerDialog() },
                    onToggleSevere = { isSevere -> viewModel.toggleSevereDriving(isSevere) },
                    onAddVehicleClick = onNavigateToAddVehicle,
                    onOpenVehicleSwitcher = { showVehicleSwitcherDialog = true },
                    onOpenThemeDialog = { viewModel.openThemeDialog() }
                )
            }

            // 2. HERO CAR GRAPHIC & HEALTH GAUGE
            item {
                Column {
                    // Hero Image Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_car_1785133856857),
                            contentDescription = "Smart Diagnostics",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, EditorialCardBg.copy(alpha = 0.9f))
                                    )
                                )
                        )
                        Text(
                            text = "نظام التنبيه المباشر ومساعد الصيانة الوقائية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    healthSummary?.let { summary ->
                        val nextDueText = summary.urgentAlerts.firstOrNull()?.let {
                            "${it.itemNameAr} (${it.remainingKm} كم المتبقي)"
                        } ?: "جميع القطع بحالة جيدة"

                        HealthGaugeCard(
                            healthScore = summary.healthScore,
                            statusTextAr = summary.statusTextAr,
                            overallLevel = summary.overallLevel,
                            nextDueText = nextDueText,
                            onTriggerNotification = {
                                viewModel.checkNotificationsNow(context)
                            }
                        )
                    }
                }
            }

            // 3. NEXT ACTION HERO CARD ("إيه المطلوب دلوقتي؟") + collapsible rest of alerts
            healthSummary?.urgentAlerts?.let { alerts ->
                val topAlert = alerts.firstOrNull()
                if (topAlert != null) {
                    item {
                        NextActionHeroCard(
                            alert = topAlert,
                            onMarkDoneNow = { viewModel.recordServiceLogQuick(topAlert.itemId) },
                            onAddDetails = { viewModel.openLogServiceDialog(topAlert.itemId) }
                        )
                    }
                }
                val remainingAlerts = alerts.drop(1)
                if (remainingAlerts.isNotEmpty()) {
                    item {
                        CollapsibleAlertsSection(
                            alerts = remainingAlerts,
                            onMarkDone = { itemId -> viewModel.openLogServiceDialog(itemId) }
                        )
                    }
                }
            }

            // 4. QUICK ACTIONS ROW
            item {
                QuickActionsRow(
                    onLogService = { viewModel.openLogServiceDialog() },
                    onLogFuel = { viewModel.openLogFuelDialog() },
                    onAddSchedule = { viewModel.openAddCustomScheduleDialog() },
                    onResaleReport = onNavigateToResaleReport,
                    onObdScanner = onNavigateToObd
                )
            }

            // 5. SMART VISCOSITY RECOMMENDATION BANNER
            item {
                ViscosityRecommendationCard(
                    car = car,
                    healthSummary = healthSummary,
                    onOpenWizard = onNavigateToViscosityWizard
                )
            }

            // 5b. AUTOMATIC TRACKING SUMMARY
            item {
                val todayEpoch = DayEpoch.startOfDay()
                val todayKm = recentTripLogs.filter { it.dayEpoch == todayEpoch }.sumOf { it.distanceKm }
                TrackingSummaryCard(
                    todayKm = todayKm,
                    onClick = onNavigateToTracking
                )
            }

            // 6. EXECUTIVE MAINTENANCE SUMMARY CARD
            healthSummary?.let { summary ->
                item {
                    MaintenanceSummaryOverviewCard(
                        summary = summary,
                        onNavigateToCatalog = onNavigateToCatalog
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        // Dialogs
        if (showOdometerDialog) {
            UpdateOdometerDialog(
                currentOdometer = car.currentOdometer,
                onDismiss = { viewModel.closeUpdateOdometerDialog() },
                onConfirm = { newOdo -> viewModel.updateOdometer(newOdo, context) }
            )
        }

        if (showLogServiceDialog) {
            LogServiceDialog(
                currentOdometer = car.currentOdometer,
                maintenanceItems = maintenanceCatalog,
                preselectedItemId = preselectedItemId,
                onDismiss = { viewModel.closeLogServiceDialog() },
                onConfirm = { itemId, odo, cost, brand, viscosity, workshop, notes ->
                    viewModel.recordServiceLog(itemId, odo, cost, brand, viscosity, workshop, notes)
                }
            )
        }

        if (showLogFuelDialog) {
            LogFuelDialog(
                currentOdometer = car.currentOdometer,
                onDismiss = { viewModel.closeLogFuelDialog() },
                onConfirm = { odo, liters, price, fuelType ->
                    viewModel.recordFuelLog(odo, liters, price, fuelType)
                }
            )
        }

        if (showAddCustomScheduleDialog) {
            AddCustomScheduleDialog(
                currentOdometer = car.currentOdometer,
                onDismiss = { viewModel.closeAddCustomScheduleDialog() },
                onConfirm = { titleAr, category, targetOdo, days, isCritical, notes ->
                    viewModel.addCustomMaintenanceReminder(
                        titleAr = titleAr,
                        category = category,
                        targetOdometer = targetOdo,
                        daysAhead = days,
                        isCritical = isCritical,
                        notes = notes,
                        context = context
                    )
                }
            )
        }

        if (showVehicleSwitcherDialog) {
            VehicleSwitcherDialog(
                allCars = allCars,
                selectedCarId = car.id,
                onSelectCar = { viewModel.selectCar(it) },
                onAddVehicle = onNavigateToAddVehicle,
                onDismiss = { showVehicleSwitcherDialog = false }
            )
        }
    }
}

@Composable
fun CarHeaderCard(
    car: CarEntity,
    allCars: List<CarEntity>,
    onSelectCar: (Long) -> Unit,
    onUpdateOdometerClick: () -> Unit,
    onToggleSevere: (Boolean) -> Unit,
    onAddVehicleClick: () -> Unit,
    onOpenVehicleSwitcher: () -> Unit,
    onOpenThemeDialog: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val lang = LocalAppLanguage.current
    val isAr = lang == com.example.ui.theme.AppLanguage.AR

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(themeStyle.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Horizontal Multi-Vehicle Switcher Carousel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAr) "السيارة النشطة 🚗:" else "Active Car 🚗:",
                    style = MaterialTheme.typography.labelSmall,
                    color = themeStyle.textSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    items(allCars) { c ->
                        val isSelected = c.id == car.id || c.isSelected
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCar(c.id) },
                            label = {
                                Text(
                                    text = "${c.brand} ${c.model}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = themeStyle.primaryColor,
                                selectedLabelColor = Color.White,
                                containerColor = themeStyle.canvasBg,
                                labelColor = themeStyle.textPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Car Selector Title & Switcher Launcher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onOpenVehicleSwitcher() }
                        .testTag("car_selector_dropdown")
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(themeStyle.primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = themeStyle.primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${car.brand} ${car.model} (${car.year})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.textPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "🔄", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            text = if (isAr) "نوع الفتيس: ${car.transmissionType} • ${car.engineCc}" else "Transmission: ${car.transmissionType} • ${car.engineCc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.textSecondary
                        )
                    }
                }

                // Action Buttons Row (Theme Selector + Add Car)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = onOpenThemeDialog,
                        label = {
                            Text(
                                text = if (isAr) "🎨 ثيمات" else "🎨 Theme",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.primaryColor
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = themeStyle.primaryColor.copy(alpha = 0.12f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, themeStyle.primaryColor),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("open_theme_dialog_chip_button")
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onOpenVehicleSwitcher,
                        modifier = Modifier.testTag("open_vehicle_fleet_switcher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة أو تبديل سيارة",
                            tint = themeStyle.primaryColor
                        )
                    }
                }
            }

            // Odometer & Severe Driving Row
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Odometer Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(EditorialCardBg)
                        .border(1.dp, EditorialCardBorder, RoundedCornerShape(50))
                        .clickable { onUpdateOdometerClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("odometer_quick_update_chip")
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "العداد: ${car.currentOdometer} كم",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary
                    )
                }

                // Severe Driving Filter Chip
                FilterChip(
                    selected = car.isSevereDriving,
                    onClick = { onToggleSevere(!car.isSevereDriving) },
                    label = {
                        Text(if (car.isSevereDriving) "قيادة شاقة ⚠️" else "قيادة قياسية 🟢")
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusYellow.copy(alpha = 0.2f),
                        selectedLabelColor = StatusYellow,
                        containerColor = EditorialCardBg,
                        labelColor = EditorialTextPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onLogService: () -> Unit,
    onLogFuel: () -> Unit,
    onAddSchedule: () -> Unit,
    onResaleReport: () -> Unit,
    onObdScanner: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionButton(
            title = "تسجيل صيانة",
            icon = Icons.Default.Build,
            color = EditorialPrimary,
            onClick = onLogService,
            modifier = Modifier.weight(1f).testTag("log_service_action_button")
        )
        Spacer(modifier = Modifier.width(6.dp))
        QuickActionButton(
            title = "تذكير صيانة 📅",
            icon = Icons.Default.Event,
            color = StatusYellow,
            onClick = onAddSchedule,
            modifier = Modifier.weight(1.1f).testTag("add_custom_schedule_action_button")
        )
        Spacer(modifier = Modifier.width(6.dp))
        QuickActionButton(
            title = "سجل الوقود",
            icon = Icons.Default.EvStation,
            color = EditorialPrimary,
            onClick = onLogFuel,
            modifier = Modifier.weight(1f).testTag("log_fuel_action_button")
        )
        Spacer(modifier = Modifier.width(6.dp))
        QuickActionButton(
            title = "تقرير البيع",
            icon = Icons.Default.PictureAsPdf,
            color = StatusGreen,
            onClick = onResaleReport,
            modifier = Modifier.weight(1f).testTag("resale_report_action_button")
        )
        Spacer(modifier = Modifier.width(6.dp))
        QuickActionButton(
            title = "أكواد OBD",
            icon = Icons.Default.QrCodeScanner,
            color = StatusYellow,
            onClick = onObdScanner,
            modifier = Modifier.weight(1f).testTag("obd_codes_action_button")
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )
        }
    }
}

@Composable
fun ViscosityRecommendationCard(
    car: CarEntity,
    healthSummary: CarHealthSummary?,
    onOpenWizard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenWizard() }
            .testTag("viscosity_wizard_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(EditorialPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Opacity,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "المساعد الذكي للزوجة المحرك (Viscosity Engine)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "اللزوجة الموصى بها حالياً: ${car.recommendedViscosity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = viscosityReasonText(car, healthSummary),
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialTextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = EditorialTextSecondary
            )
        }
    }
}

private fun viscosityReasonText(car: CarEntity, healthSummary: CarHealthSummary?): String {
    val drivingPart = if (car.isSevereDriving) "قيادتك مصنّفة شاقة" else "قيادتك قياسية"
    val oilPart = when (car.oilLevelDropStatus) {
        "SLIGHT_DROP" -> "مع نقص بسيط بمستوى الزيت"
        "HEAVY_DROP" -> "مع نقص واضح بمستوى الزيت"
        else -> "بدون أي نقص ملحوظ بالزيت"
    }
    return "السبب: $drivingPart، $oilPart، عند عداد ${car.currentOdometer} كم."
}

/** Single most urgent maintenance item, front and center, with a one-tap "done now" action. */
@Composable
fun NextActionHeroCard(
    alert: CarMaintenanceItemStatus,
    onMarkDoneNow: () -> Unit,
    onAddDetails: () -> Unit
) {
    val accentColor = if (alert.statusLevel == StatusLevel.RED) StatusRed else StatusYellow

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("next_action_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إيه المطلوب دلوقتي؟",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = alert.itemNameAr,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )
            Text(
                text = if (alert.remainingKm <= 0) {
                    "تجاوزت حد الصيانة بـ ${kotlin.math.abs(alert.remainingKm)} كم!"
                } else {
                    "متبقي ${alert.remainingKm} كم / حوالي ${alert.remainingDays} يوم"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkDoneNow,
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).testTag("hero_mark_done_now_button")
                ) {
                    Text("تم الآن ✅", fontWeight = FontWeight.Bold, color = Color.White)
                }
                OutlinedButton(
                    onClick = onAddDetails,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).testTag("hero_add_details_button")
                ) {
                    Text("إضافة تفاصيل", color = EditorialTextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

/** Everything besides the single top alert, folded away by default to keep the dashboard uncluttered. */
@Composable
fun CollapsibleAlertsSection(
    alerts: List<CarMaintenanceItemStatus>,
    onMarkDone: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .testTag("collapsible_alerts_toggle"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "باقي التنبيهات (${alerts.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EditorialTextSecondary
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = EditorialTextSecondary
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                alerts.forEach { alert ->
                    AlertItemCard(alert = alert, onMarkDone = onMarkDone)
                }
            }
        }
    }
}

@Composable
private fun AlertItemCard(
    alert: CarMaintenanceItemStatus,
    onMarkDone: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.statusLevel == StatusLevel.RED) StatusRed.copy(alpha = 0.08f) else StatusYellow.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (alert.statusLevel == StatusLevel.RED) StatusRed.copy(alpha = 0.4f) else StatusYellow.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.itemNameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary
                )
                Text(
                    text = if (alert.remainingKm <= 0) "تجاوزت حد الصيانة بـ ${kotlin.math.abs(alert.remainingKm)} كم!" else "متبقي ${alert.remainingKm} كم / حوالي ${alert.remainingDays} يوم",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (alert.statusLevel == StatusLevel.RED) StatusRed else StatusYellow,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { onMarkDone(alert.itemId) },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("mark_done_button_${alert.itemId}")
            ) {
                Text("تم التغيير الان", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun TrackingSummaryCard(
    todayKm: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tracking_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EditorialPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "التتبع التلقائي للقيادة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "${todayKm.roundToInt()} كم اليوم — اضغط للإعدادات",
                    style = MaterialTheme.typography.bodySmall,
                    color = EditorialTextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = EditorialTextSecondary
            )
        }
    }
}

@Composable
fun MaintenanceSummaryOverviewCard(
    summary: CarHealthSummary,
    onNavigateToCatalog: () -> Unit
) {
    val allItems = summary.itemsByCategory.values.flatten()
    val greenCount = allItems.count { it.statusLevel == StatusLevel.GREEN }
    val yellowCount = allItems.count { it.statusLevel == StatusLevel.YELLOW }
    val redCount = allItems.count { it.statusLevel == StatusLevel.RED }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToCatalog() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EditorialPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = EditorialPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ملخص حالة قطع الصيانة (360°)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "إجمالي القطع المتابعة: ${allItems.size} قطعة",
                            style = MaterialTheme.typography.bodySmall,
                            color = EditorialTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "التفاصيل",
                    tint = EditorialPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadgeBox(
                    label = "ممتازة 🟢",
                    count = "$greenCount",
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
                StatusBadgeBox(
                    label = "اقتربت 🟡",
                    count = "$yellowCount",
                    color = StatusYellow,
                    modifier = Modifier.weight(1f)
                )
                StatusBadgeBox(
                    label = "متأخرة 🔴",
                    count = "$redCount",
                    color = StatusRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onNavigateToCatalog,
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_detailed_catalog_button")
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "عرض القائمة التفصيلية للقطع والصيانات 📋",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StatusBadgeBox(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(EditorialNavBg, RoundedCornerShape(14.dp))
            .border(1.dp, EditorialCardBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = EditorialTextSecondary)
        }
    }
}

@Composable
fun CategoryTabsRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val categories = listOf(
        "ALL" to "الكل 🚗",
        "OILS_FLUIDS" to "الزيوت والسوائل 🛢️",
        "BELTS_ELEC" to "السيور والكهرباء ⚡",
        "FILTERS_INTAKE" to "الفلاتر والبوجيهات 🌪️",
        "SUSPENSION_BRAKES" to "العفشة والفرامل 🛑"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { (code, label) ->
            FilterChip(
                selected = selectedTab == code,
                onClick = { onTabSelected(code) },
                label = { Text(label, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EditorialPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = EditorialNavBg,
                    labelColor = EditorialTextSecondary
                )
            )
        }
    }
}

@Composable
fun MaintenanceItemCard(
    status: CarMaintenanceItemStatus,
    onLogService: () -> Unit
) {
    val statusColor = when (status.statusLevel) {
        StatusLevel.GREEN -> StatusGreen
        StatusLevel.YELLOW -> StatusYellow
        StatusLevel.RED -> StatusRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status.itemNameAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary
                    )
                }

                Text(
                    text = if (status.remainingKm <= 0) "منتهي! 🔴" else "متبقي: ${status.remainingKm} كم",
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { status.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = EditorialNavBg
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المواصفة: ${status.recommendedSpecAr.take(35)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = EditorialTextSecondary
                )

                TextButton(onClick = onLogService) {
                    Text("تسجيل تغيير 🛠️", color = EditorialPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
