package com.app.ecarepro.ui.studentProfile.academic_performance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.AcademicPerfListItemBinding
import com.app.ecarepro.model.Mark

class AcademicPerfListAdapter(
    private var markList: List<Mark>
) :
    RecyclerView.Adapter<AcademicPerfListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: AcademicPerfListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            AcademicPerfListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = markList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.subData = markList[position]


     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}