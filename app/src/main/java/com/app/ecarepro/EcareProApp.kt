package com.app.ecarepro

import androidx.compose.runtime.Composable
import com.app.ecarepro.navigation.EcareProNavDisplay

@Composable
fun ECareProApp(
    onRestartActivity: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    EcareProNavDisplay(
        onRestartActivity = onRestartActivity,
        onLogout = onLogout,
    )
}