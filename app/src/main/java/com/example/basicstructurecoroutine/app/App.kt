package com.example.basicstructurecoroutine.app

import android.app.Application
import com.example.basicstructurecoroutine.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by JeeteshSurana.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // declare used Android context
            androidContext(this@App)
            // declare the level for logging
            androidLogger()
            printLogger()

            // declare modules
            modules(listOf(appModule))
        }
    }
}