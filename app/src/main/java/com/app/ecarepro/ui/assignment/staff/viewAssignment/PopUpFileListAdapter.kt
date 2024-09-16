package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FileListItemBinding

import com.app.ecarepro.databinding.SubmittedStuListBinding
 import com.app.ecarepro.model.AssignSubmitStudent


class PopUpFileListAdapter(
    private var activityLST: List<String>,
    private var listner :(String,Int) -> Unit
) :
    RecyclerView.Adapter<PopUpFileListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: FileListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            FileListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        val binding= DataBindingUtil.bind<FileListItemBinding>(holder.itemView)
        binding?.apply {
             tvNumber.text = (position + 1).toString()
            val data = activityLST[position]


            llView.setOnClickListener {
                listner(data,1 )
            }
             llDownload.setOnClickListener {
                 listner(data,2 )
            }
        }

  }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}