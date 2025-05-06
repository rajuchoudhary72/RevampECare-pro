package com.app.ecarepro.ui.staffList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StaffListItemBinding
import com.app.ecarepro.model.Staff
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso

class StaffListAdapter(
    private var staffList: List<Staff>,
    private var staffListFragment: StaffListFragment,
    private val toFragment: String
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


             binding.llButton.isVisible=toFragment==Constant.PROFILE_FRA_STAFF_INFRACTION

            val data= staffList[position]

            tvClassName.text= buildString {
                append("( ")
                append(data.designation)
                append(" )")
            }
             tvSubjectName.text= buildString {
                 append(staffListFragment.getString(R.string.mobile_pun_bold))
                 append(data.mobile)
             }

             binding.tvQualification.text= buildString {
                 append(staffListFragment.getString(R.string.qualification_pun_bold))
                 append(data.qualification)
             }



            llMain.setOnClickListener {
                staffListFragment.onItemClick(data,1,false)
            }

             Picasso.get().
             load(data.photo)
                 .placeholder(R.drawable.default_profile)
                 .  into(circleImageViewProfile)

             binding.cvView.setOnClickListener {
                 staffListFragment.findNavController().navigate(
                     R.id.infractionListFragment,
                     Bundle().apply {
                         putInt(Constant.USER_TYPE, Constant.STAFF_TYPE)
                         putInt(Constant.USER_ID, data.sid)
                     })
             }

             binding.cvAdd.setOnClickListener {
                 staffListFragment.findNavController().navigate(
                     R.id.addInfractionFragment,
                     Bundle().apply {
                         putInt(Constant.USER_TYPE, Constant.STAFF_TYPE)
                         putInt(Constant.USER_ID, data.sid)
                     })

             }


        }



           }

    class StaffListViewHolder(itemView: StaffListItemBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}