package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.entity.CarEntity
import com.example.data.entity.MaintenanceItemEntity
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialPrimaryDark

@Composable
fun UpdateOdometerDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf(currentOdometer.toString()) }
    val currentInt = textValue.toIntOrNull() ?: currentOdometer

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("تحديث قراءة العداد (الكيلومترات)")
            }
        },
        text = {
            Column {
                Text(
                    text = "قم بإدخال قراءة العداد الحالية لسيارتك لتحديث التنبيهات وحساب استهلاك الزيت والقطع بنسبة دقيقة:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            val newVal = maxOf(0, currentInt - 500)
                            textValue = newVal.toString()
                        }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }

                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        label = { Text("قراءة العداد (كم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("odometer_input_field")
                    )

                    IconButton(
                        onClick = {
                            val newVal = currentInt + 500
                            textValue = newVal.toString()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalVal = textValue.toIntOrNull() ?: currentOdometer
                    onConfirm(finalVal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                modifier = Modifier.testTag("confirm_odometer_button")
            ) {
                Text("تأكيد وتحديث", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogServiceDialog(
    currentOdometer: Int,
    maintenanceItems: List<MaintenanceItemEntity>,
    preselectedItemId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (itemId: Long, odo: Int, cost: Double, brand: String, viscosity: String, workshop: String, notes: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf(
            maintenanceItems.find { it.id == preselectedItemId } ?: maintenanceItems.firstOrNull()
        )
    }

    var odoText by remember { mutableStateOf(currentOdometer.toString()) }
    var costText by remember { mutableStateOf("1500") }
    var brandText by remember { mutableStateOf("قطعة غيار أصلية OEM") }
    var viscosityText by remember { mutableStateOf("5W-30 Fully Synthetic") }
    var workshopText by remember { mutableStateOf("مركز كراجي للصيانة السريعة") }
    var notesText by remember { mutableStateOf("تمت الصيانة بنجاح وفحص الأجزاء المرتبطة.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("تسجيل عملية صيانة جديدة")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Item selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItem?.itemNameAr ?: "اختر القطعة",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("القطعة / الخدمة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        maintenanceItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.itemNameAr) },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = odoText,
                        onValueChange = { odoText = it },
                        label = { Text("العداد (كم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("التكلفة (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = brandText,
                    onValueChange = { brandText = it },
                    label = { Text("ماركة القطعة / الزيت") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedItem?.category == "OILS_FLUIDS") {
                    OutlinedTextField(
                        value = viscosityText,
                        onValueChange = { viscosityText = it },
                        label = { Text("اللزوجة المستخدمة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = workshopText,
                    onValueChange = { workshopText = it },
                    label = { Text("اسم المركز / الورشة") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("ملاحظات / الفاتورة") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val itemId = selectedItem?.id ?: 1L
                    val odo = odoText.toIntOrNull() ?: currentOdometer
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    onConfirm(itemId, odo, cost, brandText, viscosityText, workshopText, notesText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                modifier = Modifier.testTag("submit_service_button")
            ) {
                Text("حفظ الفاتورة والعملية", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun LogFuelDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (odo: Int, liters: Double, price: Double, fuelType: String) -> Unit
) {
    var odoText by remember { mutableStateOf(currentOdometer.toString()) }
    var litersText by remember { mutableStateOf("40") }
    var priceText by remember { mutableStateOf("540") }
    var fuelType by remember { mutableStateOf("بنزين 95") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EvStation,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("تسجيل تمويل وقود (بنزين)")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = odoText,
                    onValueChange = { odoText = it },
                    label = { Text("قراءة العداد عند التمويل (كم)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = litersText,
                        onValueChange = { litersText = it },
                        label = { Text("عدد اللترات") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("المبلغ الإجمالي (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = fuelType,
                    onValueChange = { fuelType = it },
                    label = { Text("نوع الوقود (92 / 95)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val odo = odoText.toIntOrNull() ?: currentOdometer
                    val liters = litersText.toDoubleOrNull() ?: 0.0
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    onConfirm(odo, liters, price, fuelType)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                modifier = Modifier.testTag("submit_fuel_button")
            ) {
                Text("تسجيل التمويل", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomScheduleDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (titleAr: String, category: String, targetOdometer: Int, daysAhead: Int, isCritical: Boolean, notes: String) -> Unit
) {
    var titleAr by remember { mutableStateOf("") }
    var targetOdoText by remember { mutableStateOf((currentOdometer + 5000).toString()) }
    var daysAheadText by remember { mutableStateOf("90") }
    var isCritical by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "OILS_FLUIDS" to "زيوت وفلاتر (Oils & Fluids)",
        "BELTS_ELEC" to "سيور وشمعات احتراق (Belts & Spark Plugs)",
        "SUSPENSION_BRAKES" to "فرامل وعفشة (Brakes & Suspension)",
        "FILTERS_INTAKE" to "فلاتر ومنظومات حيوية (Filters & Systems)"
    )
    var selectedCategoryCode by remember { mutableStateOf("OILS_FLUIDS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("إضافة تذكير صيانة مخصص 📅")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "حدد اسم الصيانة والقراءة المستهدفة للعداد للربط بنظام التنبيهات المحلي:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = titleAr,
                    onValueChange = { titleAr = it },
                    label = { Text("اسم الخدمة / الصيانة (مثل: تغيير سير الدينامو)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_schedule_title_input")
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.first == selectedCategoryCode }?.second ?: selectedCategoryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("التصنيف الرئيسي") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedCategoryCode = code
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetOdoText,
                        onValueChange = { targetOdoText = it },
                        label = { Text("العداد المستهدف (كم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_schedule_odo_input")
                    )
                    OutlinedTextField(
                        value = daysAheadText,
                        onValueChange = { daysAheadText = it },
                        label = { Text("التذكير بعد (أيام)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isCritical,
                        onCheckedChange = { isCritical = it },
                        colors = CheckboxDefaults.colors(checkedColor = EditorialPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "صيانة حرجــة أمان/محرك 🚨",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("ملاحظات / مواصفات زيتها أو قطعتها") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleAr.isNotBlank()) {
                        val odo = targetOdoText.toIntOrNull() ?: (currentOdometer + 5000)
                        val days = daysAheadText.toIntOrNull() ?: 90
                        onConfirm(titleAr, selectedCategoryCode, odo, days, isCritical, notesText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                modifier = Modifier.testTag("submit_custom_schedule_button")
            ) {
                Text("حفظ وجدولة التذكير 🔔", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSwitcherDialog(
    allCars: List<CarEntity>,
    selectedCarId: Long?,
    onSelectCar: (Long) -> Unit,
    onAddVehicle: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أسطول سياراتي (${allCars.size}) 🚘",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "اختر المركبة النشطة لعرض صيانة وعدادات كل سيارة مسجلة:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allCars) { car ->
                        val isSelected = car.id == selectedCarId || car.isSelected
                        Card(
                            onClick = {
                                onSelectCar(car.id)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vehicle_switcher_item_${car.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EditorialPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EditorialPrimary else MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) EditorialPrimary else EditorialPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else EditorialPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${car.brand} ${car.model} (${car.year})",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "العداد: ${car.currentOdometer} كم • ${car.engineCc}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(EditorialPrimary)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "نشطة 🟢",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onAddVehicle()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vehicle_switcher_add_new_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EditorialPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة سيارة جديدة للأسطول ➕", fontWeight = FontWeight.Bold, color = EditorialPrimary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeAndLanguageSelectorDialog(
    currentTheme: com.example.ui.theme.AppTheme,
    currentLanguage: com.example.ui.theme.AppLanguage,
    onSelectTheme: (com.example.ui.theme.AppTheme) -> Unit,
    onSelectLanguage: (com.example.ui.theme.AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val lang = currentLanguage
    val isAr = lang == com.example.ui.theme.AppLanguage.AR

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = currentTheme.primaryColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isAr) "تخصيص الثيمات واللغة 🎨" else "Themes & Language 🎨",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) "12 ثيم مميز باسطايل وأشكال مختلفة + دعم كامل للغتين" else "12 Unique styles & layout shapes + Dual Language",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Language Picker
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isAr) "لغة التطبيق / Language:" else "App Language / لغة التطبيق:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.primaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Arabic Choice
                        FilterChip(
                            selected = isAr,
                            onClick = { onSelectLanguage(com.example.ui.theme.AppLanguage.AR) },
                            label = { Text("🇸🇦 العربية (RTL)", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f).testTag("lang_select_ar"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentTheme.primaryColor,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        // English Choice
                        FilterChip(
                            selected = !isAr,
                            onClick = { onSelectLanguage(com.example.ui.theme.AppLanguage.EN) },
                            label = { Text("🇬🇧 English (LTR)", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f).testTag("lang_select_en"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentTheme.primaryColor,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Section 2: Themes List Header
                Text(
                    text = if (isAr) "اختر مظهر التطبيق (12 ثيم بتصاميم وأشكال مختلفة):" else "Choose Layout & Style (12 Unique Themes):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.primaryColor
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(com.example.ui.theme.AppTheme.values()) { theme ->
                        val isSelected = theme == currentTheme
                        val themeName = if (isAr) theme.nameAr else theme.nameEn
                        val themeBadge = if (isAr) theme.badgeAr else theme.badgeEn
                        val themeDesc = if (isAr) theme.descAr else theme.descEn

                        Card(
                            onClick = { onSelectTheme(theme) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("theme_card_${theme.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = theme.canvasBg
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.5.dp else theme.cardBorderWidth,
                                color = if (isSelected) theme.primaryColor else theme.cardBorderColor
                            ),
                            shape = RoundedCornerShape(theme.cardCornerRadius)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Preview Color Palette Circles
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(theme.primaryColor)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(theme.cardBg)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(theme.cardBorderColor)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = themeName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = theme.textPrimary
                                            )
                                            Text(
                                                text = themeDesc,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = theme.textSecondary,
                                                maxLines = 2
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(theme.primaryColor.copy(alpha = if (isSelected) 1f else 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isSelected) (if (isAr) "نشط ✨" else "Active ✨") else themeBadge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White else theme.primaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isAr) "تطبيق وإغلاق ✨" else "Apply & Close ✨",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

