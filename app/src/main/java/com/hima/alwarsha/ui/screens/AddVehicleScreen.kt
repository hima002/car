@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.database.DefaultMaintenanceCatalog
import com.hima.alwarsha.data.model.CarCatalog
import com.hima.alwarsha.ui.components.SimpleDropdownField
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.viewmodel.CarViewModel

private val transmissionOptions = listOf(
    "CVT" to "CVT",
    "DCT_DRY" to "فتيس DCT جاف",
    "DCT_WET" to "فتيس DCT مبلل",
    "TORQUE_CONVERTER" to "فتيس عادي (Torque Converter)",
    "MANUAL" to "مانيوال"
)

@Composable
fun AddVehicleScreen(viewModel: CarViewModel, onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current

    var brand by remember { mutableStateOf(CarCatalog.brands.first().brand) }
    var customBrand by remember { mutableStateOf("") }

    val models = remember(brand) { CarCatalog.modelsFor(brand) }
    var model by remember(brand) { mutableStateOf(models.firstOrNull() ?: "") }
    var customModel by remember { mutableStateOf("") }

    var year by remember { mutableStateOf("") }
    var transmissionType by remember { mutableStateOf(transmissionOptions.first().first) }
    var engineCc by remember { mutableStateOf("") }
    var currentOdometer by remember { mutableStateOf("") }
    var isSevereDriving by remember { mutableStateOf(false) }

    val selectedItems = remember { mutableStateMapOf<Long, Boolean>().apply { DefaultMaintenanceCatalog.items.forEach { put(it.id, true) } } }
    val itemBaselines = remember { mutableStateMapOf<Long, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة سيارة", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                SimpleDropdownField(
                    label = "العلامة التجارية",
                    selectedText = brand,
                    options = CarCatalog.brands.map { it.brand },
                    onOptionSelected = { brand = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (brand == CarCatalog.OTHER_BRAND) {
                item {
                    OutlinedTextField(
                        value = customBrand,
                        onValueChange = { customBrand = it },
                        label = { Text("اسم العلامة التجارية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (models.isNotEmpty()) {
                item {
                    SimpleDropdownField(
                        label = "الموديل",
                        selectedText = model,
                        options = models,
                        onOptionSelected = { model = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                item {
                    OutlinedTextField(
                        value = customModel,
                        onValueChange = { customModel = it },
                        label = { Text("الموديل") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it.filter(Char::isDigit) },
                    label = { Text("سنة الصنع") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SimpleDropdownField(
                    label = "نوع الفتيس",
                    selectedText = transmissionOptions.first { it.first == transmissionType }.second,
                    options = transmissionOptions.map { it.second },
                    onOptionSelected = { label -> transmissionType = transmissionOptions.first { it.second == label }.first },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = engineCc,
                    onValueChange = { engineCc = it },
                    label = { Text("سعة المحرك (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = currentOdometer,
                    onValueChange = { currentOdometer = it.filter(Char::isDigit) },
                    label = { Text("قراءة العداد الحالية (كم)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("قيادة شاقة (زحام/حرارة عالية)؟", color = themeStyle.textPrimary)
                    Switch(checked = isSevereDriving, onCheckedChange = { isSevereDriving = it })
                }
            }

            item {
                Text(
                    "الصيانات الدورية المقترحة — علّم على البنود المطلوبة، وحدّد عداد آخر تغيير لو مختلف عن العداد الحالي:",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.textSecondary
                )
            }

            items(DefaultMaintenanceCatalog.items) { catalogItem ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedItems[catalogItem.id] ?: true,
                        onCheckedChange = { selectedItems[catalogItem.id] = it }
                    )
                    Text(catalogItem.itemNameAr, color = themeStyle.textPrimary, modifier = Modifier.weight(1f))
                    if (selectedItems[catalogItem.id] == true) {
                        OutlinedTextField(
                            value = itemBaselines[catalogItem.id] ?: "",
                            onValueChange = { itemBaselines[catalogItem.id] = it.filter(Char::isDigit) },
                            placeholder = { Text(currentOdometer.ifBlank { "0" }) },
                            label = { Text("آخر تغيير عند") },
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val finalBrand = if (brand == CarCatalog.OTHER_BRAND) customBrand else brand
                        val finalModel = if (models.isEmpty()) customModel else model
                        val odo = currentOdometer.toIntOrNull() ?: 0
                        val selectedIds = selectedItems.filterValues { it }.keys
                        val baselines = itemBaselines.mapNotNull { (id, text) -> text.toIntOrNull()?.let { id to it } }.toMap()
                        viewModel.addNewVehicle(
                            brand = finalBrand,
                            model = finalModel,
                            year = year.toIntOrNull() ?: 0,
                            transmissionType = transmissionType,
                            engineCc = engineCc,
                            currentOdometer = odo,
                            isSevereDriving = isSevereDriving,
                            selectedItemIds = selectedIds,
                            itemBaselines = baselines
                        )
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ السيارة")
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
