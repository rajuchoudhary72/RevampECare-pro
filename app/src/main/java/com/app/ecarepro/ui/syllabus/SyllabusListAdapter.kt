package com.app.ecarepro.ui.syllabus

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
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.utils.AndroidDownloader

import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class SyllabusListAdapter(
    private var syllabusLST: List<SyllabusLST>,
    private var classSyllabus: ClassSyllabus
) :
    RecyclerView.Adapter<SyllabusListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: SyllabusListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            SyllabusListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.syllabusLSTData = syllabusLST[position]

        bindingm.relView.setOnClickListener {

            classSyllabus.onItemClick(syllabusLST[position], 1, true)


        }

        bindingm.relDownload.setOnClickListener {
            try {
                classSyllabus.onItemClick(syllabusLST[position], 2, true)
            }catch (e:SecurityException){
                e.printStackTrace()
            }
        }
    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}