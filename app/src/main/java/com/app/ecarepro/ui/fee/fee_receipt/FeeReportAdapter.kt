package com.app.ecarepro.ui.fee.fee_receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemFeeReceiptBinding
import com.app.ecarepro.databinding.SmsReportItemBinding
import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.UsesRPT

class FeeReportAdapter(
    private val feeReceipts: List<FeeReceipt>,
    private val feeReceiptFragment: FeeReceiptFragment
) :
    RecyclerView.Adapter<FeeReportAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   ItemFeeReceiptBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=ItemFeeReceiptBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm )
    }

    override fun getItemCount(): Int = feeReceipts.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<ItemFeeReceiptBinding>(holder .itemView)
        val data= feeReceipts[position]

        binding?.apply {
            binding.recData=data
            llView.setOnClickListener {
                feeReceiptFragment.onItemClick(data,1,false)
            }
            llDownload.setOnClickListener {
                feeReceiptFragment.onItemClick(data,2,false)
            }
        }


    }




   inner class SmsReportViewHolder(val binding: ItemFeeReceiptBinding ) : RecyclerView.ViewHolder(binding.root){



  }


}