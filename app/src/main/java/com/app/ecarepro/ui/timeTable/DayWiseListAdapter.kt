package com.app.ecarepro.ui.timeTable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.DayWiseTimeTableItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.model.TimeTable

class DayWiseListAdapter(
    private var activityLST: List<TimeTable>,
    private var activityCalenderFragment: DayWiseTimeTableFragment
) :
    RecyclerView.Adapter<DayWiseListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: DayWiseTimeTableItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            DayWiseTimeTableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.timeData = activityLST[position]
        val data= activityLST[position]

        if (data.period==1){
            bindingm.tvSt.text="st"
        }else if (data.period==2){
            bindingm.tvSt.text="nd"
        }else if (data.period==3){
            bindingm.tvSt.text="rd"
        }else{
            bindingm.tvSt.text="th"
        }




     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}