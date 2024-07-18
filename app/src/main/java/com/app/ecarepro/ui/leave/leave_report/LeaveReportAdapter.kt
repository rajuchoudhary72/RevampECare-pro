package com.app.ecarepro.ui.leave.leave_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.LeaveReportListItemBinding
import com.app.ecarepro.model.Dtl

class LeaveReportAdapter(private var leaveList: MutableList<Dtl>,
                         private var leaveReportFragment: LeaveReportFragment
) :
    RecyclerView.Adapter<LeaveReportAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   LeaveReportListItemBinding
        private var canTalkeAction = false



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

             tvAppliedOn.text= buildString {
                append("Applied On : ")
                append(data.submittedOn)
            }

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




        }






   }

    fun setData(leaveList: MutableList<Dtl>, canTalkeAction: Boolean){
        this.canTalkeAction=canTalkeAction
        this. leaveList.addAll(leaveList)

        notifyDataSetChanged()

    }
    fun clearData(){
        leaveList.clear()
        notifyDataSetChanged()
    }




    class LeaveHistoryViewHolder(itemView: LeaveReportListItemBinding) : RecyclerView.ViewHolder(itemView.root)


}