package com.app.ecarepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        super.onCreate(savedInstanceState)
        setContent {
            EcareProTheme {
                ECareProApp()
            }
        }
    }
}