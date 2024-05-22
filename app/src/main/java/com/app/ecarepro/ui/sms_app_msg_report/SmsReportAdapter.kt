package com.app.ecarepro.ui.sms_app_msg_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SmsReportItemBinding
import com.app.ecarepro.model.UsesRPT

class SmsReportAdapter(
    private val usesRPTS: List<UsesRPT>,
    private val smsMsgReportFragment: SmsMsgReportFragment
) :
    RecyclerView.Adapter<SmsReportAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   SmsReportItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=SmsReportItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm )
    }

    override fun getItemCount(): Int = usesRPTS.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        holder.bind(usesRPTS[position])


    }




   inner class SmsReportViewHolder(val binding: SmsReportItemBinding ) : RecyclerView.ViewHolder(binding.root){

        fun bind(usesRPT: UsesRPT) {
            binding.useDetails=usesRPT
            binding.tvSNo.text=  position.toString()
        }

  }


}