package com.app.ecarepro.ui.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.CalenderListItemBinding
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Activity
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.utils.AndroidDownloader

import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class CalenderListAdapter(
    private var activityLST: List<Activity>,
    private var activityCalenderFragment: ActivityCalenderFragment
) :
    RecyclerView.Adapter<CalenderListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: CalenderListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            CalenderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.dataActivity = activityLST[position]




    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}