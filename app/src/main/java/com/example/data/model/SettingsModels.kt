package com.example.data.model

enum class AppThemeMode(val id: String, val arabicName: String, val description: String) {
    CLASSIC_PARCHMENT("classic_parchment", "المصحف المذهب", "ورق عاجي دافئ بلمسات زمردية وذهبية فاخرة"),
    EMERALD_SANCTUARY("emerald_sanctuary", "الزمرد والياقوت", "تصميم عصري مشرق بدرجات الزمرد الملكي والذهب"),
    NIGHT_MUSHAF("night_mushaf", "المخمل الليلي", "خلفية ليلية فاخرة مريحة للعين مع ذهب مضيء")
}

enum class MushafDisplayMode(val id: String, val arabicName: String, val description: String) {
    CONTINUOUS_MUSHAF("continuous", "كتاب المصحف (تورق الصفحات)", "عرض القرآن في صفحات ورقية مزخرفة قابلة للتقليب مع تلوين الآية المقروءة بالأحمر"),
    VERSE_CARDS("cards", "بطاقات الآيات المنفصلة", "عرض كل آية في بطاقة مستقلة لسهولة التركيز والحفظ")
}

enum class SettingDetailType(val key: String, val arabicTitle: String) {
    RECITER("reciter", "اختيار القارئ الافتراضي"),
    TYPOGRAPHY("typography", "حجم ونوع الخط القرآني"),
    THEME("theme", "مظهر التطبيق والألوان"),
    RECITATION("recitation", "إعدادات التلاوة والتشغيل"),
    QUIZ("quiz", "إعدادات المسابقة وقارئ الأسئلة"),
    OFFLINE("offline", "تحميل المصحف للقراءة بدون إنترنت")
}

data class UserSettings(
    val selectedReciterId: String = "alafasy",
    val quranFontSizeSp: Float = 25f,
    val themeMode: AppThemeMode = AppThemeMode.CLASSIC_PARCHMENT,
    val mushafDisplayMode: MushafDisplayMode = MushafDisplayMode.CONTINUOUS_MUSHAF,
    val autoScrollToRedVerse: Boolean = true,
    val repeatSingleAyah: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val quizTimeSeconds: Int = 30,
    val quizAutoReadQuestion: Boolean = true,
    val ttsSpeechRate: Float = 0.95f,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val highScore: Int = 0
)
