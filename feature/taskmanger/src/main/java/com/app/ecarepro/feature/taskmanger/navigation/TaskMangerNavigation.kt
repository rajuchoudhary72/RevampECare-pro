package com.app.ecarepro.feature.taskmanger.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.taskmanger.TaskMangerScreen
import com.app.ecarepro.feature.taskmanger.screens.TaskCreateScreen
import com.app.ecarepro.feature.taskmanger.screens.TaskMangerDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface TaskMangerNavigationGraph : NavKey {
    @Serializable
    data object TaskMangerList : TaskMangerNavigationGraph

    @Serializable
    data class TaskMangerDetails(val taskId: String) : TaskMangerNavigationGraph

    @Serializable
    data object TaskCreate : TaskMangerNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryTaskMangerNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
    navigateToDocViewer: (title: String, url: String) -> Unit,
) {
    entry<TaskMangerNavigationGraph.TaskMangerList> {
        TaskMangerScreen(
            navigateToBack = navigateToBack,
            navigateToAddTask = {
                backStack.add(TaskMangerNavigationGraph.TaskCreate)
            },
            navigateToTaskDetail = { taskId ->
                backStack.add(TaskMangerNavigationGraph.TaskMangerDetails(taskId))
            }
        )
    }

    entry<TaskMangerNavigationGraph.TaskMangerDetails> { taskMangerDetails ->
        TaskMangerDetailsScreen(
            taskId = taskMangerDetails.taskId,
            navigateToBack = {
                backStack.remove(taskMangerDetails)
            },
            navigateToDocViewer = navigateToDocViewer,
        )
    }

    entry<TaskMangerNavigationGraph.TaskCreate> { taskCreate ->
        TaskCreateScreen(
            navigateToBack = {
                backStack.remove(taskCreate)
            }
        )
    }
}
