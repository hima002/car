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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusYellow
import com.example.viewmodel.CarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val popularBrands = listOf(
        "Hyundai" to "هيونداي (Hyundai)",
        "Toyota" to "تويوتا (Toyota)",
        "Chery" to "شيري (Chery)",
        "Kia" to "كيا (Kia)",
        "Nissan" to "نيسان (Nissan)",
        "MG" to "إم جي (MG)",
        "Mitsubishi" to "ميتسوبيشي (Mitsubishi)",
        "Chevrolet" to "شيفروليه (Chevrolet)",
        "Renault" to "رينو (Renault)",
        "Peugeot" to "بيجو (Peugeot)",
        "Fiat" to "فيات (Fiat)",
        "BMW" to "بي إم دبليو (BMW)",
        "Mercedes" to "مرسيدس (Mercedes-Benz)",
        "Volkswagen" to "فولكس فاجن (Volkswagen)",
        "Skoda" to "سكودا (Skoda)",
        "Suzuki" to "سوزوكي (Suzuki)",
        "Geely" to "جيلي (Geely)",
        "BYD" to "بي واي دي (BYD)",
        "Subaru" to "سوبارو (Subaru)",
        "Ford" to "فورد (Ford)",
        "Mazda" to "مازدا (Mazda)",
        "Honda" to "هوندا (Honda)",
        "OTHER" to "ماركة أخرى (أدخل يدوياً)"
    )

    var selectedBrandCode by remember { mutableStateOf("Hyundai") }
    var customBrandText by remember { mutableStateOf("") }
    var brandDropdownExpanded by remember { mutableStateOf(false) }

    var model by remember { mutableStateOf("Elantra AD") }
    var yearText by remember { mutableStateOf("2021") }
    var fuelType by remember { mutableStateOf("Gasoline 95") }
    var transmissionType by remember { mutableStateOf("TORQUE_CONVERTER") }
    var engineCc by remember { mutableStateOf("1.6L") }
    var chassisVin by remember { mutableStateOf("") }
    var odometerText by remember { mutableStateOf("85000") }

    var isOnboardingModeZeroKm by remember { mutableStateOf(false) } // false = Used Car, true = Zero KM
    var safetyBaselineReset by remember { mutableStateOf(true) }
    var isSevereDriving by remember { mutableStateOf(false) }

    var transDropdownExpanded by remember { mutableStateOf(false) }

    val transOptions = listOf(
        "TORQUE_CONVERTER" to "أوتوماتيك قياسي (Torque Converter)",
        "CVT" to "CVT (ناقل متغير باستمرار)",
        "DCT_WET" to "DCT Wet (ثنائي الدبرياج مبلل)",
        "DCT_DRY" to "DCT Dry (ثنائي الدبرياج جاف)",
        "MANUAL" to "Manual (ناقل يدوي)"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStyle.canvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                Column {
                    Text(
                        text = "إضافة سيارة جديدة (Smart Onboarding)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                    Text(
                        text = "قم بإنشاء ملف الصيانة الذكي لسيارتك",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.primaryColor
                    )
                }
            }

            // 1. CLEAR EXPLANATION CARD FOR VEHICLE CONDITION SELECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = themeStyle.cardShape,
                colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. حدد حالة السيارة الحالية عند التسجيل:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.primaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Used Car Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(themeStyle.chipShape)
                                .background(if (!isOnboardingModeZeroKm) themeStyle.primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (!isOnboardingModeZeroKm) themeStyle.primaryColor else themeStyle.textSecondary.copy(alpha = 0.3f),
                                    themeStyle.chipShape
                                )
                                .clickable { isOnboardingModeZeroKm = false }
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !isOnboardingModeZeroKm,
                                        onClick = { isOnboardingModeZeroKm = false },
                                        colors = RadioButtonDefaults.colors(selectedColor = themeStyle.primaryColor)
                                    )
                                    Text(
                                        text = "سيارة مستعملة 🚗",
                                        fontWeight = FontWeight.Bold,
                                        color = themeStyle.textPrimary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "تستلزم إدخال قراءة العداد الحالية لحساب جدول الصيانات المتبقية بفرز دقيق.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = themeStyle.textSecondary
                                )
                            }
                        }

                        // Zero KM Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(themeStyle.chipShape)
                                .background(if (isOnboardingModeZeroKm) themeStyle.primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isOnboardingModeZeroKm) themeStyle.primaryColor else themeStyle.textSecondary.copy(alpha = 0.3f),
                                    themeStyle.chipShape
                                )
                                .clickable {
                                    isOnboardingModeZeroKm = true
                                    odometerText = "0"
                                }
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isOnboardingModeZeroKm,
                                        onClick = {
                                            isOnboardingModeZeroKm = true
                                            odometerText = "0"
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = themeStyle.primaryColor)
                                    )
                                    Text(
                                        text = "سيارة زيرو 🆕",
                                        fontWeight = FontWeight.Bold,
                                        color = themeStyle.textPrimary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "العداد يبدأ من (0 كم) بقطع غيار وزيوت فابريكة جديدة تماماً.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = themeStyle.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. BRAND DROPDOWN LIST (ماركة السيارة)
            Text(
                text = "2. بيانات الماركة والموديل:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeStyle.textPrimary
            )

            ExposedDropdownMenuBox(
                expanded = brandDropdownExpanded,
                onExpandedChange = { brandDropdownExpanded = !brandDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = popularBrands.find { it.first == selectedBrandCode }?.second ?: selectedBrandCode,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("اختر ماركة السيارة (Brand) 🚘") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("add_car_brand_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = brandDropdownExpanded,
                    onDismissRequest = { brandDropdownExpanded = false }
                ) {
                    popularBrands.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (selectedBrandCode == code) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedBrandCode = code
                                brandDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (selectedBrandCode == "OTHER") {
                OutlinedTextField(
                    value = customBrandText,
                    onValueChange = { customBrandText = it },
                    label = { Text("اكتب اسم الماركة بالعربية أو الإنجليزية") },
                    modifier = Modifier.fillMaxWidth().testTag("add_car_custom_brand_field")
                )
            }

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("الموديل (Model e.g. Elantra, Corolla, Tucson)") },
                modifier = Modifier.fillMaxWidth().testTag("add_car_model_field")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = { Text("سنة الصنع") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = engineCc,
                    onValueChange = { engineCc = it },
                    label = { Text("سعة المحرك (CC)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Transmission Dropdown
            ExposedDropdownMenuBox(
                expanded = transDropdownExpanded,
                onExpandedChange = { transDropdownExpanded = !transDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = transOptions.find { it.first == transmissionType }?.second ?: transmissionType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("نوع الناقل / الفتيس") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = transDropdownExpanded,
                    onDismissRequest = { transDropdownExpanded = false }
                ) {
                    transOptions.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                transmissionType = code
                                transDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = odometerText,
                onValueChange = { odometerText = it },
                label = { Text("قراءة العداد الحالية (كم)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("add_car_odometer_field")
            )

            OutlinedTextField(
                value = chassisVin,
                onValueChange = { chassisVin = it },
                label = { Text("رقم الشاسيه / VIN (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isOnboardingModeZeroKm) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = safetyBaselineReset,
                        onCheckedChange = { safetyBaselineReset = it },
                        colors = CheckboxDefaults.colors(checkedColor = themeStyle.primaryColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تفعيل بروتوكول تصغير الأمان الاحترازي (Safety Baseline Reset) للقطع الحساسة (سير الكاتينة، الزيت، مياه التبريد)",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusYellow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = {
                    val finalBrand = if (selectedBrandCode == "OTHER") {
                        if (customBrandText.isNotBlank()) customBrandText else "ماركة أخرى"
                    } else {
                        selectedBrandCode
                    }
                    val year = yearText.toIntOrNull() ?: 2021
                    val odo = odometerText.toIntOrNull() ?: 0
                    viewModel.addNewVehicle(
                        brand = finalBrand,
                        model = model,
                        year = year,
                        fuelType = fuelType,
                        transmissionType = transmissionType,
                        engineCc = engineCc,
                        chassisVin = chassisVin,
                        currentOdometer = odo,
                        isZeroKm = isOnboardingModeZeroKm,
                        isSevereDriving = isSevereDriving,
                        safetyResetAll = safetyBaselineReset && !isOnboardingModeZeroKm
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_vehicle_button"),
                shape = themeStyle.buttonShape,
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إنشاء وتأكيد ملف الصيانة الذكي",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

