package com.app.ecarepro.ui.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Circular
import com.app.ecarepro.model.Notice
import com.app.ecarepro.utils.Constant

class NoticeListAdapter(
    private var noticeList: MutableList<Notice>,
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
        val binding = DataBindingUtil.getBinding<NoticeListItemBinding>(holder.itemView)
            binding?.apply {
                noticeData=noticeList[position]

                if(noticeList[position].isRead == true){
                    cvNotItem.cardElevation=0f
                }else{
                    cvNotItem.cardElevation=20f
                }

                if (noticeType== Constant.NOTICE_CLASS){
                    llUpdate.isVisible=false
                }else{
                     llDate.isVisible=false
                }
                clMain.setOnClickListener {
                    noticeList[position].isRead=true
                    noticeListFragment.onItemClick(noticeList[position],1,true)

                }

            }

    }

    fun setData(noticeList: MutableList<Notice> ){
        this.noticeList.addAll(noticeList)

        notifyDataSetChanged()

    }
    fun clearData(){
        this.noticeList.clear()
        notifyDataSetChanged()
    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}