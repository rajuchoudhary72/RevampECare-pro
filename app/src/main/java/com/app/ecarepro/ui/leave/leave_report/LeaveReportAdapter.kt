package com.app.ecarepro.ui.leave.leave_report

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LeaveReportListItemBinding
import com.app.ecarepro.model.Dtl
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class LeaveReportAdapter(private var leaveList: MutableList<Dtl>,
                         private var leaveReportFragment: LeaveReportFragment
) :
    RecyclerView.Adapter<LeaveReportAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   LeaveReportListItemBinding
        private var canTalkeAction = true
        private var applType: Int=0
        private var status: Int=0



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
             }else{
                 llFile.isVisible=false
             }

             tvAppliedOn.text= buildString {
                append("Applied On : ")
                append(data.submittedOn)
            }
            if (status==1){
                tvHolderApproveBy.text= buildString {
                    append("Approved By : ")
                }
                tvHolderApproveOn.text= buildString {
                    append("Approved On : ")
                }
            }else if (status==2){
                tvHolderApproveBy.text= buildString {
                    append("Rejected By : ")
                }
                tvHolderApproveOn.text= buildString {
                    append("Rejected On : ")
            }}

            tvApproveBy.text= buildString {
                 append(data.teacherName)
            }
            tvApproveOn.text= buildString {
                append(data.actionOn)
            }

            llApplicant.isVisible = data.status != "Pending"

            llApproveRej.isVisible=data.status=="Pending"

           // llApproveRej.isVisible=canTalkeAction



            tvApprove.setOnClickListener {
                leaveReportFragment.onItemClick(data,1,false)
            }
            tvReject.setOnClickListener {
                leaveReportFragment.onItemClick(data,2,false)
            }
            llFile.setOnClickListener {
                leaveReportFragment.onItemClick(data,0,false)
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
                    append(" - ")
                    append(data.studentClass)
                }
                Picasso.get().
                load(data.studentPhoto)
                    .placeholder(R.drawable.default_profile)
                    .  into(binding .userImg)
            }

        }
   }



    fun setData(leaveList: MutableList<Dtl>, canTalkeAction: Boolean, applType: Int, status: Int){
        this.canTalkeAction=canTalkeAction
        this.applType=applType
        this.status=status
        this. leaveList.addAll(leaveList)

        notifyDataSetChanged()

    }
    fun clearData(){
        leaveList.clear()
        notifyDataSetChanged()
    }




    class LeaveHistoryViewHolder(itemView: LeaveReportListItemBinding) : RecyclerView.ViewHolder(itemView.root)


}