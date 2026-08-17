# الورشة (AlWarsha)

تطبيق شخصي لصيانة السيارات — تتبع تلقائي للكيلومترات بالـ GPS، محرك ذكي لتوصية لزوجة الزيت، جدولة الصيانة الدورية، والبحث عن أفضل ورش قريبة بتقييمات حقيقية من Google Places.

## البناء محليًا

1. انسخ `.env.example` إلى `.env` وحط مفتاح `PLACES_API_KEY` بتاعك (Google Places API).
2. افتح المشروع في Android Studio (بيتكفل بتحميل Gradle وAndroid SDK تلقائيًا).
3. `Run` على جهاز حقيقي (التتبع التلقائي محتاج GPS فعلي).

## البناء عبر GitHub Actions

كل push على `main` بيشغّل workflow يبني نسخة debug APK وترفعها كـ Artifact في تبويب Actions. لازم يكون فيه Repository Secret اسمه `GOOGLE_PLACES_API_KEY`.
