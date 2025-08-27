package com.app.ecarepro.ui.edit_profile.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ProfileUpdateRecordItemBinding
import com.app.ecarepro.databinding.StaffProfileUpdateRecordItemBinding

class StaffUpdateRecordListAdapter(
    private var syllabusLST: List<com.app.ecarepro.ui.edit_profile.staff.model.ProfileUpdationRecord>,

    ) :
    RecyclerView.Adapter<StaffUpdateRecordListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: StaffProfileUpdateRecordItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = StaffProfileUpdateRecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<StaffProfileUpdateRecordItemBinding>(holder.itemView)
        if (binding != null) {
            binding.tvSrNo.text=(position+1).toString()
            binding.records = syllabusLST[position ]
        }




    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}