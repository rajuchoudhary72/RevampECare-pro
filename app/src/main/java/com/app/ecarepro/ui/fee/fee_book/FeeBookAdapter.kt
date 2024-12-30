package com.app.ecarepro.ui.fee.fee_book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.data.network.model.FeeBookModel
import com.app.ecarepro.databinding.ItemFeeBookBinding
import com.app.ecarepro.databinding.ItemFeeReceiptBinding
import com.app.ecarepro.databinding.SmsReportItemBinding
import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.UsesRPT

class FeeBookAdapter(
    private val feeReceipts: List<FeeBookModel>,
    private val feeReceiptFragment: FeeBookFragment
) :
    RecyclerView.Adapter<FeeBookAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   ItemFeeBookBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=ItemFeeBookBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm )
    }

    override fun getItemCount(): Int = feeReceipts.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<ItemFeeBookBinding>(holder .itemView)
        val data= feeReceipts[position]

        binding?.apply {
            tvDate.text=data.installname
            tvReceiptNo.text= String.format(data.Dues.toString())


            llView.setOnClickListener {
                feeReceiptFragment.onItemClick(data,1,false)
            }
            llDownload.setOnClickListener {
                feeReceiptFragment.onItemClick(data,2,false)
            }
        }


    }




   inner class SmsReportViewHolder(val binding: ItemFeeBookBinding ) : RecyclerView.ViewHolder(binding.root){



  }


}