package com.app.ecarepro.ui.dashbord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.data.network.model.Activity
import com.app.ecarepro.ui.dashbord.model.CalenderActivityModel
import com.app.ecarepro.ui.dashbord.model.DateFilterType
import com.app.ecarepro.ui.mainActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.AdmissionComparison
import com.app.ecarepro.data.network.model.BankBalance
import com.app.ecarepro.data.network.model.BirthDayCard
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.CollectionModeWise
import com.app.ecarepro.data.network.model.DataValue
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.data.network.model.LibraryDetails
import com.app.ecarepro.data.network.model.NetworkFeeDefaulter
import com.app.ecarepro.data.network.model.Questionnaire
import com.app.ecarepro.data.network.model.StaffAttendance
import com.app.ecarepro.data.network.model.StatusWiseStatistics
import com.app.ecarepro.data.network.model.Timetable
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.Workload
import com.app.ecarepro.databinding.FragmentDashboardBinding
import com.app.ecarepro.model.Feed
import com.app.ecarepro.todayModeWiseCollectionCard
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.dashbord.model.AdmissionComparisonModel
import com.app.ecarepro.ui.dashbord.model.BankBalanceModel
import com.app.ecarepro.ui.dashbord.model.EstimateCollectionModel
import com.app.ecarepro.ui.dashbord.model.FeeDefaulterModel
import com.app.ecarepro.ui.dashbord.model.FeedsModel
import com.app.ecarepro.ui.dashbord.model.LibraryBookStatusModel
import com.app.ecarepro.ui.dashbord.model.OnlineVsOfflineAdmissionModel
import com.app.ecarepro.ui.dashbord.model.ProCardCarouselModel
import com.app.ecarepro.ui.dashbord.model.QuestionnaireCarouselModel
import com.app.ecarepro.ui.dashbord.model.StaffAttendanceModel
import com.app.ecarepro.ui.dashbord.model.StanderWiseStatisticModel
import com.app.ecarepro.ui.dashbord.model.StudentStatisticModel
import com.app.ecarepro.ui.dashbord.model.TeacherWorkloadModel
import com.app.ecarepro.ui.dashbord.model.TeachersBirthdayCarouselModel
import com.app.ecarepro.ui.dashbord.model.TimeTableCarouselModel
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.utils.Constant
import com.google.android.material.datepicker.DateValidatorPointBackward
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    val dashboardViewModel: DashboardViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    var isExpanded = false
    private var modeByCollectionFilter: String = getString(R.string.general_today)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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

    private fun buildModels(
        data: UserDashboardDto
    ) {
        binding.recyclerView.withModels {
            if (data.showProCards == true || data.showCards == true) {
                val cards = mutableListOf<Card>()
                if (data.showProCards == true) {
                    cards.addAll(data.proCards ?: emptyList())
                }

                if (data.showCards == true) {
                    cards.addAll(data.cards ?: emptyList())
                }
                buildProCard(cards)
            }
            if (data.showFeeCollection == true)
                buildEstimatedCollectionCard(
                    dashboardViewModel.feeCollection.value,
                    data.collectionStartDate,
                    data.collectionEndDate
                )


            if (data.showCollectionModeWise == true)
                buildTodayModeWiseCollectionCard(data.collectionModeWise, data.sessionStartDate)
            if (data.showActivities == true)
                buildActivitiesCard(data.upcomingActivities)
            if (data.showTeacherWorkLoad == true)
                buildTeachersWorkLoad(data.teacherWorkLoad)

            if (data.showFeeDafaulter == true)
                buildFeeDefaulterCard(dashboardViewModel.feeDefaulter.value)

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

            if (data.showStuReligionWiseStatistics == true)
                buildStudentStatisticModel(data.stuReligionWiseStatistics)

            if (data.showLibraryDTL == true)
                buildLibraryBookStatusModel(data.libraryDTL)

            if (data.showBDayCards == true)
                buildTeachersBirthdayCarouselModel(data.birthDayCards)



            if (data.showTimetable == true)
                timeTableCarouselModel(data.timetable)

            if (data.showQuestionnaire == true)
                questionnaireCarouselModel(data.questionnaire)
        }
    }

    private fun EpoxyController.buildActivitiesCard(activities: List<Activity>?) {
        if (activities.isNullOrEmpty()) return
        CalenderActivityModel(activities)
            .id("cal")
            .addTo(this)
    }

    private fun EpoxyController.buildTeachersWorkLoad(teacherWorkLoad: List<Workload>?) {
        if (teacherWorkLoad.isNullOrEmpty()) return

        TeacherWorkloadModel(
            workload = teacherWorkLoad,
            onClick = { workload ->
                this@DashboardFragment.findNavController()
                    .navigate(R.id.timeTableNavHostFragment, Bundle().apply {
                        putString(Constant.ID, workload.id)
                        putString(Constant.NAME, workload.teacherName)
                    })

                dashboardViewModel.sendAnalyticEvent(
                    AnalyticsConstants.Events.TEACHER_WORKLOAD,
                    mapOf(
                        AnalyticsConstants.Attributes.TEACHER_ID to workload.id.toString(),
                        AnalyticsConstants.Attributes.USER_NAME to workload.teacherName.toString(),
                    )
                )
                /* findNavController().navigate(
                      R.id.timeTableNavHostFragment,
                      bundleOf(Constant.ID to workload.id)

                  )*/
            }
        )
            .id("workload")
            .addTo(this)

    }


    private fun EpoxyController.buildProCard(cards: List<Card>) {
        if (cards.isEmpty()) return
        ProCardCarouselModel(
            cards
        ) { favouriteSlider ->
            if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0 && favouriteSlider.sbChMenuID > 0) {
                (requireActivity() as MainActivity).getFragmentId(
                    favouriteSlider.menuID,
                    favouriteSlider.chMenuID,
                    favouriteSlider.sbChMenuID
                )
            } else if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0) {
                (requireActivity() as MainActivity).getFragmentId(
                    favouriteSlider.menuID,
                    favouriteSlider.chMenuID
                )
            } else if (favouriteSlider.menuID > 0) {
                (requireActivity() as MainActivity).getFragmentId(
                    favouriteSlider.menuID
                )
            }
            systemViewModel.sendAnalyticEvent(
                AnalyticsConstants.Events.SHOW_CARD_CLICK,
                mapOf(
                    AnalyticsConstants.Attributes.SCREEN_NAME to AnalyticsConstants.Screens.DASH_BOARD_SCREEN,
                    AnalyticsConstants.Attributes.HEADLINE to favouriteSlider.link.toString(),
                    AnalyticsConstants.Attributes.MENU_ID to favouriteSlider.menuID.toString(),
                    AnalyticsConstants.Attributes.CH_MENU_ID to favouriteSlider.chMenuID.toString(),
                    AnalyticsConstants.Attributes.SB_CH_MENU_ID to favouriteSlider.sbChMenuID.toString(),
                )
            )
        }
            .id("pro")
            .addTo(this)
    }

    private fun EpoxyController.buildTodayModeWiseCollectionCard(
        collectionModeWise: CollectionModeWise?,
        sessionStartDate: String?
    ) {
       collectionModeWise ?: return
        todayModeWiseCollectionCard {
            id(R.id.today_mode_collection)
            isExpanded(isExpanded)
            collectionModeWise(collectionModeWise)
            filterBy(modeByCollectionFilter)
            onClickFilter { _ ->
                showDatePickerDialog(requireContext(), sessionStartDate) { date ->
                    mainActivity().showLoader(true)
                    dashboardViewModel.getTodayModeWiseCollection(date) { isSuccess, message ->
                        mainActivity().showLoader(false)
                        if (isSuccess) {
                            dashboardViewModel.sendAnalyticEvent(
                                AnalyticsConstants.Events.DAILY_MODE_WISE_FILTER,
                                mapOf(
                                    AnalyticsConstants.Attributes.DATE to date
                                )
                            )
                        }
                        if (isSuccess.not()) {
                            if (message != null) {
                                mainActivity().showMessage(message)
                            }
                        } else {
                            // this@buildTodayModeWiseCollectionCard.requestModelBuild()
                        }
                    }
                }
            }
            toggleCardVisibility { _ ->
                isExpanded = isExpanded.not()
                this@buildTodayModeWiseCollectionCard.requestModelBuild()
            }
        }
    }
    private fun showDatePickerDialog(
        context: Context,
        minDateString: String?,
        onDateSelected: (String) -> Unit
    ) {
        val options = arrayOf(getString(R.string.general_today),
            getString(R.string.general_yesterday),
            getString(R.string.general_select_date))
        val dateFormat = "yyyy-MM-dd"

        // Parse the minDateString to a Long value
        val minDate = try {
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            formatter.parse(minDateString)?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
        } catch (e: Exception) {
            MaterialDatePicker.todayInUtcMilliseconds()
        }

        MaterialAlertDialogBuilder(context)
            .setTitle( getString(R.string.general_select_date))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Today
                        modeByCollectionFilter =  getString(R.string.general_today)
                        val today = Calendar.getInstance().time
                        val formattedDate =
                            SimpleDateFormat(dateFormat, Locale.getDefault()).format(today)
                        onDateSelected(formattedDate)
                    }

                    1 -> { // Yesterday
                        modeByCollectionFilter =  getString(R.string.general_yesterday)
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        val yesterday = calendar.time
                        val formattedDate =
                            SimpleDateFormat(dateFormat, Locale.getDefault()).format(yesterday)
                        onDateSelected(formattedDate)
                    }

                    2 -> { // Select Date

                        val constraintsBuilder = CalendarConstraints.Builder()
                            .setStart(minDate)
                            .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                            .setValidator(DateValidatorPointBackward.now())
                            .build()

                        val datePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText( getString(R.string.general_select_date))
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .setCalendarConstraints(constraintsBuilder)
                            .build()

                        datePicker.addOnPositiveButtonClickListener {
                            val selectedDate =
                                SimpleDateFormat(dateFormat, Locale.getDefault()).format(it)
                            modeByCollectionFilter = selectedDate
                            onDateSelected(selectedDate)
                        }

                        datePicker.show(
                            childFragmentManager,
                            datePicker.toString()
                        )
                    }
                }
            }
            .show()
    }

    private var estimatedCollectionCardExpended = false

    private fun EpoxyController.buildEstimatedCollectionCard(
        feeCollection: FeeCollection?,
        sessionStartDate: String?,
        sessionEndDate: String?
    ) {
        feeCollection ?: return
        EstimateCollectionModel(
            feeCollection,
            isExpanded = estimatedCollectionCardExpended,
            toggleCardVisibility = { isExpanded ->
                estimatedCollectionCardExpended = isExpanded
                this.requestModelBuild()
            },
            updateFeeCollectionDate = { feeTypeId, dateFilterType, selectDatefromCalender ->
                if (selectDatefromCalender) {
                    showDateRangePicker { fromDate, tillDate ->
                        updateFeeCollection(feeTypeId, fromDate, tillDate)
                    }
                } else {
                    mainActivity().showLoader(true)
                    val (fromDate, tillDate) = getDateRange(
                        dateFilterType,
                        sessionStartDate,
                        sessionEndDate
                    )
                    updateFeeCollection(feeTypeId, fromDate, tillDate)
                }
            }
        )
            .id("11")
            .addTo(this)
    }
    private fun updateFeeCollection(feeTypeId: Int, fromDate: String, tillDate: String) {
        dashboardViewModel.getFeeCollection(
            feeTypeId,
            fromDate,
            tillDate
        ) { isSuccess, message ->
            if (isSuccess) {
                dashboardViewModel.sendAnalyticEvent(
                    AnalyticsConstants.Events.ESTIMATE_COLLECTION_FILTER,
                    mapOf(
                        AnalyticsConstants.Attributes.FEE_TYPE_ID to feeTypeId.toString(),
                        AnalyticsConstants.Attributes.FROM_DATE to fromDate,
                        AnalyticsConstants.Attributes.TO_DATE to tillDate
                    )
                )
            }
            mainActivity().showLoader(false)
            if (isSuccess.not()) {
                if (message != null) {
                    mainActivity().showMessage(message)
                }
            }
        }
    }
    private fun showDateRangePicker(callback: (String, String) -> Unit) {
        val constraintsBuilder =
            CalendarConstraints.Builder() // You can add constraints here if needed

        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.general_select_date_range))
            .setCalendarConstraints(
                constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selection.first)
            val endDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selection.second)
            callback(startDate, endDate)
        }

        datePicker.show(
            childFragmentManager,
            datePicker.toString()
        )
    }
    private fun getDateRange(
        filterType: DateFilterType,
        sessionStartDate: String?,
        sessionEndDate: String?
    ): Pair<String, String> {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return when (filterType) {
            DateFilterType.TODAY -> {
                val formattedDate = currentDate.format(formatter)
                Pair(formatDate(sessionStartDate!!), formattedDate)
            }
            DateFilterType.THIS_YEAR -> {
                Pair(formatDate(sessionStartDate!!), formatDate(sessionEndDate!!))
            }
        }
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    private fun EpoxyController.buildFeeDefaulterCard(feeDefaulter: NetworkFeeDefaulter?) {

       /* //feeDefaulter ?: return
        FeeDefaulterModel(feeDefaulter)
            .id("121")
            .addTo(this)*/

        FeeDefaulterModel(
            feeDefaulter = feeDefaulter,
            onClick = {  ->
                 findNavController().navigate(
                            R.id.feeDefaulterUI)
                /*this@DashboardFragment.findNavController()
                    .navigate(R.id.feeDefaulterUI.apply {
                    })*/
            }
        )
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
        TeachersBirthdayCarouselModel(data, onClick = {
            findNavController().navigate(
                R.id.birthdayFragment,
                Bundle().apply {
                    putInt("rType", it.rtype)
                    putString("monthSelected", it.month)
                    putString("dateSelected", it.date)
                    putInt("uType", it.utype)
                })
            dashboardViewModel.sendAnalyticEvent(
                AnalyticsConstants.Events.TEACHER_BIRTHDAY,
                mapOf(
                    AnalyticsConstants.Attributes.R_TYPE to it.date.toString(),
                    AnalyticsConstants.Attributes.U_TYPE to it.date.toString(),
                    AnalyticsConstants.Attributes.BIRTH_DATE to it.date.toString(),
                    AnalyticsConstants.Attributes.BIRTH_MONTH to it.month.toString(),
                )
            )
            /*  findNavController().navigate(
                  R.id.birthdayFragment, bundleOf(
                      "rType" to it.rType,
                      "monthSelected" to it.month,
                      "dateSelected" to it.date,
                      "uType" to it.uType
                  )
              )*/
        })
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

    private fun EpoxyController.timeTableCarouselModel(data: List<Timetable>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        TimeTableCarouselModel(data)
            .id("1e5sfe36erfr55")
            .addTo(this)
    }

    private fun EpoxyController.questionnaireCarouselModel(data: List<Questionnaire>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        QuestionnaireCarouselModel(data)
            .id("1e5sfe36er55fr55")
            .addTo(this)
    }

    private fun initViews() {
        /*binding.toolbar.setNavigationOnClickListener {
            systemViewModel.navigateBack(true)
        }*/
        binding.recyclerView.apply {
            /*addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin),
                    addBeforeFirstPosition = false
                )
            )*/

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


    override fun onResume() {
        super.onResume()
        dashboardViewModel.sendScreenEvent()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}