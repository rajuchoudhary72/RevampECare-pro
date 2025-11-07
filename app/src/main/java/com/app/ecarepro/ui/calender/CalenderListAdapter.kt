package com.app.ecarepro.ui.calender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.EBookItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.utils.Constant
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CalenderListAdapter(
    private var activityLST: List<Activity>,
    private var activityCalenderFragment: ActivityCalenderFragment
) :
    RecyclerView.Adapter<CalenderListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: CalenderListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            CalenderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root )
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val activity = activityLST[position]
        val bindings= DataBindingUtil.bind<CalenderListItemBinding>(holder.itemView)
        bindings?.let { item ->
            item.dataActivity=activity

            if(!activity.fromDate.isNullOrEmpty()){
                item.tvFromDate.text=Constant.convertDateLongWeekDayToSort(activity.fromDate.toString())

                if(!activity.tillDate.isNullOrEmpty()){
                    item.endDay.text=Constant.convertDateLongWeekDayToSort(activity.tillDate.toString())

                    val days= daysExcludingStart(activity.fromDate.toString(),activity.tillDate.toString())
                    item.relTo.isVisible = days>=1
                    item.relEndDay.isVisible = days>=1
                }else{
                    item.relTo.isVisible = false
                    item.relEndDay.isVisible = false
                    item.endDay.text=""
                }

            }


        }



    }

    fun daysExcludingStart(startDateStr: String, endDateStr: String): Long {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy EEEE", Locale.ENGLISH)

        val startDate: Date = dateFormat.parse(startDateStr)!!
        val endDate: Date = dateFormat.parse(endDateStr)!!

        val diffInMillis = endDate.time - startDate.time
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        // Exclude start date by not subtracting 1
        return maxOf(0, totalDays)
    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}