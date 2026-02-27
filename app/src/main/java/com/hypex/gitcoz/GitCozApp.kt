package com.hypex.gitcoz

import android.app.Application
import com.hypex.gitcoz.di.AppContainer
import com.hypex.gitcoz.di.DefaultAppContainer

class GitCozApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
