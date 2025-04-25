package com.app.ecarepro.ui.calender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.utils.Constant

class CalenderListAdapter(
    private var activityLST: List<Activity>,
    private var activityCalenderFragment: ActivityCalenderFragment
) :
    RecyclerView.Adapter<CalenderListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: CalenderListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            CalenderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {


        holder.bind(activityLST[position])



    }


    class NoticeViewHolder(val item: CalenderListItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(activity: Activity) {

            item.dataActivity=activity

            if(!activity.fromDate.isNullOrEmpty()){
                item.tvFromDate.text=Constant.convertDateLongWeekDayToSort(activity.fromDate.toString())
                item.relTo.isVisible = activity.fromDate != activity.tillDate
            }

            if(!activity.tillDate.isNullOrEmpty()){
                item.endDay.text=Constant.convertDateLongWeekDayToSort(activity.tillDate.toString())
                item.relEndDay.isVisible = !activity.fromDate.equals(activity.tillDate)
            }

        }

    }


}