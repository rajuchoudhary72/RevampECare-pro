package com.app.ecarepro.ui.fee_report.collection.defaulter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.databinding.ItemCollectionReportBinding
import com.app.ecarepro.databinding.ItemDefaultReportBinding

class DefaulterReportListAdapter(private var defaulterDataLists: List<DefaulterDataList>?,
                                 private var defaulterReportFragment: DefaulterReportFragment
) :
    RecyclerView.Adapter<DefaulterReportListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   ItemDefaultReportBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=ItemDefaultReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = defaulterDataLists!!.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding= DataBindingUtil.getBinding<ItemDefaultReportBinding>(holder.itemView)
        val data= defaulterDataLists!![position]

        binding?.apply {
            defaulterData=data
        }


    }




    class NoticeViewHolder(itemView: ItemDefaultReportBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}