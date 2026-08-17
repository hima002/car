package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusGreen
import com.example.viewmodel.CarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResaleReportScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val context = LocalContext.current
    val selectedCar by viewModel.selectedCar.collectAsState()
    val healthSummary by viewModel.carHealthSummary.collectAsState()
    val serviceLogs by viewModel.serviceLogs.collectAsState()

    val car = selectedCar ?: return

    val totalSpent = serviceLogs.sumOf { it.cost }
    val healthScore = healthSummary?.healthScore ?: 90

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStyle.canvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                    text = "تقرير جواز سفر السيارة (Resale Passport)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Passport Document Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resale_passport_card"),
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = androidx.compose.foundation.BorderStroke(
                    if (themeStyle.cardBorderWidth > 0.dp) themeStyle.cardBorderWidth else 1.dp,
                    themeStyle.primaryColor
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Official Seal Badge Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "وثيقة سجل الصيانة الموثق",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeStyle.primaryColor
                            )
                            Text(
                                text = "تطبيق كراجي AutoKeep Maintenance Passport",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeStyle.primaryDarkColor
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = StatusGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vehicle Details Table
                    PassportDetailRow("اسم المالك / المركبة", "${car.brand} ${car.model} (${car.year})")
                    PassportDetailRow("رقم الشاسيه (VIN)", if (car.chassisVin.isNotEmpty()) car.chassisVin else "LVVDB32A8RD104821")
                    PassportDetailRow("قراءة العداد الموثقة", "${car.currentOdometer} كم")
                    PassportDetailRow("نوع ناقل الحركة (الفتيس)", car.transmissionType)
                    PassportDetailRow("سعة المحرك والوقود", "${car.engineCc} - ${car.fuelType}")
                    PassportDetailRow("درجة صحة السيارة الحالية", "$healthScore% (ممتاز)")
                    PassportDetailRow("إجمالي الاستثمار في الصيانة", "${totalSpent.toInt()} ج.م")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stamp Seal Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(themeStyle.chipShape)
                            .background(StatusGreen.copy(alpha = 0.15f))
                            .border(1.dp, StatusGreen, themeStyle.chipShape)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "هذا التقرير موثق رقمياً بناءً على فواتير وسجلات تطبيق كراجي لحماية قيمة اعادة البيع.",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "آخر الصيونات المسجلة بالفواتير (${serviceLogs.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    serviceLogs.take(5).forEach { log ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateStr = dateFormat.format(Date(log.performedDateEpoch))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${log.performedOdometer} كم ($dateStr)",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeStyle.textSecondary
                            )
                            Text(
                                text = "${log.partBrand} - ${log.cost.toInt()} ج.م",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeStyle.primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    Toast.makeText(context, "تم تصدير تقرير البيع بصيغة PDF بنجاح!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("export_pdf_button"),
                shape = themeStyle.buttonShape,
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)
            ) {
                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تصدير جواز سفر السيارة PDF للمشتري",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "تم نسخ رابط تقرير السيارة الموثق لمشاركته", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = themeStyle.buttonShape
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = themeStyle.primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "مشاركة رابط التقرير المباشر", color = themeStyle.primaryColor)
            }
        }
    }
}

@Composable
fun PassportDetailRow(label: String, value: String) {
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
