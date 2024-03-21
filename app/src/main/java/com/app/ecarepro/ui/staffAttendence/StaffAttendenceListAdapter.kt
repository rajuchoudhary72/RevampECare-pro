package com.app.ecarepro.ui.staffAttendence

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
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.databinding.StaffAttendenceListItemBinding
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Attendance
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.utils.AndroidDownloader

import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class StaffAttendenceListAdapter(
    private var syllabusLST: List<Attendance>,
    private var classSyllabus: AttendanceFragment
) :
    RecyclerView.Adapter<StaffAttendenceListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: StaffAttendenceListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            StaffAttendenceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.attData = syllabusLST[position]



    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}