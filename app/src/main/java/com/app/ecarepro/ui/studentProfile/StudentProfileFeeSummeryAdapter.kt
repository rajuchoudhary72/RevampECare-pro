package com.app.ecarepro.ui.studentProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StudentProfileFeeSummListItemBinding
import com.app.ecarepro.model.FeeInstallment

class StudentProfileFeeSummeryAdapter(
    private var feeInstallmentList: List<FeeInstallment>,
    private var activityCalenderFragment: StudentProfileFeeSummaryFragment
) :
    RecyclerView.Adapter<StudentProfileFeeSummeryAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StudentProfileFeeSummListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StudentProfileFeeSummListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = feeInstallmentList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val data= feeInstallmentList[position]
        with(bindingm) {
            tvInstallment.text= buildString { append(data.installment) }
            tvActualFee.text= buildString{ append(data.actualFee) }
            tvConcession.text= buildString{ append(data.concession) }
            tvReceived.text= buildString { append(data.received) }
            tvOutstanding.text= buildString { append(data.outstanding) }
        }
     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}