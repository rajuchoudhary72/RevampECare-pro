package com.app.ecarepro.ui.syllabus.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.databinding.TeacherSyllabusItemBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.model.Syllabuse
import com.app.ecarepro.utils.AndroidDownloader

import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class TeacherSyllabusListAdapter(
    private var syllabusLST: List<Syllabuse>,
    private var classSyllabus: TeacherSyllabusFragment
) :
    RecyclerView.Adapter<TeacherSyllabusListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: TeacherSyllabusItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            TeacherSyllabusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<TeacherSyllabusItemBinding>(holder.itemView)

         binding?.apply {
              syllabusData=syllabusLST[position]

             llView.setOnClickListener {
                 classSyllabus.onItemClick(syllabusLST[position], 1, true)
             }
             llDownload.setOnClickListener {
                 classSyllabus.onItemClick(syllabusLST[position], 2, true)
             }

             llEdit.setOnClickListener {
                 classSyllabus.onItemClick(syllabusLST[position], 3, true)
             }
             llDelete.setOnClickListener {
                 classSyllabus.onItemClick(syllabusLST[position], 4, true)
             }

         }

    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}