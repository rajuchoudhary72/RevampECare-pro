package com.app.ecarepro.feature.syllabus.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.feature.syllabus.SyllabusScreen
import com.app.ecarepro.feature.syllabus.screens.AddSyllabusScreen
import com.app.ecarepro.feature.syllabus.screens.AddSyllabusViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface SyllabusNavigationGraph : NavKey {
    @Serializable
    data object Syllabus : SyllabusNavigationGraph

    @Serializable
    data class AddSyllabus(val syllabus: com.app.ecarepro.core.domain.model.Syllabus?) :
        SyllabusNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntrySyllabusNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
    navigateToAttachmentList: (List<ECAttachment>) -> Unit,
) {
    entry<SyllabusNavigationGraph.Syllabus> {
        SyllabusScreen(
            navigateToBack = navigateToBack,
            navigateToAttachmentList = navigateToAttachmentList,
            navigateToAddSyllabus = { syllabus ->
                backStack.add(SyllabusNavigationGraph.AddSyllabus(syllabus))
            }
        )
    }
    entry<SyllabusNavigationGraph.AddSyllabus> {
        val viewModel: AddSyllabusViewModel = navKeyViewModel(it)
        AddSyllabusScreen(
            navigateToBack = navigateToBack,
        )
    }
}
