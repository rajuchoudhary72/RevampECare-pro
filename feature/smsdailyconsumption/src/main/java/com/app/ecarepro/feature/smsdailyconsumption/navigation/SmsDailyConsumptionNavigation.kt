package com.app.ecarepro.feature.smsdailyconsumption.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.smsdailyconsumption.SmsDailyConsumptionScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SmsDailyConsumptionNavGraph : NavKey {
    @Serializable
    data object SmsDailyConsumption : SmsDailyConsumptionNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entrySmsDailyConsumptionNavigation(
    navigateBack: () -> Unit,
) {
    entry<SmsDailyConsumptionNavGraph.SmsDailyConsumption> {
        SmsDailyConsumptionScreen(navigateBack = navigateBack)
    }
}
