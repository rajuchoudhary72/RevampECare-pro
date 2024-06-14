package com.app.ecarepro.ui.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.utils.Constant

class AssignmentListAdapter(
    private var activityLST: List<Assignment>,
    private var activityCalenderFragment: AssignmentListFragment,
    val userType: String
) :
    RecyclerView.Adapter<AssignmentListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StuAssignmentItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StuAssignmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {

        holder.bind(activityLST[position])


     }


   inner class AssignmentListAdapter(val bindingm: StuAssignmentItemBinding) : RecyclerView.ViewHolder(bindingm.root) {

        fun bind(assignment: Assignment) {

            bindingm.assignmentData = assignment


            bindingm.llView.setOnClickListener {
                activityCalenderFragment.onItemClick(assignment,1,false)
            }
            bindingm.llSubmit.setOnClickListener {
                activityCalenderFragment.onItemClick(assignment,2,false)
            }


            if ( userType == Constant.PRINCIPAL || userType ==  Constant.MANAGEMENT) {
                bindingm.llSubmit.isVisible=false
                bindingm.llView.isVisible=true
            }else{
                bindingm.llSubmit.isVisible=true
                bindingm.llView.isVisible=false
            }
        }

    }


}