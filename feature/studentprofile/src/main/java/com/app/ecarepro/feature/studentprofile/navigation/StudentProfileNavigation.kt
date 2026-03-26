package com.app.ecarepro.feature.studentprofile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.feature.studentprofile.StudentProfileScreen
import com.app.ecarepro.feature.studentprofile.screens.StudentProfileDetailsScreen
import com.app.ecarepro.feature.studentprofile.screens.attendance.StudentAttendanceScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface StudentProfileNavigationGraph : NavKey {
    @Serializable
    data object StudentProfileList : StudentProfileNavigationGraph

    @Serializable
    data class StudentProfileDetail(val studentId: Int) : StudentProfileNavigationGraph

    @Serializable
    data class StudentAttendance(val studentId: Int, val studentName: String) : StudentProfileNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryStudentProfileNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
    navigateToProfileDetail: (StudentProfile) -> Unit,
    navigateToDocViewer: (String, String) -> Unit,
) {
    entry<StudentProfileNavigationGraph.StudentProfileList> {
        StudentProfileScreen(
            navigateToBack = navigateToBack,
            navigateToProfileDetail = { profile ->
                // Navigate to student profile details
                backStack.add(StudentProfileNavigationGraph.StudentProfileDetail(profile.stID ?: 0))
            }
        )
    }

    entry<StudentProfileNavigationGraph.StudentProfileDetail> { StudentProfileDetail ->
        StudentProfileDetailsScreen(
            studentId = StudentProfileDetail.studentId,
            navigateToBack = {
                backStack.remove(StudentProfileDetail)
            },
            navigateToDocViewer = navigateToDocViewer
        )
    }

    entry<StudentProfileNavigationGraph.StudentAttendance> { key ->
        StudentAttendanceScreen(
            navKey = key,
            navigateToBack = { backStack.remove(key) }
        )
    }
}
