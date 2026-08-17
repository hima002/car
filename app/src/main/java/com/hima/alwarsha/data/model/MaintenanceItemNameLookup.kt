package com.hima.alwarsha.data.model

import com.hima.alwarsha.data.entity.MaintenanceItemEntity

class MaintenanceItemNameLookup(items: List<MaintenanceItemEntity>) {
    private val namesById = items.associate { it.id to it.itemNameAr }
    fun nameFor(itemId: Long): String = namesById[itemId] ?: "بند صيانة"
}
