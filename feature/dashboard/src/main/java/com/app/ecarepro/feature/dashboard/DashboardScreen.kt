package com.app.ecarepro.feature.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.dashboard.component.BottomNavigationBar
import com.app.ecarepro.feature.dashboard.component.Destination
import com.app.ecarepro.feature.dashboard.component.SearchFabButton
import com.app.ecarepro.feature.dashboard.home.DashboardHeader
import com.app.ecarepro.feature.menu.MenuDrawerContent
import com.app.ecarepro.feature.menu.MenuEvent
import com.app.ecarepro.feature.menu.MenuViewModel
import com.app.ecarepro.feature.message.MessageScreen
import com.app.ecarepro.feature.questionner.navigation.navigateToQuestionnaireGraph
import com.app.ecarepro.feature.questionner.navigation.questionnaireGraph
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    navigateToProfile: () -> Unit,
    onMenuNavigate: (Int) -> Unit = {},
    navigateToGlobalSearch: () -> Unit = {},
    navigateToNotifications: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToHomeSelection: () -> Unit = {},
    homeContent: @Composable () -> Unit = {},
) {
    val innerNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val scope = rememberCoroutineScope()

    val menuViewModel: MenuViewModel = hiltViewModel()
    val menuUiState by menuViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.screenEvent.collect { event ->
            when (event) {
                is MenuEvent.NavigateTo -> onMenuNavigate(event.menuId)
                is MenuEvent.CloseDrawer -> scope.launch { drawerState.close() }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f),
                drawerContainerColor = MaterialTheme.appColors.background,
            ) {
                MenuDrawerContent(
                    uiState = menuUiState,
                    handleIntent = menuViewModel::handleIntent,
                )
            }
        }
    ) {
        EcareProScaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedDestination = selectedDestination,
                    profilePhotoUrl = menuUiState.header?.userPhotoUrl,
                    onDestinationSelected = { destination ->
                        when (destination) {
                            Destination.MENU -> scope.launch { drawerState.open() }
                            Destination.Profile -> navigateToProfile()
                            else -> {
                                selectedDestination = destination.ordinal
                                innerNavController.navigate(destination.route) {
                                    popUpTo(innerNavController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                    })
            },
            floatingActionButton = {
                SearchFabButton { navigateToGlobalSearch() }
            },
            containerColor = MaterialTheme.appColors.background
        ) { paddingValues ->
            Column(Modifier.fillMaxSize().padding(paddingValues)) {
                if (selectedDestination == Destination.HOME.ordinal) {
                    DashboardHeader(
                        userName = menuUiState.header?.currentAccountName
                            ?.split(" ")?.firstOrNull().orEmpty(),
                        userPhotoUrl = menuUiState.header?.userPhotoUrl.orEmpty(),
                        onNotificationClick = navigateToNotifications,
                        onHomeSelectionClick = navigateToHomeSelection,
                        onSettingsClick = navigateToSettings,
                    )
                }
                NavHost(
                    navController = innerNavController,
                    startDestination = startDestination.route,
                    modifier = Modifier.weight(1f)
                ) {
                composable(Destination.HOME.route) {
                    homeContent()
                }
                composable(Destination.MESSAGE.route) {
                    MessageScreen(
                        navigateToBack = {}
                    )
                }
                composable(Destination.Profile.route) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Profile")
                    }
                }
                questionnaireGraph(
                    onBackClick = { innerNavController.popBackStack() },
                    navController = innerNavController,
                )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    EcareProTheme {
        Text("Dashboard Preview")
    }
}
