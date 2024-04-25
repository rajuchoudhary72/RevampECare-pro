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
import com.app.ecarepro.data.network.model.BankBalance
import com.app.ecarepro.data.network.model.CollectionModeWise
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.data.network.model.FeeDefaulter
import com.app.ecarepro.data.network.model.StaffAttendance
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.databinding.FragmentDashboardBinding
import com.app.ecarepro.todayModeWiseCollectionCard
import com.app.ecarepro.ui.dashbord.model.BankBalanceModel
import com.app.ecarepro.ui.dashbord.model.EstimateCollectionModel
import com.app.ecarepro.ui.dashbord.model.FeeDefaulterModel
import com.app.ecarepro.ui.dashbord.model.StaffAttendanceModel
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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
            dashboardViewModel.dashboard.collectLatest { data ->
                if (data != null) {
                    buildModels(data)
                }
            }
        }
    }

    private fun buildModels(data: UserDashboardDto) {
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

                 AdmissionComparisonModel()
                     .id("1355")
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

                 FeedsModel()
                     .id("1e5e36erfr55")
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