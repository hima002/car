package com.example.ui.theme

object AppStrings {
    fun navDashboard(lang: AppLanguage): String = if (lang == AppLanguage.AR) "الرئيسية" else "Dashboard"
    fun navCatalog(lang: AppLanguage): String = if (lang == AppLanguage.AR) "الكتالوج" else "Catalog"
    fun navExpenses(lang: AppLanguage): String = if (lang == AppLanguage.AR) "سجل الصرف" else "Expenses"
    fun navWorkshops(lang: AppLanguage): String = if (lang == AppLanguage.AR) "الورش" else "Workshops"
    fun navSettings(lang: AppLanguage): String = if (lang == AppLanguage.AR) "الإعدادات" else "Settings"

    fun activeVehicle(lang: AppLanguage): String = if (lang == AppLanguage.AR) "السيارة النشطة 🚗:" else "Active Vehicle 🚗:"
    fun fleetTitle(lang: AppLanguage, count: Int): String = if (lang == AppLanguage.AR) "أسطول سياراتي ($count) 🚘" else "My Fleet ($count) 🚘"
    fun switchVehicle(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تبديل أو إضافة سيارة" else "Switch / Add Vehicle"
    fun addVehicleBtn(lang: AppLanguage): String = if (lang == AppLanguage.AR) "إضافة سيارة جديدة ➕" else "Add New Vehicle ➕"
    fun odoMeterLabel(lang: AppLanguage): String = if (lang == AppLanguage.AR) "العداد" else "Odometer"

    // Quick Actions
    fun quickActionLogService(lang: AppLanguage): String = if (lang == AppLanguage.AR) "سجل صيانة" else "Log Service"
    fun quickActionReminder(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تذكير صيانة 📅" else "Set Reminder 📅"
    fun quickActionFuel(lang: AppLanguage): String = if (lang == AppLanguage.AR) "سجل الوقود" else "Fuel Log"
    fun quickActionResale(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تقرير البيع" else "Resale Report"
    fun quickActionObd(lang: AppLanguage): String = if (lang == AppLanguage.AR) "أكواد OBD" else "OBD Scanner"

    // Theme & Lang
    fun themePickerTitle(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تخصيص الثيمات واللغة 🎨" else "Themes & Language 🎨"
    fun chooseLanguage(lang: AppLanguage): String = if (lang == AppLanguage.AR) "لغة التطبيق / App Language:" else "App Language / لغة التطبيق:"
    fun arabicLang(lang: AppLanguage): String = if (lang == AppLanguage.AR) "🇸🇦 العربية (RTL)" else "🇸🇦 Arabic"
    fun englishLang(lang: AppLanguage): String = if (lang == AppLanguage.AR) "🇬🇧 English (LTR)" else "🇬🇧 English"
    fun selectThemeHeader(lang: AppLanguage, count: Int): String = if (lang == AppLanguage.AR) "اختر الثيم ($count ثيمات مختلفة تماماً):" else "Choose Theme ($count Unique Styles):"
    fun applyThemeBtn(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تطبيق الثيم واللغة ✨" else "Apply Theme & Language ✨"

    // Health Card
    fun carHealth(lang: AppLanguage): String = if (lang == AppLanguage.AR) "صحة السيارة" else "Vehicle Health"
    fun readinessIndicator(lang: AppLanguage): String = if (lang == AppLanguage.AR) "مؤشر الجاهزية 🚦:" else "Readiness 🚦:"
    fun nextMaintenance(lang: AppLanguage): String = if (lang == AppLanguage.AR) "أقرب صيانة:" else "Next Service:"
    fun testPushNotif(lang: AppLanguage): String = if (lang == AppLanguage.AR) "فحص وإرسال تنبيه الصيانة (Push Notification) 🔔" else "Test Service Push Notification 🔔"

    // Status Texts
    fun statusGreen(lang: AppLanguage): String = if (lang == AppLanguage.AR) "🟢 حالة آمنة وممتازة" else "🟢 Safe & Excellent Condition"
    fun statusYellow(lang: AppLanguage): String = if (lang == AppLanguage.AR) "🟡 تنبيه - صيانة قريبة" else "🟡 Notice - Service Approaching"
    fun statusRed(lang: AppLanguage): String = if (lang == AppLanguage.AR) "🔴 صيانة متأخرة حرجة" else "🔴 Critical Maintenance Due"

    // General
    fun close(lang: AppLanguage): String = if (lang == AppLanguage.AR) "إغلاق" else "Close"
    fun cancel(lang: AppLanguage): String = if (lang == AppLanguage.AR) "إلغاء" else "Cancel"
    fun confirm(lang: AppLanguage): String = if (lang == AppLanguage.AR) "تأكيد" else "Confirm"
    fun back(lang: AppLanguage): String = if (lang == AppLanguage.AR) "رجوع" else "Back"
    fun search(lang: AppLanguage): String = if (lang == AppLanguage.AR) "بحث..." else "Search..."
}
