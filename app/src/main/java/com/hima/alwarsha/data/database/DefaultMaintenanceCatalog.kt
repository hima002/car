package com.hima.alwarsha.data.database

import com.hima.alwarsha.data.entity.MaintenanceItemEntity

/**
 * Standard periodic-maintenance reference intervals (generic manufacturer-agnostic guidance,
 * not brand-specific claims). Each car can override with a custom interval; severe-driving
 * and transmission-type adjustments are applied at calculation time in CarRepository.
 */
object DefaultMaintenanceCatalog {

    const val OILS_FLUIDS = "OILS_FLUIDS"
    const val FILTERS_INTAKE = "FILTERS_INTAKE"
    const val BELTS_ELEC = "BELTS_ELEC"
    const val SUSPENSION_BRAKES = "SUSPENSION_BRAKES"

    const val ENGINE_OIL_ITEM_ID = 1L
    const val TRANSMISSION_FLUID_ITEM_ID = 2L

    val items: List<MaintenanceItemEntity> = listOf(
        MaintenanceItemEntity(
            id = ENGINE_OIL_ITEM_ID,
            itemNameAr = "زيت المحرك وفلتر الزيت",
            category = OILS_FLUIDS,
            defaultKmInterval = 10_000,
            defaultMonthInterval = 12,
            isCritical = true,
            recommendedSpecAr = "تنبيه إجباري بتغيير الفلتر مع كل غيار زيت."
        ),
        MaintenanceItemEntity(
            id = TRANSMISSION_FLUID_ITEM_ID,
            itemNameAr = "زيت الفتيس (ناقل الحركة)",
            category = OILS_FLUIDS,
            defaultKmInterval = 60_000,
            defaultMonthInterval = 48,
            isCritical = true,
            recommendedSpecAr = "الفترة الفعلية تعتمد على نوع الفتيس (CVT/DCT: أقصر، عادي: أطول)."
        ),
        MaintenanceItemEntity(
            id = 3L,
            itemNameAr = "سائل التبريد (الرادياتير)",
            category = OILS_FLUIDS,
            defaultKmInterval = 50_000,
            defaultMonthInterval = 60,
            isCritical = false,
            recommendedSpecAr = "استخدم السائل المخصص فقط، تجنب مياه الحنفية نهائيًا."
        ),
        MaintenanceItemEntity(
            id = 4L,
            itemNameAr = "زيت الفرامل",
            category = OILS_FLUIDS,
            defaultKmInterval = 40_000,
            defaultMonthInterval = 24,
            isCritical = true,
            recommendedSpecAr = "يمتص الرطوبة بمرور الوقت؛ تغييره يحمي منظومة ABS والطنابير."
        ),
        MaintenanceItemEntity(
            id = 5L,
            itemNameAr = "زيت الدركسيون الهيدروليكي",
            category = OILS_FLUIDS,
            defaultKmInterval = 60_000,
            defaultMonthInterval = 60,
            isCritical = false,
            recommendedSpecAr = "غير مطلوب في السيارات ذات الدركسيون الكهربائي بالكامل."
        ),
        MaintenanceItemEntity(
            id = 6L,
            itemNameAr = "فلتر الهواء",
            category = FILTERS_INTAKE,
            defaultKmInterval = 15_000,
            defaultMonthInterval = 12,
            isCritical = false,
            recommendedSpecAr = "تنظيف كل 5,000 كم، تغيير كامل كل 15-20 ألف كم."
        ),
        MaintenanceItemEntity(
            id = 7L,
            itemNameAr = "فلتر التكييف (المقصورة)",
            category = FILTERS_INTAKE,
            defaultKmInterval = 15_000,
            defaultMonthInterval = 12,
            isCritical = false,
            recommendedSpecAr = ""
        ),
        MaintenanceItemEntity(
            id = 8L,
            itemNameAr = "فلتر البنزين (الوقود)",
            category = FILTERS_INTAKE,
            defaultKmInterval = 25_000,
            defaultMonthInterval = 24,
            isCritical = false,
            recommendedSpecAr = "يحمي الرشاشات وطلمبة البنزين من الشوائب."
        ),
        MaintenanceItemEntity(
            id = 9L,
            itemNameAr = "البوجيهات (شمعات الإشعال)",
            category = FILTERS_INTAKE,
            defaultKmInterval = 20_000,
            defaultMonthInterval = 24,
            isCritical = false,
            recommendedSpecAr = "قياسية: كل 20,000 كم | إيريديوم/بلاتينيوم: حتى 80,000 كم."
        ),
        MaintenanceItemEntity(
            id = 10L,
            itemNameAr = "سير الكاتينة (Timing Belt)",
            category = BELTS_ELEC,
            defaultKmInterval = 80_000,
            defaultMonthInterval = 60,
            isCritical = true,
            recommendedSpecAr = "تنبيه حرج جدًا: انقطاعه يسبب تلف المحرك فورًا في أغلب الموديلات."
        ),
        MaintenanceItemEntity(
            id = 11L,
            itemNameAr = "سيور المجموعات (دينامو / باور)",
            category = BELTS_ELEC,
            defaultKmInterval = 40_000,
            defaultMonthInterval = 36,
            isCritical = false,
            recommendedSpecAr = "افحص التشققات والشد عند كل فحص دوري."
        ),
        MaintenanceItemEntity(
            id = 12L,
            itemNameAr = "البطارية",
            category = BELTS_ELEC,
            defaultKmInterval = 200_000,
            defaultMonthInterval = 36,
            isCritical = false,
            recommendedSpecAr = "العمر الافتراضي 3-4 سنوات؛ يُنصح بالفحص كل سنة."
        ),
        MaintenanceItemEntity(
            id = 13L,
            itemNameAr = "تيل الفرامل الأمامي",
            category = SUSPENSION_BRAKES,
            defaultKmInterval = 20_000,
            defaultMonthInterval = 24,
            isCritical = true,
            recommendedSpecAr = ""
        ),
        MaintenanceItemEntity(
            id = 14L,
            itemNameAr = "تيل الفرامل الخلفي",
            category = SUSPENSION_BRAKES,
            defaultKmInterval = 30_000,
            defaultMonthInterval = 24,
            isCritical = true,
            recommendedSpecAr = ""
        ),
        MaintenanceItemEntity(
            id = 15L,
            itemNameAr = "تدوير الإطارات",
            category = SUSPENSION_BRAKES,
            defaultKmInterval = 10_000,
            defaultMonthInterval = 12,
            isCritical = false,
            recommendedSpecAr = "يمنع التآكل المائل ويحافظ على ثبات السيارة."
        ),
        MaintenanceItemEntity(
            id = 16L,
            itemNameAr = "زوايا واتزان",
            category = SUSPENSION_BRAKES,
            defaultKmInterval = 20_000,
            defaultMonthInterval = 12,
            isCritical = false,
            recommendedSpecAr = ""
        )
    )
}
