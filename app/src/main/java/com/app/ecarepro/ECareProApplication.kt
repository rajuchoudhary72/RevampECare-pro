package com.app.ecarepro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ECareProApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        print("App Started")
    }
}