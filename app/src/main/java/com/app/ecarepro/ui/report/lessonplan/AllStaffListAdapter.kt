package com.app.ecarepro.ui.report.lessonplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StaffListItemBinding
import com.app.ecarepro.model.Staff
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class AllStaffListAdapter(
    private var staffList: List<Staff>,
    private var staffListFragment: AllStaffListFragment
) :
    RecyclerView.Adapter<AllStaffListAdapter.StaffListViewHolder>() {

    private lateinit var bindingm: StaffListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {
        bindingm = StaffListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffListViewHolder(bindingm)
    }

    override fun getItemCount(): Int = staffList.size

    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<StaffListItemBinding>(holder.itemView)
        with(binding!!) {
            staffData = staffList[position]
            val data = staffList[position]

            tvClassName.text = buildString {
                append("( ")
                append(data.designation)
                append(" )")
            }
            tvSubjectName.isVisible=true
            tvSubjectName.text = buildString {
                append("Pending ( ")
                append(data.pending)
                append(" )")
            }
            llMain.setOnClickListener {
                staffListFragment.onItemClick(data, 1, false)
            }

            Picasso.get().load(data.photo)
                .placeholder(R.drawable.default_profile)
                .into(circleImageViewProfile)


        }


    }

    class StaffListViewHolder(itemView: StaffListItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
    }


}