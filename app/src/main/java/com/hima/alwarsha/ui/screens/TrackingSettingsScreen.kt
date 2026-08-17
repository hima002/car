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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import kotlin.math.roundToInt

@Composable
fun TrackingSettingsScreen(
    onBack: () -> Unit,
    isTrackingEnabled: Boolean,
    onToggleTracking: (Boolean) -> Unit,
    onRequestBatteryOptimizationExemption: () -> Unit,
    todayKm: Double,
    weekKm: Double
) {
    val themeStyle = LocalThemeStyle.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التتبع التلقائي للقيادة", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = themeStyle.cardShape,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("تفعيل التتبع التلقائي", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                            }
                            Switch(checked = isTrackingEnabled, onCheckedChange = onToggleTracking, colors = SwitchDefaults.colors(checkedThumbColor = themeStyle.primaryColor))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "لما تفعّله، هيفضل شغال في الخلفية عشان يحسب الكيلومترات الفعلية اللي بتقطعها بالعربية، ويميّز بين إنك بتسوق أو ماشي. هيظهر إشعار دائم أثناء التشغيل، وهيستهلك بطارية إضافية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.textSecondary
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = themeStyle.cardShape,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("استثناء من تحسين البطارية", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "بعض الأجهزة (شاومي، هواوي، سامسونج) بتوقف الخدمات الخلفية بقوة لتوفير البطارية. استثنِ التطبيق عشان يفضل شغال باستمرار.",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.textSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onRequestBatteryOptimizationExemption,
                            colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("استثناء التطبيق الآن", color = Color.Black)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = themeStyle.cardShape,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        TrackingStat("اليوم", todayKm, themeStyle.primaryColor, themeStyle.textSecondary)
                        TrackingStat("آخر 7 أيام", weekKm, themeStyle.primaryColor, themeStyle.textSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingStat(label: String, valueKm: Double, valueColor: Color, labelColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${valueKm.roundToInt()} كم", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = valueColor)
        Text(label, style = MaterialTheme.typography.bodySmall, color = labelColor)
    }
}
