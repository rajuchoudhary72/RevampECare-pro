package com.app.ecarepro.ui.fee_report.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemCollectionReportBinding
import com.app.ecarepro.model.CollectionReport

class CollectionFeeReportListAdapter(private var collectionReports: List<CollectionReport>,
                                     private var collectionFeeReportFragment: CollectionFeeReportFragment
) :
    RecyclerView.Adapter<CollectionFeeReportListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   ItemCollectionReportBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=ItemCollectionReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = collectionReports.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding= DataBindingUtil.getBinding<ItemCollectionReportBinding>(holder.itemView)
        val data= collectionReports[position]

        binding?.apply {
           // collectionData=data
        }


    }




    class NoticeViewHolder(itemView: ItemCollectionReportBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}