package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.viewmodel.CarViewModel

@Composable
fun ViscosityWizardScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val selectedCar by viewModel.selectedCar.collectAsState()
    val car = selectedCar ?: return

    var selectedOption by remember { mutableStateOf(car.oilLevelDropStatus) }

    val recommendedViscosity = when (selectedOption) {
        "NO_DROP" -> if (car.currentOdometer < 100000) "5W-30 / 0W-20 Fully Synthetic" else "5W-30 / 5W-40 Fully Synthetic"
        "SLIGHT_DROP" -> "5W-40 / 10W-40 Semi-Synthetic"
        "HEAVY_DROP" -> "10W-40 / 15W-50 High Mileage Oils"
        else -> "5W-30 Fully Synthetic"
    }

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
                    text = "المساعد الذكي لزوجة المحرك",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Car Mileage Context Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = themeStyle.primaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "السيارة: ${car.brand} ${car.model}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.textPrimary
                        )
                        Text(
                            text = "عداد الكيلومترات الحالي: ${car.currentOdometer} كم",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeStyle.primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Question Header
            Text(
                text = "سؤال التشخيص الفني:",
                style = MaterialTheme.typography.labelLarge,
                color = themeStyle.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "هل لاحظت أي نقص في مستوى زيت المحرك بين الصيانة والأخرى؟",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1
            ViscosityOptionCard(
                title = "خيار 1: لا يوجد نقص على الإطلاق 🟢",
                subtitle = "الزيت ينزل بمستواه الطبيعي بدون أي نقصان",
                recommendationText = "النتيجة: الاستمرار على $recommendedViscosity للحفاظ على كفاءة البساتن وتوفير البنزين.",
                isSelected = selectedOption == "NO_DROP",
                statusColor = StatusGreen,
                onClick = { selectedOption = "NO_DROP" },
                testTag = "viscosity_option_no_drop"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2
            ViscosityOptionCard(
                title = "خيار 2: نقص خفيف أقل من 0.5 لتر 🟡",
                subtitle = "يوجد استهلاك خفيف جداً بين مقاس الصيانة والأخرى",
                recommendationText = "النتيجة: رفع اللزوجة إلى 5W-40 / 10W-40 وتفعيل تنبيه فحص مستوى الزيت كل 2,000 كم.",
                isSelected = selectedOption == "SLIGHT_DROP",
                statusColor = StatusYellow,
                onClick = { selectedOption = "SLIGHT_DROP" },
                testTag = "viscosity_option_slight_drop"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 3
            ViscosityOptionCard(
                title = "خيار 3: نقص ملحوظ أو ظهور أدخنة عادم 🔴",
                subtitle = "الزيت ينقص بشكل متكرر أو يوجد تسريب / تبخير عالية",
                recommendationText = "النتيجة: التحويل إلى 10W-40 / 15W-50 High Mileage لحماية الحشوات وتسكيت صوت المحرك.",
                isSelected = selectedOption == "HEAVY_DROP",
                statusColor = StatusRed,
                onClick = { selectedOption = "HEAVY_DROP" },
                testTag = "viscosity_option_heavy_drop"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Apply Button
            Button(
                onClick = {
                    viewModel.applyViscosityDecision(selectedOption, recommendedViscosity)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply_viscosity_recommendation_button"),
                shape = themeStyle.buttonShape,
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تأكيد وتطبيق اللزوجة ($recommendedViscosity)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ViscosityOptionCard(
    title: String,
    subtitle: String,
    recommendationText: String,
    isSelected: Boolean,
    statusColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else themeStyle.cardBorderWidth,
            if (isSelected) statusColor else themeStyle.cardBorderColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.textSecondary
                    )
                }
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(themeStyle.chipShape)
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = recommendationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
