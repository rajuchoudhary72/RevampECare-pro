package com.app.ecarepro.feature.report.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.report.report.SMSReportScreen
import com.app.ecarepro.feature.report.report.daily_consumption.DailyConsumptionScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SMSReportNavGraph : NavKey {
    @Serializable
    data object Report : SMSReportNavGraph
    @Serializable
    data object DailyConsumption : SMSReportNavGraph
}

fun EntryProviderBuilder<NavKey>.entrySMSReportNavigation(
    navigateBack: () -> Unit,
) {
    entry<SMSReportNavGraph.Report> {
        SMSReportScreen(navigateBack = navigateBack)
    }
    entry<SMSReportNavGraph.DailyConsumption> {
        DailyConsumptionScreen(navigateBack = navigateBack)
    }
}
