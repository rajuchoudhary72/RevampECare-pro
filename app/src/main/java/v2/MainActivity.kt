package v2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.MainActivity.Companion.EXTRA_COMPOSE_DESTINATION
import dagger.hilt.android.AndroidEntryPoint
import v2.navigation.ComposeNavigationDestination
import v2.navigation.EXTRA_DESTINATION_ID
import v2.navigation.EXTRA_EXTRAS
import v2.navigation.EXTRA_LEGACY_FLOW
import v2.navigation.LegacyNavigationDestination


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Get the Parcelable destination from the Intent
        val composeDestination: ComposeNavigationDestination? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    EXTRA_COMPOSE_DESTINATION,
                    ComposeNavigationDestination::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_COMPOSE_DESTINATION)
            }

        setContent {
            EcareProTheme {
                ECateProApp(
                    startDestination = composeDestination,
                    navigateToLegacyFlow = { legacyNavigationDestination ->
                        navigateToLegacyFlow(
                            legacyNavigationDestination = legacyNavigationDestination
                        )
                    }
                )
            }
        }
    }

    private fun navigateToLegacyFlow(legacyNavigationDestination: LegacyNavigationDestination) {
        finish()
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_LEGACY_FLOW, true)
                putExtra(EXTRA_DESTINATION_ID, legacyNavigationDestination.destinationId)
                putExtra(EXTRA_EXTRAS, legacyNavigationDestination.extras)
            }
        )
    }
}