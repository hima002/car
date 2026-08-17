package com.hima.alwarsha.data.model

/** Real brand → model reference data for the vehicle onboarding picker. Not exhaustive —
 *  the UI always offers a free-text "أخرى" (other) fallback for anything missing here. */
data class CarBrandModels(val brand: String, val models: List<String>)

object CarCatalog {
    const val OTHER_BRAND = "أخرى"

    val brands: List<CarBrandModels> = listOf(
        CarBrandModels("تويوتا", listOf("كورولا", "كامري", "ياريس", "راف 4", "فورتشنر", "هايلوكس", "لاند كروزر", "أفانزا", "كورولا كروس")),
        CarBrandModels("هيونداي", listOf("إلنترا", "أكسنت", "توسان", "سنتافي", "i10", "i20", "كريتا", "سوناتا", "فيرنا")),
        CarBrandModels("كيا", listOf("سيراتو", "ريو", "سبورتاج", "سورينتو", "بيكانتو", "سيلتوس", "كارنيفال")),
        CarBrandModels("نيسان", listOf("صني", "سنترا", "قشقاي", "إكس تريل", "جوك", "باترول")),
        CarBrandModels("شيفروليه", listOf("أوبترا", "أفيو", "كروز", "كابتيفا", "سبارك")),
        CarBrandModels("إم جي", listOf("MG5", "MG6", "ZS", "HS", "RX5", "RX8")),
        CarBrandModels("شيري", listOf("تيجو 7", "تيجو 8", "تيجو 4", "أريزو 5", "أريزو 6")),
        CarBrandModels("جيلي", listOf("إمجراند", "كولراي", "أزكارا")),
        CarBrandModels("بي واي دي", listOf("F3", "سونج", "أتو 3", "هان")),
        CarBrandModels("رينو", listOf("لوجان", "ساندرو", "داستر", "ميجان", "كليو", "فلوانس")),
        CarBrandModels("بيجو", listOf("301", "208", "2008", "3008", "508")),
        CarBrandModels("فيات", listOf("تيبو", "500", "بونتو")),
        CarBrandModels("سكودا", listOf("أوكتافيا", "فابيا", "كاروك", "سوبيرب")),
        CarBrandModels("فولكس فاجن", listOf("جولف", "باسات", "تيجوان", "جيتا", "بولو")),
        CarBrandModels("مرسيدس بنز", listOf("الفئة C", "الفئة E", "الفئة S", "GLC", "الفئة A")),
        CarBrandModels("بي إم دبليو", listOf("الفئة 3", "الفئة 5", "X1", "X3", "X5")),
        CarBrandModels("أودي", listOf("A3", "A4", "A6", "Q3", "Q5")),
        CarBrandModels("هوندا", listOf("سيفيك", "أكورد", "CR-V", "سيتي", "HR-V")),
        CarBrandModels("ميتسوبيشي", listOf("لانسر", "أتراج", "أوتلاندر", "إكسباندر")),
        CarBrandModels("سوزوكي", listOf("سويفت", "ألتو", "فيتارا", "سيليريو")),
        CarBrandModels("سيات", listOf("إيبيزا", "ليون", "أتيكا")),
        CarBrandModels("أوبل", listOf("أسترا", "كورسا")),
        CarBrandModels("سيتروين", listOf("C3", "C4", "C-إليزيه")),
        CarBrandModels("جيب", listOf("كومباس", "رينيجيد", "جراند شيروكي")),
        CarBrandModels("مازدا", listOf("مازدا 3", "مازدا 6", "CX-5")),
        CarBrandModels(OTHER_BRAND, emptyList())
    )

    fun modelsFor(brand: String): List<String> = brands.find { it.brand == brand }?.models ?: emptyList()
}
