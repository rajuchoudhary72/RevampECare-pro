package com.app.ecarepro.utils


import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.ecarepro.ui.language.LocalizationManager

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // This is the key - wrap the base context before it's used for inflation
        super.attachBaseContext(LocalizedContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install our custom layout inflater factory before calling super.onCreate()
        installLocalizedLayoutFactory(this)

        super.onCreate(savedInstanceState)

        // Register for translation updates
        LocalizationManager.registerActivity(this)
    }

    // This is optional as attachBaseContext handles the context wrapping
    // but can be kept for compatibility with existing code
    override fun getLayoutInflater(): LayoutInflater {
        // Return a layout inflater backed by our localized context
        return LayoutInflater.from(LocalizedContext(this))
    }
}