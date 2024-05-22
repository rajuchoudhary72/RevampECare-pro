package com.app.ecarepro.ui.sms_app_msg_report.sms_consumption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SmsConsumptionItemBinding
import com.app.ecarepro.databinding.SmsReportItemBinding
import com.app.ecarepro.model.DateWise
import com.app.ecarepro.model.UsesRPT

class SmsConsumptionAdapter(
    private val dateWises: List<DateWise>,
    private val smsMsgReportFragment: SMSConsumptionFragment
) :
    RecyclerView.Adapter<SmsConsumptionAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   SmsConsumptionItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=SmsConsumptionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm )
    }

    override fun getItemCount(): Int = dateWises.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        holder.bind(dateWises[position])


    }




   inner class SmsReportViewHolder(val binding: SmsConsumptionItemBinding ) : RecyclerView.ViewHolder(binding.root){

        fun bind(usesRPT: DateWise) {
            binding.dayWise=usesRPT
            binding.tvSNo.text=  position.toString()
        }

  }


}