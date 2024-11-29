package com.app.ecarepro.ui.sms_app_msg_report

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.model.RouteLST
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.UsesRPT
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso

class StaffPopUpListAdapter(private var routeLSTList: List<UsesRPT>,
                            private var itemListener: ItemListener<UsesRPT>
) :
    RecyclerView.Adapter<StaffPopUpListAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_staff_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = routeLSTList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text=routeLSTList[holder.bindingAdapterPosition].name
        Picasso.get().
        load(routeLSTList[holder.bindingAdapterPosition].photo)
            .placeholder(R.drawable.default_profile)
            .  into(holder.userImageView)
        holder.llMain.setOnClickListener {
            lastIndex=holder.bindingAdapterPosition
             itemListener.onItemClick(routeLSTList[holder.bindingAdapterPosition],1,true)
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
        val userImageView: ImageView = itemView.findViewById(R.id.circleImageViewProfile)
        val llMain: LinearLayout = itemView.findViewById(R.id.llMain)

    }


}