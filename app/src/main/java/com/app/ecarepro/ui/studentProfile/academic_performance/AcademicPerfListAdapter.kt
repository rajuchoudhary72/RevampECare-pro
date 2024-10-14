package com.app.ecarepro.ui.studentProfile.academic_performance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AcademicPerfListItemBinding
import com.app.ecarepro.databinding.ItemTransAttendanceBinding
import com.app.ecarepro.model.Mark
import com.app.ecarepro.model.StuLst
import com.app.ecarepro.model.Subject
import com.squareup.picasso.Picasso

class AcademicPerfListAdapter(
    private var markList: List<Subject>
) :
    RecyclerView.Adapter<AcademicPerfListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: ItemTransAttendanceBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm = ItemTransAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = markList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
         val binding = DataBindingUtil.getBinding<ItemTransAttendanceBinding>(holder.itemView)
        binding?.apply {
            tvStopName.text=markList[position].subjectName
            addLayout(binding.llView,markList[position].marks,position)

        }



     }

    private fun addLayout(mLinearLayout: LinearLayout, items: List<Mark>?, position: Int) {
        mLinearLayout.removeAllViews()
        if (!items.isNullOrEmpty()) {
            var i = 0
            while (items.size > i) {

                val item: Mark = items[i]
                val v: View = LayoutInflater.from(mLinearLayout.context)
                    .inflate(R.layout.academic_perf_list_item, mLinearLayout, false)
                val examName = v.findViewById<TextView>(R.id.tv_examName)
                val marksObtained = v.findViewById<TextView>(R.id.tv_marksObtained)
                val maximumMarks = v.findViewById<TextView>(R.id.tv_maximumMarks)


                examName.text = item.examName
                marksObtained.text = item.marksObtained
                maximumMarks.text = item.maximumMarks

                mLinearLayout.addView(v)
                i++

            }
        }
    }

    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}