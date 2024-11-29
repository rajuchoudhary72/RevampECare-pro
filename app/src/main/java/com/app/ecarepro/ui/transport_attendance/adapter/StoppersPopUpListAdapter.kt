package com.app.ecarepro.ui.transport_attendance.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.PopUpListItemBinding
import com.app.ecarepro.databinding.TransportAttStudentListItemBinding
import com.app.ecarepro.model.StopLST
import com.app.ecarepro.ui.transport_attendance.adapter.StudentListToMarkAttAdapter.NoticeViewHolder
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener

class StoppersPopUpListAdapter(
    private var routeLSTList: List<StopLST>,
    private val tripType: Int,
    private val selectAll: Boolean,
    private var itemListener: ItemListener<StopLST>
) :
    RecyclerView.Adapter<StoppersPopUpListAdapter.PopUpListViewHolder>() {
    private lateinit var bindingm: PopUpListItemBinding


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {

         bindingm = PopUpListItemBinding.inflate(
             LayoutInflater.from(parent.context),
             parent,
             false
         )


        return PopUpListViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = routeLSTList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        val binding = DataBindingUtil.getBinding<PopUpListItemBinding>(holder.itemView)
        val data = routeLSTList[pos]

         binding?.apply {
              tvItemName.text=routeLSTList[pos].stopName
             if (tripType== Constant.UP_TRIP || tripType==Constant.DOWN_TRIP) {
                  checkImage.isVisible=true
                 checkImage.setImageResource(if (selectAll) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)

             }
              llMain.setOnClickListener {

                 itemListener.onItemClick(routeLSTList[pos],1,true)

                 if (tripType== Constant.UP_TRIP || tripType==Constant.DOWN_TRIP) {
                     data.checked=!data.checked
                 } else {
                     for ((i, v) in routeLSTList.withIndex()) {
                         routeLSTList[i].checked = i == pos
                     }
                 }

                 notifyDataSetChanged()
             }

             if (data.checked) {
                 tvItemName.setTextColor(Color.parseColor(R.color.brand_color.toString()))
                 checkImage.setImageResource(R.drawable.ic_baseline_check_box_24)
             } else {
                 tvItemName.setTextColor(Color.parseColor("#000000"))
                 checkImage.setImageResource(R.drawable.ic_baseline_check_box_unselectblank_24)
             }
         }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){ }


}