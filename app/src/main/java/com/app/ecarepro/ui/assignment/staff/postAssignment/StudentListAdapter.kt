package com.app.ecarepro.ui.assignment.staff.postAssignment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.model.Student
import com.app.ecarepro.utils.listener.ItemListener

class StudentListAdapter(private var academicYearList: List<Student>,
                         private val selectAll: Boolean,
                         private var itemListener: ItemListener<Student>
) :
    RecyclerView.Adapter<StudentListAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_students_list_item , parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = academicYearList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text="Name: "+academicYearList[holder.bindingAdapterPosition].recipientName
        holder.rollNo.text="Roll No: "+academicYearList[holder.bindingAdapterPosition].rollNumber
        holder.className.text="Class : "+academicYearList[holder.bindingAdapterPosition].`class`
        holder.checkImage.isVisible=true
        holder.checkImage.setImageResource(if (selectAll) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)

        holder.llMain.setOnClickListener {
              itemListener.onItemClick(academicYearList[holder.bindingAdapterPosition],1,true)
            academicYearList[holder.bindingAdapterPosition].isSelected =!academicYearList[holder.bindingAdapterPosition].isSelected!!
            notifyDataSetChanged()
        }

        if (lastIndex == holder.bindingAdapterPosition) {
           holder. itemName.setTextColor(Color.parseColor(R.color.brand_color.toString()))
        } else {
            holder.itemName.setTextColor(Color.parseColor("#000000"))
        }
        if (academicYearList[holder.bindingAdapterPosition].isSelected == true) {
            holder.  itemName.setTextColor(Color.parseColor(R.color.brand_color.toString()))
            holder. checkImage.setImageResource(R.drawable.ic_baseline_check_box_24)
        } else {
            holder.  itemName.setTextColor(Color.parseColor("#000000"))
            holder. checkImage.setImageResource(R.drawable.ic_baseline_check_box_unselectblank_24)
        }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val  className: TextView = itemView.findViewById(R.id.tv_class_name)
        val rollNo: TextView = itemView.findViewById(R.id.tv_roll_no)
        val llMain: LinearLayout = itemView.findViewById(R.id.ll_main)
        val checkImage: ImageView = itemView.findViewById(R.id.checkImage)



    }


}