package com.app.ecarepro.ui.birthday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.BirthdayListItemBinding
import com.app.ecarepro.model.UsersBirthday
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso

class BirthListAdapter(
    private var noticeList: List<UsersBirthday>,
    private val userType: Int,
    private val onItemClick: (UsersBirthday) -> Unit
) :
    RecyclerView.Adapter<BirthListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   BirthdayListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=BirthdayListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<BirthdayListItemBinding>(holder.itemView)


        binding?.apply {

            val data=noticeList[position]

            binding.data=data

            userImg.setOnClickListener {
                onItemClick(data)
            }

            Picasso.get().
            load(data.photo)
                .placeholder(R.drawable.default_profile)
                .  into(binding .userImg)

            if (userType==Constant.STUDENT_TYPE){
                llMother.isVisible=false
                llFatherName.isVisible=false
            } else if (userType==Constant.PARENT_TYPE){
                tvNameHolder.text= "Student Name"
                llBirthdayOf.isVisible=true

            } else if (userType==Constant.STAFF_TYPE){
                llMother.isVisible=false
                llClass.isVisible=false
                tvFatherNameHolder.text="Father/Spouse Name"
                llDesignation.isVisible=true
            }
        }


    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}