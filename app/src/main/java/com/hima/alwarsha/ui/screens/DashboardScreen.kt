package com.hima.alwarsha.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.model.CarHealthSummary
import com.hima.alwarsha.data.model.CarMaintenanceItemStatus
import com.hima.alwarsha.data.model.StatusLevel
import com.hima.alwarsha.ui.components.LogFuelDialog
import com.hima.alwarsha.ui.components.LogServiceDialog
import com.hima.alwarsha.ui.components.UpdateOdometerDialog
import com.hima.alwarsha.ui.components.VehicleSwitcherDialog
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.ui.theme.StatusGreen
import com.hima.alwarsha.ui.theme.StatusRed
import com.hima.alwarsha.ui.theme.StatusYellow
import com.hima.alwarsha.util.DayEpoch
import com.hima.alwarsha.viewmodel.CarViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: CarViewModel,
    onNavigateToViscosityWizard: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    onNavigateToObd: () -> Unit,
    onNavigateToResaleReport: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToAddVehicle: () -> Unit
) {
    val selectedCar by viewModel.selectedCar.collectAsState()
    val allCars by viewModel.allCars.collectAsState()
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    val maintenanceCatalog by viewModel.maintenanceCatalog.collectAsState()
    val recentTripLogs by viewModel.recentTripLogs.collectAsState()

    val showOdometerDialog by viewModel.showUpdateOdometerDialog.collectAsState()
    val showLogServiceDialog by viewModel.showLogServiceDialog.collectAsState()
    val showLogFuelDialog by viewModel.showLogFuelDialog.collectAsState()
    val preselectedItemId by viewModel.selectedItemIdForLog.collectAsState()

    val context = LocalContext.current
    val themeStyle = LocalThemeStyle.current
    var showVehicleSwitcherDialog by remember { mutableStateOf(false) }

    if (selectedCar == null) {
        EmptyDashboardState(onNavigateToAddVehicle)
        return
    }
    val car = selectedCar!!

    Box(modifier = Modifier.fillMaxSize().background(themeStyle.canvasBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Column {
                    Text("الورشة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = themeStyle.primaryColor)
                    Text("مساعدك الشخصي لصيانة عربيتك", style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
                }
            }

            item {
                CarHeaderCard(
                    car = car,
                    allCars = allCars,
                    onUpdateOdometerClick = { viewModel.openUpdateOdometerDialog() },
                    onToggleSevere = { viewModel.toggleSevereDriving(it) },
                    onOpenVehicleSwitcher = { showVehicleSwitcherDialog = true }
                )
            }

            healthSummary?.let { summary ->
                item { HealthScoreCard(summary) }
            }

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
                val remaining = alerts.drop(1)
                if (remaining.isNotEmpty()) {
                    item {
                        CollapsibleAlertsSection(alerts = remaining, onMarkDone = { viewModel.openLogServiceDialog(it) })
                    }
                }
            }

            item {
                QuickActionsRow(
                    onLogService = { viewModel.openLogServiceDialog() },
                    onLogFuel = { viewModel.openLogFuelDialog() },
                    onFindWorkshop = onNavigateToWorkshops,
                    onResaleReport = onNavigateToResaleReport,
                    onObdScanner = onNavigateToObd
                )
            }

            item {
                ViscosityRecommendationCard(car = car, onOpenWizard = onNavigateToViscosityWizard)
            }

            item {
                val todayEpoch = DayEpoch.startOfDay()
                val todayKm = recentTripLogs.filter { it.dayEpoch == todayEpoch }.sumOf { it.distanceKm }
                TrackingSummaryCard(todayKm = todayKm, onClick = onNavigateToTracking)
            }

            healthSummary?.let { summary ->
                item { MaintenanceSummaryOverviewCard(summary = summary, onNavigateToCatalog = onNavigateToCatalog) }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        if (showOdometerDialog) {
            UpdateOdometerDialog(
                currentOdometer = car.currentOdometer,
                onDismiss = { viewModel.closeUpdateOdometerDialog() },
                onConfirm = { viewModel.updateOdometer(it, context) }
            )
        }
        if (showLogServiceDialog) {
            LogServiceDialog(
                currentOdometer = car.currentOdometer,
                maintenanceItems = maintenanceCatalog,
                preselectedItemId = preselectedItemId,
                onDismiss = { viewModel.closeLogServiceDialog() },
                onConfirm = { itemId, odo, cost, brand, workshop, notes ->
                    viewModel.recordServiceLog(itemId, odo, cost, brand, workshop, notes)
                }
            )
        }
        if (showLogFuelDialog) {
            LogFuelDialog(
                currentOdometer = car.currentOdometer,
                onDismiss = { viewModel.closeLogFuelDialog() },
                onConfirm = { odo, liters, price -> viewModel.recordFuelLog(odo, liters, price) }
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
private fun EmptyDashboardState(onNavigateToAddVehicle: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    Box(Modifier.fillMaxSize().background(themeStyle.canvasBg).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(80.dp), tint = themeStyle.primaryColor)
            Spacer(Modifier.height(20.dp))
            Text("مرحباً بك في الورشة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "لا توجد سيارة مسجلة حالياً. ابدأ بإضافة سيارتك لتتبع صيانتها.",
                style = MaterialTheme.typography.bodyMedium,
                color = themeStyle.textSecondary
            )
            Spacer(Modifier.height(28.dp))
            Button(onClick = onNavigateToAddVehicle, colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("إضافة سيارتك الأولى", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CarHeaderCard(
    car: CarEntity,
    allCars: List<CarEntity>,
    onUpdateOdometerClick: () -> Unit,
    onToggleSevere: (Boolean) -> Unit,
    onOpenVehicleSwitcher: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onOpenVehicleSwitcher() }) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(themeStyle.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("${car.brand} ${car.model} (${car.year})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                    Text("عدد السيارات: ${allCars.size} — دوس للتبديل", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(themeStyle.cardShape).background(themeStyle.navBg)
                        .clickable { onUpdateOdometerClick() }.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("العداد: ${car.currentOdometer} كم", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                }
                FilterChip(
                    selected = car.isSevereDriving,
                    onClick = { onToggleSevere(!car.isSevereDriving) },
                    label = { Text(if (car.isSevereDriving) "قيادة شاقة ⚠️" else "قيادة قياسية 🟢") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusYellow.copy(alpha = 0.2f),
                        selectedLabelColor = StatusYellow,
                        containerColor = themeStyle.navBg,
                        labelColor = themeStyle.textPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun HealthScoreCard(summary: CarHealthSummary) {
    val themeStyle = LocalThemeStyle.current
    val color = when (summary.overallLevel) {
        StatusLevel.GREEN -> StatusGreen
        StatusLevel.YELLOW -> StatusYellow
        StatusLevel.RED -> StatusRed
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("حالة السيارة", style = MaterialTheme.typography.labelMedium, color = themeStyle.textSecondary)
                Text(summary.statusTextAr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
            }
            Text("${summary.healthScore}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun NextActionHeroCard(
    alert: CarMaintenanceItemStatus,
    onMarkDoneNow: () -> Unit,
    onAddDetails: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val accentColor = if (alert.statusLevel == StatusLevel.RED) StatusRed else StatusYellow

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.10f)),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("إيه المطلوب دلوقتي؟", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = accentColor)
            }
            Spacer(Modifier.height(6.dp))
            Text(alert.itemNameAr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
            Text(
                text = if (alert.remainingKm <= 0) "تجاوزت حد الصيانة بـ ${kotlin.math.abs(alert.remainingKm)} كم!"
                else "متبقي ${alert.remainingKm} كم / حوالي ${alert.remainingDays} يوم",
                style = MaterialTheme.typography.bodyMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onMarkDoneNow, colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor), modifier = Modifier.weight(1f)) {
                    Text("تم الآن ✅", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                OutlinedButton(onClick = onAddDetails, modifier = Modifier.weight(1f)) {
                    Text("إضافة تفاصيل", color = themeStyle.textPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CollapsibleAlertsSection(alerts: List<CarMaintenanceItemStatus>, onMarkDone: (Long) -> Unit) {
    val themeStyle = LocalThemeStyle.current
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("باقي التنبيهات (${alerts.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.textSecondary)
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = themeStyle.textSecondary)
        }
        AnimatedVisibility(visible = expanded) {
            Column(Modifier.padding(top = 8.dp)) {
                alerts.forEach { AlertItemCard(it, onMarkDone) }
            }
        }
    }
}

@Composable
private fun AlertItemCard(alert: CarMaintenanceItemStatus, onMarkDone: (Long) -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val color = if (alert.statusLevel == StatusLevel.RED) StatusRed else StatusYellow
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(alert.itemNameAr, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                Text(
                    if (alert.remainingKm <= 0) "تجاوزت بـ ${kotlin.math.abs(alert.remainingKm)} كم!" else "متبقي ${alert.remainingKm} كم",
                    style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = { onMarkDone(alert.itemId) }, colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)) {
                Text("تم", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onLogService: () -> Unit,
    onLogFuel: () -> Unit,
    onFindWorkshop: () -> Unit,
    onResaleReport: () -> Unit,
    onObdScanner: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionButton("تسجيل صيانة", Icons.Default.Build, onLogService, Modifier.weight(1f))
        Spacer(Modifier.width(6.dp))
        QuickActionButton("سجل الوقود", Icons.Default.EvStation, onLogFuel, Modifier.weight(1f))
        Spacer(Modifier.width(6.dp))
        QuickActionButton("أفضل ورشة", Icons.Default.Store, onFindWorkshop, Modifier.weight(1f))
        Spacer(Modifier.width(6.dp))
        QuickActionButton("تقرير البيع", Icons.Default.MenuBook, onResaleReport, Modifier.weight(1f))
        Spacer(Modifier.width(6.dp))
        QuickActionButton("أكواد OBD", Icons.Default.QrCodeScanner, onObdScanner, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(themeStyle.primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = themeStyle.primaryColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
        }
    }
}

@Composable
private fun ViscosityRecommendationCard(car: CarEntity, onOpenWizard: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpenWizard() },
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape).background(themeStyle.primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Opacity, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("المساعد الذكي للزوجة المحرك", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.primaryColor)
                Text("الموصى بها حاليًا: ${car.recommendedViscosity}", style = MaterialTheme.typography.bodyMedium, color = themeStyle.textPrimary, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = themeStyle.textSecondary)
        }
    }
}

@Composable
private fun TrackingSummaryCard(todayKm: Double, onClick: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(themeStyle.primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("التتبع التلقائي للقيادة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                Text("${todayKm.roundToInt()} كم اليوم — اضغط للإعدادات", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = themeStyle.textSecondary)
        }
    }
}

@Composable
private fun MaintenanceSummaryOverviewCard(summary: CarHealthSummary, onNavigateToCatalog: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val allItems = summary.itemsByCategory.values.flatten()
    val greenCount = allItems.count { it.statusLevel == StatusLevel.GREEN }
    val yellowCount = allItems.count { it.statusLevel == StatusLevel.YELLOW }
    val redCount = allItems.count { it.statusLevel == StatusLevel.RED }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onNavigateToCatalog() },
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("ملخص حالة قطع الصيانة (${allItems.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = themeStyle.textSecondary)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadgeBox("ممتازة", "$greenCount", StatusGreen, Modifier.weight(1f))
                StatusBadgeBox("اقتربت", "$yellowCount", StatusYellow, Modifier.weight(1f))
                StatusBadgeBox("متأخرة", "$redCount", StatusRed, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatusBadgeBox(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    val themeStyle = LocalThemeStyle.current
    Box(
        modifier = modifier.background(themeStyle.navBg, themeStyle.cardShape).padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
        }
    }
}
