package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.NotSubmittedStuListBinding
import com.app.ecarepro.databinding.StaffAssignmentItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.AssignSubmitStudent


class NotSubmitAssignListAdapter(
    private var activityLST: List<AssignSubmitStudent>,
    private var activityCalenderFragment: ViewAssignmentFragment
) :
    RecyclerView.Adapter<NotSubmitAssignListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: NotSubmittedStuListBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm = NotSubmittedStuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.stuData = activityLST[position]

        bindingm.llEdit.setOnClickListener {
            activityCalenderFragment.onItemClick(activityLST[position],3,false)
        }





     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}