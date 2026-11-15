package com.example.data.model

enum class QuizCategory(val id: String, val arabicName: String, val description: String) {
    ALL("all", "مسابقة شاملة", "أسئلة متنوعة من القرآن والسيرة والفقه والتاريخ"),
    QURAN("quran", "القرآن الكريم", "علوم القرآن والسور والآيات"),
    SEERAH("seerah", "السيرة النبوية", "حياة النبي ﷺ والغزوات والأحداث"),
    FIQH("fiqh", "الفقه والعبادات", "أحكام الصلاة والصيام والزكاة والحج"),
    PROPHETS("prophets", "الأنبياء والصحابة", "قصص الأنبياء وسير الصحابة الكرام"),
    AQEEDAH("aqeedah", "العقيدة والأخلاق", "أركان الإيمان والآداب الإسلامية")
}

data class QuizQuestion(
    val id: Int,
    val category: QuizCategory,
    val questionArabic: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationArabic: String
)

data class QuizResultSummary(
    val totalQuestions: Int,
    val correctCount: Int,
    val bestStreak: Int,
    val categoryName: String
)
