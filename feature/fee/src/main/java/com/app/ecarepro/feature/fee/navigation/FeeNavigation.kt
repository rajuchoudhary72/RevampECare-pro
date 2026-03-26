package com.app.ecarepro.feature.fee.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.fee.certificate.FeeCertificateScreen
import com.app.ecarepro.feature.fee.collection.CollectionScreen
import com.app.ecarepro.feature.fee.defaulter.DefaulterListScreen
import com.app.ecarepro.feature.fee.defaulter.DefaulterListViewModel
import com.app.ecarepro.feature.fee.estimate.EstimateListScreen
import com.app.ecarepro.feature.fee.estimate.EstimateListViewModel
import com.app.ecarepro.feature.fee.filter.ReportFilterScreen
import com.app.ecarepro.feature.fee.filter.ReportFilterViewModel
import com.app.ecarepro.feature.fee.receipt.ReceiptScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface FeeNavGraph : NavKey {
    @Serializable
    data object ReceiptList : FeeNavGraph

    @Serializable
    data object CollectionReport : FeeNavGraph

    @Serializable
    data class ReportFilter(val flowType: String) : FeeNavGraph

    @Serializable
    data class DefaulterList(
        val dateFrom: String,
        val dateTo: String,
        val classId: String,
        val feeTypeId: String,
        val schoolId: String,
        val sectionId: String,
        val installId: String,
        val installmentNames: String,
    ) : FeeNavGraph

    @Serializable
    data class EstimateList(
        val dateFrom: String,
        val dateTo: String,
        val classId: String,
        val feeTypeId: String,
        val schoolId: String,
        val sectionId: String,
        val installId: String,
    ) : FeeNavGraph

    @Serializable
    data object FeeCertificate : FeeNavGraph
}

fun EntryProviderBuilder<NavKey>.entryFeeNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
) {
    entry<FeeNavGraph.ReceiptList> {
        ReceiptScreen(navigateBack = navigateBack)
    }
    entry<FeeNavGraph.CollectionReport> {
        CollectionScreen(navigateBack = navigateBack)
    }
    entry<FeeNavGraph.ReportFilter> { key ->
        val viewModel: ReportFilterViewModel = navKeyViewModel(key)
        ReportFilterScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateTo = navigateTo,
        )
    }
    entry<FeeNavGraph.DefaulterList> { key ->
        val viewModel: DefaulterListViewModel = navKeyViewModel(key)
        DefaulterListScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
    entry<FeeNavGraph.EstimateList> { key ->
        val viewModel: EstimateListViewModel = navKeyViewModel(key)
        EstimateListScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
    entry<FeeNavGraph.FeeCertificate> {
        FeeCertificateScreen(navigateBack = navigateBack)
    }
}
