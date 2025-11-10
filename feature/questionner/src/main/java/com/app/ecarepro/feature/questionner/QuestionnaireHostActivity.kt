package com.app.ecarepro.feature.questionner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.feature.questionner.navigation.questionnaireGraph
import com.app.ecarepro.feature.questionner.navigation.questionnaireGraphRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionnaireHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EcareProTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = questionnaireGraphRoute
                ) {
                    questionnaireGraph(
                        onBackClick = { finish() },
                        navController = navController
                    )
                }
            }
        }
    }
}
