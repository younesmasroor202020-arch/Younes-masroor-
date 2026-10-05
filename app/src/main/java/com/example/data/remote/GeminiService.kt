package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

data class GeneratedAudioResult(
    val base64Data: String,
    val mimeType: String,
    val title: String
)

data class GeneratedImageResult(
    val base64Data: String,
    val mimeType: String,
    val caption: String? = null
)

class GeminiService {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    private fun checkApiKey(): String? {
        val key = getApiKey()
        if (key.isBlank() || key == "MY_GEMINI_API_KEY" || key == "YOUR_API_KEY") {
            return "مفتاح API غير متوفر أو افتراضي. يرجى ضبط GEMINI_API_KEY عبر لوحة الأسرار (Secrets Panel) في AI Studio."
        }
        return null
    }

    /**
     * TTS using gemini-3.8-flash-tts
     */
    suspend fun generateSpeech(
        text: String,
        voiceName: String = "Puck",
        title: String = "تسجيل صوتي"
    ): ApiResult<GeneratedAudioResult> = withContext(Dispatchers.IO) {
        val keyError = checkApiKey()
        if (keyError != null) return@withContext ApiResult.Error(keyError)

        try {
            val endpoint = "$baseUrl/models/gemini-3.8-flash-tts:generateContent?key=${getApiKey()}"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", text)
                    ))
                ))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("AUDIO"))
                    put("speechConfig", JSONObject().apply {
                        put("voiceConfig", JSONObject().apply {
                            put("prebuiltVoiceConfig", JSONObject().apply {
                                put("voiceName", voiceName)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = parseError(bodyString, response.code)
                    return@withContext ApiResult.Error(errMsg)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext ApiResult.Error("لم يتم استلام أي رد صوتي من النموذج")
                }

                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val data = inlineData.getString("data")
                            val mime = inlineData.optString("mimeType", "audio/mp3")
                            return@withContext ApiResult.Success(
                                GeneratedAudioResult(base64Data = data, mimeType = mime, title = title)
                            )
                        }
                    }
                }
                ApiResult.Error("الرد لا يحتوي على بيانات صوتية صالحة")
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "TTS generation error", e)
            ApiResult.Error("خطأ أثناء تحويل النص إلى صوت: ${e.localizedMessage ?: e.message}")
        }
    }

    /**
     * Music generation using lyria-3-clip-preview (clips up to 30s) or lyria-3-pro-preview
     */
    suspend fun generateMusic(
        prompt: String,
        useProModel: Boolean = false,
        title: String = "موسيقى سينمائية"
    ): ApiResult<GeneratedAudioResult> = withContext(Dispatchers.IO) {
        val keyError = checkApiKey()
        if (keyError != null) return@withContext ApiResult.Error(keyError)

        try {
            val modelName = if (useProModel) "lyria-3-pro-preview" else "lyria-3-clip-preview"
            val endpoint = "$baseUrl/models/$modelName:generateContent?key=${getApiKey()}"

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("AUDIO"))
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = parseError(bodyString, response.code)
                    return@withContext ApiResult.Error(errMsg)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext ApiResult.Error("لم يتم استلام مقطع موسيقي من النموذج")
                }

                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val data = inlineData.getString("data")
                            val mime = inlineData.optString("mimeType", "audio/mp3")
                            return@withContext ApiResult.Success(
                                GeneratedAudioResult(base64Data = data, mimeType = mime, title = title)
                            )
                        }
                    }
                }
                ApiResult.Error("الرد لا يحتوي على بيانات موسيقية صالحة")
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Music generation error", e)
            ApiResult.Error("خطأ أثناء توليد الموسيقى: ${e.localizedMessage ?: e.message}")
        }
    }

    /**
     * Image Generation using gemini-3.1-flash-image-preview
     */
    suspend fun generateImage(
        prompt: String,
        aspectRatio: String = "16:9"
    ): ApiResult<GeneratedImageResult> = withContext(Dispatchers.IO) {
        val keyError = checkApiKey()
        if (keyError != null) return@withContext ApiResult.Error(keyError)

        try {
            val endpoint = "$baseUrl/models/gemini-3.1-flash-image-preview:generateContent?key=${getApiKey()}"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("TEXT").put("IMAGE"))
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", aspectRatio)
                        put("imageSize", "1K")
                    })
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = parseError(bodyString, response.code)
                    return@withContext ApiResult.Error(errMsg)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext ApiResult.Error("لم يتم استلام أي صورة من النموذج")
                }

                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                var textCaption: String? = null
                var imageBase64: String? = null
                var mimeType = "image/jpeg"

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("text")) {
                            textCaption = part.getString("text")
                        }
                        if (part.has("inlineData")) {
                            val inlineData = part.getJSONObject("inlineData")
                            imageBase64 = inlineData.getString("data")
                            mimeType = inlineData.optString("mimeType", "image/jpeg")
                        }
                    }
                }

                if (imageBase64 != null) {
                    ApiResult.Success(GeneratedImageResult(imageBase64, mimeType, textCaption))
                } else {
                    ApiResult.Error("لم يُرجع النموذج صورة مطابقة للطلب")
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Image generation error", e)
            ApiResult.Error("خطأ أثناء توليد الصورة: ${e.localizedMessage ?: e.message}")
        }
    }

    /**
     * Image Editing using gemini-3.1-flash-image-preview
     */
    suspend fun editImage(
        editPrompt: String,
        base64InputImage: String,
        aspectRatio: String = "16:9"
    ): ApiResult<GeneratedImageResult> = withContext(Dispatchers.IO) {
        val keyError = checkApiKey()
        if (keyError != null) return@withContext ApiResult.Error(keyError)

        try {
            val endpoint = "$baseUrl/models/gemini-3.1-flash-image-preview:generateContent?key=${getApiKey()}"

            val partsArray = JSONArray().apply {
                put(JSONObject().put("text", editPrompt))
                put(JSONObject().put("inlineData", JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64InputImage)
                }))
            }

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", partsArray)
                ))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("TEXT").put("IMAGE"))
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", aspectRatio)
                        put("imageSize", "1K")
                    })
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = parseError(bodyString, response.code)
                    return@withContext ApiResult.Error(errMsg)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext ApiResult.Error("لم يتم استلام أي صورة معدلة")
                }

                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                var textCaption: String? = null
                var imageBase64: String? = null
                var mimeType = "image/jpeg"

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("text")) {
                            textCaption = part.getString("text")
                        }
                        if (part.has("inlineData")) {
                            val inlineData = part.getJSONObject("inlineData")
                            imageBase64 = inlineData.getString("data")
                            mimeType = inlineData.optString("mimeType", "image/jpeg")
                        }
                    }
                }

                if (imageBase64 != null) {
                    ApiResult.Success(GeneratedImageResult(imageBase64, mimeType, textCaption))
                } else {
                    ApiResult.Error("لم يُرجع النموذج صورة معدلة")
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Image editing error", e)
            ApiResult.Error("خطأ أثناء تعديل الصورة: ${e.localizedMessage ?: e.message}")
        }
    }

    /**
     * Director AI Advice / Scene analysis using gemini-3.5-flash
     */
    suspend fun consultDirector(
        sceneTitle: String,
        directorQuestion: String
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        val keyError = checkApiKey()
        if (keyError != null) return@withContext ApiResult.Error(keyError)

        try {
            val endpoint = "$baseUrl/models/gemini-3.5-flash:generateContent?key=${getApiKey()}"
            val systemPrompt = "أنت مخرج سينمائي خبير ومستشار إخراجي متخصص في السينما العربية والتراثية اليمنية، خاصة منطقة شبوة ومدينة الروضة وتاريخ عائلة مسرور. تقدم نصائح فنية إخراجية متعمقة بأسلوب محترف وموجز حول زوايا الكاميرا، الإضاءة، الأداء التمثيلي، والديكور."
            
            val requestJson = JSONObject().apply {
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", systemPrompt)
                )))
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", "المشهد: $sceneTitle\nالسؤال الإخراجي: $directorQuestion")
                    ))
                ))
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = parseError(bodyString, response.code)
                    return@withContext ApiResult.Error(errMsg)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)?.optJSONObject("content")
                    ?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    ApiResult.Success(text)
                } else {
                    ApiResult.Error("لم يتم استلام نص استشاري")
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Director consult error", e)
            ApiResult.Error("خطأ أثناء استشارة المخرج: ${e.localizedMessage ?: e.message}")
        }
    }

    private fun parseError(body: String, code: Int): String {
        return try {
            val json = JSONObject(body)
            val error = json.optJSONObject("error")
            val message = error?.optString("message") ?: "كود الخطأ: $code"
            "فشل الطلب ($code): $message"
        } catch (_: Exception) {
            "فشل الطلب مع كود الحالة: $code"
        }
    }
}
