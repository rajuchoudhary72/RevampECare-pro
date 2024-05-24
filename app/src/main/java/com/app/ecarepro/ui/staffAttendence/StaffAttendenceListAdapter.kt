package com.app.ecarepro.ui.staffAttendence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StaffAttendenceListItemBinding
import com.app.ecarepro.model.Attendance

class StaffAttendenceListAdapter(
    private var syllabusLST: List<Attendance>,
    private var classSyllabus: AttendanceFragment
) :
    RecyclerView.Adapter<StaffAttendenceListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: StaffAttendenceListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            StaffAttendenceListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        bindingm.attData = syllabusLST[position]


    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}