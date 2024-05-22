package com.app.ecarepro.ui.statical

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.ui.statical.StaticalReport.Companion.TotalStaff
import com.app.ecarepro.ui.statical.StaticalReport.Companion.deptWiseStaffArrayList

class DeptWiseStaffStatisticalAdapter(private val context: Context) :
    RecyclerView.Adapter<DeptWiseStaffStatisticalAdapter.ViewHolder>() {
    private val multiColor: IntArray



    init {
        multiColor = context.resources.getIntArray(R.array.multi_color_array)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.item_dept_wise_staff, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tvDeptName.text = deptWiseStaffArrayList[i].department
        viewHolder.tvDeptValue.text = deptWiseStaffArrayList[i].count.toString() + ""
        viewHolder.tvDeptPer.text =
            setCalculatedPercentage(deptWiseStaffArrayList[i].count.toInt(), TotalStaff)
        if (i < 14) viewHolder.viewBox.setBackgroundColor(multiColor[i])
    }

    override fun getItemCount(): Int {
        return deptWiseStaffArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val tvDeptValue: TextView
         val tvDeptPer: TextView
         val tvDeptName: TextView
         val viewBox: View

        init {
            tvDeptValue = itemView.findViewById(R.id.tvDeptValue)
            tvDeptPer = itemView.findViewById(R.id.tvDeptPer)
            tvDeptName = itemView.findViewById(R.id.tvDeptName)
            viewBox = itemView.findViewById(R.id.viewBox)
        }
    }

    private fun setCalculatedPercentage(user: Int, totalUser: Int): String {
        return if (totalUser != 0) (Math.round(user * 100.00 / totalUser * 100.00) / 100.00).toString() + "%" else "0.0%"
    }
}
