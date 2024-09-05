package com.app.ecarepro.ui.studentProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.DayWiseTimeTableItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.databinding.StudentProfileAttendenceListItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.model.SummaryAttendance
import com.app.ecarepro.model.TimeTable

class StudentProfileAtteListAdapter(
    private var activityLST: List<SummaryAttendance>,
    private var activityCalenderFragment: StudentProfileAttendanceFragment
) :
    RecyclerView.Adapter<StudentProfileAtteListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StudentProfileAttendenceListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StudentProfileAttendenceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val binding  = DataBindingUtil.bind<StudentProfileAttendenceListItemBinding>(holder.itemView)
         binding?.apply {
             binding.atteData = activityLST[position]
             binding.tvMonthName.setOnClickListener {
                 activityCalenderFragment.onItemClick(activityLST[position],0,false)
             }
         }


     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}