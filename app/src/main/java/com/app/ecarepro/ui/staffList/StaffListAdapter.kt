package com.app.ecarepro.ui.staffList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StaffListItemBinding
import com.app.ecarepro.model.Staff
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class StaffListAdapter(private var staffList: List<Staff>,
                       private var staffListFragment: StaffListFragment
) :
    RecyclerView.Adapter<StaffListAdapter.StaffListViewHolder>() {

        private lateinit var bindingm:   StaffListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {
        bindingm=StaffListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StaffListViewHolder(bindingm )
    }

    override fun getItemCount(): Int = staffList.size

    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<StaffListItemBinding>(holder.itemView)
         with(binding!!) {
             staffData=staffList[position]
            val data= staffList[position]

            tvClassName.text= buildString {
                append("( ")
                append(data.designation)
                append(" )")
            }
            llMain.setOnClickListener {
                staffListFragment.onItemClick(data,1,false)
            }

             Picasso.get().
             load(data.photo)
                 .placeholder(R.drawable.default_profile)
                 .  into(circleImageViewProfile)


        }



           }

    class StaffListViewHolder(itemView: StaffListItemBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}