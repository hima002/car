package com.hima.alwarsha.data.model

/** Real brand → model reference data for the vehicle onboarding picker. Not exhaustive —
 *  [modelsFor] always appends an [OTHER_MODEL] free-text fallback for anything missing here. */
data class CarBrandModels(val brand: String, val models: List<String>)

/**
 * One factory-offered engine/transmission combination for a specific model, e.g. the standard
 * naturally-aspirated engine vs. an optional turbo trim. Only added for models whose real specs
 * have been verified — see [CarCatalog.modelEngineSpecs]. Never guessed or invented: a model with
 * no verified entry here falls back to manual engine/transmission selection in the UI instead of
 * silently showing wrong data.
 */
data class CarEngineOption(
    val label: String,
    val transmissionType: String, // CVT, DCT_DRY, DCT_WET, TORQUE_CONVERTER, MANUAL — see AddVehicleScreen's transmissionOptions labels
    val isTurbo: Boolean
)

object CarCatalog {
    const val OTHER_BRAND = "أخرى"
    const val OTHER_MODEL = "أخرى (موديل مش موجود)"

    /**
     * Verified engine/transmission specs keyed by "brand|model", researched per-model for the
     * Egyptian market (cross-checked against official brand pages and reputable Egyptian auto
     * listing/news sites). Models not present here are either not yet researched or came back
     * with conflicting/unconfirmed sources — those deliberately fall back to manual
     * engine/transmission selection in the UI instead of showing an unverified guess.
     */
    private val modelEngineSpecs: Map<String, List<CarEngineOption>> = mapOf(
        // ===== تويوتا =====
        "تويوتا|كورولا" to listOf(CarEngineOption("1.6L عادي", "CVT", false)),
        "تويوتا|كامري" to listOf(CarEngineOption("2.5L عادي", "TORQUE_CONVERTER", false)),
        "تويوتا|ياريس" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "تويوتا|راف 4" to listOf(CarEngineOption("2.5L عادي", "TORQUE_CONVERTER", false)),
        "تويوتا|فورتشنر" to listOf(
            CarEngineOption("2.7L عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("4.0L V6 عادي", "TORQUE_CONVERTER", false)
        ),
        "تويوتا|هايلوكس" to listOf(
            CarEngineOption("2.4L ديزل تيربو", "MANUAL", true),
            CarEngineOption("2.7L بنزين عادي", "MANUAL", false)
        ),
        "تويوتا|أفانزا" to listOf(
            CarEngineOption("1.5L عادي أوتوماتيك", "TORQUE_CONVERTER", false),
            CarEngineOption("1.3L عادي يدوي", "MANUAL", false)
        ),
        "تويوتا|كورولا كروس" to listOf(CarEngineOption("1.8L هايبرد", "CVT", false)),

        // ===== هيونداي =====
        "هيونداي|إلنترا" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),
        "هيونداي|أكسنت" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),
        "هيونداي|توسان" to listOf(
            CarEngineOption("2.0L عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("1.6L تيربو", "DCT_DRY", true)
        ),
        "هيونداي|سنتافي" to listOf(CarEngineOption("2.5L عادي", "TORQUE_CONVERTER", false)),
        "هيونداي|i10" to listOf(
            CarEngineOption("1.2L عادي أوتوماتيك", "TORQUE_CONVERTER", false),
            CarEngineOption("1.2L عادي يدوي", "MANUAL", false)
        ),
        "هيونداي|كريتا" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),
        "هيونداي|فيرنا" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),

        // ===== كيا =====
        "كيا|سيراتو" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),
        "كيا|سبورتاج" to listOf(CarEngineOption("1.6L تيربو", "DCT_DRY", true)),
        "كيا|سورينتو" to listOf(CarEngineOption("1.6L تيربو هايبرد", "TORQUE_CONVERTER", true)),
        "كيا|بيكانتو" to listOf(
            CarEngineOption("1.2L عادي أوتوماتيك", "TORQUE_CONVERTER", false),
            CarEngineOption("1.2L عادي يدوي", "MANUAL", false)
        ),
        "كيا|سيلتوس" to listOf(CarEngineOption("1.6L عادي", "CVT", false)),
        "كيا|كارنيفال" to listOf(CarEngineOption("3.5L V6 عادي", "TORQUE_CONVERTER", false)),

        // ===== نيسان =====
        "نيسان|صني" to listOf(
            CarEngineOption("1.5L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.5L عادي أوتوماتيك", "TORQUE_CONVERTER", false)
        ),
        "نيسان|سنترا" to listOf(CarEngineOption("1.6L عادي", "CVT", false)),
        "نيسان|قشقاي" to listOf(CarEngineOption("1.3L تيربو", "CVT", true)),
        "نيسان|جوك" to listOf(CarEngineOption("1.0L تيربو", "DCT_DRY", true)),
        "نيسان|باترول" to listOf(
            CarEngineOption("3.8L V6 عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("3.5L تيربو V6", "TORQUE_CONVERTER", true)
        ),
        "نيسان|نافارا" to listOf(
            CarEngineOption("2.5L ديزل تيربو يدوي", "MANUAL", true),
            CarEngineOption("2.5L ديزل تيربو أوتوماتيك", "TORQUE_CONVERTER", true)
        ),

        // ===== شيفروليه =====
        "شيفروليه|أوبترا" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "شيفروليه|أفيو" to listOf(CarEngineOption("1.5L عادي", "TORQUE_CONVERTER", false)),
        "شيفروليه|كابتيفا" to listOf(CarEngineOption("1.5L تيربو", "CVT", true)),
        "شيفروليه|تاهو" to listOf(CarEngineOption("5.3L V8 عادي", "TORQUE_CONVERTER", false)),

        // ===== إم جي =====
        "إم جي|MG5" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "إم جي|ZS" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "إم جي|RX5" to listOf(CarEngineOption("1.5L تيربو", "DCT_DRY", true)),
        "إم جي|MG3" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "إم جي|GT" to listOf(CarEngineOption("1.5L تيربو", "DCT_DRY", true)),

        // ===== شيري =====
        "شيري|تيجو 3" to listOf(CarEngineOption("1.6L عادي", "CVT", false)),
        "شيري|تيجو 4" to listOf(
            CarEngineOption("1.5L عادي", "CVT", false),
            CarEngineOption("1.5L تيربو", "CVT", true)
        ),
        "شيري|تيجو 7" to listOf(CarEngineOption("1.5L تيربو", "TORQUE_CONVERTER", true)),
        "شيري|تيجو 8 برو" to listOf(CarEngineOption("1.6L تيربو", "DCT_DRY", true)),
        "شيري|أريزو 5" to listOf(
            CarEngineOption("1.5L عادي أوتوماتيك", "CVT", false),
            CarEngineOption("1.5L عادي يدوي", "MANUAL", false)
        ),
        "شيري|أريزو 6" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),

        // ===== جيلي =====
        "جيلي|إمجراند" to listOf(CarEngineOption("1.5L عادي", "TORQUE_CONVERTER", false)),
        "جيلي|كولراي" to listOf(CarEngineOption("1.5L تيربو", "DCT_WET", true)),
        "جيلي|جيلي GX3" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),

        // ===== بي واي دي =====
        "بي واي دي|F3" to listOf(
            CarEngineOption("1.5L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.5L عادي أوتوماتيك", "CVT", false)
        ),

        // ===== رينو =====
        "رينو|لوجان" to listOf(
            CarEngineOption("1.6L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.6L عادي أوتوماتيك", "CVT", false)
        ),
        "رينو|ساندرو" to listOf(
            CarEngineOption("1.6L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.6L عادي أوتوماتيك", "CVT", false)
        ),
        "رينو|داستر" to listOf(CarEngineOption("1.3L تيربو", "DCT_WET", true)),
        "رينو|ميجان" to listOf(
            CarEngineOption("1.6L عادي", "CVT", false),
            CarEngineOption("1.3L تيربو", "DCT_WET", true)
        ),
        "رينو|فلوانس" to listOf(
            CarEngineOption("1.6L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.6L عادي أوتوماتيك", "CVT", false)
        ),
        "رينو|كادجار" to listOf(CarEngineOption("1.2L تيربو", "DCT_WET", true)),

        // ===== بيجو =====
        "بيجو|301" to listOf(
            CarEngineOption("1.6L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.6L عادي أوتوماتيك", "TORQUE_CONVERTER", false)
        ),
        "بيجو|208" to listOf(CarEngineOption("1.2L تيربو", "TORQUE_CONVERTER", true)),
        "بيجو|2008" to listOf(CarEngineOption("1.2L تيربو", "TORQUE_CONVERTER", true)),
        "بيجو|3008" to listOf(CarEngineOption("1.6L تيربو", "TORQUE_CONVERTER", true)),
        "بيجو|508" to listOf(CarEngineOption("1.6L تيربو", "TORQUE_CONVERTER", true)),
        "بيجو|بارتنر" to listOf(CarEngineOption("1.6L ديزل تيربو", "MANUAL", true)),

        // ===== فيات =====
        "فيات|تيبو" to listOf(
            CarEngineOption("1.4L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.6L عادي أوتوماتيك", "TORQUE_CONVERTER", false)
        ),
        "فيات|500" to listOf(
            CarEngineOption("1.4L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.4L عادي أوتوماتيك", "TORQUE_CONVERTER", false)
        ),
        "فيات|بونتو" to listOf(CarEngineOption("1.4L عادي", "MANUAL", false)),
        "فيات|دوبلو" to listOf(CarEngineOption("1.4L عادي", "MANUAL", false)),

        // ===== سكودا =====
        "سكودا|أوكتافيا" to listOf(
            CarEngineOption("1.4L تيربو TSI", "DCT_DRY", true),
            CarEngineOption("2.0L تيربو VRS", "DCT_WET", true)
        ),
        "سكودا|فابيا" to listOf(
            CarEngineOption("1.0L تيربو TSI", "DCT_DRY", true),
            CarEngineOption("1.5L تيربو TSI", "DCT_WET", true)
        ),
        "سكودا|كاروك" to listOf(CarEngineOption("1.4L تيربو TSI", "DCT_DRY", true)),
        "سكودا|سوبيرب" to listOf(
            CarEngineOption("1.5L تيربو TSI", "DCT_DRY", true),
            CarEngineOption("2.0L تيربو TSI", "DCT_WET", true)
        ),
        "سكودا|كاميك" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),

        // ===== فولكس فاجن =====
        "فولكس فاجن|جولف" to listOf(CarEngineOption("1.4L تيربو TSI", "DCT_DRY", true)),
        "فولكس فاجن|باسات" to listOf(CarEngineOption("1.4L تيربو TSI", "DCT_WET", true)),
        "فولكس فاجن|تيجوان" to listOf(CarEngineOption("1.4L تيربو TSI", "DCT_DRY", true)),
        "فولكس فاجن|تي روك" to listOf(CarEngineOption("1.4L تيربو TSI", "DCT_DRY", true)),

        // ===== مرسيدس بنز =====
        "مرسيدس بنز|الفئة C" to listOf(
            CarEngineOption("C180 1.5L تيربو", "TORQUE_CONVERTER", true),
            CarEngineOption("C200 1.5L تيربو مع هجين خفيف", "TORQUE_CONVERTER", true),
            CarEngineOption("C300 2.0L تيربو 4Matic", "TORQUE_CONVERTER", true)
        ),
        "مرسيدس بنز|الفئة E" to listOf(
            CarEngineOption("E200 2.0L تيربو", "TORQUE_CONVERTER", true),
            CarEngineOption("E300 2.0L تيربو", "TORQUE_CONVERTER", true)
        ),
        "مرسيدس بنز|الفئة S" to listOf(
            CarEngineOption("S450 3.0L تيربو 6 سلندر", "TORQUE_CONVERTER", true),
            CarEngineOption("S500 3.0L تيربو 6 سلندر", "TORQUE_CONVERTER", true)
        ),
        "مرسيدس بنز|GLC" to listOf(
            CarEngineOption("GLC200 2.0L تيربو", "TORQUE_CONVERTER", true),
            CarEngineOption("GLC300 3.0L تيربو 6 سلندر", "TORQUE_CONVERTER", true)
        ),
        "مرسيدس بنز|الفئة A" to listOf(CarEngineOption("A200 1.3L تيربو", "DCT_DRY", true)),
        "مرسيدس بنز|GLA" to listOf(CarEngineOption("GLA200 1.4L تيربو", "DCT_DRY", true)),

        // ===== بي إم دبليو =====
        "بي إم دبليو|الفئة 3" to listOf(CarEngineOption("320i 2.0L تيربو", "TORQUE_CONVERTER", true)),
        "بي إم دبليو|الفئة 5" to listOf(
            CarEngineOption("520i 2.0L تيربو", "TORQUE_CONVERTER", true),
            CarEngineOption("530i 2.0L تيربو", "TORQUE_CONVERTER", true)
        ),
        "بي إم دبليو|X1" to listOf(CarEngineOption("X1 1.5L تيربو 3 سلندر", "DCT_WET", true)),
        "بي إم دبليو|X3" to listOf(CarEngineOption("xDrive30i 2.0L تيربو", "TORQUE_CONVERTER", true)),
        "بي إم دبليو|X5" to listOf(
            CarEngineOption("X5 3.0L تيربو 6 سلندر", "TORQUE_CONVERTER", true),
            CarEngineOption("X5 4.4L تيربو V8", "TORQUE_CONVERTER", true)
        ),
        "بي إم دبليو|الفئة 7" to listOf(CarEngineOption("740Li 3.0L تيربو 6 سلندر", "TORQUE_CONVERTER", true)),

        // ===== أودي =====
        "أودي|A3" to listOf(CarEngineOption("1.4L TFSI تيربو", "DCT_DRY", true)),
        "أودي|A4" to listOf(CarEngineOption("2.0L TFSI تيربو", "DCT_WET", true)),
        "أودي|Q3" to listOf(
            CarEngineOption("1.4L TSI تيربو دفع أمامي", "DCT_DRY", true),
            CarEngineOption("2.0L TFSI تيربو كواترو", "DCT_DRY", true)
        ),
        "أودي|Q7" to listOf(CarEngineOption("2.0L TFSI تيربو كواترو", "TORQUE_CONVERTER", true)),

        // ===== هوندا =====
        "هوندا|سيفيك" to listOf(CarEngineOption("Sport 1.5L تيربو", "CVT", true)),
        "هوندا|أكورد" to listOf(CarEngineOption("1.5L تيربو", "CVT", true)),
        "هوندا|CR-V" to listOf(CarEngineOption("1.5L تيربو", "CVT", true)),
        "هوندا|سيتي" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "هوندا|HR-V" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),

        // ===== ميتسوبيشي =====
        "ميتسوبيشي|لانسر" to listOf(
            CarEngineOption("1.6L عادي", "CVT", false),
            CarEngineOption("2.0L عادي", "CVT", false)
        ),
        "ميتسوبيشي|أتراج" to listOf(CarEngineOption("1.2L عادي", "CVT", false)),
        "ميتسوبيشي|أوتلاندر" to listOf(CarEngineOption("1.5L عادي", "CVT", false)),
        "ميتسوبيشي|باجيرو" to listOf(CarEngineOption("3.8L V6 عادي", "TORQUE_CONVERTER", false)),

        // ===== سوزوكي =====
        "سوزوكي|ألتو" to listOf(CarEngineOption("1.0L عادي", "MANUAL", false)),
        "سوزوكي|إرتيجا" to listOf(CarEngineOption("1.5L عادي", "TORQUE_CONVERTER", false)),
        "سوزوكي|جيمني" to listOf(
            CarEngineOption("1.5L عادي يدوي", "MANUAL", false),
            CarEngineOption("1.5L عادي أوتوماتيك", "TORQUE_CONVERTER", false)
        ),

        // ===== سيات =====
        "سيات|إيبيزا" to listOf(CarEngineOption("1.0L TSI تيربو", "DCT_DRY", true)),
        "سيات|أتيكا" to listOf(CarEngineOption("1.4L تيربو", "TORQUE_CONVERTER", true)),

        // ===== أوبل =====
        "أوبل|أسترا" to listOf(CarEngineOption("1.4L تيربو", "TORQUE_CONVERTER", true)),
        "أوبل|كورسا" to listOf(CarEngineOption("1.2L تيربو", "TORQUE_CONVERTER", true)),
        "أوبل|موكا" to listOf(CarEngineOption("1.2L تيربو 3 سلندر", "TORQUE_CONVERTER", true)),

        // ===== سيتروين =====
        "سيتروين|C3" to listOf(CarEngineOption("1.2L تيربو 3 سلندر", "TORQUE_CONVERTER", true)),
        "سيتروين|C4" to listOf(CarEngineOption("1.2L تيربو 3 سلندر", "TORQUE_CONVERTER", true)),
        "سيتروين|C-إليزيه" to listOf(CarEngineOption("1.6L عادي", "TORQUE_CONVERTER", false)),

        // ===== جيب =====
        "جيب|رينيجيد" to listOf(
            CarEngineOption("1.4L تيربو 140 حصان دفع أمامي", "TORQUE_CONVERTER", true),
            CarEngineOption("1.4L تيربو 170 حصان دفع رباعي", "TORQUE_CONVERTER", true)
        ),
        "جيب|جراند شيروكي" to listOf(CarEngineOption("3.6L V6 عادي", "TORQUE_CONVERTER", false)),
        "جيب|شيروكي" to listOf(
            CarEngineOption("2.4L عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("3.2L V6 عادي", "TORQUE_CONVERTER", false)
        ),

        // ===== مازدا =====
        "مازدا|مازدا 3" to listOf(CarEngineOption("1.5L عادي", "TORQUE_CONVERTER", false)),
        "مازدا|مازدا 6" to listOf(
            CarEngineOption("2.0L عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("2.5L عادي", "TORQUE_CONVERTER", false),
            CarEngineOption("2.5L تيربو", "TORQUE_CONVERTER", true)
        ),
        "مازدا|CX-5" to listOf(CarEngineOption("2.5L عادي", "TORQUE_CONVERTER", false)),
        "مازدا|CX-3" to listOf(CarEngineOption("1.5L عادي", "TORQUE_CONVERTER", false)),

        // ===== جي إيه سي (GAC) =====
        "جي إيه سي (GAC)|GS3" to listOf(CarEngineOption("1.5L تيربو", "DCT_WET", true)),
        "جي إيه سي (GAC)|GS4" to listOf(CarEngineOption("1.5L تيربو", "DCT_WET", true)),

        // ===== دونجفنج (DFSK) =====
        "دونجفنج (DFSK)|جلوري 580" to listOf(CarEngineOption("1.5L تيربو", "CVT", true)),
        "دونجفنج (DFSK)|580 برو" to listOf(CarEngineOption("1.5L تيربو", "CVT", true))
    )

    fun engineOptionsFor(brand: String, model: String): List<CarEngineOption> =
        modelEngineSpecs["$brand|$model"] ?: emptyList()

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
