package com.app.ecarepro.ui.syllabus.teacher.add_syllabus

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.ClassSection
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.utils.listener.ItemListener

class SectionListAdapter(
    private var academicYearList: List<ClassSection>,
    private val selectAll: Boolean,
    private val multiSelect: Boolean,

    private var itemListener: ItemListener<ClassSection>
) :
    RecyclerView.Adapter<SectionListAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = academicYearList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text=academicYearList[holder.bindingAdapterPosition].secName
        holder.checkImage.isVisible=multiSelect
        holder.checkImage.setImageResource(if (selectAll) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)

        holder.llMain.setOnClickListener {
              itemListener.onItemClick(academicYearList[holder.bindingAdapterPosition],1,true)
            academicYearList[holder.bindingAdapterPosition].isSelected =! academicYearList[holder.bindingAdapterPosition].isSelected
            lastIndex = holder.bindingAdapterPosition
            notifyDataSetChanged()

        }

        if (lastIndex == holder.bindingAdapterPosition) {
           holder. itemName.setTextColor(Color.parseColor("#4DAC3C"))
        } else {
            holder.itemName.setTextColor(Color.parseColor("#000000"))
        }
        if (academicYearList[holder.bindingAdapterPosition].isSelected) {
            holder.  itemName.setTextColor(Color.parseColor("#4DAC3C"))
            holder. checkImage.setImageResource(R.drawable.ic_baseline_check_box_24)
        } else {
            holder.  itemName.setTextColor(Color.parseColor("#000000"))
            holder. checkImage.setImageResource(R.drawable.ic_baseline_check_box_unselectblank_24)
        }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val llMain: LinearLayout = itemView.findViewById(R.id.ll_main)
        val checkImage: ImageView = itemView.findViewById(R.id.checkImage)



    }


}