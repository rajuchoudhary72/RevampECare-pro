package com.app.ecarepro.ui.leave.apply_leave

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LeaveListItemBinding
import com.app.ecarepro.model.Dtl

class StudentLeaveHistoryAdapter(private var leaveList: List<Dtl>,
                                 private var applyLeaveFragment: ApplyLeaveFragment
) :
    RecyclerView.Adapter<StudentLeaveHistoryAdapter.LeaveHistoryViewHolder>() {

        private lateinit var binding:   LeaveListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveHistoryViewHolder {
        binding=LeaveListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaveHistoryViewHolder(binding.root)
    }

    override fun getItemCount(): Int = leaveList.size

    override fun onBindViewHolder(holder: LeaveHistoryViewHolder, position: Int) {

        binding.data=leaveList[position]
        binding.dot.setOnClickListener {


            deleteAlert(position)
        }


   }

    private fun deleteAlert(position: Int) {
        val builder = AlertDialog.Builder(applyLeaveFragment.context)
        builder.setTitle(applyLeaveFragment.getString(R.string.delete_alert))
        builder.setMessage(applyLeaveFragment.getString(R.string.delete_alert_are_you_sure))

        builder.setPositiveButton( R.string.yes) { _, _ ->
          //  applyLeaveFragment.onItemClick(leaveList[position],1,false)

        }

        builder.setNegativeButton( R.string.cancel) { _, _ ->

        }


        builder.show()
    }





    class LeaveHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}