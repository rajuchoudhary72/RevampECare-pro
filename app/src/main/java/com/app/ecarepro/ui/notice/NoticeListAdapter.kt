package com.app.ecarepro.ui.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.utils.Constant

class NoticeListAdapter(
    private var noticeList: List<Notice>,
    private var noticeListFragment: NoticeListFragment,
    private var noticeType: String
) :
    RecyclerView.Adapter<NoticeListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   NoticeListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=NoticeListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.noticeData=noticeList[position]
        if (noticeType== Constant.NOTICE_CLASS){
            bindingm.llUpdate.isVisible=false
        }else{
            bindingm.llDate.isVisible=false
        }
        bindingm.clMain.setOnClickListener {
            noticeListFragment.onItemClick(noticeList[position],1,true)
        }

    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}