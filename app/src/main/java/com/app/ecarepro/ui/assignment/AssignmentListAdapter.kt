package com.app.ecarepro.ui.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Assignment

class AssignmentListAdapter(
    private var activityLST: List<Assignment>,
    private var activityCalenderFragment: AssignmentListFragment
) :
    RecyclerView.Adapter<AssignmentListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StuAssignmentItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StuAssignmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {

        val binding= DataBindingUtil.getBinding<StuAssignmentItemBinding>(holder.itemView)

        binding!!.assignmentData = activityLST[position]

        binding.llView.setOnClickListener {
            activityCalenderFragment.onItemClick(activityLST[position],1,false)
        }



     }


    class AssignmentListAdapter(itemView: StuAssignmentItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}