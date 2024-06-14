package com.app.ecarepro.ui.leave.leave_setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.LeaveSettingListItemBinding
import com.app.ecarepro.model.LeaveDetail

class LeaveSettingDetailsAdapter(private var leaveDetailList: List<LeaveDetail>,
                                 private var leaveSettingFragment: LeaveSettingFragment
) :
    RecyclerView.Adapter<LeaveSettingDetailsAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   LeaveSettingListItemBinding
    var hide= true



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=LeaveSettingListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding )
    }

    override fun getItemCount(): Int = leaveDetailList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        holder.bind(leaveDetailList[position])

    }
  inner  class LeaveHistoryViewHolder( val binding: LeaveSettingListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(data: LeaveDetail) {

            binding.tvLeaveType.text=data.leaveType
            val leftLeave=data.total-data.taken
            binding.tvLeaveResult.text= "$leftLeave/${data.total}"


            binding.ivShowHide.setOnClickListener {

                if (hide){
                    binding.llLeaveDtl.isVisible=true
                    hide=false
                }else{
                    binding.llLeaveDtl.isVisible=false
                    hide= true
                }
            }

            binding.tvTotal.text= buildString {
                append("Total Leave : ")
                append(data.total) }

            binding.tvTaken.text= buildString {
                append("Total Taken : ")
                append(data.taken) }

            binding.tvBalance.text= buildString {
                append("Total Balance : ")
                append(leftLeave) }

            binding.leaveProgress.max=data.total.toInt()
            binding.leaveProgress.progress=data.taken.toInt()


        }

    }


}