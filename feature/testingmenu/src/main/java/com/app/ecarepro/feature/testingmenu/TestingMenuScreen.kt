package com.app.ecarepro.feature.testingmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.navigation.AssignmentNavigationGraph
import com.app.ecarepro.feature.dashboard.navigation.DashboardNavigationGraph
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.feature.syllabus.navigation.SyllabusNavigationGraph
import com.app.ecarepro.feature.timetable.navigation.TimetableNavigationGraph
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph

object ModuleRegistry {
    val screens: Map<String, NavKey> = mapOf(
        "OnBoarding" to OnboardingNavigationGraph.Onboarding,
        "School Code" to SchoolCodeNavigationGraph.SchoolCode,
        "Login" to LoginNavigationGraph.Login(schoolCode = "DEMOIN"),
        "Dashboard" to DashboardNavigationGraph.Dashboard,
        "Timetable" to TimetableNavigationGraph.Timetable,
        "Syllabus" to SyllabusNavigationGraph.Syllabus,
        "Assignment" to AssignmentNavigationGraph.Assignment,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestingMenuScreen(
    navigateToBack: () -> Unit,
    navigateToModule: (NavKey) -> Unit,
) {

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Feature Modules",
                onNavigationClicked = navigateToBack,

                )
        },
        containerColor = MaterialTheme.appColors.background
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
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

}


@Preview()
@Composable
private fun TestingMenuPreview() {
    EcareProTheme {
        TestingMenuScreen(
            navigateToBack = {},
            navigateToModule = {}
        )
    }
}