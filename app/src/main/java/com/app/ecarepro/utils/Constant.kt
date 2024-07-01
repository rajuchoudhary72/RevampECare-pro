package com.app.ecarepro.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Constant {
    companion object {
        const val DEVICE_TYPE: Int= 1
        const val BASE_URL = "https://api.franciscanecare.net/"
        const val BASE_URL_COM = "https://app.franciscanecare.com"
        const val AUTH_BEFORE_LOGIN_NEW ="Kq4IYAuSXLh4EsnexoTSfA=="
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
        const val  ID  = "ID"
        const val LESSON_ID_ARGUMENT = "LessonID"
        const val NAME = "name"
        const val TO = "to"
        const val NOTICE_TYPE = "notice_type"
        const val NOTICE_CLASS = "notice_class"
        const val NOTICE_SCHOOL = "notice_school"
        const val USER_TYPE = "user_type"
        const val USER_STAFF = "user_staff"
        const val USER_PARENT_STUDENT = "user_parent_student"

        const val url = "url"

        const val GALLERY_TYPE = "galleryType"
        const val GALLERY_TYPE_PHOTO = 1
        const val GALLERY_TYPE_VIDEO = 2
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



        const val CIRCULAR_ID = "CircularID"
        const val ASSIGNMENT_ID = "AssiID"
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
        const val DROP_CONFORM=5

        const val UP_TRIP= 1
        const val DOWN_TRIP= 2
        const val DROP_STUDENT_TRIP= 3

        const val DOWNLOAD= 1
        const val EDIT= 2
        const val DELETE= 3

        const val TRUE_VALUE = 1
        const val FALSE_VALUE = 0

        const val STUDENT_TYPE = 1
        const val PARENT_TYPE = 2
        const val STAFF_TYPE = 3

        const val APPOINTMENT_PENDING = 0
        const val APPOINTMENT_APPROVE = 1
        const val APPOINTMENT_REJECT = 2

        const val PRINCIPAL =  "Principal"
        const val MANAGEMENT =  "Management"



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

        fun dateToShowCon(inputDateStr:String):String {
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault() )
            val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
            val date: Date? = inputFormat.parse(inputDateStr)
            return  outputFormat.format(date!!)
        }



        fun currentDate():String{
            val c: Date = Calendar.getInstance().time
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return df.format(c)
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
    }
}