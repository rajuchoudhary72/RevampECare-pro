package com.app.ecarepro.ui.firebaseAnalytics

object AnalyticsConstants {

    // Event Names
    object Events {
        const val FAQ_CLICK: String = "faq_click"
        const val ESTIMATE_COLLECTION_FILTER: String = "estimate_collection_filter"
        const val DAILY_MODE_WISE_FILTER: String = "daily_mode_wise_filter"
        const val TEACHER_WORKLOAD: String = "teacher_workload"
        const val GLOBAL_SEARCH: String = "global_Search"
        const val TEACHER_BIRTHDAY: String = "teacher_birthday"
        const val QUESTIONNAIRE: String = "questionnaire"
        const val SWITCH_ACCOUNT: String = "switch_account"
        const val SCREEN_VIEW = "screen_view"
        const val BUTTON_CLICK = "button_click"
        const val API_ERROR = "api_error"
        const val LOGIN_SUCCESS = "login_success"
        const val SIGNUP_COMPLETE = "signup_complete"
        const val PURCHASE_MADE = "purchase_made"
        const val LOGIN = "login"
        const val LOGOUT = "logout"
        const val VALIDATE_SCHOOL_CODE = "validate_school_code"
        const val FORGOT_PASSWORD = "forgot_password"
        const val SYNC_SUCCESS = "sync_success"
        const val PROFILE_PHOTO_UPDATED = "profile_photo_updated"
        const val CHANGE_USER_NAME_DETAIL = "change_userName_detail"
        const val CHANGE_USER_PASSWORD_DETAIL = "change_userPassword_detail"
        const val RATE_US = "rate_us"
        const val SEND_SMS_APP_MESSAGE = "send_sms_app_message"
        const val ONLY_APP_MESSAGE = "only_app_message"
    }

    // Attribute Keys
    object Attributes {
        const val URL: String = "url"
        const val SEARCH_TYPE: String = "search_type"
        const val R_TYPE: String = "r_type"
        const val U_TYPE: String = "u_type"
        const val BIRTH_DATE: String = "birth_date"
        const val BIRTH_MONTH: String = "birth_month"
        const val DATE: String = "date"
        const val TO_DATE: String = "to_date"
        const val FROM_DATE: String = "from_date"
        const val FEE_TYPE_ID: String = "fee_type_id"

        const val NEW_USER_NAME: String = "new_user_name"
        const val NEW_USER_ID: String = "new_user_id"
        const val NEW_USER_TYPE: String = "new_user_type"
        const val NEW_SCHOOL_CODE: String = "new_school_code"

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
        const val OLD_USER_NAME = "old_user_name"
        const val OLD_PASSWORD = "old_password"
        const val NEW_PASSWORD = "new_password"
        const val USER_NAME = "user_name"
        const val TEACHER_ID = "teacher_id"
        const val SCHOOL_CODE = "school_code"
        const val SIGN_IN_TYPE = "sign_in_type"
        const val USER_TYPE = "user_type"
        const val RCV_ON = "rcv_on"
        const val PROFILE_PHOTO_TYPE = "profile_photo_type"
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
        const val NORMAL_LOGIN = "Normal Login"
        const val ADD_ACCOUNT = "Add Account"
    }

    object Screens {
        const val SCHOOL_CODE = "SchoolCode"
        const val HOME_SCREEN = "HomeScreen"
        const val USER_PROFILE = "UserProfile"
        const val NOTIFICATION_LIST = "NotificationList"
        const val INBOX_MESSAGE_LIST = "InboxMessageList"
        const val SENT_MESSAGE_LIST = "SentMessageList"
        const val MESSAGE_LIST = "MessageList"
        const val GLOBAL_SEARCH = "GlobalSearch"
        const val DASH_BOARD_SCREEN = "DashBoardScreen"
        const val CHANGE_USER_NAME = "ChangeUserName"
        const val HELP_SCREEN = "HelpScreen"
        const val CHANGE_USER_PASSWORD = "ChangeUserPassword"
        const val STUDENT_PROFILE_LIST = "StudentProfileList"
        const val STUDENT_DETAIL_SCREEN = "StudentDetailScreen"
        const val STUDENT_REPORT_CARD = "StudentReportCard"
        const val ATTENDANCE_TAB = "AttendanceTab"
        const val FEEDS_TAB = "FeedsTab"

    }
}