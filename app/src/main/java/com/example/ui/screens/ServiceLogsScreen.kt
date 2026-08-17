package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.theme.LocalThemeStyle
import com.example.viewmodel.CarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServiceLogsScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val serviceLogs by viewModel.serviceLogs.collectAsState()
    val fuelLogs by viewModel.fuelLogs.collectAsState()
    val catalogItems by viewModel.maintenanceCatalog.collectAsState()
    val selectedCar by viewModel.selectedCar.collectAsState()

    val itemMap = catalogItems.associateBy { it.id }

    val totalServiceCost = serviceLogs.sumOf { it.cost }
    val totalFuelCost = fuelLogs.sumOf { it.totalPrice }
    val totalSpent = totalServiceCost + totalFuelCost

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
                    text = "يرجى إضافة سيارة أولاً لعرض سجلات الصيانة والتكاليف.",
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
    val totalDrivenKm = car.currentOdometer
    val costPerKm = if (totalDrivenKm > 0) totalSpent / totalDrivenKm else 0.0

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("سجل الصيانة 🛠️", "حاسبة تكلفة التشغيل TCO 📊")

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

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = themeStyle.textPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "سجل الصرف والتكاليف والخدمات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = themeStyle.navBg,
                contentColor = themeStyle.primaryColor
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
                // Logs list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Button(
                            onClick = { viewModel.openLogServiceDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                            shape = themeStyle.buttonShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_new_service_log_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إضافة فاتورة صيانة جديدة", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    items(serviceLogs) { log ->
                        val itemName = itemMap[log.itemId]?.itemNameAr ?: "صيانة عامة"
                        ServiceLogCard(log = log, itemName = itemName)
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            } else {
                // TCO Calculator
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        TcoCalculatorCard(
                            totalSpent = totalSpent,
                            totalServiceCost = totalServiceCost,
                            totalFuelCost = totalFuelCost,
                            totalDrivenKm = totalDrivenKm,
                            costPerKm = costPerKm
                        )
                    }

                    item {
                        Text(
                            text = "تفاصيل تمويل الوقود الموثّق:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.textPrimary
                        )
                    }

                    items(fuelLogs) { fuel ->
                        FuelLogCard(fuel = fuel)
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun ServiceLogCard(log: com.example.data.entity.ServiceLogEntity, itemName: String) {
    val themeStyle = LocalThemeStyle.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(log.performedDateEpoch))

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
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )

                Text(
                    text = "${log.cost.toInt()} ج.م",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeStyle.primaryColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "العداد: ${log.performedOdometer} كم • التاريخ: $dateStr",
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.textSecondary
            )

            if (log.partBrand.isNotEmpty()) {
                Text(
                    text = "القطعة/الزيت: ${log.partBrand} (${log.viscosityUsed})",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.primaryDarkColor
                )
            }

            if (log.workshopName.isNotEmpty()) {
                Text(
                    text = "المركز/الورشة: ${log.workshopName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.textSecondary
                )
            }

            if (log.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ملاحظات: ${log.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.textPrimary
                )
            }
        }
    }
}

@Composable
fun TcoCalculatorCard(
    totalSpent: Double,
    totalServiceCost: Double,
    totalFuelCost: Double,
    totalDrivenKm: Int,
    costPerKm: Double
) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tco_summary_card"),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "حاسبة التكلفة الفعلية للتشغيل (Total Cost of Ownership)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TcoStatBox("إجمالي المصاريف", "${totalSpent.toInt()} ج.م", themeStyle.primaryColor)
                TcoStatBox("التكلفة لكل كم", String.format("%.2f ج.م/كم", costPerKm), themeStyle.primaryDarkColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TcoStatBox("مصاريف الصيانة", "${totalServiceCost.toInt()} ج.م", StatusGreen)
                TcoStatBox("مصاريف الوقود", "${totalFuelCost.toInt()} ج.م", themeStyle.primaryColor)
            }
        }
    }
}

@Composable
fun TcoStatBox(label: String, value: String, color: Color) {
    val themeStyle = LocalThemeStyle.current
    Column(
        modifier = Modifier
            .background(themeStyle.navBg, themeStyle.cardShape)
            .border(themeStyle.cardBorderWidth, themeStyle.cardBorderColor, themeStyle.cardShape)
            .padding(12.dp)
            .width(140.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun FuelLogCard(fuel: com.example.data.entity.FuelLogEntity) {
    val themeStyle = LocalThemeStyle.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(fuel.dateEpoch))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "تمويل ${fuel.fuelType} (${fuel.liters} لتر)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )
                Text(
                    text = "العداد: ${fuel.odometer} كم • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.textSecondary
                )
            }

            Text(
                text = "${fuel.totalPrice.toInt()} ج.م",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.primaryColor
            )
        }
    }
}

