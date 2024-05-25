package com.app.ecarepro.ui.assignment.staff

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StaffAssignmentItemBinding
import com.app.ecarepro.model.TeacherAssignment

class StaffAssignmentListAdapter(
    private var activityLST: List<TeacherAssignment>,
    private var activityCalenderFragment: StaffAssignmentsListFragment
) :
    RecyclerView.Adapter<StaffAssignmentListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StaffAssignmentItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StaffAssignmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.assignmentData = activityLST[position]

        bindingm.llView.setOnClickListener {
            activityCalenderFragment.onItemClick(activityLST[position], 1, false)
        }
        bindingm.llDelete.setOnClickListener {
            activityCalenderFragment.onItemClick(activityLST[position], 3, false)
        }
        bindingm.llEdit.setOnClickListener {
            activityCalenderFragment.onItemClick(activityLST[position], 2, false)
        }
        val data = activityLST[position]
        if (data.isActive) {
            bindingm.tvStatus.text = "  Active"
            bindingm.tvStatus.setTextColor(Color.parseColor("#4DAC3C"))

        } else {
            bindingm.tvStatus.text = "  InActive"
            bindingm.tvStatus.setTextColor(Color.parseColor("#848484"))
        }


    }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}