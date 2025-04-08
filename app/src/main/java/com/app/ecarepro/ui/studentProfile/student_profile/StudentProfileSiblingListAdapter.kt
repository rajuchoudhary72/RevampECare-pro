package com.app.ecarepro.ui.studentProfile.student_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.DayWiseTimeTableItemBinding
import com.app.ecarepro.databinding.SiblingListItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.databinding.StudentProfileAttendenceListItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.model.SiblingDetails
import com.app.ecarepro.model.SummaryAttendance
import com.app.ecarepro.model.TimeTable

class StudentProfileSiblingListAdapter(
    private var siblingList: List<SiblingDetails>,
) :
    RecyclerView.Adapter<StudentProfileSiblingListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: SiblingListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            SiblingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = siblingList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val binding  = DataBindingUtil.bind<SiblingListItemBinding>(holder.itemView)
         binding?.apply {
             binding.siblingDetails=siblingList[position]
            binding.tvClasses.text=siblingList[position].`class`
         }


     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}