package com.app.ecarepro.ui.transport_attendance.out_pass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemPassOutReportViewBinding
import com.app.ecarepro.databinding.ItemTransAttendanceBinding
import com.app.ecarepro.model.StuLst
import com.squareup.picasso.Picasso

class OutPassReportAdapter(
    private val stuLstList: List<StuLst>,
    private val outPassReportFragment: OutPassReportFragment
) :
    RecyclerView.Adapter<OutPassReportAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   ItemPassOutReportViewBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=ItemPassOutReportViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = stuLstList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<ItemPassOutReportViewBinding>(holder.itemView)
        val data= stuLstList[position]
        binding?.apply {
           tvStuName.text=data.stName
           tvClass.text=data.className
           tvAdmissionNo.text=data.admissionNo
           tvRollNo.text=data.rollNo
           tvRoute.text=data.route
           tvStop.text=data.stop
         }

    }




    class NoticeViewHolder(itemView: ItemPassOutReportViewBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}