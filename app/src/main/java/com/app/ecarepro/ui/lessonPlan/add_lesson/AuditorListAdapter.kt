package com.app.ecarepro.ui.lessonPlan.add_lesson

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AuditorListItemBinding
import com.app.ecarepro.model.AuditorLst

class AuditorListAdapter(private var auditorLsts: List<AuditorLst>,
                         private var addLessonFragment: AddLessonFragment ) :
    RecyclerView.Adapter<AuditorListAdapter.NoticeViewHolder>() {

    private var lastIndex = 1000


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val bindingm=AuditorListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = auditorLsts.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<AuditorListItemBinding>(holder.itemView)

        with(binding!!) {
             auditorData=auditorLsts[position]

            llMain.setOnClickListener {
                addLessonFragment.onItemClick(auditorLsts[position],1,false)
             }

            tvItemName.setOnClickListener {
                addLessonFragment.onItemClick(auditorLsts[position],1,false)
                lastIndex=holder.bindingAdapterPosition
                notifyDataSetChanged()
            }

            if (lastIndex == holder.bindingAdapterPosition) {
                tvItemName.setTextColor(Color.parseColor("#4DAC3C"))
            } else {
                tvItemName.setTextColor(Color.parseColor("#991E1D0E"))
            }

        }





    }




    class NoticeViewHolder(itemView: AuditorListItemBinding) : RecyclerView.ViewHolder(itemView.root){

  }


}