package com.huhn.jmpcexample.application

import android.app.Application
import android.content.Context
import com.huhn.androidarchitectureexample.dependencyInjection.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class JmpcExampleApplication :Application() {
    companion object {
        lateinit  var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = this.applicationContext

        startKoin {
            androidLogger()
            androidContext(this@JmpcExampleApplication)
            modules(listOf(koinModule))
        }
    }
}