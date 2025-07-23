package com.app.ecarepro.ui.assignClub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.CustomPopupSelectClassItemBinding


class ClubSelectAdapter(private var houseList: List<Clubs>, val callback: (poss:Int) -> Unit) :
    RecyclerView.Adapter<ClubSelectAdapter.HomeSelectViewHolder>() {
     var lastIndex = -1
        get() = field
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSelectViewHolder {
        val binding =
            CustomPopupSelectClassItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeSelectViewHolder(binding)
    }

    override fun getItemCount(): Int = houseList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeSelectViewHolder, position: Int) {
        val house=houseList[position]
        val mainBinding= DataBindingUtil.getBinding<CustomPopupSelectClassItemBinding>(holder.itemView)
        with(mainBinding!!){
            tvClassName.text = house.clubName
            tvClassName.setOnClickListener({
                lastIndex = position
                notifyDataSetChanged()
            })
            if (lastIndex == position) {
                tvClassName.setTextColor(
                    tvClassName.context.resources.getColor(R.color.brand_color)
                )
            } else {
                tvClassName.setTextColor(tvClassName.context.resources.getColor(R.color.module))
            }
        }
    }

    class HomeSelectViewHolder(itemView: CustomPopupSelectClassItemBinding) :
        RecyclerView.ViewHolder(itemView.root)


}