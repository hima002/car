package com.hima.alwarsha.data.model

/** Real brand → model reference data for the vehicle onboarding picker. Not exhaustive —
 *  [modelsFor] always appends an [OTHER_MODEL] free-text fallback for anything missing here. */
data class CarBrandModels(val brand: String, val models: List<String>)

object CarCatalog {
    const val OTHER_BRAND = "أخرى"
    const val OTHER_MODEL = "أخرى (موديل مش موجود)"

    val brands: List<CarBrandModels> = listOf(
        CarBrandModels("تويوتا", listOf("كورولا", "كامري", "ياريس", "راف 4", "فورتشنر", "هايلوكس", "لاند كروزر", "أفانزا", "كورولا كروس", "برادو", "يارس كروس", "هايس")),
        CarBrandModels("هيونداي", listOf("إلنترا", "أكسنت", "توسان", "سنتافي", "i10", "i20", "كريتا", "سوناتا", "فيرنا", "بالو", "أزيرا", "سنتافيه")),
        CarBrandModels("كيا", listOf("سيراتو", "ريو", "سبورتاج", "سورينتو", "بيكانتو", "سيلتوس", "كارنيفال", "أوبتيما", "سول")),
        CarBrandModels("نيسان", listOf("صني", "سنترا", "قشقاي", "إكس تريل", "جوك", "باترول", "كيكس", "نافارا")),
        CarBrandModels("شيفروليه", listOf("أوبترا", "أفيو", "كروز", "كابتيفا", "سبارك", "تاهو")),
        CarBrandModels("إم جي", listOf("MG5", "MG6", "ZS", "HS", "RX5", "RX8", "MG3", "GT")),
        CarBrandModels(
            "شيري",
            listOf(
                "تيجو 2", "تيجو 3", "تيجو 4", "تيجو 5X", "تيجو 7", "تيجو 8", "تيجو 8 برو", "تيجو 8 برو ماكس",
                "أريزو 5", "أريزو 6", "أريزو 8", "كيوكيو (QQ)", "إيستار", "أومودا 5"
            )
        ),
        CarBrandModels("جيلي", listOf("إمجراند", "كولراي", "أزكارا", "جيلي GX3", "بينروي")),
        CarBrandModels("بي واي دي", listOf("F3", "سونج", "أتو 3", "هان", "F0", "S5", "تانج")),
        CarBrandModels("رينو", listOf("لوجان", "ساندرو", "داستر", "ميجان", "كليو", "فلوانس", "كادجار", "سيمبول")),
        CarBrandModels("بيجو", listOf("301", "208", "2008", "3008", "508", "بارتنر")),
        CarBrandModels("فيات", listOf("تيبو", "500", "بونتو", "دوبلو")),
        CarBrandModels("سكودا", listOf("أوكتافيا", "فابيا", "كاروك", "سوبيرب", "كاميك")),
        CarBrandModels("فولكس فاجن", listOf("جولف", "باسات", "تيجوان", "جيتا", "بولو", "تي روك")),
        CarBrandModels("مرسيدس بنز", listOf("الفئة C", "الفئة E", "الفئة S", "GLC", "الفئة A", "GLA", "فيتو")),
        CarBrandModels("بي إم دبليو", listOf("الفئة 3", "الفئة 5", "X1", "X3", "X5", "الفئة 7")),
        CarBrandModels("أودي", listOf("A3", "A4", "A6", "Q3", "Q5", "Q7")),
        CarBrandModels("هوندا", listOf("سيفيك", "أكورد", "CR-V", "سيتي", "HR-V", "بي لوت")),
        CarBrandModels("ميتسوبيشي", listOf("لانسر", "أتراج", "أوتلاندر", "إكسباندر", "باجيرو")),
        CarBrandModels("سوزوكي", listOf("سويفت", "ألتو", "فيتارا", "سيليريو", "إرتيجا", "جيمني")),
        CarBrandModels("سيات", listOf("إيبيزا", "ليون", "أتيكا")),
        CarBrandModels("أوبل", listOf("أسترا", "كورسا", "موكا")),
        CarBrandModels("سيتروين", listOf("C3", "C4", "C-إليزيه", "بيرلينجو")),
        CarBrandModels("جيب", listOf("كومباس", "رينيجيد", "جراند شيروكي", "شيروكي")),
        CarBrandModels("مازدا", listOf("مازدا 3", "مازدا 6", "CX-5", "CX-3")),
        CarBrandModels("جي إيه سي (GAC)", listOf("GS3", "GS4", "إمباو")),
        CarBrandModels("دونجفنج (DFSK)", listOf("جلوري 580", "جلوري 500", "580 برو")),
        CarBrandModels("فاو (FAW)", listOf("بيسون", "V2")),
        CarBrandModels(OTHER_BRAND, emptyList())
    )

    fun modelsFor(brand: String): List<String> {
        val curated = brands.find { it.brand == brand }?.models ?: emptyList()
        return curated + OTHER_MODEL
    }
}
