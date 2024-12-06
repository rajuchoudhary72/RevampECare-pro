package com.app.ecarepro.ui.sms_app_msg_report.sms_report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.SmS
import com.app.ecarepro.databinding.ItemSmsReportListBinding
import com.app.ecarepro.databinding.LeaveReportListItemBinding
import com.app.ecarepro.databinding.LikeBySingleItemBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.stringFormat2String
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class SmsReportAdapter(private var smsReportList : MutableList<SmS> ,
                       private var smsReportFragment: SmsReportFragment
) :
    RecyclerView.Adapter<SmsReportAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   ItemSmsReportListBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=ItemSmsReportListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding )
    }

    override fun getItemCount(): Int = smsReportList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<ItemSmsReportListBinding>(holder.itemView)
         binding?.apply {
             val smSs: SmS = smsReportList[position]

             val includePersonDetailsTopBinding =  binding.includePersonDetailsTop
             val mIncludePersonDetailsTopBinding = DataBindingUtil.getBinding<LikeBySingleItemBinding>(includePersonDetailsTopBinding.root)

             mIncludePersonDetailsTopBinding?.apply {
                 Picasso.get().
                 load(smSs.receiver.photo)
                     .placeholder(R.drawable.default_profile)
                     .  into(imPerson)
                 tvHeadingTitle.text=smSs.receiver.name

                 if (smSs.receiver.receiverType ==  3) {
                     tvName.text=smSs.receiver.designation
                 } else if (smSs.receiver.receiverType == 2) {

                     tvName.text=  stringFormat2String(
                         smsReportFragment.requireActivity() as MainActivity,
                         R.string.InboxList,
                         smSs.receiver.childName,
                         smSs.receiver.className
                     )

                 } else {
                    tvName.text= buildString {
                        append("Class :- ")
                        append(smSs.receiver.className)
                    }
                 }

             }

             tvSmsDetails.text=smSs.text
             tvSmsDate.text= buildString {
                 append("Sent on: ")
                 append(smSs.sentOn)
             }
             tvSmsTyp.text= buildString {
                 append("SMS Type: ")
                 append(smSs.smsType)
             }
             tvStatus.text= buildString {
                 append("Delivery Status: ")
                 append(smSs.status)
             }

             if (!smSs.statusOn.isNullOrEmpty()){
                 tvStatusOn.isVisible=true
                 tvStatusOn.text= buildString {
                     append("On ")
                     append(smSs.statusOn)
                 }
             }else{
                 tvStatusOn.isVisible=false
             }

             val includePersonDetailsBottomBinding =  binding.includePersonDetailsBottom
             val mIncludePersonDetailsBottomBinding = DataBindingUtil.getBinding<LikeBySingleItemBinding>(includePersonDetailsBottomBinding.root)

             mIncludePersonDetailsBottomBinding?.apply {
                 Picasso.get().
                 load(smSs.photo)
                     .placeholder(R.drawable.default_profile)
                     .  into(imPerson)
                 tvHeadingTitle.text= smsReportFragment.requireContext().getString(R.string.sent_by)

                 tvName.text= buildString {
                     append(smSs.senderName)
                     append(", ")
                     append(smSs.designation)
                 }

             }
         }


   }



    fun setData(leaveList: MutableList<SmS> ){

        this. smsReportList.addAll(leaveList)

        notifyDataSetChanged()

    }
    fun clearData(){
        smsReportList.clear()
        notifyDataSetChanged()
    }




    class LeaveHistoryViewHolder(itemView: ItemSmsReportListBinding) : RecyclerView.ViewHolder(itemView.root)


}