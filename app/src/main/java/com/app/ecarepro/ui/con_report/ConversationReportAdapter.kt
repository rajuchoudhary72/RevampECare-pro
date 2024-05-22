package com.app.ecarepro.ui.con_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ConversationReportItemBinding
import com.app.ecarepro.model.Conversation

class ConversationReportAdapter(private var conversationList: List<Conversation>) :
    RecyclerView.Adapter<ConversationReportAdapter.MedicineIssueViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineIssueViewHolder {
        val binding =
            ConversationReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineIssueViewHolder(binding)
    }

    override fun getItemCount(): Int = conversationList.size


    override fun onBindViewHolder(holder: MedicineIssueViewHolder, position: Int) {
         holder.bind(conversationList[position]) }

    class MedicineIssueViewHolder(private val binding:   ConversationReportItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(conversation: Conversation) {
            binding.apply {

            }
        }
        }


}