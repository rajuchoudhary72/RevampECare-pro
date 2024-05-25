package com.app.ecarepro.ui.transport_attendance.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.model.RouteLST
import com.app.ecarepro.model.StopLST
import com.app.ecarepro.utils.listener.ItemListener

class StoppersPopUpListAdapter(private var routeLSTList: List<StopLST>,
                               private var itemListener: ItemListener<StopLST>
) :
    RecyclerView.Adapter<StoppersPopUpListAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = routeLSTList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text=routeLSTList[holder.bindingAdapterPosition].stopName
        holder.llMain.setOnClickListener {
            lastIndex=holder.bindingAdapterPosition
             itemListener.onItemClick(routeLSTList[holder.bindingAdapterPosition],1,true)
            notifyDataSetChanged()
        }

        if (lastIndex == holder.bindingAdapterPosition) {
           holder. itemName.setTextColor(Color.parseColor("#4DAC3C"))
        } else {
            holder.itemName.setTextColor(Color.parseColor("#000000"))
        }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val llMain: LinearLayout = itemView.findViewById(R.id.ll_main)

    }


}