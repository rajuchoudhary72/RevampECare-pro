package com.app.ecarepro

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

private const val PREFS_LANGUAGE = "language_prefs"
private const val KEY_LANGUAGE_CODE = "language_code"
private const val DEFAULT_LANGUAGE = "en"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val code = newBase
            .getSharedPreferences(PREFS_LANGUAGE, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EcareProTheme {
                ECareProApp(
                    onRestartActivity = { restartApp() },
                    onLogout = { logoutAndRestart() },
                )
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun logoutAndRestart() {
        try {
            cacheDir?.deleteRecursively()
        } catch (_: Exception) {}
        restartApp()
    }
}