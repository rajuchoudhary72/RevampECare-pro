package com.app.ecarepro.ui.lessonPlan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LessonPlanListItemBinding
import com.app.ecarepro.model.LessonPlan

class LessonPlanListAdapter(private var lessonPlanList: MutableList<LessonPlan>,
                            private var lessonPlanListFragment: LessonPlanListFragment) :
    RecyclerView.Adapter<LessonPlanListAdapter.NoticeViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
       val  binding =LessonPlanListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(binding)



    }

    override fun getItemCount(): Int = lessonPlanList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<LessonPlanListItemBinding>(holder.itemView)
         val data = lessonPlanList[position]

        with(binding!!) {
            tvHeading.text=data.topic
            tvPlanSubj.text=data.subject
            tvPlanClass.text=data.classesName
            tvPlanDuration.text = buildString {
                append(data.fromDate)
                append("to")
                append(data.tillDate)
            }

            if (data.status==0){
                tvStatus.setTextColor( lessonPlanListFragment.resources.getColor(R.color.att_late_color,null))
                 tvStatus.text="Pending"
                llEdit.isVisible=true
                llDelete.isVisible=true
            }else{
                tvStatus.setTextColor( lessonPlanListFragment.resources.getColor(R.color.green,null))
                 tvStatus.text="Approved"
                llEdit.isVisible=false
                llDelete.isVisible=false
            }

            llView.setOnClickListener {
                lessonPlanListFragment.onItemClick(data,1,false)
            }
             llEdit.setOnClickListener {

            }
             llDelete.setOnClickListener {
                lessonPlanListFragment.onItemClick(data,3,false)
            }
        }  }


    fun setData(lessonList: MutableList<LessonPlan>){
        lessonPlanList.addAll(lessonList)
        notifyDataSetChanged()
    }
    fun clearData(){
        lessonPlanList.clear()
        notifyDataSetChanged()
    }




    class NoticeViewHolder(itemView: LessonPlanListItemBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}