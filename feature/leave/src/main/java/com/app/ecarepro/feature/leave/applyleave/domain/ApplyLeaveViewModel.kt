package com.app.ecarepro.feature.leave.applyleave.domain

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.ApplyLeaveRequest
import com.app.ecarepro.core.domain.model.Holiday
import com.app.ecarepro.core.domain.model.LeaveDetail
import com.app.ecarepro.core.domain.model.LeaveSettingResponse
import com.app.ecarepro.core.domain.repository.AppliedLeavesRepository
import com.app.ecarepro.feature.leave.applyleave.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
open class ApplyLeaveViewModel @Inject constructor(
    private val appliedLeavesRepository: AppliedLeavesRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // Screen Type
    private val _screenType = MutableStateFlow(ApplyLeaveScreenType.STUDENT)
    val screenType: StateFlow<ApplyLeaveScreenType> = _screenType.asStateFlow()

    // Data
    private val _leaveSettings = MutableStateFlow<LeaveSettingResponse?>(null)
    val leaveSettings: StateFlow<LeaveSettingResponse?> = _leaveSettings.asStateFlow()

    // Form Fields
    private val _selectedLeaveType = MutableStateFlow<LeaveDetail?>(null)
    val selectedLeaveType: StateFlow<LeaveDetail?> = _selectedLeaveType.asStateFlow()

    private val _startDate = MutableStateFlow(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(LocalDate.now())
    val endDate: StateFlow<LocalDate> = _endDate.asStateFlow()

    private val _startSession = MutableStateFlow(SessionType.FIRST_HALF)
    val startSession: StateFlow<SessionType> = _startSession.asStateFlow()

    private val _endSession = MutableStateFlow(SessionType.SECOND_HALF)
    val endSession: StateFlow<SessionType> = _endSession.asStateFlow()

    private val _selectedReason = MutableStateFlow<LeaveReason?>(null)
    val selectedReason: StateFlow<LeaveReason?> = _selectedReason.asStateFlow()

    private val _customReason = MutableStateFlow("")
    val customReason: StateFlow<String> = _customReason.asStateFlow()

    private val _isTermsAccepted = MutableStateFlow(false)
    val isTermsAccepted: StateFlow<Boolean> = _isTermsAccepted.asStateFlow()

    // Attachment
    private val _attachmentData = MutableStateFlow<ByteArray?>(null)
    private val _attachmentFileName = MutableStateFlow<String?>(null)
    private val _attachmentFileExtension = MutableStateFlow<String?>(null)
    val attachmentFileName: StateFlow<String?> = _attachmentFileName.asStateFlow()

    // Calculated
    private val _calculatedDuration = MutableStateFlow(0.0)
    val calculatedDuration: StateFlow<Double> = _calculatedDuration.asStateFlow()

    // Validation
    private val _showFieldErrors = MutableStateFlow(false)
    val showFieldErrors: StateFlow<Boolean> = _showFieldErrors.asStateFlow()

    // Messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Date formatter for API
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Computed Properties
    open val leaveTypes: List<LeaveDetail>
        get() = _leaveSettings.value?.leaveDetails ?: emptyList()

    val holidays: List<Holiday>
        get() = _leaveSettings.value?.holidayList?.holiday ?: emptyList()

    val weekOffs: Set<String>
        get() = _leaveSettings.value?.leaveTerms?.weekOffDays ?: emptySet()

    val minSelectableDate: LocalDate
        get() {
            val today = LocalDate.now()
            val terms = _leaveSettings.value?.leaveTerms ?: return today

            val backward = terms.backwardDays
            if (terms.isPrevDatesAllow == true && backward != null) {
                return today.minusDays(backward.toLong())
            }
            return today
        }

    val maxSelectableDate: LocalDate
        get() {
            val today = LocalDate.now()
            val forwardDays = _leaveSettings.value?.leaveTerms?.forwardDays ?: 30
            return today.plusDays(forwardDays.toLong())
        }

    open val isAttachmentRequired: Boolean
        get() = _selectedLeaveType.value?.attachmentMandatory ?: false

    open val termsAndConditions: String
        get() = _leaveSettings.value?.termCondition?.rules ?: ""

    val leaveBalance: LeaveBalancePresentation?
        get() = _selectedLeaveType.value?.let { LeaveBalancePresentation.from(it) }

    // Validation error messages
    open val leaveTypeError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().filterIsInstance<LeaveValidationError.NoLeaveTypeSelected>()
                .firstOrNull()?.message
        } else null

    open val reasonError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().filterIsInstance<LeaveValidationError.NoReasonSelected>()
                .firstOrNull()?.message
        } else null

    open val attachmentError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().find {
                it is LeaveValidationError.MissingAttachment ||
                        it is LeaveValidationError.FileTooLarge
            }?.message
        } else null

    open val termsError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().filterIsInstance<LeaveValidationError.TermsNotAccepted>()
                .firstOrNull()?.message
        } else null

    open val dateError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().find {
                it is LeaveValidationError.EndDateBeforeStartDate ||
                        it is LeaveValidationError.StartDateTooFarInFuture ||
                        it is LeaveValidationError.StartDateTooFarInPast ||
                        it is LeaveValidationError.PastDatesNotAllowed
            }?.message
        } else null

    open val balanceError: String?
        get() = if (_showFieldErrors.value) {
            validateForm().find {
                it is LeaveValidationError.InsufficientBalance ||
                        it is LeaveValidationError.ExceedsDaysLimit ||
                        it is LeaveValidationError.ApplyAfterHoursRestriction
            }?.message
        } else null

    fun initialize(screenType: ApplyLeaveScreenType) {
        _screenType.value = screenType
        loadSettings()
    }

    // Load settings
    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            appliedLeavesRepository.getLeaveSettings().collect { result ->
                result
                    .onSuccess { response ->
                        _leaveSettings.value = response
                        _uiState.value = UIState.Loaded
                        recalculateDuration()
                    }
                    .onFailure { error ->
                        _errorMessage.value = "Failed to load leave settings: ${error.message}"
                        _uiState.value = UIState.Error
                    }
            }
        }
    }

    // Calculate duration
    fun recalculateDuration() {
        val sandwichEnabled = _selectedLeaveType.value?.sandwichEnable ?: true

        _calculatedDuration.value = LeaveDurationCalculator.calculateDuration(
            fromDate = _startDate.value,
            tillDate = _endDate.value,
            startSession = _startSession.value,
            endSession = _endSession.value,
            weekOffs = weekOffs,
            holidays = holidays,
            sandwichEnabled = sandwichEnabled,
            isStaff = _screenType.value == ApplyLeaveScreenType.STAFF
        )
    }

    // Validation
    private fun validateForm(): List<LeaveValidationError> {
        val errors = mutableListOf<LeaveValidationError>()
        val today = LocalDate.now()

        // Check leave type (staff only)
        if (_screenType.value == ApplyLeaveScreenType.STAFF && _selectedLeaveType.value == null) {
            errors.add(LeaveValidationError.NoLeaveTypeSelected)
        }

        // Check reason
        if (_selectedReason.value == null && _customReason.value.isBlank()) {
            errors.add(LeaveValidationError.NoReasonSelected)
        }

        // Check date range
        if (_endDate.value < _startDate.value) {
            errors.add(LeaveValidationError.EndDateBeforeStartDate)
        }

        // Check forward days
        _leaveSettings.value?.leaveTerms?.let { terms ->
            terms.forwardDays?.let { forwardDays ->
                val maxDate = today.plusDays(forwardDays.toLong())
                if (_startDate.value > maxDate) {
                    errors.add(LeaveValidationError.StartDateTooFarInFuture(forwardDays))
                }
            }

            // Check backward days
            if (terms.isPrevDatesAllow == false && _startDate.value < today) {
                errors.add(LeaveValidationError.PastDatesNotAllowed)
            } else if (terms.isPrevDatesAllow == true) {
                terms.backwardDays?.let { backwardDays ->
                    val minDate = today.minusDays(backwardDays.toLong())
                    if (_startDate.value < minDate) {
                        errors.add(LeaveValidationError.StartDateTooFarInPast(backwardDays))
                    }
                }
            }

            // Check days limit
            terms.daysLimit?.let { daysLimit ->
                if (_calculatedDuration.value > daysLimit) {
                    errors.add(LeaveValidationError.ExceedsDaysLimit(daysLimit))
                }
            }
        }

        // Check leave balance (staff only)
        if (_screenType.value == ApplyLeaveScreenType.STAFF) {
            _selectedLeaveType.value?.let { leaveType ->
                val available = leaveType.availableBalance
                if (_calculatedDuration.value > available) {
                    errors.add(LeaveValidationError.InsufficientBalance(available))
                }

                // Check max acceptable limit
                leaveType.maxAcceptableLimit?.let { maxLimit ->
                    if (maxLimit > 0 && _calculatedDuration.value > maxLimit) {
                        errors.add(LeaveValidationError.ExceedsDaysLimit(maxLimit.toInt()))
                    }
                }

                // Check apply before hours
                leaveType.applyBeforeHours?.let { applyBeforeHours ->
                    if (applyBeforeHours > 0) {
                        val currentHour = LocalTime.now().hour
                        if (currentHour >= applyBeforeHours) {
                            errors.add(LeaveValidationError.ApplyAfterHoursRestriction(applyBeforeHours))
                        }
                    }
                }
            }
        }

        // Check attachment
        if (isAttachmentRequired && _attachmentData.value == null) {
            errors.add(LeaveValidationError.MissingAttachment)
        }

        // Check file size (10 MB)
        _attachmentData.value?.let { data ->
            if (data.size > 10 * 1024 * 1024) {
                errors.add(LeaveValidationError.FileTooLarge)
            }
        }

        // Check terms
        if (!_isTermsAccepted.value) {
            errors.add(LeaveValidationError.TermsNotAccepted)
        }

        return errors
    }

    // Submit leave request
    fun submitLeaveRequest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _showFieldErrors.value = true
            val errors = validateForm()

            if (errors.isNotEmpty()) {
                _errorMessage.value = errors.first().message
                return@launch
            }

            _uiState.value = UIState.SilentLoading

            // Prepare attachment as Base64 if present
            val attachmentBase64 = _attachmentData.value?.let {
                Base64.encodeToString(it, Base64.NO_WRAP)
            }

            // Get reason text
            val reasonText = _selectedReason.value?.name ?: _customReason.value

            // Create request
            val request = ApplyLeaveRequest(
                leaveID = _selectedLeaveType.value?.leaveID ?: 1,
                fromDate = _startDate.value.format(apiDateFormatter),
                tillDate = _endDate.value.format(apiDateFormatter),
                duration = _calculatedDuration.value,
                reason = reasonText,
                attachment = attachmentBase64,
                attachmentExt = _attachmentFileExtension.value
            )

            appliedLeavesRepository.applyLeave(request).collect { result ->
                result.onSuccess { response ->
                    if (response.isSuccess) {
                        _successMessage.value = response.message.ifEmpty { "Leave request submitted successfully" }
                        _uiState.value = UIState.Loaded
                        delay(1500)
                        onSuccess()
                    } else {
                        _errorMessage.value = response.message.ifEmpty { "Failed to submit leave request" }
                        _uiState.value = UIState.Loaded
                    }
                }
                result.onFailure { error ->
                    _errorMessage.value = "Failed to submit leave request: ${error.message}"
                    _uiState.value = UIState.Loaded
                }
            }
        }
    }

    // Form actions
    fun onLeaveTypeSelected(leaveType: LeaveDetail?) {
        _selectedLeaveType.value = leaveType
        recalculateDuration()
    }

    fun onStartDateChanged(date: LocalDate) {
        _startDate.value = date
        if (_endDate.value < date) {
            _endDate.value = date
        }
        recalculateDuration()
    }

    fun onEndDateChanged(date: LocalDate) {
        _endDate.value = date
        recalculateDuration()
    }

    fun onStartSessionChanged(session: SessionType) {
        _startSession.value = session
        recalculateDuration()
    }

    fun onEndSessionChanged(session: SessionType) {
        _endSession.value = session
        recalculateDuration()
    }

    fun onReasonSelected(reason: LeaveReason?) {
        _selectedReason.value = reason
        if (reason != null) {
            _customReason.value = ""
        }
    }

    fun onCustomReasonChanged(reason: String) {
        _customReason.value = reason
        if (reason.isNotBlank()) {
            _selectedReason.value = null
        }
    }

    fun handleSelectedFile(data: ByteArray, fileName: String) {
        _attachmentData.value = data
        _attachmentFileName.value = fileName
        _attachmentFileExtension.value = fileName.substringAfterLast(".", "")
    }

    fun removeAttachment() {
        _attachmentData.value = null
        _attachmentFileName.value = null
        _attachmentFileExtension.value = null
    }

    fun toggleTermsAccepted() {
        _isTermsAccepted.value = !_isTermsAccepted.value
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

sealed class UIState {
    object NotInitialized : UIState()
    object Loading : UIState()
    object SilentLoading : UIState()
    object Loaded : UIState()
    object Error : UIState()

    val shouldShowLoading: Boolean
        get() = this is Loading
}
