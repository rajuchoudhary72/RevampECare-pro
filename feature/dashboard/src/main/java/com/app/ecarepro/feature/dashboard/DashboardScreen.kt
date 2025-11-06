package com.app.ecarepro.feature.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.dashboard.component.BottomNavigationBar
import com.app.ecarepro.feature.dashboard.component.Destination
import com.app.ecarepro.feature.dashboard.component.SearchFabButton
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
) {
    val innerNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.appColors.surface,
                drawerShape = MaterialTheme.shapes.extraSmall
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Drawer Content")
                }

            }
        }
    ) {
        EcareProScaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { destination ->
                        if (destination == Destination.MENU) {
                            scope.launch { drawerState.open() }
                        } else {
                            selectedDestination = destination.ordinal
                            innerNavController.navigate(destination.route) {
                                popUpTo(innerNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    })
            },
            floatingActionButton = {
                SearchFabButton { }
            },
            containerColor = MaterialTheme.appColors.background
        ) { paddingValues ->
            NavHost(
                navController = innerNavController,
                startDestination = startDestination.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Destination.HOME.route) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Home")
                    }
                }
                composable(Destination.MESSAGE.route) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Message")
                    }
                }
                composable(Destination.Profile.route) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Profile")
                    }
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    EcareProTheme {
        DashboardScreen(

        )
    }
}
