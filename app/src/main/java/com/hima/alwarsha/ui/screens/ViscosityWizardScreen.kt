@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.repository.ViscosityEngine
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.viewmodel.CarViewModel

private val dropOptions = listOf(
    "NO_DROP" to "لا يوجد نقص في مستوى الزيت",
    "SLIGHT_DROP" to "نقص بسيط (أقل من 0.5 لتر)",
    "HEAVY_DROP" to "نقص واضح / دخان من العفريت"
)

@Composable
fun ViscosityWizardScreen(viewModel: CarViewModel, onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val car by viewModel.selectedCar.collectAsState()
    var selected by remember(car) { mutableStateOf(car?.oilLevelDropStatus ?: "NO_DROP") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المساعد الذكي للزوجة المحرك", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Text(
                "هل لاحظت نقص في مستوى زيت المحرك مؤخرًا؟",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.textPrimary
            )
            Spacer(Modifier.height(16.dp))

            val currentOdometer = car?.currentOdometer ?: 0
            dropOptions.forEach { (code, label) ->
                val recommendation = ViscosityEngine.calculate(currentOdometer, code)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { selected = code },
                    shape = themeStyle.cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected == code) themeStyle.primaryColor.copy(alpha = 0.15f) else themeStyle.cardBg
                    ),
                    border = BorderStroke(
                        if (selected == code) 1.5.dp else themeStyle.cardBorderWidth,
                        if (selected == code) themeStyle.primaryColor else themeStyle.cardBorderColor
                    )
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(label, fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("التوصية: ${recommendation.label}", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.applyViscosityDecision(selected, ViscosityEngine.calculate(currentOdometer, selected))
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تأكيد", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
