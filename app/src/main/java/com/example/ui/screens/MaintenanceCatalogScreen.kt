package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalThemeStyle
import com.example.viewmodel.CarViewModel

@Composable
fun MaintenanceCatalogScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val selectedCar by viewModel.selectedCar.collectAsState()
    val catalogItems by viewModel.maintenanceCatalog.collectAsState()
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    val showLogServiceDialog by viewModel.showLogServiceDialog.collectAsState()
    val showAddCustomScheduleDialog by viewModel.showAddCustomScheduleDialog.collectAsState()
    val selectedItemIdForLog by viewModel.selectedItemIdForLog.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val oemSpec = viewModel.getOemSpecs()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedCategoryTab by remember { mutableStateOf("ALL") }
    val tabs = listOf("قائمة وحالة القطع 🛠️", "دليل المواصفات OEM 📑")

    if (selectedCar == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeStyle.canvasBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "يرجى إضافة سيارة أولاً لعرض جدول ومواصفات الصيانة.",
                    style = MaterialTheme.typography.titleMedium,
                    color = themeStyle.textPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                    shape = themeStyle.buttonShape
                ) {
                    Text("رجوع", color = Color.White)
                }
            }
        }
        return
    }

    val car = selectedCar!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStyle.canvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = themeStyle.textPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "دليل الصيانة والمواصفات OEM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.textPrimary
                        )
                        Text(
                            text = "${car.brand} ${car.model} (${car.year})",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.primaryColor
                        )
                    }
                }

                Button(
                    onClick = { viewModel.openAddCustomScheduleDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                    shape = themeStyle.buttonShape,
                    modifier = Modifier.testTag("catalog_add_schedule_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة تذكير 📅", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = themeStyle.navBg,
                contentColor = themeStyle.primaryColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = themeStyle.primaryColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTabIndex == index) themeStyle.primaryColor else themeStyle.textSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTabIndex == 0) {
                // Detailed Maintenance Items List
                Column {
                    CategoryTabsRow(
                        selectedTab = selectedCategoryTab,
                        onTabSelected = { selectedCategoryTab = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        healthSummary?.let { summary ->
                            val allStatuses = summary.itemsByCategory.values.flatten()
                            val filteredList = when (selectedCategoryTab) {
                                "OILS_FLUIDS" -> summary.itemsByCategory["OILS_FLUIDS"] ?: emptyList()
                                "BELTS_ELEC" -> summary.itemsByCategory["BELTS_ELEC"] ?: emptyList()
                                "SUSPENSION_BRAKES" -> summary.itemsByCategory["SUSPENSION_BRAKES"] ?: emptyList()
                                "FILTERS_INTAKE" -> summary.itemsByCategory["FILTERS_INTAKE"] ?: emptyList()
                                else -> allStatuses
                            }

                            items(filteredList) { status ->
                                MaintenanceItemCard(
                                    status = status,
                                    onLogService = { viewModel.openLogServiceDialog(status.itemId) }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            } else {
                // OEM Technical Specs Guide
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        oemSpec?.let { spec ->
                            OemSpecsCard(spec = spec)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }

        if (showLogServiceDialog) {
            com.example.ui.components.LogServiceDialog(
                currentOdometer = car.currentOdometer,
                maintenanceItems = catalogItems,
                preselectedItemId = selectedItemIdForLog,
                onDismiss = { viewModel.closeLogServiceDialog() },
                onConfirm = { itemId, odo, cost, brand, viscosity, workshop, notes ->
                    viewModel.recordServiceLog(itemId, odo, cost, brand, viscosity, workshop, notes)
                }
            )
        }

        if (showAddCustomScheduleDialog) {
            com.example.ui.components.AddCustomScheduleDialog(
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
    }
}

@Composable
fun CatalogItemCard(item: com.example.data.entity.MaintenanceItemEntity) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.itemNameAr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )

                if (item.isCritical) {
                    Text(
                        text = "قطعة حاسمة ⚠️",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.descriptionAr,
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.textSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "فترة التغيير القياسية: كل ${item.defaultKmInterval} كم / ${item.defaultMonthInterval} شهر",
                    style = MaterialTheme.typography.labelMedium,
                    color = themeStyle.primaryColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "المواصفة الموصى بها: ${item.recommendedSpecAr}",
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.primaryDarkColor
            )
        }
    }
}

@Composable
fun OemSpecsCard(spec: com.example.data.model.CarOemSpec) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("oem_specs_guide_card"),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "كتالوج المواصفات الفنية OEM - ${spec.brand} ${spec.model}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.primaryColor
            )
            Text(
                text = "سنة الصنع: ${spec.yearRange}",
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpecRow("سعة زيت المحرك", "${spec.engineOilCapacityLiters} لتر (${spec.recommendedOilViscosity})")
            SpecRow("زيت الناقل (الفتيس)", "${spec.transFluidType} (${spec.transFluidCapacityLiters} لتر)")
            SpecRow("ضغط الإطارات الصحيح", "أمام: ${spec.tirePressurePsiFront} PSI | خلف: ${spec.tirePressurePsiRear} PSI")
            SpecRow("سعة خزان البنزين", "${spec.fuelTankCapacityLiters} لتر")
            SpecRow("خلوص شمعات الإشعال", spec.sparkPlugGapMm)
            SpecRow("مواصفة البطارية", spec.batterySpec)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "أرقام قطع الغيار الأصلية (Genuine OEM Part Numbers):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = themeStyle.primaryDarkColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            SpecRow("فلتر الزيت الأصلي", spec.oemOilFilterPart)
            SpecRow("بوجيهات الإشعال", spec.oemSparkPlugsPart)
            SpecRow("سير الكاتينة / المجموعة", spec.oemTimingBeltPart)
            SpecRow("تيل الفرامل الأمامي", spec.oemBrakePadsFrontPart)
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    val themeStyle = LocalThemeStyle.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = themeStyle.textSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = themeStyle.textPrimary, fontWeight = FontWeight.Bold)
    }
}

