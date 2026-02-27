package com.hypex.gitcoz.data.feedback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TelegramFeedbackSender(
    private val botToken: String,
    private val chatId: String,
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun sendFeedback(
        timestamp: String,
        username: String,
        feedbackBody: String,
        attachments: List<FeedbackAttachment>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (botToken.isBlank() || chatId.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Telegram bot token or chat id is missing.")
            )
        }

        try {
            val message = buildString {
                appendLine("<b>GitCoz Feedback</b>")
                appendLine("___")
                appendLine("<b>Timestamp:</b> ${timestamp.escapeHtml()}")
                appendLine("<b>Username:</b> ${username.escapeHtml()}")
                appendLine("<b>Feedback:</b>")
                appendLine(feedbackBody.escapeHtml())
                append("___")
            }

            sendMessage(message)
            attachments.forEach { sendPhoto(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sendMessage(message: String) {
        val body = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", message)
            .add("parse_mode", "HTML")
            .add("disable_web_page_preview", "true")
            .build()

        val request = Request.Builder()
            .url("https://api.telegram.org/bot$botToken/sendMessage")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()?.take(300)
                error("Telegram sendMessage failed (${response.code}): $errorBody")
            }
        }
    }

    private fun sendPhoto(attachment: FeedbackAttachment) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("chat_id", chatId)
            .addFormDataPart(
                "photo",
                attachment.fileName,
                attachment.bytes.toRequestBody(attachment.mimeType.toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("https://api.telegram.org/bot$botToken/sendPhoto")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()?.take(300)
                error("Telegram sendPhoto failed (${response.code}): $errorBody")
            }
        }
    }

    private fun String.escapeHtml(): String = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
