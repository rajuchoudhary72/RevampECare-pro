package com.app.ecarepro.feature.studentprofile.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.InternalSerializationApi
import com.app.ecarepro.core.domain.model.Appreciation
import com.app.ecarepro.core.domain.model.FeeInstallment
import com.app.ecarepro.core.domain.model.FeeSummary
import com.app.ecarepro.core.domain.model.Infraction
import com.app.ecarepro.core.domain.model.LibraryDetail
import com.app.ecarepro.core.domain.model.LibraryFine
import com.app.ecarepro.core.domain.model.LibraryTransaction
import com.app.ecarepro.core.domain.model.MedicalCard
import com.app.ecarepro.core.domain.model.MedicineIssued
import com.app.ecarepro.core.domain.model.PersonalDetailSection
import com.app.ecarepro.core.domain.model.ProfileSection
import com.app.ecarepro.core.domain.model.ReportCardDetail
import com.app.ecarepro.core.domain.model.SiblingDetail
import com.app.ecarepro.core.domain.model.StudentProfileDetail
import com.app.ecarepro.core.domain.model.TransportDetails
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.domain.util.ProfileUtils.formatCurrency
import com.app.ecarepro.core.domain.util.ProfileUtils.formatDate
import com.app.ecarepro.core.domain.util.ProfileUtils.orDefault
import com.app.ecarepro.core.domain.util.ProfileUtils.getFirstName
import com.app.ecarepro.core.ui.component.PlaceholderTab
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProDropdownField
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.ExpandableCardSection
import com.app.ecarepro.designsystem.core.component.HorizontalTabBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBarConfiguration
import com.app.ecarepro.designsystem.core.component.InfoGridItem
import com.app.ecarepro.designsystem.core.component.InfoGridView
import com.app.ecarepro.designsystem.core.component.InfoItemType
import com.app.ecarepro.designsystem.core.component.SiblingRowView
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.StatCard
import com.app.ecarepro.designsystem.core.component.StatItem
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.studentprofile.screens.attendance.AttendanceTab
import kotlinx.coroutines.launch

