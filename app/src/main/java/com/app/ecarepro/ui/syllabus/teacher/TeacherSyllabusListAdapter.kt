package com.app.ecarepro.ui.syllabus.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.TeacherSyllabusItemBinding
import com.app.ecarepro.model.Syllabuse

class TeacherSyllabusListAdapter(
    private var syllabusLST: List<Syllabuse>,
    private var classSyllabus: TeacherSyllabusFragment,
   private var showDeleteEdit: Boolean
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

             llEdit.isVisible=showDeleteEdit
             llDelete.isVisible=showDeleteEdit

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