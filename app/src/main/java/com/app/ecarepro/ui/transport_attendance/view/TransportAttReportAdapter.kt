package com.app.ecarepro.ui.transport_attendance.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemTransAttendanceBinding
import com.app.ecarepro.model.StopLST
import com.app.ecarepro.model.StuLst
import com.squareup.picasso.Picasso

class TransportAttReportAdapter(
    private val stopLSTList: List<StopLST>,
    private val transportAttendanceFragment: TransportAttendanceReportFragment
) :
    RecyclerView.Adapter<TransportAttReportAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   ItemTransAttendanceBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=ItemTransAttendanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = stopLSTList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<ItemTransAttendanceBinding>(holder.itemView)
        val data= stopLSTList[position]
        binding?.apply {
            tvStopName.text=data.stopName
            addLayout(binding.llView,data.stuLst)
        }

    }

    private fun addLayout(mLinearLayout: LinearLayout, items: List<StuLst>?) {
        mLinearLayout.removeAllViews()
        if (items != null && items.size > 0) {
            var i = 0
            while (items.size > i) {
                val item: StuLst = items[i]
                val v: View = LayoutInflater.from(mLinearLayout.context)
                    .inflate(R.layout.item_trans_view, mLinearLayout, false)
                val name = v.findViewById<TextView>(R.id.tv_stu_name)
                val tv_class = v.findViewById<TextView>(R.id.tv_class)
                val tv_roll_no = v.findViewById<TextView>(R.id.tv_roll_no)
                val tv_admission_no = v.findViewById<TextView>(R.id.tv_admission_no)
                val tvPickup = v.findViewById<TextView>(R.id.tvPickup)
                val tvNoRecord = v.findViewById<TextView>(R.id.tv_no_record)
                val tvDropUp = v.findViewById<TextView>(R.id.tvDropUp)
                val civ_stu_img = v.findViewById<ImageView>(R.id.circleImageViewProfile)
                Picasso.get().load( item.photo)
                    .placeholder(transportAttendanceFragment.resources.getDrawable(R.drawable.default_profile))
                    .into(civ_stu_img)
                name.text = item.stName
                tv_class.text = "Class: " + item.className
                tv_roll_no.text = "Roll No.:" + item.rollNo
                tv_admission_no.text = "Admission No. : " + item.admissionNo
                when (item.pickupAtt) {
                    "Present" -> {
                        tvPickup.setTextColor(v.context.resources.getColor(R.color.disabled))
                    }
                    "Absent" -> {
                        tvPickup.setTextColor(v.context.resources.getColor(R.color.absent_red))
                    }
                    else -> {
                        tvPickup.setTextColor(v.context.resources.getColor(R.color.att_late_color))
                    }
                }
                when (item.dropAtt) {
                    "Present" -> {
                        tvDropUp.setTextColor(v.context.resources.getColor(R.color.disabled))
                    }
                    "Absent" -> {
                        tvDropUp.setTextColor(v.context.resources.getColor(R.color.absent_red))
                    }
                    else -> {
                        tvDropUp.setTextColor(v.context.resources.getColor(R.color.att_late_color))
                    }
                }

                 tvPickup.text = buildString {
                    append( item.pickupAtt )
                    append( "( ")
                    append( item.pickupTime )
                    append( " )")
                }
                tvDropUp.text = buildString {
                    append( item.dropAtt )
                    append( "( ")
                    append( item.dropTime )
                    append( " )")
                }
                mLinearLayout.addView(v)
                i++
            }
        }
    }




    class NoticeViewHolder(itemView: ItemTransAttendanceBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}