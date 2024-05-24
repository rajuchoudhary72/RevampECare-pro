package com.app.ecarepro.ui.leave

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StaffLeaveListItemBinding
import com.app.ecarepro.model.Dtl

class LeaveHistoryAdapter(
    private var leaveList: List<Dtl>,
    private var leaveHistoryFragment: LeaveHistoryFragment
) :
    RecyclerView.Adapter<LeaveHistoryAdapter.LeaveHistoryViewHolder>() {

    private lateinit var binding: StaffLeaveListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding =
            StaffLeaveListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveHistoryViewHolder(binding.root)
    }

    override fun getItemCount(): Int = leaveList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        binding.data = leaveList[position]
        val data = leaveList[position]
        binding.dot.setOnClickListener {
            deleteAlert(position)
        }
        binding.tvAppliedOn.text = buildString {
            append("Applied On : ")
            append(data.submittedOn)
        }
        binding.tvTtlLeaves.text = buildString {
            append("Total Leave(s): ")
            append(data.duration)
            append(" Day")
        }
        binding.tvReason.text = buildString {
            append("Reason: ")
            append(data.reason)

        }
        binding.tvActionOn.text = buildString {
            append("On: ")
            append(data.actionOn)

        }
        binding.relViewAttac.setOnClickListener {
            leaveHistoryFragment.onItemClick(leaveList[position], 2, false)
        }


    }

    private fun deleteAlert(position: Int) {
        val builder = AlertDialog.Builder(leaveHistoryFragment.context)
        builder.setTitle(leaveHistoryFragment.getString(R.string.delete_alert))
        builder.setMessage(leaveHistoryFragment.getString(R.string.delete_alert_are_you_sure))

        builder.setPositiveButton(R.string.yes) { _, _ ->
            leaveHistoryFragment.onItemClick(leaveList[position], 1, false)

        }

        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }


        builder.show()
    }


    class LeaveHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}