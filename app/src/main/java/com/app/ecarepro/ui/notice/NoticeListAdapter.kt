package com.app.ecarepro.ui.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Notice
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class NoticeListAdapter(private var noticeList: List<Notice>,
                        private var noticeListFragment: NoticeListFragment) :
    RecyclerView.Adapter<NoticeListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   NoticeListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=NoticeListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.noticeData=noticeList[position]
        bindingm.clMain.setOnClickListener {
            noticeListFragment.onItemClick(noticeList[position],1,true)
        }

    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}