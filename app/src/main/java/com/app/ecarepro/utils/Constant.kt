package com.app.ecarepro.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Constant {
    companion object {
        const val BASE_URL = "https://api.franciscanecare.net/"
        const val AUTH_BEFORE_LOGIN ="Kq4IYAuSXLh4EsnexoTSfA=="
        const val PDF_Mime_Type = "application/pdf"
        const val WEBVIEW_PDF_BASE_URL = "https://docs.google.com/gview?embedded=true&url="
        const val SMS_USER_PASSWORD = "07Pro2019"
        const val SMS_USER_USER_NAME = "FSPL"
         const val SMS_TOKEN_URL = "http://sms.franciscanecare.com/api/Token/Generate"
         const val SMS_BULK_MSG_URL = "http://sms.franciscanecare.com/api/SMSService/BulkSMS"
        const val BOOK_ID_ARGUMENT = "bookID"
        const val URL_ARGUMENT = "url"
        const val NOTICE_ID_ARGUMENT = "NoticeID"
        const val STUDENT_ID_ARGUMENT = "StudentID"
        const val STAFF_ID_ARGUMENT = "StaffID"
        const val QUES_ID_ARGUMENT = "QuesID"
        const val LEAVE_ID_ARGUMENT = "LeaveID"
        const val CLASS_ID_ARGUMENT = "ClassID"
        const val  ID  = "ID"
        const val LESSON_ID_ARGUMENT = "LessonID"
        const val NAME = "name"
        const val TO = "to"

        const val CIRCULAR_ID = "CircularID"
        const val ASSIGNMENT_ID = "AssiID"
        const val DEFAULT_ID = 0
        const val SUB_ID = 0
        const val MY_CLASS_ID = 1
        const val DEFAULT_ID_CIRCULAR = 1
        const val PAGE_INDEX = 1
        const val THOUGHTS_DIR = 1

        const val TRUE_VALUE = 1
        const val FALSE_VALUE = 0

        const val STUDENT_TYPE = 1
        const val PARENT_TYPE = 2
        const val STAFF_TYPE = 3
        fun getLongTimeDate(sessionStart: String?): Long {
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            try {
                val parse = simpleDateFormat.parse(sessionStart.toString())
                return parse!!.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
        fun dateToShow(inputDateStr:String):String {
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault() )
            val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
             val date: Date? = inputFormat.parse(inputDateStr)
            return  outputFormat.format(date!!)
        }
        fun currentDate():String{
            val c: Date = Calendar.getInstance().time
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return df.format(c)
        }
    }
}