@Composable
fun StudentProfileDetailsScreen(
    studentId: Int,
    viewModel: StudentProfileDetailsViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToDocViewer: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(studentId) {
        viewModel.loadStudentProfile(studentId)
    }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                StudentProfileDetailsEvent.NavigateBack -> navigateToBack()
                is StudentProfileDetailsEvent.NavigateToDocViewer -> {
                    navigateToDocViewer(event.title, event.url)
                }
                is StudentProfileDetailsEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    StudentProfileDetailsScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun StudentProfileDetailsScreenContent(
    uiState: UiState<StudentProfileDetailsUiState>,
    handleIntent: (StudentProfileDetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = if (uiState is UiState.Success) uiState.data.selectedTabIndex else 0,
        pageCount = { if (uiState is UiState.Success) uiState.data.visibleTabs.size else 0 }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            handleIntent(StudentProfileDetailsIntent.OnTabSelected(pagerState.currentPage))
        }
    }

    EcareProScaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState is UiState.Success && uiState.data.profileData != null) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile photo
                            if (uiState.data.profileData!!.photo.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.appColors.textSecondary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile photo",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.appColors.textSecondary
                                    )
                                }
                            } else {
                                EcareProAsyncImage(
                                    imageUrl = uiState.data.profileData!!.photo,
                                    contentDescription = "Profile photo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name and info
                            Column {
                                Text(
                                    text = formatDisplayName(uiState.data.profileData!!),
                                    style = MaterialTheme.appTypography.interMedium16px,
                                    color = MaterialTheme.appColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatAdmissionInfo(uiState.data.profileData!!),
                                    style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                                    color = MaterialTheme.appColors.textSecondary
                                )
                            }
                        }
                    }

                    // Close button
                    IconButton(onClick = { handleIntent(StudentProfileDetailsIntent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }

                // Tabs
                if (uiState is UiState.Success && uiState.data.visibleTabs.isNotEmpty()) {
                    HorizontalTabBar(
                        tabs = uiState.data.visibleTabs.map { it.displayName },
                        selectedTab = uiState.data.visibleTabs.getOrNull(uiState.data.selectedTabIndex)?.displayName
                            ?: "",
                        onTabSelected = { tab ->
                            val index = uiState.data.visibleTabs.indexOfFirst { it.displayName == tab }
                            if (index != -1) {
                                coroutineScope.launch {
                                    handleIntent(StudentProfileDetailsIntent.OnTabSelected(index))
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        },
                        configuration = HorizontalTabBarConfiguration(
                            tabSpacing = 24.dp
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.appColors.background,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed
    ) { paddingValues ->
        UiStateHandler(
            state = uiState
        ) { data ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { page ->
                val currentTab = data.visibleTabs.getOrNull(page)
                when (currentTab) {
                    ProfileSection.PERSONAL_DETAILS -> {
                        PersonalDetailsTab(
                            profileData = data.profileData,
                            siblingDetails = data.siblingDetails,
                            expandedSections = data.expandedSections,
                            onToggleSection = { section ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleSection(section))
                            }
                        )
                    }

                    ProfileSection.INFIRMARY -> {
                        InfirmaryTab(
                            medicineIssued = data.medicineIssued,
                            expandedVisits = data.expandedInfirmaryVisits,
                            onToggleVisit = { index ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleInfirmaryVisit(index))
                            }
                        )
                    }

                    ProfileSection.LIBRARY -> {
                        LibraryTab(libraryDetail = data.libraryDetail)
                    }

                    ProfileSection.TRANSPORT -> {
                        TransportTab(transportDetails = data.transportDetails)
                    }

                    ProfileSection.APPRECIATION -> {
                        DisciplineTab(
                            items = data.recentAppreciations,
                            studentName = data.profileData?.name,
                            expandedIndices = data.expandedAppreciations,
                            onToggle = { index ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleAppreciation(index))
                            },
                            isAppreciation = true
                        )
                    }

                    ProfileSection.INFRACTION -> {
                        DisciplineTab(
                            items = data.recentInfractions,
                            studentName = data.profileData?.name,
                            expandedIndices = data.expandedInfractions,
                            onToggle = { index ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleInfraction(index))
                            },
                            isAppreciation = false
                        )
                    }

                    ProfileSection.FEE_DETAILS -> {
                        FeeDetailTab(
                            feeSummery = data.feeSummery,
                            expandedIndices = data.expandedFeeInstallmentIndices,
                            onToggle = { index ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleFeeInstallment(index))
                            }
                        )
                    }

                    ProfileSection.MEDICAL_CARD -> {
                        MedicalCardTab(medicalCard = data.medicalCard)
                    }

                    ProfileSection.REPORT_CARD -> {
                        ReportCardTab(
                            reportCardDetails = data.reportCardDetails,
                            selectedClassIndex = data.selectedReportCardClassIndex,
                            onClassSelected = { index ->
                                handleIntent(StudentProfileDetailsIntent.OnReportCardClassSelected(index))
                            },
                            onReportCardClicked = { reportCard ->
                                handleIntent(StudentProfileDetailsIntent.OnReportCardClicked(reportCard))
                            },
                            onViewReportCard = { title, url ->
                                handleIntent(StudentProfileDetailsIntent.OnViewReportCard(title, url))
                            },
                            onDownloadReportCard = { fileName, url ->
                                handleIntent(StudentProfileDetailsIntent.OnDownloadReportCard(fileName, url))
                            }
                        )
                    }

                    ProfileSection.ATTENDANCE -> {
                        AttendanceTab(
                            attendanceDTL = data.attendanceDTL,
                            academicYears = data.academicYears,
                            selectedYearId = data.selectedAttendanceYearId,
                            expandedMonthIds = data.expandedMonthIds,
                            monthlyDetailCache = data.monthlyDetailCache,
                            loadingMonthIds = data.loadingMonthIds,
                            isLoadingYear = data.isLoadingAttendanceYear,
                            onYearSelected = { yearId ->
                                handleIntent(StudentProfileDetailsIntent.OnAttendanceYearSelected(yearId))
                            },
                            onToggleMonth = { monthId ->
                                handleIntent(StudentProfileDetailsIntent.OnToggleAttendanceMonth(monthId))
                            }
                        )
                    }

                    else -> {
                        PlaceholderTab(section = currentTab ?: ProfileSection.PERSONAL_DETAILS)
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalDetailsTab(
    profileData: StudentProfileDetail?,
    siblingDetails: List<SiblingDetail>,
    expandedSections: Set<PersonalDetailSection>,
    onToggleSection: (PersonalDetailSection) -> Unit
) {
    if (profileData == null) {
        EcareProEmptyState(
            message = "No profile data available",
            icon = Icons.Default.Person
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Personal Info Section
        item(key = "personal_info") {
            ExpandableCardSection(
                title = PersonalDetailSection.PERSONAL_INFO.displayName,
                icon = Icons.Default.School,
                iconColor = Color(PersonalDetailSection.PERSONAL_INFO.iconColor),
                iconBackgroundColor = Color(PersonalDetailSection.PERSONAL_INFO.iconBackgroundColor),
                isExpanded = expandedSections.contains(PersonalDetailSection.PERSONAL_INFO),
                onToggle = { onToggleSection(PersonalDetailSection.PERSONAL_INFO) }
            ) {
                val items = listOf(
                    InfoGridItem("Gender", profileData.gender ?: "NA"),
                    InfoGridItem("Date of birth", profileData.dob ?: "NA"),
                    InfoGridItem("Roll No", profileData.rollNo ?: "NA"),
                    InfoGridItem("House", profileData.house ?: "NA"),
                    InfoGridItem("Transport", profileData.transport ?: "NA"),
                    InfoGridItem("Blood group", profileData.bloodGroup ?: "NA"),
                    InfoGridItem("Caste", profileData.caste ?: "NA"),
                    InfoGridItem("Category", profileData.category ?: "NA"),
                    InfoGridItem("Dise No", profileData.diseNo ?: "NA"),
                    InfoGridItem("Aadhar Number", profileData.aadhaarNumber ?: "NA"),
                    InfoGridItem("PEN", profileData.penNumber ?: "NA"),
                    InfoGridItem("Club", profileData.club ?: "NA"),
                    InfoGridItem("Religion", profileData.religion ?: "NA"),
                    InfoGridItem("Nationality", profileData.nationality ?: "NA"),
                    InfoGridItem("Address", profileData.address ?: "NA"),
                    InfoGridItem("Classification", profileData.classification ?: "NA"),
                    InfoGridItem("SATs No.", profileData.srnUmrnSatsNumber ?: "NA"),
                    InfoGridItem("Bill No", profileData.billNumber ?: "NA"),
                    InfoGridItem("APAAR ID", profileData.apaarId ?: "NA"),
                )
                InfoGridView(items = items)
            }
        }

        // Father's Details Section
        item(key = "father_details") {
            ExpandableCardSection(
                title = PersonalDetailSection.FATHER_DETAILS.displayName,
                icon = Icons.Default.AccountCircle,
                iconColor = Color(PersonalDetailSection.FATHER_DETAILS.iconColor),
                iconBackgroundColor = Color(PersonalDetailSection.FATHER_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(PersonalDetailSection.FATHER_DETAILS),
                onToggle = { onToggleSection(PersonalDetailSection.FATHER_DETAILS) }
            ) {
                val items = listOf(
                    InfoGridItem("Full name", profileData.fatherName ?: "NA"),
                    InfoGridItem("Date of birth", profileData.fatherDOB ?: "NA"),
                    InfoGridItem("Profession", profileData.fatherProfession ?: "NA"),
                    InfoGridItem("Designation", profileData.fatherDesignation ?: "NA"),
                    InfoGridItem("Professional details", profileData.fatherProfessionDetail ?: "NA"),
                    InfoGridItem("Designation details", profileData.fatherDesignationDetail ?: "NA"),
                    InfoGridItem("Contact no", profileData.fatherMob1 ?: "NA", InfoItemType.PHONE),
                    InfoGridItem("Email Id", profileData.fatherEmail1 ?: "NA", InfoItemType.EMAIL),
                    InfoGridItem("Aadhar Number", profileData.fatherAadhaarNumber ?: "NA"),
                    InfoGridItem("PAN", profileData.fatherPAN ?: "NA"),
                    InfoGridItem("Address", profileData.fatherResidentialAddress ?: "NA"),
                    InfoGridItem("Office address", profileData.fatherOfficeAddress ?: "NA")
                )
                InfoGridView(items = items)
            }
        }

        // Mother's Details Section
        item(key = "mother_details") {
            ExpandableCardSection(
                title = PersonalDetailSection.MOTHER_DETAILS.displayName,
                icon = Icons.Default.FavoriteBorder,
                iconColor = Color(PersonalDetailSection.MOTHER_DETAILS.iconColor),
                iconBackgroundColor = Color(PersonalDetailSection.MOTHER_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(PersonalDetailSection.MOTHER_DETAILS),
                onToggle = { onToggleSection(PersonalDetailSection.MOTHER_DETAILS) }
            ) {
                val items = listOf(
                    InfoGridItem("Full name", profileData.motherName ?: "NA"),
                    InfoGridItem("Date of birth", profileData.motherDOB ?: "NA"),
                    InfoGridItem("Profession", profileData.motherProfession ?: "NA"),
                    InfoGridItem("Designation", profileData.motherDesignation ?: "NA"),
                    InfoGridItem("Professional details", profileData.motherProfessionDetail ?: "NA"),
                    InfoGridItem("Designation details", profileData.motherDesignationDetail ?: "NA"),
                    InfoGridItem("Contact no", profileData.motherMob1 ?: "NA", InfoItemType.PHONE),
                    InfoGridItem("Email Id", profileData.motherEmail1 ?: "NA", InfoItemType.EMAIL),
                    InfoGridItem("Aadhar Number", profileData.motherAadhaarNumber ?: "NA"),
                    InfoGridItem("PAN", profileData.motherPAN ?: "NA"),
                    InfoGridItem("Address", profileData.motherResidentialAddress ?: "NA"),
                    InfoGridItem("Office address", profileData.motherOfficeAddress ?: "NA")
                )
                InfoGridView(items = items)
            }
        }

        // Sibling's Details Section
        item(key = "sibling_details") {
            ExpandableCardSection(
                title = PersonalDetailSection.SIBLING_DETAILS.displayName,
                icon = Icons.Default.PersonOutline,
                iconColor = Color(PersonalDetailSection.SIBLING_DETAILS.iconColor),
                iconBackgroundColor = Color(PersonalDetailSection.SIBLING_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(PersonalDetailSection.SIBLING_DETAILS),
                onToggle = { onToggleSection(PersonalDetailSection.SIBLING_DETAILS) }
            ) {
                if (siblingDetails.isEmpty()) {
                    Text(
                        text = "No siblings found",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        siblingDetails.forEach { sibling ->
                            SiblingRowView(sibling = sibling)
                        }
                    }
                }
            }
        }

        // Other Details Section
        item(key = "other_details") {
            ExpandableCardSection(
                title = PersonalDetailSection.OTHER_DETAILS.displayName,
                icon = Icons.Default.Error,
                iconColor = Color(PersonalDetailSection.OTHER_DETAILS.iconColor),
                iconBackgroundColor = Color(PersonalDetailSection.OTHER_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(PersonalDetailSection.OTHER_DETAILS),
                onToggle = { onToggleSection(PersonalDetailSection.OTHER_DETAILS) }
            ) {
                val items = listOf(
                    InfoGridItem("Contact person", profileData.contactPerson ?: "NA"),
                    InfoGridItem("Contact number", profileData.contactMobile ?: "NA", InfoItemType.PHONE)
                )
                InfoGridView(items = items)
            }
        }
    }
}

@Composable
private fun InfirmaryTab(
    medicineIssued: List<MedicineIssued>,
    expandedVisits: Set<Int>,
    onToggleVisit: (Int) -> Unit
) {
    if (medicineIssued.isEmpty()) {
        EcareProEmptyState(
            message = "No Infirmary Visits\nNo medical visits recorded for this student",
            icon = Icons.Default.LocalHospital
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(medicineIssued.size) { index ->
            InfirmaryVisitCard(
                visit = medicineIssued[index],
                isExpanded = expandedVisits.contains(index),
                onToggle = { onToggleVisit(index) }
            )
        }
    }
}

@Composable
private fun InfirmaryVisitCard(
    visit: MedicineIssued,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCardSection(
        title = visit.receiptDate ?: "NA",
        icon = Icons.Default.CalendarMonth,
        iconColor = Color(0xFF4CAF50),
        iconBackgroundColor = Color(0x264CAF50),
        isExpanded = isExpanded,
        onToggle = onToggle
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoDetailItem("Medicine", visit.medicine)
            InfoDetailItem("Diagnosis", visit.diagnosis)
            InfoDetailItem("Attended by", visit.attendedBy)
            InfoDetailItem("Informed parent", visit.informedParent)
            InfoDetailItem("Reason", visit.reasontoVisitInfirmary)
            InfoDetailItem("In", visit.inTime)
            InfoDetailItem("Out", visit.outTime)
            InfoDetailItem("Treatment", visit.remark)
        }
    }
}

@Composable
private fun InfoDetailItem(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value?.ifBlank { "NA" } ?: "NA",
            style = MaterialTheme.appTypography.interMedium14px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTab(libraryDetail: LibraryDetail?) {
    if (libraryDetail == null) {
        EcareProEmptyState(
            message = "No library data available",
            icon = Icons.Default.BookmarkBorder
        )
        return
    }

    var showLibraryBottomSheet by remember { mutableStateOf(false) }
    var libraryBottomSheetType by remember { mutableStateOf(LibraryBottomSheetType.TRANSACTION) }

    val hasTransactions = !libraryDetail.libraryTransaction.isNullOrEmpty()
    val hasFines = !libraryDetail.libraryFineDTL.isNullOrEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Library Transaction Details Card
        item(key = "library_transaction") {
            StatCard(
                title = "Library transaction details",
                icon = Icons.Default.BookmarkBorder,
                iconColor = Color(0xFF00C7BE),
                iconBackgroundColor = Color(0xFFF5F5F5),
                stats = listOf(
                    StatItem(
                        label = "Issued",
                        value = (libraryDetail.issued ?: 0).toString()
                    ),
                    StatItem(
                        label = "Returned",
                        value = (libraryDetail.returned ?: 0).toString()
                    ),
                    StatItem(
                        label = "Pending",
                        value = (libraryDetail.pending ?: 0).toString(),
                        labelColor = if ((libraryDetail.pending ?: 0) > 0) Color(0xFFF44336) else null,
                        valueColor = if ((libraryDetail.pending ?: 0) > 0) Color(0xFFF44336) else null
                    )
                ),
                onViewDetails = if (hasTransactions) {{
                    libraryBottomSheetType = LibraryBottomSheetType.TRANSACTION
                    showLibraryBottomSheet = true
                }} else null
            )
        }

        // Library Fine Details Card
        item(key = "library_fine") {
            StatCard(
                title = "Library fine details",
                icon = Icons.Default.MedicalServices,
                iconColor = Color(0xFFFF7043),
                iconBackgroundColor = Color(0xFFF5F5F5),
                stats = listOf(
                    StatItem(
                        label = "Dues",
                        value = formatCurrency(libraryDetail.duesAMT ?: 0.0)
                    ),
                    StatItem(
                        label = "Waive off",
                        value = formatCurrency(libraryDetail.waiveAMT ?: 0.0)
                    ),
                    StatItem(
                        label = "Paid",
                        value = formatCurrency(libraryDetail.paidAMT ?: 0.0)
                    ),
                    StatItem(
                        label = "Pending",
                        value = formatCurrency(libraryDetail.pendingAMT ?: 0.0),
                        labelColor = if ((libraryDetail.pendingAMT ?: 0.0) > 0) Color(0xFFF44336) else null,
                        valueColor = if ((libraryDetail.pendingAMT ?: 0.0) > 0) Color(0xFFF44336) else null
                    )
                ),
                onViewDetails = if (hasFines) {{
                    libraryBottomSheetType = LibraryBottomSheetType.FINE
                    showLibraryBottomSheet = true
                }} else null
            )
        }
    }

    // Library Details Bottom Sheet
    if (showLibraryBottomSheet) {
        LibraryDetailsBottomSheet(
            type = libraryBottomSheetType,
            transactions = libraryDetail.libraryTransaction,
            fines = libraryDetail.libraryFineDTL,
            onDismiss = { showLibraryBottomSheet = false }
        )
    }
}

private enum class LibraryBottomSheetType {
    TRANSACTION,
    FINE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryDetailsBottomSheet(
    type: LibraryBottomSheetType,
    transactions: List<LibraryTransaction>?,
    fines: List<LibraryFine>?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val title = when (type) {
        LibraryBottomSheetType.TRANSACTION -> "Transaction details"
        LibraryBottomSheetType.FINE -> "Fine details"
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on type
            when (type) {
                LibraryBottomSheetType.TRANSACTION -> {
                    transactions?.forEach { transaction ->
                        LibraryDetailItem(
                            bookName = transaction.bookName,
                            status = transaction.status,
                            amount = null
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                LibraryBottomSheetType.FINE -> {
                    fines?.forEach { fine ->
                        LibraryDetailItem(
                            bookName = fine.bookName,
                            status = fine.status,
                            amount = fine.fineAmount
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LibraryDetailItem(
    bookName: String?,
    status: String?,
    amount: Double?
) {
    val statusLower = status?.lowercase() ?: ""
    val isPaidOrReturned = statusLower == "paid" || statusLower == "returned"
    val statusColor = if (isPaidOrReturned) Color(0xFF4CAF50) else Color(0xFFF44336)
    val iconBackgroundColor = if (isPaidOrReturned) Color(0xFFF5F5F5) else Color(0xFFFFEBEE)
    val iconTintColor = if (isPaidOrReturned) Color(0xFF4CAF50) else Color(0xFFF44336)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Book Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconBackgroundColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTintColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Book Name and Status
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bookName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "N/A",
                style = MaterialTheme.appTypography.interMedium14px,
                color = MaterialTheme.appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status ?: "N/A",
                style = MaterialTheme.appTypography.interRegular12px,
                color = statusColor
            )
        }

        // Amount (only for fines)
        if (amount != null) {
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.appTypography.interMedium14px,
                color = MaterialTheme.appColors.textPrimary
            )
        }
    }
}

@Composable
private fun TransportTab(transportDetails: TransportDetails?) {
    // Determine transport type and check if we have data
    val transportType = transportDetails?.transportType?.lowercase() ?: ""
    val hasData = when {
        transportType == "school" -> transportDetails?.schoolTransport != null
        transportType.isNotEmpty() -> transportDetails != null
        else -> false
    }

    if (!hasData) {
        EcareProEmptyState(
            message = "No Transport Data\nNo transport details available for this student",
            icon = Icons.Default.DirectionsBus
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "transport_details") {
            val stats = if (transportType == "school") {
                val schoolTransport = transportDetails?.schoolTransport
                listOf(
                    StatItem("Transport type", transportDetails?.transportType.orDefault()),
                    StatItem("Vehicle number", schoolTransport?.vehicleNumber.orDefault()),
                    StatItem("Vehicle type", schoolTransport?.vehicleType.orDefault()),
                    StatItem("Driver name", schoolTransport?.driverName.orDefault()),
                    StatItem("Vehicle name", schoolTransport?.vehicleName.orDefault()),
                    StatItem("Contact number", schoolTransport?.driverMob.orDefault()),
                    StatItem("Route number", schoolTransport?.routeNo.orDefault()),
                    StatItem("Stop name", schoolTransport?.stopName.orDefault()),
                    StatItem("Route in charge name", schoolTransport?.routeInchargeName.orDefault()),
                    StatItem("Route in charge contact", schoolTransport?.routeInchargeMobile.orDefault())
                )
            } else {
                listOf(
                    StatItem("Transport type", transportDetails?.transportType.orDefault()),
                    StatItem("Vehicle number", transportDetails?.vehicleNumber.orDefault()),
                    StatItem("Vehicle type", transportDetails?.vehicleType.orDefault())
                )
            }

            StatCard(
                title = "Transport details",
                icon = Icons.Default.DirectionsBus,
                iconColor = Color(0xFF00C7BE),
                iconBackgroundColor = Color(0xFFF5F5F5),
                stats = stats
            )
        }
    }
}

@Composable
private fun MedicalCardTab(medicalCard: MedicalCard?) {
    if (medicalCard == null) {
        EcareProEmptyState(
            message = "No medical card data available",
            icon = Icons.Default.MedicalServices
        )
        return
    }

    val medicalCardItems = buildMedicalCardItems(medicalCard)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Medical Card Section Header
        item(key = "medical_card_header") {
            MedicalCardHeader()
        }

        // All medical card items
        items(medicalCardItems.size, key = { medicalCardItems[it].label }) { index ->
            val item = medicalCardItems[index]
            MedicalCardItem(
                label = item.label,
                value = item.value,
                formatAsDate = item.formatAsDate
            )
        }
    }
}

@Composable
private fun MedicalCardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFE8E8), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFFF6B6B)
            )
        }
        Text(
            text = "Medical card",
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@Composable
private fun MedicalCardItem(
    label: String,
    value: String?,
    formatAsDate: Boolean = false
) {
    val hasValue = !value.isNullOrBlank()
    val displayValue = when {
        !hasValue -> "N/A"
        formatAsDate -> formatDisciplineDate(value)
        else -> value
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular16px,
                color = MaterialTheme.appColors.textSecondary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayValue,
                    style = MaterialTheme.appTypography.interMedium14px,
                    color = if (hasValue) MaterialTheme.appColors.textPrimary
                           else MaterialTheme.appColors.textSecondary
                )

                MedicalCardCheckbox(hasValue = hasValue)
            }
        }

        androidx.compose.material3.Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.appColors.border.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun MedicalCardCheckbox(hasValue: Boolean) {
    if (hasValue) {
        Checkbox(
            checked = true,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF4CAF50),
                checkmarkColor = White
            )
        )
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .padding(10.dp)
                .background(Color(0xFFF44336), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Not Available",
                modifier = Modifier.size(14.dp),
                tint = White
            )
        }
    }
}

private data class MedicalCardItemData(
    val label: String,
    val value: String?,
    val formatAsDate: Boolean = false
)

private fun buildMedicalCardItems(medicalCard: MedicalCard): List<MedicalCardItemData> {
    return listOf(
        // Vaccinations
        MedicalCardItemData("BCG", medicalCard.bcg),
        MedicalCardItemData("Diphtheria", medicalCard.diphtheria),
        MedicalCardItemData("DPT Booster", medicalCard.dptBooster),
        MedicalCardItemData("Whooping Cough", medicalCard.whoopingCough),
        MedicalCardItemData("Tetanus", medicalCard.tetanus),
        MedicalCardItemData("Measles", medicalCard.measles),
        MedicalCardItemData("MMR", medicalCard.mmr),
        MedicalCardItemData("Chicken Pox", medicalCard.chickenPox),
        MedicalCardItemData("Hepatitis A", medicalCard.hepatitisA),
        MedicalCardItemData("Hepatitis B", medicalCard.hepatitisB),
        MedicalCardItemData("Typhoid", medicalCard.typhoid),

        // COVID Vaccinations with dates
        MedicalCardItemData("Covid Dose 1", medicalCard.covidDose1, formatAsDate = true),
        MedicalCardItemData("Covid Dose 2", medicalCard.covidDose2, formatAsDate = true),
        MedicalCardItemData("Covid Booster Dose", medicalCard.covidBoosterDose, formatAsDate = true),

        // Medical History
        MedicalCardItemData("Allergies", medicalCard.allergies),
        MedicalCardItemData("Surgery undergone\nin the past", medicalCard.surgeryUndergoneInthePast),
        MedicalCardItemData("Specific past\ndisease", medicalCard.specificPastDisease)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardTab(
    reportCardDetails: List<ReportCardDetail>,
    selectedClassIndex: Int,
    onClassSelected: (Int) -> Unit,
    onReportCardClicked: (com.app.ecarepro.core.domain.model.ReportCard) -> Unit,
    onViewReportCard: (String, String) -> Unit,
    onDownloadReportCard: (String, String) -> Unit
) {
    if (reportCardDetails.isEmpty()) {
        EcareProEmptyState(
            message = "No report cards available",
            icon = Icons.Default.BookmarkBorder
        )
        return
    }

    var showClassPicker by remember { mutableStateOf(false) }
    var selectedReportCard by remember { mutableStateOf<com.app.ecarepro.core.domain.model.ReportCard?>(null) }
    var showReportCardBottomSheet by remember { mutableStateOf(false) }

    val classOptions = reportCardDetails.map { it.className ?: "Class" }
    val selectedReportCardDetail = reportCardDetails.getOrNull(selectedClassIndex)
    val selectedClassName = classOptions.getOrNull(selectedClassIndex) ?: classOptions.firstOrNull() ?: ""

    Column(modifier = Modifier.fillMaxSize()) {
        // Class Dropdown (like syllabus)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(16.dp)
        ) {
            EcareProDropdownField(
                value = selectedClassName,
                placeholder = "Select class",
                onClick = { showClassPicker = true }
            )
        }

        // Report Cards List
        selectedReportCardDetail?.let { detail ->
            val reportCards = detail.reportCards ?: emptyList()

            if (reportCards.isEmpty()) {
                EcareProEmptyState(
                    message = "No report cards for ${detail.className}",
                    icon = Icons.Default.BookmarkBorder
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    items(reportCards.size, key = { index -> "${detail.classID}_${index}" }) { index ->
                        ReportCardItem(
                            reportCard = reportCards[index],
                            detail = detail,
                            onClick = {
                                selectedReportCard = reportCards[index]
                                showReportCardBottomSheet = true
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(4.dp)) }
                }
            }
        }
    }

    // Class Picker Bottom Sheet
    EcareProSelectionBottomSheet(
        title = "Select class",
        isVisible = showClassPicker,
        options = classOptions,
        selectedOptions = listOf(selectedClassName),
        isMultiSelection = false,
        onDismiss = { showClassPicker = false },
        onOptionsSelected = { selected ->
            val index = classOptions.indexOf(selected.firstOrNull())
            if (index != -1) {
                onClassSelected(index)
            }
            showClassPicker = false
        }
    )

    // Report Card Attachment Bottom Sheet
    if (showReportCardBottomSheet && selectedReportCard != null) {
        ReportCardAttachmentBottomSheet(
            reportCard = selectedReportCard!!,
            onDismiss = { showReportCardBottomSheet = false },
            onViewClicked = { url ->
                onViewReportCard(
                    selectedReportCard?.examName ?: "Report Card",
                    url
                )
                showReportCardBottomSheet = false
            },
            onDownloadClicked = { url ->
                onDownloadReportCard(
                    "${selectedReportCard?.examName ?: "ReportCard"}.pdf",
                    url
                )
                showReportCardBottomSheet = false
            }
        )
    }
}

@Composable
private fun ReportCardItem(
    reportCard: com.app.ecarepro.core.domain.model.ReportCard,
    detail: ReportCardDetail,
    onClick: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 300), label = "rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header - Date with chevron (like infraction)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF2196F3)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${reportCard.examName ?: "Term"} (${detail.academicYear})",
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Updated on: ${reportCard.updatedOn ?: "N/A"}",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(rotationAngle),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardAttachmentBottomSheet(
    reportCard: com.app.ecarepro.core.domain.model.ReportCard,
    onDismiss: () -> Unit,
    onViewClicked: (String) -> Unit,
    onDownloadClicked: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Determine view mode: 1 = single file, 2 = front/back
    val hasFrontBack = reportCard.viewMode == 2 &&
                       !reportCard.frontFileName.isNullOrBlank() &&
                       !reportCard.backFileName.isNullOrBlank()
    val hasSingleFile = !reportCard.fileName.isNullOrBlank()

    var selectedView by remember { mutableStateOf("front") } // "front" or "back"

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reportCard.examName ?: "Report Card",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Front/Back Toggle (only if viewMode == 2)
            if (hasFrontBack) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Front View Button
                    Button(
                        title = "Front view",
                        onClick = { selectedView = "front" },
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (selectedView == "front")
                            MaterialTheme.appColors.primary
                        else
                            Color(0xFFF5F5F5)
                    )

                    // Back View Button
                    Button(
                        title = "Back view",
                        onClick = { selectedView = "back" },
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (selectedView == "back")
                            MaterialTheme.appColors.primary
                        else
                            Color(0xFFF5F5F5)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // View Button
            val viewUrl = when {
                hasFrontBack -> if (selectedView == "front") reportCard.frontFileName else reportCard.backFileName
                hasSingleFile -> reportCard.fileName
                else -> null
            }

            if (!viewUrl.isNullOrBlank()) {
                // View Button
                androidx.compose.material3.Button(
                    onClick = { viewUrl?.let { onViewClicked(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = MaterialTheme.appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View",
                        style = MaterialTheme.appTypography.interMedium16px
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Download Button
                androidx.compose.material3.Button(
                    onClick = { viewUrl?.let { onDownloadClicked(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = MaterialTheme.appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download",
                        style = MaterialTheme.appTypography.interMedium16px
                    )
                }
            } else {
                Text(
                    text = "No file available",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DisciplineTab(
    items: List<Any>,
    studentName: String?,
    expandedIndices: Set<Int>,
    onToggle: (Int) -> Unit,
    isAppreciation: Boolean
) {
    val titleColor = if (isAppreciation) Color(0xFF29B6F6) else Color(0xFFF44336)
    val footerBgColor = if (isAppreciation) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val footerTextColor = if (isAppreciation) Color(0xFF4CAF50) else Color(0xFFFF7043)
    val headerIcon = if (isAppreciation) Icons.Default.Star else Icons.Default.Warning
    val headerText = if (isAppreciation) "Total Appreciations" else "Total Infractions"

    if (items.isEmpty()) {
        EcareProEmptyState(
            message = if (isAppreciation)
                "No Appreciations\nThis student has no appreciation records"
            else
                "No Infractions\nThis student has no infraction records",
            icon = if (isAppreciation) Icons.Default.Star else Icons.Default.Warning
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = headerIcon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = titleColor
            )
            Text(
                text = "$headerText - ${items.size}",
                style = MaterialTheme.appTypography.interMedium14px,
                color = MaterialTheme.appColors.textPrimary
            )
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(items, key = { index, _ -> index }) { index, item ->
                DisciplineCard(
                    item = item,
                    index = index,
                    studentName = studentName,
                    isExpanded = expandedIndices.contains(index),
                    isAppreciation = isAppreciation,
                    titleColor = titleColor,
                    footerBgColor = footerBgColor,
                    footerTextColor = footerTextColor,
                    onToggle = { onToggle(index) }
                )
            }
        }
    }
}

@Composable
private fun DisciplineCard(
    item: Any,
    index: Int,
    studentName: String?,
    isExpanded: Boolean,
    isAppreciation: Boolean,
    titleColor: Color,
    footerBgColor: Color,
    footerTextColor: Color,
    onToggle: () -> Unit
) {
    val (date, title, description, metaInfo, footerText) = when {
        isAppreciation && item is Appreciation -> buildAppreciationTexts(item, studentName)
        !isAppreciation && item is Infraction -> buildInfractionTexts(item, studentName)
        else -> return
    }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = "chevron_rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header - Date with chevron
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF2196F3)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = date,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(rotationAngle),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    androidx.compose.material3.Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.appColors.border
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Title
                        Text(
                            text = title,
                            style = MaterialTheme.appTypography.interMedium14px,
                            color = titleColor
                        )

                        // Description
                        Text(
                            text = description,
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textPrimary,
                            lineHeight = 16.sp
                        )

                        // Meta info
                        Text(
                            text = metaInfo,
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )

                        // Footer banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(footerBgColor, RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = footerText,
                                style = MaterialTheme.appTypography.interMedium14px,
                                color = footerTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class DisciplineTexts(
    val date: String,
    val title: String,
    val description: String,
    val metaInfo: String,
    val footerText: String
)

private fun buildAppreciationTexts(appreciation: Appreciation, studentName: String?): DisciplineTexts {
    val firstName = getFirstName(studentName)
    val date = formatDisciplineDate(appreciation.appreciationOn)

    return DisciplineTexts(
        date = date,
        title = appreciation.appreciation ?: "Appreciation",
        description = "$firstName has been appreciated as a \"${appreciation.reward ?: "reward"}\" for showing ${appreciation.subAppreciation ?: "qualities"}.",
        metaInfo = "Reward: ${appreciation.reward ?: "NA"} • Instance: ${appreciation.instance ?: 0}",
        footerText = "Appreciated on $date • ${appreciation.staffName ?: "Staff"}"
    )
}

private fun buildInfractionTexts(infraction: Infraction, studentName: String?): DisciplineTexts {
    val firstName = getFirstName(studentName)
    val date = formatDisciplineDate(infraction.infractionOn)

    return DisciplineTexts(
        date = date,
        title = infraction.infraction ?: "Infraction",
        description = "$firstName received an infraction for ${infraction.infraction ?: "violation"}, which is considered ${infraction.subInfraction ?: "serious"}.",
        metaInfo = "Consequence: ${infraction.consequences ?: "NA"} • Instance: ${infraction.instance ?: "NA"}",
        footerText = "Infraction on $date • ${infraction.staffName ?: "Staff"}"
    )
}

private fun formatDisciplineDate(dateString: String?): String {
    return formatDate(dateString)
}

@Composable
private fun PlaceholderTab(section: ProfileSection) {
    PlaceholderTab(
        title = section.displayName,
        icon = getIconForSection(section),
        message = "Coming soon"
    )
}

private fun getIconForSection(section: ProfileSection): ImageVector {
    return when (section) {
        ProfileSection.PERSONAL_DETAILS -> Icons.Default.Person
        ProfileSection.ATTENDANCE -> Icons.Default.CalendarMonth
        ProfileSection.ACADEMIC_PERFORMANCE -> Icons.Default.School
        ProfileSection.FEE_DETAILS -> Icons.Default.BookmarkBorder
        ProfileSection.INFIRMARY -> Icons.Default.LocalHospital
        ProfileSection.LIBRARY -> Icons.Default.BookmarkBorder
        ProfileSection.TRANSPORT -> Icons.Default.DirectionsBus
        ProfileSection.INFRACTION -> Icons.Default.Warning
        ProfileSection.APPRECIATION -> Icons.Default.Star
        ProfileSection.MEDICAL_CARD -> Icons.Default.MedicalServices
        ProfileSection.REPORT_CARD -> Icons.Default.BookmarkBorder
        ProfileSection.SESSION_LOG -> Icons.Default.CalendarMonth
    }
}

// ──────────────────────────────────────────────
// Fee Detail Tab
// ──────────────────────────────────────────────

@Composable
private fun FeeDetailTab(
    feeSummery: FeeSummary?,
    expandedIndices: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    val installments = feeSummery?.feeInstallment ?: emptyList()

    if (installments.isEmpty()) {
        EcareProEmptyState(
            message = "No fee data available",
            icon = Icons.Default.AccountCircle
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary cards
        item {
            feeSummery?.let { fee ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeeSummaryCardItem(
                        title = "Total Fee",
                        amount = fee.totalActualFee ?: 0.0,
                        color = Color(0xFF64B5F6)
                    )
                    FeeSummaryCardItem(
                        title = "Concession",
                        amount = fee.totalConcession ?: 0.0,
                        color = Color(0xFF81C784)
                    )
                    FeeSummaryCardItem(
                        title = "Received",
                        amount = fee.totalReceived ?: 0.0,
                        color = Color(0xFFFFD54F)
                    )
                    FeeSummaryCardItem(
                        title = "Outstanding",
                        amount = fee.totalOutstanding ?: 0.0,
                        color = Color(0xFFF48FB1)
                    )
                }
            }
        }

        // Spacer between sections
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Installment cards
        itemsIndexed(installments) { index, installment ->
            FeeInstallmentCard(
                installment = installment,
                isExpanded = index in expandedIndices,
                onToggle = { onToggle(index) }
            )
        }
    }
}

@Composable
private fun FeeSummaryCardItem(
    title: String,
    amount: Double,
    color: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .background(color = color, shape = RoundedCornerShape(4.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary
                )
                Text(
                    text = formatFeeAmount(amount),
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun FeeInstallmentCard(
    installment: FeeInstallment,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp),
                        color = Color(0xFF64B5F6)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = installment.installment ?: "N/A",
                        style = MaterialTheme.appTypography.interMedium16px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Text(
                        text = "Outstanding: ${formatFeeAmount(installment.outstanding ?: 0.0)}",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isExpanded) 180f else 0f),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }

            // Expanded details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    FeeDetailRow("Actual Fee", formatFeeAmount(installment.actualFee ?: 0.0))
                    FeeDetailRow("Concession", formatFeeAmount(installment.concession ?: 0.0))
                    FeeDetailRow("Received", formatFeeAmount(installment.received ?: 0.0))
                    FeeDetailRow("Outstanding", formatFeeAmount(installment.outstanding ?: 0.0))
                }
            }
        }
    }
}

@Composable
private fun FeeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp),
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

private fun formatFeeAmount(amount: Double): String {
    return "₹${String.format("%,.2f", amount)}"
}

// ──────────────────────────────────────────────

private fun formatDisplayName(profile: StudentProfileDetail): String {
    val className = profile.className ?: ""
    return if (className.isEmpty()) {
        profile.name
    } else {
        "${profile.name}, $className"
    }
}

private fun formatAdmissionInfo(profile: StudentProfileDetail): String {
    val admNo = profile.admissionNo ?: "NA"
    val doj = profile.joiningDate ?: profile.admissionDate ?: "NA"
    return "Admission No - $admNo • DOJ - $doj"
}

// Preview Data
private val sampleStudentProfile = StudentProfileDetail(
    // Basic Info
    username = null,
    name = "John Doe",
    gender = "Male",
    admissionNo = "ADM12345",
    rollNo = "15",
    billNumber = "BILL123",
    dob = "15-Jan-2010",
    admissionDate = "01-Apr-2020",
    joiningDate = "01-Apr-2020",
    className = "Class 10-A",

    // Photos
    photo = null,
    coverImg = null,
    escortPhoto = null,

    // Personal Details
    bloodGroup = "A+",
    religion = "Hindu",
    nationality = "Indian",
    transport = "Bus Route 5",
    birthPlace = "Mumbai",
    house = "Red House",
    isBoarding = false,
    classification = "Day Scholar",
    caste = "General",
    category = "General",
    club = "Science Club",

    // Address Details
    address = "123, Main Street, City - 123456",
    city = "Mumbai",
    state = "Maharashtra",
    permanentAddress = "123, Main Street, City - 123456",
    permanentCity = "Mumbai",
    permanentState = "Maharashtra",

    // ID Numbers
    diseNo = "DISE123456",
    aadhaarNumber = "1234-5678-9012",
    penNumber = "PEN12345",
    apaarId = null,
    srnUmrnSatsNumber = "SRN12345",

    // Father's Details
    fatherName = "Mr. John Doe Sr.",
    fatherProfession = "Engineer",
    fatherDesignationID = null,
    fatherDesignation = "Senior Manager",
    fatherDOB = "10-May-1980",
    fatherResidentialAddress = "123, Main Street, City - 123456",
    fatherOfficeAddress = "456, Tech Park, City - 123456",
    fatherEmail1 = "john.doe.sr@email.com",
    fatherEmail2 = null,
    fatherMob1 = "+91 9876543210",
    fatherMob2 = null,
    fatherAadhaarNumber = "9876-5432-1098",
    fatherPAN = "ABCDE1234F",
    fatherAnnualIncome = null,
    fatherPhoto = null,
    fatherProfessionDetail = "Software Engineering",
    fatherDesignationDetail = "Tech Lead",

    // Mother's Details
    motherName = "Mrs. Jane Doe",
    motherProfession = "Teacher",
    motherDesignation = "Principal",
    motherDOB = "15-Jun-1982",
    motherResidentialAddress = "123, Main Street, City - 123456",
    motherOfficeAddress = "789, School Road, City - 123456",
    motherEmail1 = "jane.doe@email.com",
    motherEmail2 = null,
    motherMob1 = "+91 9876543211",
    motherMob2 = null,
    motherAadhaarNumber = "9876-5432-1099",
    motherPAN = "ABCDE1234G",
    motherAnnualIncome = null,
    motherPhoto = null,
    motherProfessionDetail = "Mathematics",
    motherDesignationDetail = "Head of Department",

    // Contact Details
    contactPerson = "Mr. John Doe Sr.",
    parentStaus = null,
    contactMobile = "+91 9876543210",
    contactEmailID = null,
    studentEmail = null,
    parentAnniversaryDate = null,
    additionalMobile = null,

    // Misc
    canChangeCoverImg = null,
    coverImgReq = null,
    previousSchoolDTL = null
)

private val sampleSiblings = listOf(
    SiblingDetail(
        stID = 1001,
        name = "Jane Doe",
        gender = "Female",
        siblingClass = "Class 8-B",
        rollNumber = "12",
        admissionNumber = "ADM12346",
        dob = "20-Mar-2012",
        fatherName = "Mr. John Doe Sr.",
        contactPerson = "Mr. John Doe Sr.",
        contactMob = "+91 9876543210",
        photo = null,
        houseID = 1,
        houseName = "Red House",
        clubID = 1,
        clubName = "Art Club",
        fatherPhoto = null,
        motherPhoto = null,
        escortPhoto = null,
        isSelected = false
    ),
    SiblingDetail(
        stID = 1002,
        name = "Jack Doe",
        gender = "Male",
        siblingClass = "Class 6-A",
        rollNumber = "8",
        admissionNumber = "ADM12347",
        dob = "10-Jul-2014",
        fatherName = "Mr. John Doe Sr.",
        contactPerson = "Mr. John Doe Sr.",
        contactMob = "+91 9876543210",
        photo = null,
        houseID = 1,
        houseName = "Red House",
        clubID = 2,
        clubName = "Sports Club",
        fatherPhoto = null,
        motherPhoto = null,
        escortPhoto = null,
        isSelected = false
    )
)

private val sampleMedicineIssued = listOf(
    MedicineIssued(
        medicine = "Paracetamol 500mg",
        qty = 2,
        receiptDate = "15-Dec-2024",
        inTime = "09:30 AM",
        outTime = "10:00 AM",
        reasontoVisitInfirmary = "High fever and headache",
        diagnosis = "Fever",
        remark = "Advised rest",
        attendedBy = "Dr. Smith",
        informedParent = "Yes"
    ),
    MedicineIssued(
        medicine = "Cetrizine 10mg",
        qty = 1,
        receiptDate = "10-Nov-2024",
        inTime = "11:00 AM",
        outTime = "11:15 AM",
        reasontoVisitInfirmary = "Skin rash and itching",
        diagnosis = "Allergy",
        remark = "Antihistamine prescribed",
        attendedBy = "Dr. Johnson",
        informedParent = "Yes"
    )
)

private val sampleLibraryDetail = LibraryDetail(
    issued = 23,
    returned = 21,
    pending = 2,
    duesAMT = 454.56,
    waiveAMT = 4.56,
    paidAMT = 420.0,
    pendingAMT = 30.0,
    libraryTransaction = null,
    libraryFineDTL = null
)

private val sampleTransportDetails = TransportDetails(
    transportType = "School",
    transportTypeID = 0,
    vehicleTypeID = 0,
    vehicleNumber = null,
    vehicleType = null,
    driverName = null,
    driverMob = null,
    driverAdd = null,
    driverAadharNumber = null,
    driverVoterIDNo = null,
    driverDrivingLNo = null,
    driverClearanceNo = null,
    isLadyGuardAvailabile = false,
    transporterName = null,
    transporterMob = null,
    transporterAdd = null,
    transporterAadharNumber = null,
    transporterVoterIDNo = null,
    transporterDrivingLNo = null,
    routeNumber = null,
    stopName = null,
    vehicleUsingFrom = null,
    schoolTransport = com.app.ecarepro.core.domain.model.SchoolTransport(
        vehicleType = "Bus",
        vehicleName = "Thakurdawara 1",
        vehicleNumber = "MH 45 5678",
        driverName = "Mr. Santosh kumar",
        driverMob = "+91 9873045662",
        routeNo = "Thakurdawara to Dhanuhari",
        stopName = "Thakurdawara",
        routeInchargeName = "Mr. Santosh kumar",
        routeInchargeMobile = "+91 9873045662"
    )
)

private val sampleAppreciations = listOf(
    Appreciation(
        stID = 362,
        studentName = "Aastha Saini",
        studentClass = "11-A",
        admissionNo = "6788",
        rollNumber = "8",
        photo = null,
        appreciation = "Behavioural Appreciation",
        subAppreciation = "leadership qualities",
        instance = 2,
        reward = "⭐ Good lad",
        point = 10,
        appreciationOn = "22-Jan-2025",
        remark = "Excellent leadership",
        staffName = "Mohit Singh Pawar",
        designation = "Teacher",
        stffPhoto = null,
        canDelete = false
    )
)

private val sampleInfractions = listOf(
    Infraction(
        stID = 362,
        studentName = "Aastha Saini",
        studentClass = "11-A",
        admissionNo = "6788",
        rollNumber = "8",
        photo = null,
        infraction = "Late Attendance",
        subInfraction = "Morning Assembly",
        instance = "1st Warning",
        consequences = "Verbal Warning",
        consequencesAttachment = null,
        point = -5,
        infractionOn = "10-Dec-2024",
        correctiveAction = "Report on time",
        staffName = "Mrs. Gupta",
        designation = "Class Teacher",
        stffPhoto = null,
        canDelete = false,
        isComplianceActive = false,
        compliance = null,
        complianceAttachment = null,
        isCompModified = false,
        compCreatedBy = null,
        compCreatedOn = null,
        showResolvedButton = false,
        isResolved = false,
        resolvedOn = null
    )
)

private val sampleMedicalCard = MedicalCard(
    bcg = "2010-05-15",
    diphtheria = "2010-06-20",
    dptBooster = "2015-08-10",
    whoopingCough = "2010-07-12",
    tetanus = "4/10/2014 12:00:00 AM",
    measles = "2011-03-15",
    mmr = "",
    chickenPox = "2012-11-20",
    hepatitisA = "",
    hepatitisB = "2010-09-10",
    typhoid = "2013-12-05",
    covidDose1 = "2021-02-25",
    covidDose2 = "2021-09-24",
    covidBoosterDose = "2023-01-01",
    allergies = "",
    surgeryUndergoneInthePast = "",
    specificPastDisease = "NO",
    childRegularMedication = ""
)

private val sampleReportCards = listOf(
    ReportCardDetail(
        classID = 37,
        className = "11-A",
        yrID = 8,
        academicYear = "2024-2025",
        isCur = 1,
        reportCards = listOf(
            com.app.ecarepro.core.domain.model.ReportCard(
                examName = "Term-1",
                viewMode = 2,
                fileName = null,
                fileSize = null,
                frontFileName = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/ReportCard/NIVEDITA_362_2024-2025_Terms-1_Front.pdf",
                frontFileSize = "0",
                backFileName = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/ReportCard/NIVEDITA_362_2024-2025_Terms-1_Back.pdf",
                backFileSize = "0",
                updatedOn = "02-Jul-2024"
            ),
            com.app.ecarepro.core.domain.model.ReportCard(
                examName = "Term-2",
                viewMode = 1,
                fileName = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/ReportCard/NIVEDITA_362_2024-2025_Term-2.pdf",
                fileSize = "285.43 KB",
                frontFileName = null,
                frontFileSize = null,
                backFileName = null,
                backFileSize = null,
                updatedOn = "21-Mar-2024"
            )
        )
    ),
    ReportCardDetail(
        classID = 33,
        className = "10-C",
        yrID = 7,
        academicYear = "2023-2024",
        isCur = 0,
        reportCards = listOf(
            com.app.ecarepro.core.domain.model.ReportCard(
                examName = "Term-1",
                viewMode = 1,
                fileName = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/ReportCard/NIVEDITA_362_2023-2024_Term-1.pdf",
                fileSize = "290.12 KB",
                frontFileName = null,
                frontFileSize = null,
                backFileName = null,
                backFileSize = null,
                updatedOn = "15-Oct-2023"
            )
        )
    )
)

// Preview Composables
@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Student Profile - Success State")
@Composable
private fun PreviewStudentProfileDetailsSuccess() {
    val uiState = UiState.Success(
        StudentProfileDetailsUiState(
            profileData = sampleStudentProfile,
            siblingDetails = sampleSiblings,
            medicineIssued = sampleMedicineIssued,
            libraryDetail = sampleLibraryDetail,
            transportDetails = sampleTransportDetails,
            recentAppreciations = sampleAppreciations,
            recentInfractions = sampleInfractions,
            medicalCard = sampleMedicalCard,
            reportCardDetails = sampleReportCards,
            selectedReportCardClassIndex = 0,
            visibleTabs = listOf(
                ProfileSection.PERSONAL_DETAILS,
                ProfileSection.LIBRARY,
                ProfileSection.TRANSPORT,
                ProfileSection.INFIRMARY,
                ProfileSection.ATTENDANCE,
                ProfileSection.ACADEMIC_PERFORMANCE,
                ProfileSection.APPRECIATION,
                ProfileSection.INFRACTION,
                ProfileSection.MEDICAL_CARD,
                ProfileSection.REPORT_CARD
            ),
            selectedTabIndex = 0,
            expandedSections = emptySet(), // All collapsed by default
            expandedInfirmaryVisits = emptySet()
        )
    )

    StudentProfileDetailsScreenContent(
        uiState = uiState,
        handleIntent = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Student Profile - With One Expanded")
@Composable
private fun PreviewStudentProfileDetailsExpanded() {
    val uiState = UiState.Success(
        StudentProfileDetailsUiState(
            profileData = sampleStudentProfile,
            siblingDetails = sampleSiblings,
            medicineIssued = sampleMedicineIssued,
            libraryDetail = sampleLibraryDetail,
            transportDetails = sampleTransportDetails,
            recentAppreciations = sampleAppreciations,
            recentInfractions = sampleInfractions,
            medicalCard = sampleMedicalCard,
            reportCardDetails = sampleReportCards,
            selectedReportCardClassIndex = 0,
            visibleTabs = listOf(
                ProfileSection.PERSONAL_DETAILS,
                ProfileSection.LIBRARY,
                ProfileSection.TRANSPORT,
                ProfileSection.INFIRMARY,
                ProfileSection.ATTENDANCE,
                ProfileSection.ACADEMIC_PERFORMANCE,
                ProfileSection.APPRECIATION,
                ProfileSection.INFRACTION,
                ProfileSection.MEDICAL_CARD,
                ProfileSection.REPORT_CARD
            ),
            selectedTabIndex = 0,
            expandedSections = setOf(PersonalDetailSection.PERSONAL_INFO), // One section expanded
            expandedInfirmaryVisits = emptySet()
        )
    )

    StudentProfileDetailsScreenContent(
        uiState = uiState,
        handleIntent = {}
    )
}

@Preview(showBackground = true, name = "Student Profile - Loading State")
@Composable
private fun PreviewStudentProfileDetailsLoading() {
    StudentProfileDetailsScreenContent(
        uiState = UiState.Loading,
        handleIntent = {}
    )
}

@Preview(showBackground = true, name = "Student Profile - Error State")
@Composable
private fun PreviewStudentProfileDetailsError() {
    StudentProfileDetailsScreenContent(
        uiState = UiState.Error("Failed to load student profile"),
        handleIntent = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Personal Details Tab - Collapsed")
@Composable
private fun PreviewPersonalDetailsTab() {
    PersonalDetailsTab(
        profileData = sampleStudentProfile,
        siblingDetails = sampleSiblings,
        expandedSections = emptySet(), // All collapsed by default
        onToggleSection = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Personal Details Tab - One Expanded")
@Composable
private fun PreviewPersonalDetailsTabExpanded() {
    PersonalDetailsTab(
        profileData = sampleStudentProfile,
        siblingDetails = sampleSiblings,
        expandedSections = setOf(PersonalDetailSection.PERSONAL_INFO), // One section expanded
        onToggleSection = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Infirmary Tab - Collapsed")
@Composable
private fun PreviewInfirmaryTab() {
    InfirmaryTab(
        medicineIssued = sampleMedicineIssued,
        expandedVisits = emptySet(), // All collapsed by default
        onToggleVisit = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Infirmary Tab - One Expanded")
@Composable
private fun PreviewInfirmaryTabExpanded() {
    InfirmaryTab(
        medicineIssued = sampleMedicineIssued,
        expandedVisits = setOf(0), // First visit expanded
        onToggleVisit = {}
    )
}

@Preview(showBackground = true, name = "Infirmary Tab - Empty")
@Composable
private fun PreviewInfirmaryTabEmpty() {
    InfirmaryTab(
        medicineIssued = emptyList(),
        expandedVisits = emptySet(),
        onToggleVisit = {}
    )
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Medical Card Tab")
@Composable
private fun PreviewMedicalCardTab() {
    MedicalCardTab(medicalCard = sampleMedicalCard)
}

@Preview(showBackground = true, name = "Medical Card Tab - Empty")
@Composable
private fun PreviewMedicalCardTabEmpty() {
    MedicalCardTab(medicalCard = null)
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Report Card Tab")
@Composable
private fun PreviewReportCardTab() {
    ReportCardTab(
        reportCardDetails = sampleReportCards,
        selectedClassIndex = 0,
        onClassSelected = {},
        onReportCardClicked = {},
        onViewReportCard = { _, _ -> },
        onDownloadReportCard = { _, _ -> }
    )
}

@Preview(showBackground = true, name = "Report Card Tab - Empty")
@Composable
private fun PreviewReportCardTabEmpty() {
    ReportCardTab(
        reportCardDetails = emptyList(),
        selectedClassIndex = 0,
        onClassSelected = {},
        onReportCardClicked = {},
        onViewReportCard = { _, _ -> },
        onDownloadReportCard = { _, _ -> }
    )
}
