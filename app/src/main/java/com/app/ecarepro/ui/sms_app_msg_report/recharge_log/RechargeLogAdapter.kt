package com.app.ecarepro.ui.sms_app_msg_report.recharge_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SmsConsumptionItemBinding
import com.app.ecarepro.databinding.SmsRechargeLogItemBinding
import com.app.ecarepro.model.DateWise
import com.app.ecarepro.model.RechargeLog

class RechargeLogAdapter(
    private val rechargeLogs: List<RechargeLog>,
    private val rechargeLogFragment: RechargeLogFragment
) :
    RecyclerView.Adapter<RechargeLogAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   SmsRechargeLogItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=SmsRechargeLogItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm )
    }

    override fun getItemCount(): Int = rechargeLogs.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        holder.bind(rechargeLogs[position])


    }




   inner class SmsReportViewHolder(val binding: SmsRechargeLogItemBinding ) : RecyclerView.ViewHolder(binding.root){

        fun bind(usesRPT: RechargeLog) {
            binding.rechargeLog=usesRPT
            binding.tvSNo.text=  position.toString()
        }

  }


}