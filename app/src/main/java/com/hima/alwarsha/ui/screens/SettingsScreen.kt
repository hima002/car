@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.viewmodel.CarViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: CarViewModel, onNavigateToTracking: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    val context = LocalContext.current
    var pendingRestoreJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.exportBackupJson { json ->
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
            }.onSuccess {
                Toast.makeText(context, "تم حفظ النسخة الاحتياطية بنجاح", Toast.LENGTH_LONG).show()
            }.onFailure {
                Toast.makeText(context, "حصل خطأ أثناء الحفظ", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val json = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull()
        if (json.isNullOrBlank()) {
            Toast.makeText(context, "تعذّرت قراءة الملف", Toast.LENGTH_LONG).show()
        } else {
            pendingRestoreJson = json
        }
    }

    pendingRestoreJson?.let { json ->
        AlertDialog(
            onDismissRequest = { pendingRestoreJson = null },
            title = { Text("استعادة نسخة احتياطية") },
            text = { Text("هيتم استبدال كل بيانات التطبيق الحالية بمحتوى النسخة الاحتياطية دي. متأكد؟") },
            confirmButton = {
                Button(onClick = {
                    pendingRestoreJson = null
                    viewModel.importBackupJson(json) { success ->
                        Toast.makeText(
                            context,
                            if (success) "تمت الاستعادة بنجاح" else "الملف مش نسخة احتياطية صالحة",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) { Text("استعادة") }
            },
            dismissButton = { TextButton(onClick = { pendingRestoreJson = null }) { Text("إلغاء") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("الإعدادات", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStyle.canvasBg)
            )
        },
        containerColor = themeStyle.canvasBg
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToTracking() },
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GpsFixed, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("التتبع التلقائي للقيادة (GPS)", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = themeStyle.textSecondary)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("نسخة احتياطية واستعادة", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "احفظ نسخة من كل بياناتك في ملف تقدر تخزّنه في جوجل درايف أو أي مكان تختاره، واستخدمها لاسترجاع بياناتك لو مسحت التطبيق أو غيّرت الموبايل.",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.textSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val stamp = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.US).format(java.util.Date())
                                exportLauncher.launch("alwarsha_backup_$stamp.json")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("حفظ نسخة")
                        }
                        OutlinedButton(
                            onClick = { importLauncher.launch(arrayOf("application/json")) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("استعادة")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = themeStyle.textSecondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("الورشة — مساعدك الشخصي لصيانة سيارتك", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
                }
            }
        }
    }
}
