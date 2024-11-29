package com.app.ecarepro.ui.assignment.staff

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StaffAssignmentItemBinding
import com.app.ecarepro.model.TeacherAssignment

class StaffAssignmentListAdapter(
    private var activityLST: List<TeacherAssignment>,
    private var activityCalenderFragment: StaffAssignmentsListNavHostFragment,
    val isReportView: Boolean
) :
    RecyclerView.Adapter<StaffAssignmentListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StaffAssignmentItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm = StaffAssignmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        holder.bind(activityLST[position])
    }


   inner class AssignmentListAdapter(val bindingm: StaffAssignmentItemBinding) : RecyclerView.ViewHolder(bindingm.root) {

        fun bind(teacherAssignment: TeacherAssignment) {

            bindingm.assignmentData = teacherAssignment

            if (!teacherAssignment.isMine!!) {
                bindingm.llDelete.isVisible=false
                bindingm.llEdit.isVisible=false
            }

            if (isReportView){
                bindingm.llAssignmentBy.isVisible=true
                bindingm.tvAssignmentBy.text = teacherAssignment.assignmentBy
            }

            bindingm.tvClass.text = teacherAssignment.`class`

            bindingm.llView.setOnClickListener {
                activityCalenderFragment.onItemClick(teacherAssignment,1,false)
            }
            bindingm.llDelete.setOnClickListener {
                activityCalenderFragment.onItemClick(teacherAssignment,3,false)
            }
            bindingm.llEdit.setOnClickListener {
                activityCalenderFragment.onItemClick(teacherAssignment,2,false)
            }
             if (teacherAssignment.isActive!!){
                bindingm.tvStatus.text="  Active"
                bindingm.tvStatus.setTextColor(Color.parseColor(R.color.brand_color.toString()))

            }else{
                bindingm.tvStatus.text="  InActive"
                bindingm.tvStatus.setTextColor(Color.parseColor("#848484"))
            }

        }

    }


}