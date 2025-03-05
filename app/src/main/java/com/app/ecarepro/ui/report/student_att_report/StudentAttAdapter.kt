package com.app.ecarepro.ui.report.student_att_report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ClassAttListItemBinding
import com.app.ecarepro.databinding.StudentAttRepoItemBinding
import com.app.ecarepro.model.StuAttendanceRepo
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class StudentAttAdapter(private var stuAttendanceRepos: List<StuAttendanceRepo>,
                        private var attendanceReportFragment: StudentAttRepoFragment
) :
    RecyclerView.Adapter<StudentAttAdapter.StudentAttSummeryViewHolder>() {

        private lateinit var bindingm:   StudentAttRepoItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttSummeryViewHolder {
        bindingm=StudentAttRepoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentAttSummeryViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = stuAttendanceRepos.size

    override fun onBindViewHolder(holder: StudentAttSummeryViewHolder, position: Int) {
        val data= stuAttendanceRepos[position]
        with(bindingm) {
            tvDate.text=data.attDate
            tvDay.text=data.dayName

        }
        when (data.status) {
            0 -> bindingm.tvStatus.text = ""
            1 -> {
                bindingm.tvStatus.text = "P"
                if (data.isLate) {
                    bindingm.tvStatus.background =  ResourcesCompat.getDrawable(attendanceReportFragment.resources,R.drawable.circle_pending,null)
                } else {
                    bindingm.tvStatus.background = ResourcesCompat.getDrawable(attendanceReportFragment.resources,R.drawable.circle_present,null)
                }
            }  2 -> {
                bindingm.tvStatus.text = "A"
                bindingm.tvStatus.background =  ResourcesCompat.getDrawable(attendanceReportFragment.resources,R.drawable.absent_circle2,null)
            }
            3 -> {
                bindingm.tvStatus.text = "L"
                bindingm.tvStatus.background = ResourcesCompat.getDrawable(attendanceReportFragment.resources,R.drawable.circle_leave,null)
            }
            7-> {
                bindingm.tvStatus.text = "WH"
                bindingm.tvStatus.background = ResourcesCompat.getDrawable(attendanceReportFragment.resources,R.drawable.circle_wh,null)
            }

            4 -> bindingm.tvStatus.text = ""
        }

    }




    class StudentAttSummeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}