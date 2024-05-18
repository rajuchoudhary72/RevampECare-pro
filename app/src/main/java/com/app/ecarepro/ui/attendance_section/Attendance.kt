package com.app.ecarepro.ui.attendance_section

data class Attendance(
    val attDate: String?,
    val dayName: String?,
    val isLate: Boolean,
    val status: Int
) {
    fun isLateStatus(): String {
        return if (isLate) "Yes" else "No"
    }
    fun attendanceStatus(): String {
        when(status){
            1->{
                return  "Present"
            }
            2->{
                return  "Absent"
            }
            6->{

                return  "Holiday"
            }
            7->{
              return  "Week Off"
            }
        }
       return "Unknown"
    }

}