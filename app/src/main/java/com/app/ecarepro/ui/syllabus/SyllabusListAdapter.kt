package com.app.ecarepro.ui.syllabus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SyllabusListItemBinding
import com.app.ecarepro.model.SyllabusLST

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

            classSyllabus.onItemClick(syllabusLST[position], 2, true)


        }


    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}