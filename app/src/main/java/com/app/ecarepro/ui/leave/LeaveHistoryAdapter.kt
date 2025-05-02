package com.app.ecarepro.ui.leave

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StudentsLeaveListItemBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.utils.Constant

class LeaveHistoryAdapter(
    private var leaveList: List<Dtl>,
    private var leaveHistoryFragment: LeaveHistoryFragment,
    private var userType: Int
) :
    RecyclerView.Adapter<LeaveHistoryAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   StudentsLeaveListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=StudentsLeaveListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding )
    }

    override fun getItemCount(): Int = leaveList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {


        holder.bind(leaveList[position])


   }

    private fun deleteAlert(data: Dtl) {
        val builder = AlertDialog.Builder(leaveHistoryFragment.context)
        builder.setTitle(leaveHistoryFragment.getString(R.string.delete_alert))
        builder.setMessage(leaveHistoryFragment.getString(R.string.delete_alert_are_you_sure))

        builder.setPositiveButton( R.string.yes) { _, _ ->
            leaveHistoryFragment.onItemClick(data,1,false)

        }

        builder.setNegativeButton( R.string.cancel) { _, _ ->

        }


        builder.show()
    }





   inner class LeaveHistoryViewHolder(val binding: StudentsLeaveListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Dtl) {


            binding.data=data


             binding.dot.setOnClickListener {

                deleteAlert(data)
            }

            when (data.status) {
                Constant.LEAVE_APPROVE -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.background_box_rectangle_app_color)
                    binding.dot.isVisible=false
                    binding.tvRejectReason.isVisible=false

                }
                Constant.LEAVE_REJECT -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.tv_bg_rounded_status_red)
                    binding.dot.isVisible=false

                    binding.tvRejectReason.isVisible=true
                    binding.tvRejectReason.text= buildString {
                        append(leaveHistoryFragment.getString(R.string.rejection_reason))
                        append(data.rejectionReason)
                    }

                }
                Constant.LEAVE_PENDING -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.tv_bg_rounded_status_blue)
                    binding.dot.isVisible=true
                    binding.tvRejectReason.isVisible=false

                }
            }
            binding.tvAppliedOn.text= buildString {
                append("Applied On : ")
                append(data.submittedOn)
            }

            if (userType == Constant.STAFF_TYPE) {
                binding.tvTtlLeaves.text= buildString {
                    append("Total Leave(s): ")
                    append(data.duration_str)
                    append(" Day")
                }
            }else{
                binding.tvTtlLeaves.text= buildString {
                    append("Total Leave(s): ")
                    append(data.duration)
                    append(" Day")
                }
            }

            binding.tvReason.text= buildString {
                append("Reason: ")
                append(data.reason)

            }
            binding.tvActionOn.text= buildString {
                append("On: ")
                append(data.actionOn)

            }
            if (data.attachment!=null && data.attachment.isNotEmpty()){
                binding.relViewAttac.isVisible=true
                binding.relViewAttac.setOnClickListener {
                    leaveHistoryFragment.onItemClick(data,2,false)
                }
            }else{
                binding.relViewAttac.isVisible=false
            }




        }
    }


}