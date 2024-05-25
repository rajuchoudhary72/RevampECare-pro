package com.app.ecarepro.ui.report.class_report

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ClassAttListItemBinding
import com.app.ecarepro.model.AttReport
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class ClassAttAdapter(private var classSummaryList: List<AttReport>,
                      private var studentAttendanceReportFragment: ClassAttSubFragment
) :
    RecyclerView.Adapter<ClassAttAdapter.StudentAttSummeryViewHolder>() {

        private lateinit var bindingm:   ClassAttListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttSummeryViewHolder {
        bindingm=ClassAttListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentAttSummeryViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = classSummaryList.size

    override fun onBindViewHolder(holder: StudentAttSummeryViewHolder, position: Int) {
        val data= classSummaryList[position]
        with(bindingm) {
            tvName.text=data.name
            Picasso.get()
                .load( data.photo )
                .placeholder(R.drawable.default_profile)
                .networkPolicy(NetworkPolicy.OFFLINE).into(imApproveBy )

            llMain.setOnClickListener {
                studentAttendanceReportFragment.onItemClick(data,1,false)
            }


        }
        when (data.status) {
            0 -> bindingm.tvStatus.text = ""
            1 -> {
                bindingm.tvStatus.text = "P"
                if (data.isLate) {
                    bindingm.tvStatus.background =  ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_pending,null)
                } else {
                    bindingm.tvStatus.background = ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_present,null)
                }
            }  2 -> {
                bindingm.tvStatus.text = "A"
                bindingm.tvStatus.background =  ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.absent_circle2,null)
            }
            3 -> {
                bindingm.tvStatus.text = "L"
                bindingm.tvStatus.background = ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_leave,null)
            }

            4 -> bindingm.tvStatus.text = ""
        }

    }




    class StudentAttSummeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}