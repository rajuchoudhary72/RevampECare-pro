package com.app.ecarepro.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Constant {
    companion object {
        val LEAVE_ACTION_FORWARD: Int =4
        const val DEVICE_TYPE: Int = 1
        /*old base url*/
   //    const val BASE_URL = "https://api.franciscanecare.net/"
        /*New base url*/
        const val BASE_URL = "https://apiuat.franciscanecare.net/"
        const val BASE_URL_COM = "https://app.franciscanecare.com"
        const val AUTH_BEFORE_LOGIN_NEW = "Kq4IYAuSXLh4EsnexoTSfA=="
        const val PDF_Mime_Type = "application/pdf"
        const val WEBVIEW_PDF_BASE_URL = "https://docs.google.com/gview?embedded=true&url="
        const val SMS_USER_PASSWORD = "07Pro2019"
        const val SMS_USER_USER_NAME = "FSPL"
        const val SMS_TOKEN_URL = "http://sms.franciscanecare.com/api/Token/Generate"
        const val SMS_BULK_MSG_URL = "http://sms.franciscanecare.com/api/SMSService/BulkSMS"
        const val APPOINTMENT_BASEURL = "https://fomapi.franciscanecare.com/api/Appointment/"
        const val APPROVE_APPOINTMENT_URL = "approveappointment/"
        const val REJECT_APPOINTMENT_URL = "rejectappointment/"
        const val BOOK_ID_ARGUMENT = "bookID"
        const val URL_ARGUMENT = "url"
        const val FULL_URL_ARGUMENT = "full_url"
        const val NOTICE_ID_ARGUMENT = "NoticeID"
        const val STUDENT_ID_ARGUMENT = "StudentID"
        const val STAFF_ID_ARGUMENT = "StaffID"
        const val TIME_TABLE_TYPE = "TimeTableType"
        const val ASSIGNMENT_TYPE = "AssignmentType"
        const val CLASS_ASSIGNMENT = "ClassType"
        const val TEACHER_ASSIGNMENT = "TeacherType"
        const val TEACHER_TIME_TABLE = "TeacherTimeTable"
        const val CLASS_TIME_TABLE = "ClassTimeTable"
        const val QUES_ID_ARGUMENT = "QuesID"
        const val LEAVE_ID_ARGUMENT = "LeaveID"
        const val CLASS_ID_ARGUMENT = "ClassID"
        const val ID = "ID"
        const val LESSON_ID_ARGUMENT = "LessonID"
        const val NAME = "name"
        const val TO = "to"
        const val NOTICE_TYPE = "notice_type"
        const val NOTICE_CLASS = "notice_class"
        const val NOTICE_SCHOOL = "notice_school"
        const val USER_TYPE = "user_type"
        const val USER_STAFF = "user_staff"
        const val USER_TEACHER = "user_teacher"
        const val USER_PARENT_STUDENT = "user_parent_student"

        const val url = "url"

        const val GALLERY_TYPE = "galleryType"
        const val GALLERY_TYPE_PHOTO = 1
        const val GALLERY_TYPE_VIDEO = 2
        const val CLASS_WISE = 1
        const val SECTION_WISE = 2
        const val GALLERY_ACTION_TYPE = "galleryActionType"
        const val GALLERY_ACTION_ADD = "add"
        const val GALLERY_ACTION_REMOVE = "remove"


        const val TODAY = 1
        const val UP_COMING = 2
        const val DATE_RANGE = 3
        const val CHECK_IN = 4
        const val CHECK_OUT = 5
        const val APPROVE = 3
        const val REJECT = 4

        const val FROM = "from"
        const val LEAVE_ACTION_APPROVE = 1
        const val LEAVE_ACTION_REJECT = 2

        const val PROFILE_FRA_STU = "ProfileFragmentStudent"
        const val PROFILE_FRA_STAFF = "ProfileFragmentStaff"
        const val FRA_LEAVE = "FragmentLeave"
        const val FRA_ASSI = "FragmentAssignment"
        const val FRA_TIMETABLE = "FragmentTimeTable"
        const val FRA_ADD_APPRE = "FragmentAddApprecation"
        const val FRA_VIEW_APPRE = "FragmentViewApprecation"
        const val FRA_VIEW_INFE = "FragmentViewInfe"
        const val FRA_ADD_INFE = "FragmentAddInfe"
        const val FRA_LESSON_PLAN = "FragmentLessonPlan"
        const val FRA_STAFF_LEAVE = "FragmentStaffLeave"
        const val FRA_STU_LEAVE = "FragmentStudentLeave"
        const val DATE = "DATE"

        const val SHARED_PREF_NAME_PROMPT = "SHARED_PREF_NAME_PROMPT"
        const val SHARED_PREF_SHOW_PROMPT = "SHARED_PREF_SHOW_PROMPT"

        const val CIRCULAR_ID = "CircularID"
        const val ASSIGNMENT_ID = "AssiID"
        const val IS_LATE_SUBMITTED = "isLateSubmitted"
        const val DEFAULT_ID = 0
        const val SUB_ID = 0
        const val MY_CLASS_ID = 1
        const val DEFAULT_ID_CIRCULAR = 1
        const val PAGE_INDEX = 1
        const val THOUGHTS_DIR = 1


        const val FILTER_NAME = 0
        const val FILTER_ROLL_NO = 1
        const val FILTER_ADMISSION_NO = 2


        const val PRESENT = 1
        const val ABSENT = 0
        const val OP = 2
        const val DROP_CONFORM = 5

        const val UP_TRIP = 1
        const val DOWN_TRIP = 2
        const val DROP_STUDENT_TRIP = 3

        const val DOWNLOAD = 1
        const val EDIT = 2
        const val DELETE = 3

        const val TRUE_VALUE = 1
        const val FALSE_VALUE = 0

        const val STUDENT_TYPE = 1
        const val PARENT_TYPE = 2
        const val STAFF_TYPE = 3

        const val APPOINTMENT_PENDING = 0
        const val APPOINTMENT_APPROVE = 1
        const val APPOINTMENT_REJECT = 2

        const val PRINCIPAL = "Principal"
        const val MANAGEMENT = "Management"



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

        fun strToApiDate(dt: String): String {
            val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
            try {
                val parse = simpleDateFormat.parse(dt)
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                return formatter.format(parse)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dt
        }
        fun dateToShow(inputDateStr: String): String {
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date: Date? = inputFormat.parse(inputDateStr)
            return outputFormat.format(date!!)
        }

        fun toSystemDate(inputDateStr: String): String {
            val inputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date: Date? = inputFormat.parse(inputDateStr)
            return outputFormat.format(date!!)
        }

        fun dateToShowConn(inputDateStr: String): String {
            val inputFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date: Date? = inputFormat.parse(inputDateStr)
            return outputFormat.format(date!!)
        }


        fun getDateDiff(dateString1: String?, dateString2: String?): Double {
            var diff = 0.0
            val df1: DateFormat = SimpleDateFormat("dd MMM yyyy")
            var date1: Date? = null
            var date2: Date? = null
            try {
                date1 = df1.parse(dateString1)
                date2 = df1.parse(dateString2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal1 = Calendar.getInstance()
            cal1.time = date1
            val cal2 = Calendar.getInstance()
            cal2.time = date2
            while (!cal1.after(cal2)) {
                val dayOfWeek = cal1[Calendar.DAY_OF_WEEK]
                if (dayOfWeek == Calendar.SUNDAY) {
                    cal1.add(Calendar.DATE, 1)
                } else {
                    diff++
                    cal1.add(Calendar.DATE, 1)
                }
            }
            return diff
        }


        fun isDateInBetweenIncludingEndPoints(
            start_date: String?,
            end_date: String?,
            holiDay: String?,
        ): Boolean {
            var result = false

            try {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val start = sdf.parse(start_date)
                val end = sdf.parse(end_date)
                val holiday = sdf.parse(holiDay)
                checkNotNull(start)
                checkNotNull(end)
                checkNotNull(holiday)

                if (holiday.before(end) && holiday.after(start)) {
                    result = true
                } else if (holiday == end || holiday == start) {
                    result = true
                }

                return result
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return false
        }

        fun holidayLastDateGreaterSelectLastDate(
            holiday_date: String?,
            selected_day: String?,
        ): Boolean {
            var result = false

            try {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val holi_day = sdf.parse(holiday_date)
                val sele_day = sdf.parse(selected_day)
                checkNotNull(holi_day)
                checkNotNull(sele_day)

                if (holi_day.after(sele_day)) {
                    result = true
                }

                return result
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return false
        }

        fun currentDate(): String {
            val c: Date = Calendar.getInstance().time
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return df.format(c)
        }

//        fun getCurrentDateTime():String{
//            val c: Date = Calendar.getInstance().time
//            val df = SimpleDateFormat("dd-MMM-yy HH:mm", Locale.getDefault())
//            return df.format(c)
//        }

        fun getCurrentDateTime(): String {
            val calendar = Calendar.getInstance()
            val currentDate = calendar.time
            val dateFormat = SimpleDateFormat("dd-MMM-yy HH:mm", Locale.getDefault())
            return dateFormat.format(currentDate)
        }

        fun getCurrentDateTimeSecond(): String {
            val dateFormat = SimpleDateFormat("dd-MMM-yy HH:mm", Locale.ENGLISH)
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }
        fun getCurrentimeSecond(): String {
            val dateFormat = SimpleDateFormat("dd-MMM-yy HH:mm:ss", Locale.ENGLISH)
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        /* fun incrementDateByDay( noOfDays:Int):Long{
             val c = Calendar.getInstance()
             c.time = Calendar.getInstance().time
             c.add(Calendar.DATE, noOfDays)
              return  getLongTimeDate(df.format(c))
         }*/

        /* fun incrementDateByDay( noOfDays:Int): Date? {
             val c = Calendar.getInstance()
             c.time = Calendar.getInstance().time
             val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
             df.format(c)
             c.add(Calendar.DATE, noOfDays)
             return c.time
         }*/


        fun boldFindStartIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var startIndex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    val start = word[0].toString()
                    if (start == "*") {
                        if (firstTime) {
                            startIndex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            startIndex = sentence.indexOf(word, startIndex + 1)
                        }
                        indexes.add(startIndex)
                    }
                }
            }

            return indexes
        }

        fun boldFindEndStarIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var firstindex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    if (word.endsWith("*")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 1
                        indexes.add(endIndex)
                    } else if (word.endsWith("*,")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    } else if (word.endsWith("*.")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    }
                }
            }

            return indexes
        }

        fun italicFindStartIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var startIndex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    val start = word[0].toString()
                    if (start == "_") {
                        if (firstTime) {
                            startIndex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            startIndex = sentence.indexOf(word, startIndex + 1)
                        }
                        indexes.add(startIndex)
                    }
                }
            }

            return indexes
        }

        fun italicFindEndStarIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var firstindex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    if (word.endsWith("_")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 1
                        indexes.add(endIndex)
                    } else if (word.endsWith("_,")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    } else if (word.endsWith("_.")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    }
                }
            }

            return indexes
        }

        fun strikethroughFindEndStarIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var firstindex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    if (word.endsWith("~")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 1
                        indexes.add(endIndex)
                    } else if (word.endsWith("~,")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    } else if (word.endsWith("~.")) {
                        if (firstTime) {
                            firstindex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            firstindex = sentence.indexOf(word, firstindex + 1)
                        }
                        val worlem = word.length
                        val endIndex = firstindex + worlem - 2
                        indexes.add(endIndex)
                    }
                }
            }

            return indexes
        }

        fun strikethroughFindStartIndexes(sentence: String): List<Int> {
            val indexes: MutableList<Int> = java.util.ArrayList()
            val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var startIndex = 0
            var firstTime = true
            for (word in words) {
                if (word.length >= 2) {
                    val start = word[0].toString()
                    if (start == "~") {
                        if (firstTime) {
                            startIndex = sentence.indexOf(word)
                            firstTime = false
                        } else {
                            startIndex = sentence.indexOf(word, startIndex + 1)
                        }
                        indexes.add(startIndex)
                    }
                }
            }

            return indexes
        }

        fun isPdfUrl(url: String): Int {

            val extension = url.substringAfterLast(".", "").lowercase()

            return when(extension){
                "pdf" -> 1
                "jpg" -> 2
                "docx" -> 3
                else -> 2
            }


        }

        fun checkApiResponse(errorCode: Int, context: Context): Boolean {
            if (errorCode==1) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                return true
            } else {
                return false
            }
        }

          fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }


    }






}