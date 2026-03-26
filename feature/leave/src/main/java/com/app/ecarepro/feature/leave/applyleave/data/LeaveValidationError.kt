package com.app.ecarepro.feature.leave.applyleave.data

sealed class LeaveValidationError(val message: String) {
    data class StartDateTooFarInFuture(val days: Int) :
        LeaveValidationError("Cannot apply leave beyond $days days from today")

    data class StartDateTooFarInPast(val days: Int) :
        LeaveValidationError("Cannot apply leave before $days days from today")

    object PastDatesNotAllowed :
        LeaveValidationError("Past date leaves are not allowed")

    object EndDateBeforeStartDate :
        LeaveValidationError("End date cannot be before start date")

    data class ExceedsDaysLimit(val limit: Int) :
        LeaveValidationError("You can apply for maximum $limit consecutive days")

    data class InsufficientBalance(val available: Double) :
        LeaveValidationError("Insufficient leave balance. Available: $available")

    data class ApplyAfterHoursRestriction(val hour: Int) :
        LeaveValidationError("Cannot apply leave after $hour:00 hours")

    object MissingAttachment :
        LeaveValidationError("Please attach supporting document")

    object FileTooLarge :
        LeaveValidationError("File size exceeds 10 MB limit")

    object TermsNotAccepted :
        LeaveValidationError("Please accept terms and conditions")

    object InvalidDateRange :
        LeaveValidationError("Please select a valid date range")

    object NoLeaveTypeSelected :
        LeaveValidationError("Please select a leave type")

    object NoReasonSelected :
        LeaveValidationError("Please select a reason")
}
