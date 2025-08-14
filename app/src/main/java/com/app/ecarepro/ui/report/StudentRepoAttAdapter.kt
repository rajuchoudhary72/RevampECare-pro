package com.app.ecarepro.ui.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AttSummeryListItemBinding
import com.app.ecarepro.model.ClassSummary

class StudentRepoAttAdapter(
    private var classSummaryList: List<ClassSummary>,
    private var studentAttendanceReportFragment: StudentAttendanceSummeryFragment,
    private var lateEnabled: Boolean
) :
    RecyclerView.Adapter<StudentRepoAttAdapter.StudentAttSummeryViewHolder>() {

        private lateinit var bindingm:   AttSummeryListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttSummeryViewHolder {
        bindingm=AttSummeryListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentAttSummeryViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = classSummaryList.size

    override fun onBindViewHolder(holder: StudentAttSummeryViewHolder, position: Int) {
        val data= classSummaryList[position]
        with(bindingm) {
            tvClassName.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_classes_pun))
                append(data.className)
            }
            tvTotalStudentCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.total_students_pun))
                append(data.present+data.absent+data.leave+data.wh+data.na)
            }
            tvTotalPresentWorkingCount.text= buildString {
                append("Total Present : ")
                append(data.totalPresent)
            }
            tvTotalAbsentCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_absent_pun))
                append(data.absent)
            }
            tvTotalLateCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_late_pun))
                append(data.late)
            }
            tvTotalLateCount.isVisible=lateEnabled
            tvTotalLeaveCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_leave_pun))
                append(data.leave)
            }
            tvTotalPresentCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_present_pun))
                append(data.present)
            }
            tvTotalNaCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_na_pun))
                append(data.na)
            }
            tvTotalWhCount.text= buildString {
                append(studentAttendanceReportFragment.getString(R.string.general_wh_pun))
                append(data.wh)
            }

            cvMain.setOnClickListener {
                studentAttendanceReportFragment.onItemClick(data,1,false)
//                if (data.present+data.absent+data.late+data.leave+data.present>0){
//                    studentAttendanceReportFragment.onItemClick(data,1,false)
//
//                }
             }
        }



    }




    class StudentAttSummeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}