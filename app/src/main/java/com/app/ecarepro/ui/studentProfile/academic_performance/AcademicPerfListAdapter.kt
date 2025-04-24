package com.app.ecarepro.ui.studentProfile.academic_performance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemTransAttendanceBinding
import com.app.ecarepro.model.Mark
import com.app.ecarepro.model.Subject

class AcademicPerfListAdapter(
    private var markList: List<Subject>,
    private var isExpanded: Boolean
) :
    RecyclerView.Adapter<AcademicPerfListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: ItemTransAttendanceBinding
    private val expandedStateMap = mutableMapOf<String, Boolean>()



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

            binding.llView.isVisible= isExpanded
            if (isExpanded) {
                ivExpandButton.setImageResource(R.drawable.keyboard_arrow_up_24dp)
            } else {
                ivExpandButton.setImageResource(R.drawable.keyboard_arrow_down_24dp)
            }
            rlHead.setOnClickListener {
                if (llView.visibility == View.VISIBLE) {
                    llView.visibility = View.GONE
                    ivExpandButton.setImageResource(R.drawable.keyboard_arrow_down_24dp)
                    } else {
                    llView.visibility = View.VISIBLE
                    ivExpandButton.setImageResource(R.drawable.keyboard_arrow_up_24dp)
                }
            }

        }


     }

    fun getSubject(position: Int): String {
        return markList[position].subjectName.toString()
    }

    fun isFirstInGroup(position: Int): Boolean {
        if (position == 0) return true
        return markList[position].subjectName != markList[position - 1].subjectName
    }

     fun addLayout(mLinearLayout: LinearLayout, items: List<Mark>?, position: Int) {
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