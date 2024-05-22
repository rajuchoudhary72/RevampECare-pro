package com.app.ecarepro.ui.attendance_section

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ShowAttendanceItemBinding
import com.app.ecarepro.utils.getColorRes

class ShowAttendanceListAdapter(
    private var syllabusLST: List<Attendance>,
) :
    RecyclerView.Adapter<ShowAttendanceListAdapter.NoticeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val bindingm =
            ShowAttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val bindings = DataBindingUtil.getBinding<ShowAttendanceItemBinding>(holder.itemView)
        bindings?.let {
            bindings.attData = syllabusLST[position]
            bindings.sNo = "${position + 1}"
            if (syllabusLST[position].status == 2) {
                bindings.llMain.setBackgroundColor(bindings.llMain.context.getColorRes(R.color.red))
            } else {
                bindings.llMain.setBackgroundColor(bindings.llMain.context.getColorRes(R.color.white))
            }
        }



    }


    class NoticeViewHolder(itemView: ShowAttendanceItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
    }


}