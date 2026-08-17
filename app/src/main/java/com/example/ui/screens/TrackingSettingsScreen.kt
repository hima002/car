package com.example.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalThemeStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
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
    val isAr = LocalAppLanguage.current == AppLanguage.AR

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAr) "التتبع التلقائي للقيادة" else "Automatic Driving Tracking",
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isAr) "رجوع" else "Back",
                            tint = themeStyle.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStyle.canvasBg)
            )
        },
        containerColor = themeStyle.canvasBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = themeStyle.primaryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "تفعيل التتبع التلقائي" else "Enable Automatic Tracking",
                                    fontWeight = FontWeight.Bold,
                                    color = themeStyle.textPrimary
                                )
                            }
                            Switch(
                                checked = isTrackingEnabled,
                                onCheckedChange = onToggleTracking,
                                colors = SwitchDefaults.colors(checkedThumbColor = themeStyle.primaryColor)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) {
                                "لما تفعّله، هيفضل شغال دايمًا في الخلفية (حتى لو التطبيق مقفول) عشان يحسب الكيلومترات اللي بتقطعها فعليًا بالعربية، وهيقدر يميّز بين إنك بتسوق أو ماشي على رجلك. هيظهر إشعار دائم أثناء التشغيل، وهيستهلك بطارية إضافية."
                            } else {
                                "Once enabled, it keeps running in the background (even if the app is closed) to measure the km you actually drive, and can tell driving apart from walking. A persistent notification stays visible while active, and it uses extra battery."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.textSecondary
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryAlert,
                                contentDescription = null,
                                tint = themeStyle.primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "استثناء من تحسين البطارية" else "Battery Optimization Exemption",
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.textPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) {
                                "بعض الأجهزة (شاومي، هواوي، سامسونج) بتوقف الخدمات الخلفية بقوة لتوفير البطارية، وده هيمنع التتبع من الدقة. استثنِ التطبيق من تحسين البطارية عشان يفضل شغال باستمرار."
                            } else {
                                "Some devices (Xiaomi, Huawei, Samsung) aggressively kill background services to save battery, which breaks tracking accuracy. Exempt the app so it keeps running reliably."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRequestBatteryOptimizationExemption,
                            colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isAr) "استثناء التطبيق الآن" else "Exempt App Now")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TrackingStat(
                            label = if (isAr) "اليوم" else "Today",
                            valueKm = todayKm,
                            valueColor = themeStyle.primaryColor,
                            labelColor = themeStyle.textSecondary
                        )
                        TrackingStat(
                            label = if (isAr) "آخر 7 أيام" else "Last 7 Days",
                            valueKm = weekKm,
                            valueColor = themeStyle.primaryColor,
                            labelColor = themeStyle.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingStat(
    label: String,
    valueKm: Double,
    valueColor: androidx.compose.ui.graphics.Color,
    labelColor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${valueKm.roundToInt()} كم",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = labelColor)
    }
}
