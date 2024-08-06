package com.app.ecarepro.ui.edit_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.PrevExamItemBinding
import com.app.ecarepro.databinding.ProfileUpdateRecordItemBinding
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.ui.edit_profile.model.ProfileUpdationRecord

class UpdateRecordListAdapter(
    private var syllabusLST: List<ProfileUpdationRecord>,

) :
    RecyclerView.Adapter<UpdateRecordListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ProfileUpdateRecordItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ProfileUpdateRecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<ProfileUpdateRecordItemBinding>(holder.itemView)
        if (binding != null) {
            binding.tvSrNo.text=position.toString()
            binding.records = syllabusLST[position]
        }




    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}