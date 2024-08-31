package com.app.ecarepro.ui.leave.leave_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LeaveReportListItemBinding
import com.app.ecarepro.model.Dtl
import com.squareup.picasso.Picasso

class LeaveReportAdapter(private var leaveList: MutableList<Dtl>,
                         private var leaveReportFragment: LeaveReportFragment
) :
    RecyclerView.Adapter<LeaveReportAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   LeaveReportListItemBinding
        private var canTalkeAction = false
        private var applType: Int=0



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=LeaveReportListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding )
    }

    override fun getItemCount(): Int = leaveList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<LeaveReportListItemBinding>(holder.itemView)
        binding!!.data=leaveList[position]

        with(binding) {
             val data =leaveList[position]

             if (data.attachment!=null){
                 llFile.isVisible=data.attachment.isNotEmpty()
             }

             tvAppliedOn.text= buildString {
                append("Applied On : ")
                append(data.submittedOn)
            }

            tvApproveBy.text= buildString {
                 append(data.teacherName)
            }
            tvApproveOn.text= buildString {
                append(data.actionOn)
            }

            llApplicant.isVisible = data.status != "Pending"

            tvApprove.setOnClickListener {
                leaveReportFragment.onItemClick(data,1,false)
            }
            tvReject.setOnClickListener {
                leaveReportFragment.onItemClick(data,2,false)
            }
            llFile.setOnClickListener {
                leaveReportFragment.onItemClick(data,0,false)
            }

            if (canTalkeAction){
                llApproveRej.isVisible=data.status=="Pending"
            }

            if (applType==3){
                tvApplicant.isVisible=false
                tvApplicantVal.isVisible=false
                textUserName.text= buildString {
                     append(data.applicantName)
                }
                Picasso.get().
                load(data.applicantPhoto)
                    .placeholder(R.drawable.default_profile)
                    .  into(binding .userImg)
                }else{
                textUserName.text= buildString {
                    append(data.studentName)
                }
                Picasso.get().
                load(data.studentPhoto)
                    .placeholder(R.drawable.default_profile)
                    .  into(binding .userImg)
            }

        }
   }

    fun setData(leaveList: MutableList<Dtl>, canTalkeAction: Boolean, applType: Int){
        this.canTalkeAction=canTalkeAction
        this.applType=applType
        this. leaveList.addAll(leaveList)

        notifyDataSetChanged()

    }
    fun clearData(){
        leaveList.clear()
        notifyDataSetChanged()
    }




    class LeaveHistoryViewHolder(itemView: LeaveReportListItemBinding) : RecyclerView.ViewHolder(itemView.root)


}