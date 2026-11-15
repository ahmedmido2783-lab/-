package com.example.data.catalog

import com.example.data.model.QuizCategory
import com.example.data.model.QuizCategory.AQEEDAH
import com.example.data.model.QuizCategory.FIQH
import com.example.data.model.QuizCategory.PROPHETS
import com.example.data.model.QuizCategory.QURAN
import com.example.data.model.QuizCategory.SEERAH
import com.example.data.model.QuizQuestion

object QuizCatalog {
    val allQuestions: List<QuizQuestion> = listOf(
        // القرآن الكريم (10 أسئلة)
        QuizQuestion(
            id = 1,
            category = QURAN,
            questionArabic = "كم عدد سور القرآن الكريم؟",
            options = listOf("١١٢ سورة", "١١٤ سورة", "١١٦ سورة", "١٢٠ سورة"),
            correctIndex = 1,
            explanationArabic = "يتكون القرآن الكريم من ١١٤ سورة، تبدأ بسورة الفاتحة وتنتهي بسورة الناس."
        ),
        QuizQuestion(
            id = 2,
            category = QURAN,
            questionArabic = "ما هي السورة التي تُسمى قلب القرآن؟",
            options = listOf("سورة الرحمن", "سورة الملك", "سورة يس", "سورة الواقعة"),
            correctIndex = 2,
            explanationArabic = "سورة يس تُعرف عند أهل العلم بقلب القرآن الكريم لعظيم معانيها في التوحيد والبعث."
        ),
        QuizQuestion(
            id = 3,
            category = QURAN,
            questionArabic = "ما هي السورة الوحيدة في القرآن التي لا تبدأ بالبسملة؟",
            options = listOf("سورة الأنفال", "سورة التوبة", "سورة الرعد", "سورة الحجرات"),
            correctIndex = 1,
            explanationArabic = "سورة التوبة (براءة) هي السورة الوحيدة التي لا تفتتح ببسم الله الرحمن الرحيم."
        ),
        QuizQuestion(
            id = 4,
            category = QURAN,
            questionArabic = "ما هي أطول آية في القرآن الكريم؟",
            options = listOf("آية الكرسي", "آية الدين في سورة البقرة", "آية المواريث", "آية النور"),
            correctIndex = 1,
            explanationArabic = "آية الدَّين (الآية ٢٨٢ من سورة البقرة) هي أطول آية في كتاب الله وتبلغ صفحة كاملة تقريباً."
        ),
        QuizQuestion(
            id = 5,
            category = QURAN,
            questionArabic = "ما هي السورة التي ذُكرت فيها البسملة مرتين؟",
            options = listOf("سورة النمل", "سورة سبأ", "سورة الأنبياء", "سورة القصص"),
            correctIndex = 0,
            explanationArabic = "سورة النمل تبدأ بالبسملة ثم تذكرها مرة ثانية في رسالة نبي الله سليمان: ﴿إِنَّهُ مِن سُلَيْمَانَ وَإِنَّهُ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ﴾."
        ),
        QuizQuestion(
            id = 6,
            category = QURAN,
            questionArabic = "ما هي السورة التي تعدل ثلث القرآن الكريم في الأجر؟",
            options = listOf("سورة الكافرون", "سورة الفاتحة", "سورة الإخلاص", "سورة الزلزلة"),
            correctIndex = 2,
            explanationArabic = "ثبت في الصحيح أن سورة الإخلاص (قل هو الله أحد) تعدل ثلث القرآن."
        ),
        QuizQuestion(
            id = 7,
            category = QURAN,
            questionArabic = "في أي سورة وردت آية الكرسي؟",
            options = listOf("سورة آل عمران", "سورة النساء", "سورة البقرة", "سورة الأنعام"),
            correctIndex = 2,
            explanationArabic = "آية الكرسي هي الآية رقم ٢٥٥ من سورة البقرة، وهي أعظم آية في كتاب الله."
        ),
        QuizQuestion(
            id = 8,
            category = QURAN,
            questionArabic = "ما هي أقصر سورة في القرآن الكريم؟",
            options = listOf("سورة العصر", "سورة الكوثر", "سورة النصر", "سورة الإخلاص"),
            correctIndex = 1,
            explanationArabic = "سورة الكوثر هي أقصر سورة في القرآن الكريم وتضم ثلاث آيات فقط."
        ),
        QuizQuestion(
            id = 9,
            category = QURAN,
            questionArabic = "ما هو عدد أجزاء القرآن الكريم؟",
            options = listOf("٢٠ جزءاً", "٢٥ جزءاً", "٣٠ جزءاً", "٦٠ جزءاً"),
            correctIndex = 2,
            explanationArabic = "ينقسم المصحف الشريف إلى ٣٠ جزءاً و٦٠ حزباً لتسهيل التلاوة والحفظ."
        ),
        QuizQuestion(
            id = 10,
            category = QURAN,
            questionArabic = "ما أول ما نزل من القرآن الكريم على النبي ﷺ؟",
            options = listOf("سورة الفاتحة", "أوائل سورة العلق", "أوائل سورة المدثر", "أوائل سورة المزمل"),
            correctIndex = 1,
            explanationArabic = "أول ما نزل من القرآن قول الله تعالى: ﴿اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ﴾ من سورة العلق."
        ),

        // السيرة النبوية (10 أسئلة)
        QuizQuestion(
            id = 11,
            category = SEERAH,
            questionArabic = "في أي عام وُلد النبي محمد ﷺ؟",
            options = listOf("عام الحزن", "عام الفيل", "عام الفتح", "عام الرمادة"),
            correctIndex = 1,
            explanationArabic = "وُلد النبي محمد ﷺ في مكة المكرمة في عام الفيل الموافق ٥٧١م تقريباً."
        ),
        QuizQuestion(
            id = 12,
            category = SEERAH,
            questionArabic = "من هي أول من آمن بالنبي ﷺ من النساء؟",
            options = listOf("عائشة بنت أبي بكر", "فاطمة الزهراء", "خديجة بنت خويلد", "حفصة بنت عمر"),
            correctIndex = 2,
            explanationArabic = "السيدة خديجة بنت خويلد رضي الله عنها هي أول من آمن بالنبي ﷺ وصدّقه وآزره."
        ),
        QuizQuestion(
            id = 13,
            category = SEERAH,
            questionArabic = "في أي غار كان يتعبد النبي ﷺ قبل البعثة ونزل فيه الوحي؟",
            options = listOf("غار ثور", "غار حراء", "غار أحد", "غار الجبل"),
            correctIndex = 1,
            explanationArabic = "كان النبي ﷺ يتحنث في غار حراء بجبل النور في مكة وفيه نزل عليه جبريل بالوحي."
        ),
        QuizQuestion(
            id = 14,
            category = SEERAH,
            questionArabic = "من رافق النبي ﷺ في الهجرة من مكة إلى المدينة؟",
            options = listOf("عمر بن الخطاب", "عثمان بن عفان", "أبو بكر الصديق", "علي بن أبي طالب"),
            correctIndex = 2,
            explanationArabic = "أبو بكر الصديق رضي الله عنه هو صاحب النبي ﷺ في الغار ورفيقه في الهجرة النبوية."
        ),
        QuizQuestion(
            id = 15,
            category = SEERAH,
            questionArabic = "في أي سنة هجرية وقعت غزوة بدر الكبرى؟",
            options = listOf("السنة الأولى للهجرة", "السنة الثانية للهجرة", "السنة الثالثة للهجرة", "السنة الخامسة للهجرة"),
            correctIndex = 1,
            explanationArabic = "وقعت غزوة بدر الكبرى في السابع عشر من رمضان في السنة الثانية للهجرة."
        ),
        QuizQuestion(
            id = 16,
            category = SEERAH,
            questionArabic = "ما هو أول مسجد بُني في الإسلام؟",
            options = listOf("المسجد النبوي", "مسجد القبلتين", "مسجد قباء", "المسجد الأقصى"),
            correctIndex = 2,
            explanationArabic = "مسجد قباء بالمدينة المنورة هو أول مسجد أسسه النبي ﷺ على التقوى عند هجرته."
        ),
        QuizQuestion(
            id = 17,
            category = SEERAH,
            questionArabic = "من الذي اقترح حفر الخندق في غزوة الأحزاب؟",
            options = listOf("سعد بن معاذ", "سلمان الفارسي", "خالد بن الوليد", "عمرو بن العاص"),
            correctIndex = 1,
            explanationArabic = "أشار الصحابي الجليل سلمان الفارسي رضي الله عنه بحفر الخندق حول المدينة فأخذ النبي ﷺ بمشورته."
        ),
        QuizQuestion(
            id = 18,
            category = SEERAH,
            questionArabic = "في أي سنة هجرية تم فتح مكة المكرمة؟",
            options = listOf("السنة السادسة للهجرة", "السنة الثامنة للهجرة", "السنة التاسعة للهجرة", "السنة العاشرة للهجرة"),
            correctIndex = 1,
            explanationArabic = "فُتحت مكة المكرمة في شهر رمضان من السنة الثامنة للهجرة."
        ),
        QuizQuestion(
            id = 19,
            category = SEERAH,
            questionArabic = "من هي مرضعة النبي ﷺ من بني سعد؟",
            options = listOf("ثويبة الأسلمية", "حليمة السعدية", "أم أيمن", "الشفاء بنت عوف"),
            correctIndex = 1,
            explanationArabic = "حليمة السعدية رضي الله عنها هي التي أرضعت النبي ﷺ في بادية بني سعد."
        ),
        QuizQuestion(
            id = 20,
            category = SEERAH,
            questionArabic = "كم عدد حجّات النبي ﷺ بعد الهجرة؟",
            options = listOf("حجة واحدة", "حجتان", "ثلاث حجّات", "أربع حجّات"),
            correctIndex = 0,
            explanationArabic = "حج النبي ﷺ بعد الهجرة حجة واحدة وهي حجة الوداع في السنة العاشرة للهجرة."
        ),

        // الفقه والعبادات (10 أسئلة)
        QuizQuestion(
            id = 21,
            category = FIQH,
            questionArabic = "كم عدد الصلوات المفروضة على المسلم في اليوم والليلة؟",
            options = listOf("ثلاث صلوات", "أربع صلوات", "خمس صلوات", "ست صلوات"),
            correctIndex = 2,
            explanationArabic = "الصلوات المفروضة خمس في اليوم والليلة: الفجر والظهر والعصر والمغرب والعشاء."
        ),
        QuizQuestion(
            id = 22,
            category = FIQH,
            questionArabic = "ما هي الصلاة الوسطى التي ورد الأمر بالمحافظة عليها في القرآن؟",
            options = listOf("صلاة الفجر", "صلاة الظهر", "صلاة العصر", "صلاة المغرب"),
            correctIndex = 2,
            explanationArabic = "بين النبي ﷺ في الأحاديث الصحيحة أن الصلاة الوسطى هي صلاة العصر."
        ),
        QuizQuestion(
            id = 23,
            category = FIQH,
            questionArabic = "ما هو الركن الأعظم في الحج الذي لا يصح الحج بدونه؟",
            options = listOf("طواف القدوم", "الوقوف بعرفة", "رمي الجمرات", "السعي بين الصفا والمروة"),
            correctIndex = 1,
            explanationArabic = "قال النبي ﷺ: «الحج عرفة»، فالوقوف بعرفة هو أعظم أركان الحج."
        ),
        QuizQuestion(
            id = 24,
            category = FIQH,
            questionArabic = "كم عدد ركعات صلاة الكسوف في كل ركعة؟",
            options = listOf("ركوع واحد", "ركوعان", "ثلاثة ركوعات", "أربعة ركوعات"),
            correctIndex = 1,
            explanationArabic = "صلاة الكسوف ركعتان، وفي كل ركعة قيامان وقراءتان وركوعان وسجدتان."
        ),
        QuizQuestion(
            id = 25,
            category = FIQH,
            questionArabic = "في أي شهر هجري فرض الله صيام الفريضة؟",
            options = listOf("شهر شعبان", "شهر رمضان", "شهر ذي الحجة", "شهر محرم"),
            correctIndex = 1,
            explanationArabic = "صيام شهر رمضان المبارك هو الركن الرابع من أركان الإسلام."
        ),
        QuizQuestion(
            id = 26,
            category = FIQH,
            questionArabic = "ما مقدار زكاة النقود إذا بلغت النصاب وحال عليها الحول؟",
            options = listOf("١٪", "٢.٥٪ (ربع العشر)", "٥٪", "١٠٪"),
            correctIndex = 1,
            explanationArabic = "مقدار زكاة المال هو ربع العشر أي ٢.٥٪ عند بلوغ النصاب ومرور حول هجري كامل."
        ),
        QuizQuestion(
            id = 27,
            category = FIQH,
            questionArabic = "ما اسم السجدتين اللتين يُصلّيهما المسلم لجبر النقص أو الزيادة في الصلاة؟",
            options = listOf("سجود التلاوة", "سجود الشكر", "سجود السهو", "سجود التحية"),
            correctIndex = 2,
            explanationArabic = "سجود السهو سجدتان يجبر بهما المصلي ما وقع في صلاته من سهو."
        ),
        QuizQuestion(
            id = 28,
            category = FIQH,
            questionArabic = "كم عدد أشواط الطواف حول الكعبة المشرفة؟",
            options = listOf("خمسة أشواط", "ستة أشواط", "سبعة أشواط", "ثمانية أشواط"),
            correctIndex = 2,
            explanationArabic = "يتكون الطواف حول الكعبة من سبعة أشواط تبدأ من الحجر الأسود وتنتهي عنده."
        ),
        QuizQuestion(
            id = 29,
            category = FIQH,
            questionArabic = "ما هي الصلاة التي لا ركوع فيها ولا سجود؟",
            options = listOf("صلاة الاستسقاء", "صلاة الجنازة", "صلاة العيدين", "صلاة الوتر"),
            correctIndex = 1,
            explanationArabic = "صلاة الجنازة تتكون من أربع تكبيرات مع القيام والدعاء للميت دون ركوع أو سجود."
        ),
        QuizQuestion(
            id = 30,
            category = FIQH,
            questionArabic = "ما حكم قراءة سورة الفاتحة في كل ركعة من الصلاة؟",
            options = listOf("مستحبة فقط", "ركن لا تصح الصلاة إلا بها", "واجبة في الركعة الأولى فقط", "مباحة"),
            correctIndex = 1,
            explanationArabic = "قال النبي ﷺ: «لا صلاة لمن لم يقرأ بفاتحة الكتاب»، فهي ركن أساسي في الصلاة."
        ),

        // الأنبياء والصحابة (10 أسئلة)
        QuizQuestion(
            id = 31,
            category = PROPHETS,
            questionArabic = "من هو النبي الذي لُقّب بكليم الله؟",
            options = listOf("إبراهيم عليه السلام", "موسى عليه السلام", "عيسى عليه السلام", "نوح عليه السلام"),
            correctIndex = 1,
            explanationArabic = "نبي الله موسى عليه السلام هو كليم الله لأن الله تعالى كلّمه تكليماً."
        ),
        QuizQuestion(
            id = 32,
            category = PROPHETS,
            questionArabic = "من هو النبي الذي ابتلعه الحوت ثم نجّاه الله؟",
            options = listOf("أيوب عليه السلام", "يونس عليه السلام", "زكريا عليه السلام", "إلياس عليه السلام"),
            correctIndex = 1,
            explanationArabic = "يونس عليه السلام (ذو النون) التقمه الحوت فسبّح الله في الظلمات فنجّاه الله."
        ),
        QuizQuestion(
            id = 33,
            category = PROPHETS,
            questionArabic = "من هو الصحابي الملقب بـ «ذو النورين»؟",
            options = listOf("أبو بكر الصديق", "عمر بن الخطاب", "عثمان بن عفان", "الزبير بن العوام"),
            correctIndex = 2,
            explanationArabic = "عثمان بن عفان رضي الله عنه لُقّب بذي النورين لزواجه من ابنتي النبي ﷺ رقية ثم أم كلثوم."
        ),
        QuizQuestion(
            id = 34,
            category = PROPHETS,
            questionArabic = "من هو النبي الذي سُخّر له الريح والطير وعُلّم منطق الطير؟",
            options = listOf("داود عليه السلام", "سليمان عليه السلام", "يوسف عليه السلام", "إسحاق عليه السلام"),
            correctIndex = 1,
            explanationArabic = "آتى الله نبيه سليمان عليه السلام ملكاً عظيماً وسخر له الريح والجن وعلمه منطق الطير."
        ),
        QuizQuestion(
            id = 35,
            category = PROPHETS,
            questionArabic = "من هو أول مؤذن في الإسلام؟",
            options = listOf("عبد الله بن أم مكتوم", "بلال بن رباح", "أبو محذورة", "سعد القرظ"),
            correctIndex = 1,
            explanationArabic = "بلال بن رباح رضي الله عنه هو أول من أذّن في الإسلام بأمر النبي ﷺ."
        ),
        QuizQuestion(
            id = 36,
            category = PROPHETS,
            questionArabic = "من هو الصحابي الذي اهتز عرش الرحمن لموته؟",
            options = listOf("حمزة بن عبد المطلب", "سعد بن معاذ", "مصعب بن عمير", "جعفر بن أبي طالب"),
            correctIndex = 1,
            explanationArabic = "ثبت في الصحيحين أن عرش الرحمن اهتز لموت سعد بن معاذ رضي الله عنه."
        ),
        QuizQuestion(
            id = 37,
            category = PROPHETS,
            questionArabic = "من هو النبي الذي بنى الكعبة المشرفة مع ابنه إسماعيل عليه السلام؟",
            options = listOf("نوح عليه السلام", "إبراهيم عليه السلام", "يعقوب عليه السلام", "شعيب عليه السلام"),
            correctIndex = 1,
            explanationArabic = "رفع إبراهيم وإسماعيل عليهما السلام القواعد من البيت الحرام في مكة المكرمة."
        ),
        QuizQuestion(
            id = 38,
            category = PROPHETS,
            questionArabic = "من هو الصحابي الملقب بـ «سيف الله المسلول»؟",
            options = listOf("علي بن أبي طالب", "سعد بن أبي وقاص", "خالد بن الوليد", "أبو عبيدة بن الجراح"),
            correctIndex = 2,
            explanationArabic = "خالد بن الوليد رضي الله عنه لقّبه النبي ﷺ بسيف الله المسلول في غزوة مؤتة."
        ),
        QuizQuestion(
            id = 39,
            category = PROPHETS,
            questionArabic = "من هو النبي الذي أُلقي في النار فجعلها الله عليه برداً وسلاماً؟",
            options = listOf("إبراهيم عليه السلام", "صالح عليه السلام", "هود عليه السلام", "لوط عليه السلام"),
            correctIndex = 0,
            explanationArabic = "قال الله تعالى: ﴿قُلْنَا يَا نَارُ كُونِي بَرْدًا وَسَلَامًا عَلَىٰ إِبْرَاهِيمَ﴾."
        ),
        QuizQuestion(
            id = 40,
            category = PROPHETS,
            questionArabic = "من هو الصحابي الوحيد الذي ذُكر اسمه صريحاً في القرآن الكريم؟",
            options = listOf("أبو بكر الصديق", "زيد بن حارثة", "أنس بن مالك", "معاذ بن جبل"),
            correctIndex = 1,
            explanationArabic = "زيد بن حارثة رضي الله عنه هو الصحابي الذي ورد اسمه صريحاً في سورة الأحزاب."
        ),

        // العقيدة والأخلاق (10 أسئلة)
        QuizQuestion(
            id = 41,
            category = AQEEDAH,
            questionArabic = "كم عدد أركان الإيمان؟",
            options = listOf("أربعة أركان", "خمسة أركان", "ستة أركان", "سبعة أركان"),
            correctIndex = 2,
            explanationArabic = "أركان الإيمان ستة: الإيمان بالله، وملائكته، وكتبه، ورسله، واليوم الآخر، والقدر خيره وشره."
        ),
        QuizQuestion(
            id = 42,
            category = AQEEDAH,
            questionArabic = "كم عدد أركان الإسلام؟",
            options = listOf("أربعة أركان", "خمسة أركان", "ستة أركان", "سبعة أركان"),
            correctIndex = 1,
            explanationArabic = "بُني الإسلام على خمس: الشهادتان، وإقام الصلاة، وإيتاء الزكاة، وصوم رمضان، وحج البيت."
        ),
        QuizQuestion(
            id = 43,
            category = AQEEDAH,
            questionArabic = "من هو الملك الموكل بنزول الوحي على الأنبياء؟",
            options = listOf("ميكائيل عليه السلام", "إسرافيل عليه السلام", "جبريل عليه السلام", "رضوان عليه السلام"),
            correctIndex = 2,
            explanationArabic = "جبريل عليه السلام هو الروح الأمين الموكل بتبليغ الوحي من الله إلى أنبيائه ورسله."
        ),
        QuizQuestion(
            id = 44,
            category = AQEEDAH,
            questionArabic = "ما معنى الإحسان كما عرّفه النبي ﷺ في حديث جبريل؟",
            options = listOf("الصدقة في السر", "أن تعبد الله كأنك تراه فإن لم تكن تراه فإنه يراك", "صيام التطوع", "قراءة القرآن يومياً"),
            correctIndex = 1,
            explanationArabic = "الإحسان أعلى مراتب الدين: أن تعبد الله كأنك تراه، فإن لم تكن تراه فإنه يراك."
        ),
        QuizQuestion(
            id = 45,
            category = AQEEDAH,
            questionArabic = "ما هي أعظم كلمة ذكر في الإسلام؟",
            options = listOf("سبحان الله", "الحمد لله", "لا إله إلا الله", "الله أكبر"),
            correctIndex = 2,
            explanationArabic = "أفضل الذكر لا إله إلا الله، وأفضل الدعاء الحمد لله."
        ),
        QuizQuestion(
            id = 46,
            category = AQEEDAH,
            questionArabic = "ما هي السماوات السبع؟ وأي الكتب السماوية أنزل على داود عليه السلام؟",
            options = listOf("التوراة", "الإنجيل", "الزبور", "الصحف"),
            correctIndex = 2,
            explanationArabic = "الزبور هو الكتاب السماوي الذي أنزله الله على نبيه داود عليه السلام."
        ),
        QuizQuestion(
            id = 47,
            category = AQEEDAH,
            questionArabic = "ما هو الخلق الذي قال عنه النبي ﷺ: «أثقل شيء في ميزان المؤمن يوم القيامة»؟",
            options = listOf("الكرم", "حسن الخلق", "الشجاعة", "الفصاحة"),
            correctIndex = 1,
            explanationArabic = "قال النبي ﷺ: «ما من شيء أثقل في ميزان المؤمن يوم القيامة من خلق حسن»."
        ),
        QuizQuestion(
            id = 48,
            category = AQEEDAH,
            questionArabic = "كم عدد أسماء الله الحسنى المأثور حفظها وإحصاؤها بدخول الجنة؟",
            options = listOf("٦٦ اسماً", "٧٧ اسماً", "٩٩ اسماً", "١١٤ اسماً"),
            correctIndex = 2,
            explanationArabic = "قال النبي ﷺ: «إن لله تسعة وتسعين اسماً مائة إلا واحداً من أحصاها دخل الجنة»."
        ),
        QuizQuestion(
            id = 49,
            category = AQEEDAH,
            questionArabic = "ما هو الدعاء المأثور عند الدخول في الصباح أو المساء لحفظ المسلم؟",
            options = listOf("دعاء السفر", "أذكار الصباح والمساء", "دعاء الاستخارة", "دعاء القنوت"),
            correctIndex = 1,
            explanationArabic = "أذكار الصباح والمساء حصن حصين للمسلم في يومه وليلته."
        ),
        QuizQuestion(
            id = 50,
            category = AQEEDAH,
            questionArabic = "من هم أولو العزم من الرسل؟",
            options = listOf("نوح وإبراهيم وموسى وعيسى ومحمد عليهم السلام", "آدم وهود وصالح وشعيب ولوط", "داود وسليمان ويوسف ويونس وزكريا", "إدريس وإسماعيل وإسحاق ويعقوب"),
            correctIndex = 0,
            explanationArabic = "أولو العزم من الرسل خمسة: نوح وإبراهيم وموسى وعيسى ومحمد صلوات الله وسلامه عليهم أجمعين."
        )
    )

    fun getForCategory(category: QuizCategory): List<QuizQuestion> =
        if (category == QuizCategory.ALL) allQuestions
        else allQuestions.filter { it.category == category }
}
