package com.app.ecarepro.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.designsystem.core.component.DownloadFilesView
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.assignment.navigation.EntryAssignmentNavigation
import com.app.ecarepro.feature.dashboard.navigation.DashboardNavigationGraph
import com.app.ecarepro.feature.dashboard.navigation.EntryDashboardNavigation
import com.app.ecarepro.feature.docviewer.navigation.DocViewerNavigationGraph
import com.app.ecarepro.feature.docviewer.navigation.EntryDocViewerNavigation
import com.app.ecarepro.feature.login.navigation.EntryLoginNavigation
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.EntrySchoolCodeNavigation
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.feature.splash.navigation.EntrySplashNavigation
import com.app.ecarepro.feature.splash.navigation.SplashNavigationGraph
import com.app.ecarepro.feature.syllabus.navigation.EntrySyllabusNavigation
import com.app.ecarepro.feature.testingmenu.navigation.EntryTestingMenuNavigation
import com.app.ecarepro.feature.testingmenu.navigation.TestingMenuNavigationGraph
import com.app.ecarepro.feature.timetable.navigation.EntryTimetableNavigation
import com.app.ecarepro.onboarding.feature.navigation.EntryOnboardingNavigation
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph
import kotlinx.serialization.Serializable

@Serializable
sealed interface AttachmentNavigationGraph : NavKey {
    @Serializable
    data class AttachmentList(
        val attachments: List<ECAttachment>
    ) : AttachmentNavigationGraph
}

@Composable
fun EcareProNavDisplay(
    startDestination: NavKey? = null,
) {
    val backStack = remember {
        val initialKey = startDestination ?: SplashNavigationGraph.Splash
        mutableStateListOf(initialKey)
    }

    NavDisplay(
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {

            EntryTestingMenuNavigation(
                navigateToBack = { backStack.removeLastOrNull() },
                navigateToModule = { navKey ->
                    backStack.add(navKey)
                }
            )

            EntrySplashNavigation(
                navigateToLogin = {
                    backStack.clear()
                    backStack.add(OnboardingNavigationGraph.Onboarding)
                },
                navigateToDashboard = { user ->
                    backStack.clear()
                    backStack.add(DashboardNavigationGraph.Dashboard)
                }
            )

            EntryOnboardingNavigation(
                navigateToAddSchool = {
                    backStack.clear()
                    backStack.add(SchoolCodeNavigationGraph.SchoolCode)
                }
            )

            EntrySchoolCodeNavigation(
                backStack = backStack,
                navigateToLogin = { schoolCode ->
                    backStack.add(LoginNavigationGraph.Login(schoolCode = schoolCode))
                }
            )

            EntryLoginNavigation(
                backStack = backStack,
                backToSchoolCode = {
                    backStack.removeLastOrNull()
                },
                navigateToMain = { user ->
                    backStack.clear()
                    backStack.add(DashboardNavigationGraph.Dashboard)
                }
            )

            EntryDashboardNavigation(
                navigateToTestingMenu = {
                    backStack.add(TestingMenuNavigationGraph.TestingMenu)
                }
            )

            EntryTimetableNavigation(
                navigateToBack = {
                    backStack.removeLastOrNull()
                }
            )

            EntrySyllabusNavigation(
                backStack = backStack,
                navigateToBack = {
                    backStack.removeLastOrNull()
                },
                navigateToAttachmentList = { attachments ->
                    backStack.add(
                        AttachmentNavigationGraph.AttachmentList(
                            attachments = attachments
                        )
                    )
                }
            )

            EntryDocViewerNavigation(
                navigateToBack = {
                    backStack.removeLastOrNull()
                }
            )

            entry<AttachmentNavigationGraph.AttachmentList> { navEntry ->
                val viewModel: AttachmentListViewModel = hiltViewModel()
                val snackbarHostState = remember { SnackbarHostState() }
                var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

                LaunchedEffect(Unit) {
                    viewModel.messageEvent.collect { message ->
                        snackbarMessage = message
                        snackbarHostState.showSnackbar(message.text)
                    }
                }

                EcareProScaffold(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    snackbarHostState = snackbarHostState,
                    snackbarMessage = snackbarMessage,
                    onSnackbarDismissed = { snackbarMessage = null }
                ) {
                    DownloadFilesView(
                        attachments = navEntry.attachments,
                        onBackPressed = {
                            backStack.removeLastOrNull()
                        },
                        onAttachmentClick = { attachment ->
                            backStack.add(
                                DocViewerNavigationGraph.DocViewer(
                                    title = attachment.name,
                                    docUrl = attachment.url
                                )
                            )
                        },
                        onDownloadClick = { attachment ->
                            viewModel.downloadAttachment(attachment)
                        }
                    )
                }
            }

            EntryAssignmentNavigation(
                backStack = backStack,
                navigateToBack = {
                    backStack.removeLastOrNull()
                },
                openDocVier = { title, url ->
                    backStack.add(
                        DocViewerNavigationGraph.DocViewer(
                            title = title,
                            docUrl = url
                        )
                    )
                }
            )
        }
    )
}