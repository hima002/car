@file:OptIn(ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity

@Composable
fun UpdateOdometerDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf(currentOdometer.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث قراءة العداد") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter(Char::isDigit) },
                label = { Text("العداد الحالي (كم)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { text.toIntOrNull()?.let(onConfirm) }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun LogServiceDialog(
    currentOdometer: Int,
    maintenanceItems: List<MaintenanceItemEntity>,
    preselectedItemId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (itemId: Long, odometer: Int, cost: Double, brand: String, workshop: String, notes: String) -> Unit
) {
    var selectedItem by remember(preselectedItemId) {
        mutableStateOf(maintenanceItems.find { it.id == preselectedItemId } ?: maintenanceItems.firstOrNull())
    }
    var odometerText by remember { mutableStateOf(currentOdometer.toString()) }
    var costText by remember { mutableStateOf("") }
    var brandText by remember { mutableStateOf("") }
    var workshopText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var itemMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل صيانة") },
        text = {
            Column {
                ExposedDropdownMenuBox(expanded = itemMenuExpanded, onExpandedChange = { itemMenuExpanded = it }) {
                    OutlinedTextField(
                        value = selectedItem?.itemNameAr ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("البند") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemMenuExpanded) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.ExposedDropdownMenu(
                        expanded = itemMenuExpanded,
                        onDismissRequest = { itemMenuExpanded = false }
                    ) {
                        maintenanceItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.itemNameAr) },
                                onClick = { selectedItem = item; itemMenuExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = odometerText,
                    onValueChange = { odometerText = it.filter(Char::isDigit) },
                    label = { Text("العداد عند التغيير (كم)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("التكلفة (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = workshopText,
                    onValueChange = { workshopText = it },
                    label = { Text("الورشة (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val itemId = selectedItem?.id ?: return@Button
                val odo = odometerText.toIntOrNull() ?: currentOdometer
                val cost = costText.toDoubleOrNull() ?: 0.0
                onConfirm(itemId, odo, cost, brandText, workshopText, notesText)
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun LogFuelDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (odometer: Int, liters: Double, price: Double) -> Unit
) {
    var odometerText by remember { mutableStateOf(currentOdometer.toString()) }
    var litersText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل تعبئة وقود") },
        text = {
            Column {
                OutlinedTextField(
                    value = odometerText,
                    onValueChange = { odometerText = it.filter(Char::isDigit) },
                    label = { Text("العداد الحالي (كم)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = litersText,
                    onValueChange = { litersText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("عدد اللترات") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("الإجمالي (جنيه)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val odo = odometerText.toIntOrNull() ?: currentOdometer
                val liters = litersText.toDoubleOrNull() ?: 0.0
                val price = priceText.toDoubleOrNull() ?: 0.0
                onConfirm(odo, liters, price)
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun VehicleSwitcherDialog(
    allCars: List<CarEntity>,
    selectedCarId: Long,
    onSelectCar: (Long) -> Unit,
    onAddVehicle: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختيار السيارة") },
        text = {
            LazyColumn {
                items(allCars) { car ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(),
                        onClick = { onSelectCar(car.id); onDismiss() }
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Text("${car.brand} ${car.model} (${car.year})")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAddVehicle(); onDismiss() }) { Text("إضافة سيارة جديدة") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}
