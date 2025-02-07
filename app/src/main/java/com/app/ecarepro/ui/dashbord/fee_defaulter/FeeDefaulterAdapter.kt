package com.app.ecarepro.ui.dashbord.fee_defaulter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.FeeDefaulterDashboardListItemBinding
import com.app.ecarepro.databinding.LeaveSettingListItemBinding
import com.app.ecarepro.model.FeeDefaulter
import com.app.ecarepro.model.LeaveDetail
import kotlin.math.roundToInt

class FeeDefaulterAdapter(private var leaveDetailList: List<FeeDefaulter>) :
    RecyclerView.Adapter<FeeDefaulterAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   FeeDefaulterDashboardListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=FeeDefaulterDashboardListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding )
    }

    override fun getItemCount(): Int = leaveDetailList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        holder.bind(leaveDetailList[position])

    }
  inner  class LeaveHistoryViewHolder( val binding: FeeDefaulterDashboardListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(data: FeeDefaulter) {

            binding.tvClassName.text=data.className
            binding.tvAmount.text=data.amount

            binding.tvTotalStudents.text= buildString {
                append("Total: ")
                append(data.totalStudent) }

            binding.tvDefaulterStudents.text= buildString {
                append("Defaulter: ")
                append(data.defaulterCount) }

            binding.tvPercent.text= buildString {
                append(setCalculatedPercentageToInt(data.defaulterCount!!,data.totalStudent!!))
                append("%") }

            binding.progressBar.max= data.totalStudent!!
            binding.progressBar.progress= data.defaulterCount!!


        }

      private fun setCalculatedPercentageToInt(day: Int, totalDay: Int): Double {
          return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00)
      }

    }




}