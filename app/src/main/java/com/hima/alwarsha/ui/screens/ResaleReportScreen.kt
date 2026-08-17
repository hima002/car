@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import android.content.Intent
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.hima.alwarsha.data.model.MaintenanceItemNameLookup
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.util.PdfReportGenerator
import com.hima.alwarsha.viewmodel.CarViewModel

@Composable
fun ResaleReportScreen(viewModel: CarViewModel, onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val context = LocalContext.current
    val car by viewModel.selectedCar.collectAsState()
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    val serviceLogs by viewModel.serviceLogs.collectAsState()
    val maintenanceCatalog by viewModel.maintenanceCatalog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تقرير إعادة البيع", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
                "بيصدّر التقرير ملخص رسمي بحالة سيارتك وسجل الصيانة الفعلي المسجل في التطبيق، يساعدك تثبت جدية العناية بالسيارة وقت البيع.",
                style = MaterialTheme.typography.bodyMedium,
                color = themeStyle.textSecondary
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val currentCar = car ?: return@Button
                    val itemNames = MaintenanceItemNameLookup(maintenanceCatalog)
                    val file = PdfReportGenerator.generate(context, currentCar, healthSummary, serviceLogs) { itemNames.nameFor(it) }
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة التقرير"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                enabled = car != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إنشاء ومشاركة التقرير", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
