package com.app.ecarepro.testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.feature.login.navigation.EntryLoginNavigation
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.EntrySchoolCodeNavigation
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.onboarding.feature.navigation.EntryOnboardingNavigation
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

// Registry of available module screens
object ModuleRegistry {
    val screens: Map<String, NavKey> = mapOf(
        "OnBoarding" to OnboardingNavigationGraph.Onboarding,
        "School Code" to SchoolCodeNavigationGraph.SchoolCode,
        "Login" to LoginNavigationGraph.Login(schoolCode = "DEMOIN"),
    )
}

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EcareProTheme {
                TestNav()
            }
        }
    }
}


@Composable
fun TestNav() {
    val backStack = remember { mutableStateListOf<NavKey>(TestNavigationGraph.Modules) }

    NavDisplay(
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator(),
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<TestNavigationGraph.Modules> {
                TestHostScreen {
                    backStack.add(it)
                }
            }
            EntryOnboardingNavigation(navigateToAddSchool = {
                backStack.removeLastOrNull()
            })

            EntrySchoolCodeNavigation(
                backStack = backStack,
                navigateToLogin = { schoolCode ->
                    backStack.add(LoginNavigationGraph.Login(schoolCode = schoolCode))
                },
            )

            EntryLoginNavigation(
                backStack = backStack,
                backToSchoolCode = {
                    backStack.removeLastOrNull()
                },
                navigateToMain = {

                }
            )

        }
    )
}

@Serializable
sealed interface TestNavigationGraph : NavKey {
    @Serializable
    data object Modules : TestNavigationGraph
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestHostScreen(
    navigateToModule: (NavKey) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    "Feature Modules"
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ModuleRegistry.screens.keys.toList()) { name ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToModule(ModuleRegistry.screens[name]!!)
                        }
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

