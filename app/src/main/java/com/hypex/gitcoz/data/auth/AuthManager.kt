package com.hypex.gitcoz.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthManager(context: Context) {

    companion object {
        const val CLIENT_ID = "Ov23li32W3KIXPSqYL0M"
        const val CLIENT_SECRET = "132ea94a2285eeee38c52e3d6ec8eb136a107074"
        const val REDIRECT_URI = "gitcoz://callback"
        const val SCOPES = "user repo read:org"

        private const val PREFS_NAME = "gitcoz_auth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_DISPLAY_NAME = "display_name"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(getAccessToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _accessToken = MutableStateFlow(getAccessToken())
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    fun getAuthUrl(): String {
        return Uri.Builder()
            .scheme("https")
            .authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .build()
            .toString()
    }

    suspend fun exchangeCodeForToken(code: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val body = FormBody.Builder()
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build()

            val request = Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            val json = JSONObject(responseBody)

            if (json.has("access_token")) {
                val token = json.getString("access_token")
                saveAccessToken(token)
                Result.success(token)
            } else {
                val error = json.optString("error_description", "Token exchange failed")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        _accessToken.value = token
        _isLoggedIn.value = true
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun saveUserInfo(username: String, avatarUrl: String, displayName: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_AVATAR_URL, avatarUrl)
            .putString(KEY_DISPLAY_NAME, displayName)
            .apply()
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getAvatarUrl(): String? = prefs.getString(KEY_AVATAR_URL, null)
    fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)

    fun logout() {
        prefs.edit().clear().apply()
        _accessToken.value = null
        _isLoggedIn.value = false
    }
}
