package com.app.ecarepro.feature.report.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.report.birthday_report.BirthdayListScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface BirthdayNavGraph : NavKey {
    @Serializable
    data object BirthdayList : BirthdayNavGraph
}

fun EntryProviderBuilder<NavKey>.entryBirthdayNavigation(
    navigateBack: () -> Unit,
) {
    entry<BirthdayNavGraph.BirthdayList> {
        BirthdayListScreen(navigateBack = navigateBack)
    }
}
