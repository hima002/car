@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hima.alwarsha.data.database.DefaultMaintenanceCatalog
import com.hima.alwarsha.data.model.CarMaintenanceItemStatus
import com.hima.alwarsha.data.model.StatusLevel
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.ui.theme.StatusGreen
import com.hima.alwarsha.ui.theme.StatusRed
import com.hima.alwarsha.ui.theme.StatusYellow
import com.hima.alwarsha.viewmodel.CarViewModel

private val categories = listOf(
    "ALL" to "الكل",
    DefaultMaintenanceCatalog.OILS_FLUIDS to "الزيوت والسوائل",
    DefaultMaintenanceCatalog.BELTS_ELEC to "السيور والكهرباء",
    DefaultMaintenanceCatalog.FILTERS_INTAKE to "الفلاتر والبوجيهات",
    DefaultMaintenanceCatalog.SUSPENSION_BRAKES to "العفشة والفرامل"
)

@Composable
fun MaintenanceCatalogScreen(viewModel: CarViewModel, onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    var selectedTab by remember { mutableStateOf("ALL") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("كتالوج الصيانة", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { (code, label) ->
                    FilterChip(
                        selected = selectedTab == code,
                        onClick = { selectedTab = code },
                        label = { Text(label, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = themeStyle.primaryColor,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.Black,
                            containerColor = themeStyle.navBg,
                            labelColor = themeStyle.textSecondary
                        )
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            val allItems = healthSummary?.itemsByCategory?.values?.flatten() ?: emptyList()
            val filtered = if (selectedTab == "ALL") allItems else allItems.filter { it.category == selectedTab }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { status ->
                    MaintenanceItemCard(status = status, onLogService = { viewModel.openLogServiceDialog(status.itemId) })
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun MaintenanceItemCard(status: CarMaintenanceItemStatus, onLogService: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val statusColor = when (status.statusLevel) {
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
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(statusColor))
                    Spacer(Modifier.width(8.dp))
                    Text(status.itemNameAr, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                }
                Text(
                    if (status.remainingKm <= 0) "منتهي!" else "متبقي ${status.remainingKm} كم",
                    style = MaterialTheme.typography.labelLarge, color = statusColor, fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { status.progressPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = themeStyle.navBg
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (status.recommendedSpecAr.isNotBlank()) {
                    Text(status.recommendedSpecAr, style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary, modifier = Modifier.weight(1f))
                }
                TextButton(onClick = onLogService) {
                    Text("تسجيل تغيير", color = themeStyle.primaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
