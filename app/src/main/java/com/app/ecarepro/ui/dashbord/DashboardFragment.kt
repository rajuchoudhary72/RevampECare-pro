package com.app.ecarepro.ui.dashbord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentDashboardBinding
import com.app.ecarepro.todayModeWiseCollectionCard
import com.app.ecarepro.ui.dashbord.model.AdmissionComparisonModel
import com.app.ecarepro.ui.dashbord.model.BankBalanceModel
import com.app.ecarepro.ui.dashbord.model.EstimateCollectionModel
import com.app.ecarepro.ui.dashbord.model.FeeDefaulterModel
import com.app.ecarepro.ui.dashbord.model.FeedsModel
import com.app.ecarepro.ui.dashbord.model.LibraryBookStatusModel
import com.app.ecarepro.ui.dashbord.model.LibraryFeeStatusModel
import com.app.ecarepro.ui.dashbord.model.OnlineVsOfflineAdmissionModel
import com.app.ecarepro.ui.dashbord.model.RecentPhotoCarouselModel
import com.app.ecarepro.ui.dashbord.model.StaffAttendanceModel
import com.app.ecarepro.ui.dashbord.model.StanderWiseStatisticModel
import com.app.ecarepro.ui.dashbord.model.StudentStatisticModel
import com.app.ecarepro.ui.dashbord.model.SubscriberModel
import com.app.ecarepro.ui.dashbord.model.TeacherWorkloadModel
import com.app.ecarepro.ui.dashbord.model.TeachersBirthdayCarouselModel
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

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

        binding.recyclerView.apply {

            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin),
                    addBeforeFirstPosition = false
                )
            )

            withModels {
                todayModeWiseCollectionCard {
                    id(R.id.today_mode_collection)
                    isExpanded(isExpanded)
                    toggleCardVisibility { _ ->
                        isExpanded = isExpanded.not()
                        this@withModels.requestModelBuild()
                    }
                }

                EstimateCollectionModel()
                    .id("11")
                    .addTo(this)

                FeeDefaulterModel()
                    .id("121")
                    .addTo(this)

                BankBalanceModel()
                    .id("1")
                    .addTo(this)

                StaffAttendanceModel()
                    .id("13")
                    .addTo(this)

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
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}