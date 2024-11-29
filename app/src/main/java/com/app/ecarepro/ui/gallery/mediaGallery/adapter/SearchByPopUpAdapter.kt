package com.app.ecarepro.ui.gallery.mediaGallery.adapter

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
import com.app.ecarepro.model.FeeReceiptSession
import com.app.ecarepro.model.UsesRPT
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso

class SearchByPopUpAdapter(private var routeLSTList: List<String>,
                           private var itemListener: ItemListener<String>
) :
    RecyclerView.Adapter<SearchByPopUpAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_staff_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = routeLSTList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text=routeLSTList[holder.bindingAdapterPosition]

        holder.circleImageViewProfile.isVisible=false

        holder.llMain.setOnClickListener {
            lastIndex=holder.bindingAdapterPosition
             itemListener.onItemClick(routeLSTList[holder.bindingAdapterPosition],pos,true)
            notifyDataSetChanged()
        }

        if (lastIndex == holder.bindingAdapterPosition) {
           holder. itemName.setTextColor(Color.parseColor(R.color.brand_color.toString()))
        } else {
            holder.itemName.setTextColor(Color.parseColor("#000000"))
        }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.tv_staff_name)
         val llMain: LinearLayout = itemView.findViewById(R.id.llMain)
         val circleImageViewProfile: ImageView = itemView.findViewById(R.id.circleImageViewProfile)


    }


}