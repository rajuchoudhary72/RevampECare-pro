package com.app.ecarepro.ui.birthday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.BirthdayListItemBinding
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.UsersBirthday

class BirthListAdapter(private var noticeList: List<UsersBirthday>,
                       private var noticeListFragment: BirthdayFragment) :
    RecyclerView.Adapter<BirthListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   BirthdayListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=BirthdayListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.data=noticeList[position]


    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}