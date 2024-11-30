package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R

import com.app.ecarepro.databinding.SubmittedStuListBinding
 import com.app.ecarepro.model.AssignSubmitStudent


class LateSubmitAssignListAdapter(
    private var activityLST: List<AssignSubmitStudent>,
    private var viewAssignmentFragment:  ViewAssignmentFragment
) :
    RecyclerView.Adapter<LateSubmitAssignListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: SubmittedStuListBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            SubmittedStuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.stuData = activityLST[position]
         val data = activityLST[position]

        if (data.isOfflineSubmitted){
            bindingm.tvSubmittedBy.text=  viewAssignmentFragment.getString(R.string.offline)
            bindingm.tvSubmittedBy.setTextColor(Color.parseColor("#000000"))
        }else{
            bindingm.tvSubmittedBy.text= viewAssignmentFragment.getString(R.string.online)
            bindingm.tvSubmittedBy.setTextColor(Color.parseColor("#4DAC3C"))

        }

        if (data.asgFile==null){
            bindingm.llView.isVisible=false
            bindingm.llDownload.isVisible=false
        }else{
            bindingm.llView.isVisible=true
            bindingm.llDownload.isVisible=true
        }

        bindingm.llView.setOnClickListener {
            viewAssignmentFragment.onItemClick(data,1,false)
        }
        bindingm.llDownload.setOnClickListener {
            viewAssignmentFragment.onItemClick(data,2,false)
        }
  }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}