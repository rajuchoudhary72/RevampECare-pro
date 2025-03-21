package com.app.ecarepro.ui.know_your_teacher

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

class TeacherListAdapter(private var staffList: List<Staff>,
                         private var staffListFragment: TeacherListFragment
) :
    RecyclerView.Adapter<TeacherListAdapter.StaffListViewHolder>() {

        private lateinit var bindingm:   StaffListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {
        bindingm=StaffListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StaffListViewHolder(bindingm )
    }

    override fun getItemCount(): Int = staffList.size

    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<StaffListItemBinding>(holder.itemView)
        val data= staffList[position]
         binding?.apply {
             staffData=staffList[position]
            tvClassName.text= buildString {
                append("( ")
                append(data.designation)
                append(" )")
            }

             tvSubjectName.isVisible=true
             tvSubjectName.text="Subject: ${data.teachersSubject}"



             Picasso.get().
             load(data.photo)
                 .placeholder(R.drawable.default_profile)
                 .  into(circleImageViewProfile)


        }



           }

    class StaffListViewHolder(itemView: StaffListItemBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}