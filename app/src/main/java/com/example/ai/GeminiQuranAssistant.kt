package com.example.ai

import android.util.Log
import com.example.data.model.QuizCategory
import com.example.data.model.QuizQuestion
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiQuranAssistant"

sealed interface TafsirUiState {
    data object Idle : TafsirUiState
    data object Loading : TafsirUiState
    data class Streaming(val title: String, val text: String) : TafsirUiState
    data class Success(val title: String, val text: String) : TafsirUiState
    data class Error(val message: String) : TafsirUiState
}

class GeminiQuranAssistant {

    private val tafsirModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "أنت باحث ومعلم متخصص في التفسير الميسر وعلوم القرآن الكريم باللغة العربية الفصحى الواضحة. " +
                        "قدّم شرحاً موثوقاً ومختصراً وميسراً يوضح معنى الآية الكريمة ومقصدها التربوي والإيماني بأسلوب رصين وأدب رفيع."
                )
            }
        )
    }

    private val quizSchema = Schema.obj(
        properties = mapOf(
            "questions" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "questionArabic" to Schema.string(description = "Arabic Islamic quiz question text"),
                        "options" to Schema.array(
                            items = Schema.string(),
                            description = "Exactly 4 short Arabic answer choices"
                        ),
                        "correctIndex" to Schema.integer(description = "Zero-based index (0 to 3) of the correct option"),
                        "explanationArabic" to Schema.string(description = "Short educational explanation in Arabic")
                    )
                ),
                description = "List of Islamic knowledge quiz questions"
            )
        )
    )

    private val quizModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = quizSchema
            }
        )
    }

    fun streamVerseTafsir(
        surahName: String,
        ayahNumber: Int,
        ayahText: String
    ): Flow<TafsirUiState> = flow {
        val title = "التفسير الميسر: سورة $surahName (آية $ayahNumber)"
        emit(TafsirUiState.Loading)
        val prompt = """
            اشرح هذه الآية الكريمة شرحاً ميسراً وواضحاً باللغة العربية في نقاط مرتبة:
            1. المعنى الإجمالي للآية.
            2. أبرز المفردات أو اللطائف البلاغية.
            3. فائدة إيمانية وعملية للقارئ.
            
            الآية: «$ayahText» (سورة $surahName، الآية رقم $ayahNumber).
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                tafsirModel.generateContentStream(prompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(TafsirUiState.Streaming(title, accumulated.toString()))
                    }
                }
            }
            if (accumulated.isNotBlank()) {
                emit(TafsirUiState.Success(title, accumulated.toString()))
            } else {
                emit(TafsirUiState.Error("لم يُرجع النموذج نصاً للتفسير. حاول مرة أخرى."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Tafsir streaming timed out", e)
            emit(TafsirUiState.Error("انتهت مهلة الاتصال بعد ٦٠ ثانية. يرجى المحاولة مجدداً."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI tafsir failed", e)
            emit(TafsirUiState.Error(e.localizedMessage ?: "تعذّر الحصول على التفسير عبر Gemini AI."))
        }
    }

    fun streamScholarlyAnswer(questionArabic: String): Flow<TafsirUiState> = flow {
        val title = "إجابة المساعد القرآني (Gemini)"
        emit(TafsirUiState.Loading)
        val prompt = """
            أجب باللغة العربية الواضحة عن السؤال التالي في علوم القرآن أو السيرة أو المعرفة الإسلامية بأسلوب علمي رصين ومختصر:
            $questionArabic
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                tafsirModel.generateContentStream(prompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(TafsirUiState.Streaming(title, accumulated.toString()))
                    }
                }
            }
            if (accumulated.isNotBlank()) {
                emit(TafsirUiState.Success(title, accumulated.toString()))
            } else {
                emit(TafsirUiState.Error("لم يتم استلام إجابة من النموذج."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Scholarly answer timed out", e)
            emit(TafsirUiState.Error("انتهت مهلة الانتظار. يرجى إعادة المحاولة."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI scholarly answer failed", e)
            emit(TafsirUiState.Error(e.localizedMessage ?: "حدث خطأ أثناء الاتصال بخدمة Gemini AI."))
        }
    }

    suspend fun generateIslamicQuizQuestions(
        topicArabic: String,
        count: Int = 5
    ): Result<List<QuizQuestion>> {
        return try {
            val prompt = """
                أنشئ $count أسئلة دينية ثقافية موثوقة باللغة العربية حول موضوع: "$topicArabic".
                يجب أن يحتوي كل سؤال على 4 خيارات بالضبط، وأن يكون correctIndex بين 0 و 3، مع شرح علمي مختصر ومفيد في explanationArabic.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                quizModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("استجابة فارغة من النموذج.")
            val root = JSONObject(jsonText)
            val arr = root.getJSONArray("questions")
            val resultList = buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val qText = obj.getString("questionArabic")
                    val optArr = obj.getJSONArray("options")
                    val opts = (0 until optArr.length()).map { optArr.getString(it) }.take(4)
                    if (opts.size == 4) {
                        val correctIdx = obj.optInt("correctIndex", 0).coerceIn(0, 3)
                        val explanation = obj.optString("explanationArabic", "")
                        add(
                            QuizQuestion(
                                id = 1000 + i,
                                category = QuizCategory.ALL,
                                questionArabic = qText,
                                options = opts,
                                correctIndex = correctIdx,
                                explanationArabic = explanation
                            )
                        )
                    }
                }
            }
            if (resultList.isEmpty()) {
                Result.failure(IllegalStateException("تعذّر تكوين أسئلة صالحة من الاستجابة."))
            } else {
                Result.success(resultList)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Quiz generation timed out", e)
            Result.failure(Exception("انتهت مهلة توليد الأسئلة. حاول مجدداً."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate Gemini quiz questions", e)
            Result.failure(e)
        }
    }
}
