package com.app.ecarepro.ui.dashbord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.AdmissionComparison
import com.app.ecarepro.data.network.model.BankBalance
import com.app.ecarepro.data.network.model.BirthDayCard
import com.app.ecarepro.data.network.model.CollectionModeWise
import com.app.ecarepro.data.network.model.DataValue
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.data.network.model.FeeDefaulter
import com.app.ecarepro.data.network.model.LibraryDetails
import com.app.ecarepro.data.network.model.StaffAttendance
import com.app.ecarepro.data.network.model.StatusWiseStatistics
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.databinding.FragmentDashboardBinding
import com.app.ecarepro.todayModeWiseCollectionCard
import com.app.ecarepro.ui.dashbord.model.AdmissionComparisonModel
import com.app.ecarepro.ui.dashbord.model.BankBalanceModel
import com.app.ecarepro.ui.dashbord.model.EstimateCollectionModel
import com.app.ecarepro.ui.dashbord.model.FeeDefaulterModel
import com.app.ecarepro.ui.dashbord.model.LibraryBookStatusModel
import com.app.ecarepro.ui.dashbord.model.OnlineVsOfflineAdmissionModel
import com.app.ecarepro.ui.dashbord.model.StaffAttendanceModel
import com.app.ecarepro.ui.dashbord.model.StanderWiseStatisticModel
import com.app.ecarepro.ui.dashbord.model.StudentStatisticModel
import com.app.ecarepro.ui.dashbord.model.TeachersBirthdayCarouselModel
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.model.Feed
import com.app.ecarepro.ui.dashbord.model.FeedsModel


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    val dashboardViewModel: DashboardViewModel by viewModels()

    var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.dashboard.collectLatest { (data, feeds) ->
                if (data != null) {
                    buildModels(data, feeds)
                }
            }
        }
    }

    private fun buildModels(data: UserDashboardDto, feeds: List<Feed>) {
        binding.recyclerView.withModels {
            if (data.showCollectionModeWise == true)
                buildTodayModeWiseCollectionCard(data.collectionModeWise)

            if (data.showFeeCollection == true)
                buildEstimatedCollectionCard(data.feeCollection)

            if (data.showFeeDafaulter == true)
                buildFeeDefaulterCard(data.feeDafaulter)

            if (data.showBankBalnce == true)
                buildBankBalanceCard(data.bankBalance)

            if (data.showStfAttendanceSummary == true)
                buildStaffAttendanceCard(data.staffAttendanceSummary)

            if (data.showAdmissionComparison == true)
                buildAdmissionComparisonCard(data.admissionComparison)

            if (data.showStuStatusWiseStatistics == true)
                buildStanderWiseStatistic(data.stuStatusWiseStatistics)

            if (data.showAdmissionModeComparison == true)
                buildOnlineVsOfflineAdmissionCard(data.admissionModeComparison)

            if (data.showStuCategoryStatistics == true)
                buildStudentStatisticModel(data.stuCategoryWiseStatistics)

            if (data.showLibraryDTL == true)
                buildLibraryBookStatusModel(data.libraryDTL)

            if (data.showBDayCards == true)
                buildTeachersBirthdayCarouselModel(data.birthDayCards)
            buildFeedsModel(feeds)

        }


    }


    private fun EpoxyController.buildTodayModeWiseCollectionCard(collectionModeWise: CollectionModeWise?) {
        collectionModeWise ?: return
        todayModeWiseCollectionCard {
            id(R.id.today_mode_collection)
            isExpanded(isExpanded)
            collectionModeWise(collectionModeWise)
            toggleCardVisibility { _ ->
                isExpanded = isExpanded.not()
                this@buildTodayModeWiseCollectionCard.requestModelBuild()
            }
        }
    }

    private fun EpoxyController.buildEstimatedCollectionCard(feeCollection: FeeCollection?) {
        feeCollection ?: return
        EstimateCollectionModel(feeCollection)
            .id("11")
            .addTo(this)
    }

    private fun EpoxyController.buildFeeDefaulterCard(feeDefaulter: FeeDefaulter?) {
        feeDefaulter ?: return
        FeeDefaulterModel(feeDefaulter)
            .id("121")
            .addTo(this)

    }

    private fun EpoxyController.buildBankBalanceCard(bankBalance: List<BankBalance>?) {
        if (bankBalance.isNullOrEmpty()) return
        BankBalanceModel(bankBalance)
            .id("1")
            .addTo(this)

    }

    private fun EpoxyController.buildStaffAttendanceCard(staffAttendance: StaffAttendance?) {
        staffAttendance ?: return
        StaffAttendanceModel(staffAttendance)
            .id("13")
            .addTo(this)

    }

    private fun EpoxyController.buildAdmissionComparisonCard(admissionComparisonModel: AdmissionComparison?) {
        admissionComparisonModel ?: return
        AdmissionComparisonModel(admissionComparisonModel)
            .id("1355")
            .addTo(this)
    }

    private fun EpoxyController.buildStanderWiseStatistic(statusWiseStatistics: List<StatusWiseStatistics>?) {
        if (statusWiseStatistics.isNullOrEmpty()) {
            return
        }
        StanderWiseStatisticModel(statusWiseStatistics)
            .id("1e355")
            .addTo(this)
    }

    private fun EpoxyController.buildOnlineVsOfflineAdmissionCard(data: List<DataValue>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        OnlineVsOfflineAdmissionModel(data)
            .id("1e3e55")
            .addTo(this)
    }


    private fun EpoxyController.buildStudentStatisticModel(data: List<DataValue>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        StudentStatisticModel(data)
            .id("1e5e36eerfr55")
            .addTo(this)
    }

    private fun EpoxyController.buildLibraryBookStatusModel(data: LibraryDetails?) {
        if (data == null) {
            return
        }
        LibraryBookStatusModel(data)
            .id("1ee36er55")
            .addTo(this)
    }

    private fun EpoxyController.buildTeachersBirthdayCarouselModel(data: List<BirthDayCard>?) {
        if (data == null) {
            return
        }
        TeachersBirthdayCarouselModel(data)
            .id("1e5e")
            .addTo(this)
    }
    private fun EpoxyController.buildFeedsModel(data: List<Feed>) {
        if (data.isEmpty()) {
            return
        }
        FeedsModel(data)
            .id("1e5e36erfr55")
            .addTo(this)
    }
    private fun initViews() {
        binding.recyclerView.apply {

            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin),
                    addBeforeFirstPosition = false
                )
            )

            /* withModels {




                 RecentPhotoCarouselModel()
                     .id("135")
                     .addTo(this)


                 StanderWiseStatisticModel()
                     .id("1e355")
                     .addTo(this)

                 OnlineVsOfflineAdmissionModel()
                     .id("1e3e55")
                     .addTo(this)

                 TeacherWorkloadModel()
                     .id("1e3er55")
                     .addTo(this)

                 LibraryFeeStatusModel()
                     .id("1ee3er55")
                     .addTo(this)

                 LibraryBookStatusModel()
                     .id("1ee36er55")
                     .addTo(this)

                 SubscriberModel()
                     .id("1ee36erfr55")
                     .addTo(this)


                 StudentStatisticModel()
                     .id("1e5e36eerfr55")
                     .addTo(this)

                 TeachersBirthdayCarouselModel()
                     .id("1e5e")
                     .addTo(this)
             }*/
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}