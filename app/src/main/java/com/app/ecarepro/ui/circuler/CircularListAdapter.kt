package com.app.ecarepro.ui.circuler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.CircularListItemBinding
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Circular
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.model.Notice

class CircularListAdapter(private var circularList: MutableList<Circular>,
                          private var circularFragment: CircularFragment ) :
    RecyclerView.Adapter<CircularListAdapter.CircularViewHolder>() {

        private lateinit var bindingm:   CircularListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        bindingm=CircularListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CircularViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = circularList.size

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {
        val binding=DataBindingUtil.getBinding<CircularListItemBinding>(holder.itemView)

        binding?.apply {
             circularData=circularList[position]
             clMain.setOnClickListener {
                circularFragment.onItemClick(circularList[position],1,true)
            }

            if (circularList[position].isRead){
                 cvNotItem.elevation=0f
            }else{
                 cvNotItem.elevation=8f
            }
        }




    }
    fun setData(circularList: MutableList<Circular> ){
        this.circularList.addAll(circularList)

        notifyDataSetChanged()

    }
    fun clearData(){
        circularList.clear()
        notifyDataSetChanged()
    }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}