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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.ObdCode
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.viewmodel.CarViewModel

@Composable
fun ObdDictionaryScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val obdCodes = viewModel.getObdCodes()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCodes = if (searchQuery.isBlank()) {
        obdCodes
    } else {
        obdCodes.filter {
            it.code.contains(searchQuery, ignoreCase = true) ||
                    it.titleAr.contains(searchQuery, ignoreCase = true) ||
                    it.titleEn.contains(searchQuery, ignoreCase = true)
        }
    }

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
                Column {
                    Text(
                        text = "قاموس الأعطال ورموز OBD-II باللغة العربية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                    Text(
                        text = "ابحث برقم الكود المقروء من جهازم القارئ (مثلاً P0300)",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("ابحث برقم الكود أو اسم العطل (e.g. P0300)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = themeStyle.primaryColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("obd_search_input_field")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCodes) { code ->
                    ObdCodeCard(obd = code)
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun ObdCodeCard(obd: ObdCode) {
    val themeStyle = LocalThemeStyle.current
    val severityColor = when (obd.severity) {
        "CRITICAL" -> StatusRed
        "HIGH" -> StatusRed
        "MEDIUM" -> StatusYellow
        else -> StatusGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("obd_code_card_${obd.code}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = obd.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeStyle.primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${obd.category})",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.primaryDarkColor
                    )
                }

                Text(
                    text = obd.severity,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = obd.titleAr,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = themeStyle.textPrimary
            )

            Text(
                text = obd.titleEn,
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.textSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الأعراض المصاحبة: ${obd.symptomsAr}",
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "خطوات الإصلاح والميكانيكا: ${obd.solutionAr}",
                style = MaterialTheme.typography.bodySmall,
                color = StatusGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
