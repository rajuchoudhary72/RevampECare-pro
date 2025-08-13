package com.app.ecarepro.ui.assignment.submit_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AssignmentSubmitReportListItemBinding

import com.app.ecarepro.model.AssignSubmitStudent


class SubmitReportAdapter(
    private var activityLST: List<AssignSubmitStudent>,
    private val submitAssignmentFragment: SubmitAssignmentFragment,
    private var listner: (AssignSubmitStudent, Int) -> Unit
) :
    RecyclerView.Adapter<SubmitReportAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: AssignmentSubmitReportListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            AssignmentSubmitReportListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val binding= DataBindingUtil.bind<AssignmentSubmitReportListItemBinding>(holder.itemView)
        binding?.apply {
             tvSubmitOn.text = buildString {
                 append(submitAssignmentFragment.getString(R.string.general_submitted_on))
                 append(activityLST[position].submittedOn)
             }
            tvtittle.text = buildString {
                append("Title  : ")
                append(activityLST[position].submittedOn)
            }
            tvAsgData.text = buildString {

                append(activityLST[position].asgData)
            }
            if (activityLST[position].asgFile.isNullOrEmpty()) {
                llEditDelete.isVisible = false


            }
            val data = activityLST[position]


            llView.setOnClickListener {
                listner(data,1  )
            }
             llDownload.setOnClickListener {
                 listner(data,2  )
            }
        }

  }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}