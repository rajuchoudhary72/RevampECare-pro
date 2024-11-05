package com.app.ecarepro.ui.collection_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemCollectionReportBinding
import com.app.ecarepro.model.CollectionReport
import java.text.SimpleDateFormat
import java.util.Locale


class CollectionReportAdapter(private var collectionReports: List<CollectionReport>,
                                     private var collectionFeeReportFragment: CollectionReportFragment
) :
    RecyclerView.Adapter<CollectionReportAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemCollectionReportBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=
            ItemCollectionReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = collectionReports.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding= DataBindingUtil.getBinding<ItemCollectionReportBinding>(holder.itemView)
        val data= collectionReports[position]

        binding?.apply {
           date=data.date.changeDateFormat()
           amount="₹ ${data.amount} Cr"
        }


    }


private fun String.changeDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM dd",Locale.getDefault())
    val inputDateStr = this
    val date = inputFormat.parse(inputDateStr)
    return date?.let { outputFormat.format(it) } ?: this
}

    class NoticeViewHolder(itemView: ItemCollectionReportBinding) : RecyclerView.ViewHolder(itemView.root){
    }


}