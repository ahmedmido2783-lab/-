package com.example.data.catalog

import com.example.data.model.RuqyahAudioSource.QuranVerse
import com.example.data.model.RuqyahAudioSource.SpokenDua
import com.example.data.model.RuqyahProgram
import com.example.data.model.RuqyahSegment

object RuqyahCatalog {

    private fun quranSeg(
        id: String,
        title: String,
        surah: Int,
        ayah: Int,
        reference: String,
        repeatHint: String = ""
    ): RuqyahSegment = RuqyahSegment(
        id = id,
        titleArabic = title,
        textArabic = OfflineSeedVerses.verseText(surah, ayah),
        referenceArabic = reference,
        repeatHint = repeatHint,
        audioSource = QuranVerse(surah, ayah)
    )

    val programs: List<RuqyahProgram> = listOf(
        RuqyahProgram(
            id = "comprehensive_quran_ruqyah",
            titleArabic = "الرقية الشرعية الشاملة من القرآن الكريم",
            subtitleArabic = "الفاتحة وآية الكرسي وأواخر البقرة والمعوذات بصوت القارئ كاملة",
            durationEstimate = "متصلة بالكامل • ٢٧ آية قرآنية",
            categoryBadge = "القرآن الكريم",
            segments = buildList {
                // Al-Fatihah 1..7
                for (a in 1..7) {
                    add(quranSeg("fatiha_$a", "سورة الفاتحة", 1, a, "سورة الفاتحة : $a"))
                }
                // Al-Baqarah 1..5
                for (a in 1..5) {
                    add(quranSeg("baqarah_$a", "أول سورة البقرة", 2, a, "سورة البقرة : $a"))
                }
                // Ayat al-Kursi
                add(quranSeg("kursi", "آية الكرسي", 2, 255, "سورة البقرة : ٢٥٥", "آية عظيمة للحفظ"))
                // Al-Baqarah 285-286
                add(quranSeg("baqarah_285", "خواتيم سورة البقرة", 2, 285, "سورة البقرة : ٢٨٥"))
                add(quranSeg("baqarah_286", "خواتيم سورة البقرة", 2, 286, "سورة البقرة : ٢٨٦"))
                // Al-A'raf 54 & Yunus 81-82
                add(quranSeg("araf_54", "آية التسخير والخلق", 7, 54, "سورة الأعراف : ٥٤"))
                add(quranSeg("yunus_81", "آيات إبطال السحر", 10, 81, "سورة يونس : ٨١"))
                add(quranSeg("yunus_82", "آيات إبطال السحر", 10, 82, "سورة يونس : ٨٢"))
                // Al-Isra 82
                add(quranSeg("isra_82", "آية الشفاء والرحمة", 17, 82, "سورة الإسراء : ٨٢"))
                // Al-Hashr 21-24
                for (a in 21..24) {
                    add(quranSeg("hashr_$a", "خواتيم سورة الحشر", 59, a, "سورة الحشر : $a"))
                }
                // Al-Ikhlas 1..4
                for (a in 1..4) {
                    add(quranSeg("ikhlas_$a", "سورة الإخلاص", 112, a, "سورة الإخلاص : $a"))
                }
                // Al-Falaq 1..5
                for (a in 1..5) {
                    add(quranSeg("falaq_$a", "سورة الفلق", 113, a, "سورة الفلق : $a"))
                }
                // An-Nas 1..6
                for (a in 1..6) {
                    add(quranSeg("nas_$a", "سورة الناس", 114, a, "سورة الناس : $a"))
                }
            }
        ),
        RuqyahProgram(
            id = "healing_tranquility",
            titleArabic = "رقية الشفاء والسكينة وانشراح الصدر",
            subtitleArabic = "آيات الشفاء الست وسورة الشرح وآيات الطمأنينة مع التلاوة المتزامنة",
            durationEstimate = "متصلة بالكامل • ١٧ آية قرآنية",
            categoryBadge = "الشفاء والسكينة",
            segments = buildList {
                add(quranSeg("shifa_tawbah", "آية شفاء الصدور", 9, 14, "سورة التوبة : ١٤"))
                add(quranSeg("shifa_yunus", "آية الموعظة والشفاء", 10, 57, "سورة يونس : ٥٧"))
                add(quranSeg("shifa_nahl", "آية الشفاء للناس", 16, 69, "سورة النحل : ٦٩"))
                add(quranSeg("shifa_isra", "آية القرآن شفاء ورحمة", 17, 82, "سورة الإسراء : ٨٢"))
                add(quranSeg("shifa_shuara", "آية اليقين بالشفاء", 26, 80, "سورة الشعراء : ٨٠"))
                add(quranSeg("shifa_fussilat", "آية الهدى والشفاء", 41, 44, "سورة فصلت : ٤٤"))
                add(quranSeg("sakinah_fath", "آية إنزال السكينة", 48, 4, "سورة الفتح : ٤"))
                for (a in 1..8) {
                    add(quranSeg("sharh_$a", "سورة الشرح كاملة", 94, a, "سورة الشرح : $a"))
                }
                add(quranSeg("qalam_51", "آية دفع العين", 68, 51, "سورة القلم : ٥١"))
                add(quranSeg("qalam_52", "آية الذكر للعالمين", 68, 52, "سورة القلم : ٥٢"))
            }
        ),
        RuqyahProgram(
            id = "prophetic_ruqyah_duas",
            titleArabic = "أدعية الرقية الشرعية من السنة النبوية",
            subtitleArabic = "مجموعة الأدعية النبوية الصحيحة للشفاء والتحصين مع القارئ الصوتي",
            durationEstimate = "متصلة بالكامل • ٨ أدعية مأثورة",
            categoryBadge = "السنة النبوية",
            segments = listOf(
                RuqyahSegment(
                    id = "dua_1",
                    titleArabic = "دعاء رقية جبريل عليه السلام",
                    textArabic = "بِسْمِ اللَّهِ أَرْقِيكَ، مِنْ كُلِّ شَيْءٍ يُؤْذِيكَ، مِنْ شَرِّ كُلِّ نَفْسٍ أَوْ عَيْنِ حَاسِدٍ، اللَّهُ يَشْفِيكَ، بِسْمِ اللَّهِ أَرْقِيكَ.",
                    referenceArabic = "رواه مسلم",
                    repeatHint = "يُقرأ ثلاث مرات",
                    audioSource = SpokenDua("بسم الله أرقيك، من كل شيء يؤذيك، من شر كل نفس أو عين حاسد، الله يشفيك، بسم الله أرقيك")
                ),
                RuqyahSegment(
                    id = "dua_2",
                    titleArabic = "دعاء إزالة البأس وطلب الشفاء",
                    textArabic = "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا.",
                    referenceArabic = "متفق عليه",
                    repeatHint = "دعاء نبوي عظيم",
                    audioSource = SpokenDua("اللهم رب الناس، أذهب البأس، اشفه وأنت الشافي، لا شفاء إلا شفاؤك، شفاءً لا يغادر سقماً")
                ),
                RuqyahSegment(
                    id = "dua_3",
                    titleArabic = "دعاء وضع اليد على موضع الألم",
                    textArabic = "بِسْمِ اللَّهِ، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ.",
                    referenceArabic = "رواه مسلم",
                    repeatHint = "بسم الله ثلاثاً ثم الدعاء سبعاً",
                    audioSource = SpokenDua("بسم الله، أعوذ بالله وقدرته من شر ما أجد وأحاذر")
                ),
                RuqyahSegment(
                    id = "dua_4",
                    titleArabic = "دعاء الكلمات التامات للحفظ",
                    textArabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
                    referenceArabic = "رواه مسلم",
                    repeatHint = "يُقرأ ثلاث مرات صباحاً ومساءً",
                    audioSource = SpokenDua("أعوذ بكلمات الله التامات من شر ما خلق")
                ),
                RuqyahSegment(
                    id = "dua_5",
                    titleArabic = "دعاء التعويذ الشامل",
                    textArabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّةِ، مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ.",
                    referenceArabic = "رواه البخاري",
                    repeatHint = "كان النبي ﷺ يعوّذ به الحسن والحسين",
                    audioSource = SpokenDua("أعوذ بكلمات الله التامة، من كل شيطان وهامة، ومن كل عين لامة")
                ),
                RuqyahSegment(
                    id = "dua_6",
                    titleArabic = "دعاء الحماية اليومية",
                    textArabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
                    referenceArabic = "رواه أبو داود والترمذي",
                    repeatHint = "ثلاث مرات صباحاً ومساءً",
                    audioSource = SpokenDua("بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء وهو السميع العليم")
                ),
                RuqyahSegment(
                    id = "dua_7",
                    titleArabic = "دعاء شفاء المريض العظيم",
                    textArabic = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ.",
                    referenceArabic = "رواه أبو داود والترمذي",
                    repeatHint = "يُكرر سبع مرات",
                    audioSource = SpokenDua("أسأل الله العظيم رب العرش العظيم أن يشفيك")
                ),
                RuqyahSegment(
                    id = "dua_8",
                    titleArabic = "دعاء الكرب والهم",
                    textArabic = "لَا إِلَٰهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَٰهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَٰهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ.",
                    referenceArabic = "متفق عليه",
                    repeatHint = "دعاء تفريج الكروب",
                    audioSource = SpokenDua("لا إله إلا الله العظيم الحليم، لا إله إلا الله رب العرش العظيم، لا إله إلا الله رب السماوات ورب الأرض ورب العرش الكريم")
                )
            )
        ),
        RuqyahProgram(
            id = "daily_protection",
            titleArabic = "رقية التحصين اليومي بالمعوذات وآية الكرسي",
            subtitleArabic = "التحصين الصباحي والمسائي الثابت بآيات القرآن الكريم بصوت الشيخ",
            durationEstimate = "متصلة بالكامل • ٢٤ آية قرآنية",
            categoryBadge = "التحصين اليومي",
            segments = buildList {
                add(quranSeg("prot_kursi", "آية الكرسي", 2, 255, "سورة البقرة : ٢٥٥"))
                add(quranSeg("prot_b285", "أواخر البقرة", 2, 285, "سورة البقرة : ٢٨٥"))
                add(quranSeg("prot_b286", "أواخر البقرة", 2, 286, "سورة البقرة : ٢٨٦"))
                for (a in 1..6) {
                    add(quranSeg("prot_kaf_$a", "سورة الكافرون", 109, a, "سورة الكافرون : $a"))
                }
                for (a in 1..4) {
                    add(quranSeg("prot_ikh_$a", "سورة الإخلاص", 112, a, "سورة الإخلاص : $a"))
                }
                for (a in 1..5) {
                    add(quranSeg("prot_fal_$a", "سورة الفلق", 113, a, "سورة الفلق : $a"))
                }
                for (a in 1..6) {
                    add(quranSeg("prot_nas_$a", "سورة الناس", 114, a, "سورة الناس : $a"))
                }
            }
        )
    )

    fun getById(id: String): RuqyahProgram =
        programs.firstOrNull { it.id == id } ?: programs.first()
}
