package com.hima.alwarsha.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.hima.alwarsha.BuildConfig
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.model.ChatMessage
import com.hima.alwarsha.data.model.ChatRole
import com.hima.alwarsha.network.GeminiApiClient
import com.hima.alwarsha.network.GeminiBlob
import com.hima.alwarsha.network.GeminiContent
import com.hima.alwarsha.network.GeminiGenerateContentRequest
import com.hima.alwarsha.network.GeminiGenerationConfig
import com.hima.alwarsha.network.GeminiPart
import java.io.ByteArrayOutputStream

class DiagnosticsRepository {

    /**
     * Sends the full conversation (including the new turn) to Gemini and returns the assistant's
     * reply text, or a friendly Arabic error message if the request fails or is blocked.
     */
    suspend fun sendMessage(car: CarEntity?, history: List<ChatMessage>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MISSING") return "محتاج مفتاح Gemini API الأول عشان الميزة دي تشتغل."

        val request = GeminiGenerateContentRequest(
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt(car)))),
            contents = history.map { it.toGeminiContent() },
            generationConfig = GeminiGenerationConfig(temperature = 0.4, maxOutputTokens = 1024)
        )

        return try {
            val response = GeminiApiClient.service.generateContent(GeminiApiClient.MODEL, apiKey, request)
            response.text ?: "معنديش رد واضح على السؤال ده، جرّب تسأل بطريقة تانية أو ترفق صورة أوضح."
        } catch (e: Exception) {
            "حصل خطأ في الاتصال بالمساعد الذكي. جرّب تاني بعد شوية."
        }
    }

    private fun systemPrompt(car: CarEntity?): String {
        val carInfo = if (car != null) {
            "سيارة المستخدم الحالية: ${car.brand} ${car.model} (${car.year})، نوع الفتيس: ${car.transmissionType}، " +
                "قراءة العداد الحالية: ${car.currentOdometer} كم، نوع الزيت المستخدم: ${car.oilType}."
        } else {
            "المستخدم لسه ما سجّلش بيانات سيارة في التطبيق."
        }
        return "أنت مساعد ميكانيكي خبير اسمه \"الورشة\". $carInfo " +
            "المستخدم غالبًا مش متخصص في السيارات، فجاوب دايمًا بالعربية وبلغة بسيطة وعملية ومباشرة، " +
            "واقترح خطوات واضحة قابلة للتنفيذ. لو المستخدم أرفق صورة لعطل أو جزء في السيارة، حلّلها واربط " +
            "ملاحظاتك بالسياق اللي وصفه. لو مش متأكد من السبب الدقيق، قول كذا احتمال مرتب من الأقرب للأبعد " +
            "بدل ما تدّعي يقين مش موجود."
    }

    private fun ChatMessage.toGeminiContent(): GeminiContent {
        val parts = mutableListOf<GeminiPart>()
        if (text.isNotBlank()) parts.add(GeminiPart(text = text))
        imageBitmap?.let { bitmap ->
            parts.add(GeminiPart(inlineData = GeminiBlob(mimeType = "image/jpeg", data = bitmap.toBase64Jpeg())))
        }
        return GeminiContent(role = if (role == ChatRole.USER) "user" else "model", parts = parts)
    }

    private fun Bitmap.toBase64Jpeg(): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}
