package com.app.ecarepro.ui.award

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemNameMarksBinding

class SportsAdapter() : RecyclerView.Adapter<SportsAdapter.ClassPromotionsHolder>() {
    private var sportsMutableList = mutableListOf<Pair<String, String>>()

    fun addItems(itemList: MutableList<Pair<String, String>>) {
        sportsMutableList.clear()
        sportsMutableList.addAll(itemList)
        notifyItemRangeChanged(0, sportsMutableList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassPromotionsHolder {
        val mBinding =
            ItemNameMarksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassPromotionsHolder(mBinding)
    }

    override fun getItemCount(): Int = sportsMutableList.size

    override fun onBindViewHolder(holder: ClassPromotionsHolder, position: Int) {
        val mBinding = DataBindingUtil.getBinding<ItemNameMarksBinding>(holder.itemView)
        val student = sportsMutableList[position]
        mBinding?.name = student.first
        mBinding?.value = student.second


    }


    class ClassPromotionsHolder(itemView: ItemNameMarksBinding) :
        RecyclerView.ViewHolder(itemView.root)


}