package com.app.ecarepro.ui.report.class_report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
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

        private lateinit var binding:   ClassAttListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttSummeryViewHolder {
        binding =ClassAttListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentAttSummeryViewHolder(binding.root)
    }

    override fun getItemCount(): Int = classSummaryList.size

    override fun onBindViewHolder(holder: StudentAttSummeryViewHolder, position: Int) {
        val bindings = DataBindingUtil.bind<ClassAttListItemBinding>(holder.itemView)
        val data= classSummaryList[position]
         bindings?.apply {

                 tvName.text=data.name
                 Picasso.get()
                     .load( data.photo )
                     .placeholder(R.drawable.default_profile)
                     .networkPolicy(NetworkPolicy.OFFLINE).into(imApproveBy )

                 llMain.setOnClickListener {
                     studentAttendanceReportFragment.onItemClick(data,1,false)
                 }



             when (data.status) {
                 0 ->  tvStatus.text = ""
                 1 -> {
                      tvStatus.text = "P"
                     if (data.isLate) {
                          tvStatus.background =  ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_pending,null)
                     } else {
                          tvStatus.background = ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_present,null)
                     }
                 }
                 2 -> {
                       tvStatus.text = "A"
                       tvStatus.background =  ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.absent_circle2,null)
                 }
                 3 -> {
                      tvStatus.text = "L"
                      tvStatus.background = ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_leave,null)
                 }
                 4 ->  {
                     tvStatus.text = "NA"
                     tvStatus.background = ResourcesCompat.getDrawable(studentAttendanceReportFragment.resources,R.drawable.circle_na,null)
                 }
             }
         }

    }




    class StudentAttSummeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}