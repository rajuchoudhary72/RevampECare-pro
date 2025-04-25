package com.app.ecarepro.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.text.format


class LMSConstant {
    companion object {
         const val LMS_BASE_URL = "https://lmsapiuat.franciscanecare.net/"
        const val LAYOUT_API = "Workspace/Layout"
        const val SKILL_CATEGORY_API = "Skills/Categories"
        const val SKILL_ALL = "Skills/All"
        const val SKILL_DELETE_SKILL = "Skills/DeleteSkill"
        const val SKILL_SKILL_TYPE = "Skills/Types"
        const val SKILL_SKILL_SAVE = "Skills/SaveSkill"
    }
}