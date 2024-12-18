package com.app.ecarepro.ui.firebaseAnalytics

object AnalyticsConstants {

    // Event Names
    object Events {
        const val SCREEN_VIEW = "screen_view"
        const val BUTTON_CLICK = "button_click"
        const val API_ERROR = "api_error"
        const val LOGIN_SUCCESS = "login_success"
        const val SIGNUP_COMPLETE = "signup_complete"
        const val LOGIN = "login"
        const val LOGOUT = "logout"
        const val VALIDATE_SCHOOL_CODE = "validate_school_code"
        const val FORGOT_PASSWORD = "forgot_password"
    }

    // Attribute Keys
    object Attributes {
        // Contextual Attributes
        const val SCREEN_NAME = "screen_name"
        const val SCREEN_ID = "screen_id"
        const val PREVIOUS_SCREEN = "previous_screen"
        const val SCREEN_LOAD_TIME = "screen_load_time_ms"
        const val ACTIVE_TAB = "active_tab"
        const val SEARCH_QUERY = "search_query"
        const val APPLIED_FILTERS = "applied_filters"

        // User-Specific Attributes
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val SCHOOL_CODE = "school_code"
        const val USER_TYPE = "user_type"
        const val RCV_ON = "rcv_on"
        const val SESSION_ID = "session_id"
        const val AGE_GROUP = "age_group"
        const val GENDER = "gender"
        const val SUBSCRIPTION_STATUS = "subscription_status"

        // Device-Specific Attributes
        const val DEVICE_MODEL = "device_model"
        const val OS_VERSION = "os_version"
        const val APP_VERSION = "app_version"
        const val NETWORK_TYPE = "network_type"
        const val BATTERY_LEVEL = "battery_level"

        // Crash and Debug Attributes
        const val SCREEN_STATE = "screen_state"
        const val ERROR_MESSAGE = "error_message"
        const val CRASH_STACK_TRACE = "crash_stack_trace"
        const val API_STATUS_CODE = "api_status_code"
        const val API_RESPONSE_TIME = "api_response_time_ms"

        // User Journey Attributes
        const val SCREEN_DURATION = "screen_duration_seconds"
        const val NAVIGATION_TYPE = "navigation_type"
        const val EXPERIMENT_ID = "experiment_id"
        const val VARIANT_ID = "variant_id"

        // Interaction Attributes
        const val CTA_NAME = "cta_name"
        const val CTA_VALUE = "cta_value"
    }

    object Screens {
        const val USER_PROFILE = "UserProfile"
        const val NOTIFICATION_LIST = "NotificationList"
        const val MESSAGE_LIST = "MessageList"
        const val GLOBAL_SEARCH = "GlobalSearch"
        const val DASH_BOARD_SCREEN = "DashBoardScreen"
        const val CHANGE_USER_NAME = "ChangeUserName"
        const val CHANGE_USER_PASSWORD = "ChangeUserPassword"
        const val SCHOOL_CODE = "SchoolCode"
        const val HOME_SCREEN = "HomeScreen"

        const val STUDENT_PROFILE_LIST = "StudentProfileList"
        const val STUDENT_DETAIL_SCREEN = "StudentDetailScreen"
        const val STUDENT_ATTENDANCE_SCREEN = "StudentAttendanceMonthWise"
        const val STUDENT_ACADEMIC_SCREEN = "StudentAcademicStatus"
        const val STUDENT_FEE_SCREEN = "StudentFeeStatus"

        const val STUDENT_REPORT_CARD = "StudentReportCard"

    }
}