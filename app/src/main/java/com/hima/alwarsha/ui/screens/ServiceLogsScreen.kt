@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.model.MaintenanceItemNameLookup
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.ui.theme.StatusGreen
import com.hima.alwarsha.viewmodel.CarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServiceLogsScreen(viewModel: CarViewModel, onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val serviceLogs by viewModel.serviceLogs.collectAsState()
    val fuelLogs by viewModel.fuelLogs.collectAsState()
    val maintenanceCatalog by viewModel.maintenanceCatalog.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val totalServiceCost = serviceLogs.sumOf { it.cost }
    val totalFuelCost = fuelLogs.sumOf { it.totalPrice }
    val itemNames = remember(maintenanceCatalog) { MaintenanceItemNameLookup(maintenanceCatalog) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل الصيانة والوقود", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = themeStyle.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStyle.canvasBg)
            )
        },
        containerColor = themeStyle.canvasBg
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TcoStatBox("مصاريف الصيانة", "${totalServiceCost.toInt()} ج.م", StatusGreen, Modifier.weight(1f))
                TcoStatBox("مصاريف الوقود", "${totalFuelCost.toInt()} ج.م", themeStyle.primaryColor, Modifier.weight(1f))
            }

            TabRow(selectedTabIndex = selectedTab, containerColor = themeStyle.canvasBg, contentColor = themeStyle.primaryColor) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("الصيانة") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("الوقود") })
            }

            if (selectedTab == 0) {
                LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(serviceLogs) { log -> ServiceLogRow(log, itemNames.nameFor(log.itemId)) }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            } else {
                LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(fuelLogs) { log -> FuelLogRow(log) }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TcoStatBox(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = modifier,
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ServiceLogRow(log: ServiceLogEntity, itemName: String) {
    val themeStyle = LocalThemeStyle.current
    val dateStr = remember(log.performedDateEpoch) { SimpleDateFormat("d MMM yyyy", Locale("ar")).format(Date(log.performedDateEpoch)) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(itemName, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                Text("${log.cost.toInt()} ج.م", color = themeStyle.primaryColor, fontWeight = FontWeight.Bold)
            }
            Text("عند ${log.performedOdometer} كم — $dateStr", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
            if (log.workshopName.isNotBlank()) {
                Text("الورشة: ${log.workshopName}", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
            }
        }
    }
}

@Composable
private fun FuelLogRow(log: FuelLogEntity) {
    val themeStyle = LocalThemeStyle.current
    val dateStr = remember(log.dateEpoch) { SimpleDateFormat("d MMM yyyy", Locale("ar")).format(Date(log.dateEpoch)) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${log.liters} لتر", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                Text("${log.totalPrice.toInt()} ج.م", color = themeStyle.primaryColor, fontWeight = FontWeight.Bold)
            }
            Text("عند ${log.odometer} كم — $dateStr", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
        }
    }
}
