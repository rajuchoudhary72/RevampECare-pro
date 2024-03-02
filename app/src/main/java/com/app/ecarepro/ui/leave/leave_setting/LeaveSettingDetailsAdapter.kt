package com.app.ecarepro.ui.leave.leave_setting

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LeaveListItemBinding
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
        return LeaveHistoryViewHolder(binding.root)
    }

    override fun getItemCount(): Int = leaveDetailList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        val data = leaveDetailList[position]
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
    class LeaveHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}