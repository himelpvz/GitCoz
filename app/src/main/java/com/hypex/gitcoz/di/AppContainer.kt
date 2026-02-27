package com.hypex.gitcoz.di

import android.content.Context
import android.util.Log
import com.hypex.gitcoz.BuildConfig
import com.hypex.gitcoz.data.api.GitHubApi
import com.hypex.gitcoz.data.auth.AuthManager
import com.hypex.gitcoz.data.feedback.TelegramFeedbackSender
import com.hypex.gitcoz.data.repository.GitHubRepositoryImpl
import com.hypex.gitcoz.data.settings.SettingsManager
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val gitHubRepository: GitHubRepositoryContract
    val settingsManager: SettingsManager
    val authManager: AuthManager
    val telegramFeedbackSender: TelegramFeedbackSender
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://api.github.com/"

    override val settingsManager: SettingsManager by lazy {
        SettingsManager(context)
    }

    override val authManager: AuthManager by lazy {
        AuthManager(context)
    }

    override val telegramFeedbackSender: TelegramFeedbackSender by lazy {
        TelegramFeedbackSender(
            botToken = BuildConfig.TELEGRAM_BOT_TOKEN,
            chatId = BuildConfig.TELEGRAM_CHAT_ID
        )
    }

    private val networkDebugInterceptor = Interceptor { chain ->
        val request = chain.request()
        if (BuildConfig.DEBUG) {
            Log.d("GitCozNetwork", "-> ${request.method} ${request.url}")
        }
        val response = chain.proceed(request)
        if (BuildConfig.DEBUG) {
            Log.d("GitCozNetwork", "<- ${response.code} ${request.url}")
        }
        response
    }

    private val authInterceptor = Interceptor { chain ->
        val token = authManager.getAccessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(networkDebugInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val retrofitService: GitHubApi by lazy {
        retrofit.create(GitHubApi::class.java)
    }

    override val gitHubRepository: GitHubRepositoryContract by lazy {
        GitHubRepositoryImpl(retrofitService)
    }
}
