package com.app.ecarepro.ui.timeTable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.DayWiseTimeTableItemBinding
import com.app.ecarepro.databinding.StuAssignmentItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.model.TimeTable
import com.app.ecarepro.utils.Constant

class DayWiseListAdapter(
    private var activityLST: List<TimeTable>,
    private var activityCalenderFragment: DayWiseTimeTableFragment,
    val toFragment: String
) :
    RecyclerView.Adapter<DayWiseListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: DayWiseTimeTableItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            DayWiseTimeTableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val binding= DataBindingUtil.getBinding<DayWiseTimeTableItemBinding>(holder.itemView)
        binding?.apply {
            bindingm.timeData = activityLST[position]
            val data= activityLST[position]


            if (toFragment== Constant.CLASS_TIME_TABLE){

                tvClass.text=data.teachBy

            }else{
                tvClass.text=data.className
            }

            when (data.period) {
                1 -> {
                    tvSt.text="st"
                }
                2 -> {
                    tvSt.text="nd"
                }
                3 -> {
                    tvSt.text="rd"
                }
                else -> {
                    tvSt.text="th"
                }
            }
        }




     }


    class AssignmentListAdapter(itemView: DayWiseTimeTableItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}