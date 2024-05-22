package com.app.ecarepro.ui.statical

import android.graphics.Typeface.DEFAULT_BOLD
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ViewpagerStudentBinding
import com.app.ecarepro.ui.statical.StaticalReport.Companion.studentArrayList
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class StudentsFragment : Fragment() {
    private lateinit var binding: ViewpagerStudentBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate<ViewpagerStudentBinding?>(inflater, R.layout.viewpager_student, container, false).apply {
            viewBoxColor1.setBackgroundColor(requireContext().resources.getColor(R.color.male_color))
            viewBoxColor2.setBackgroundColor(requireContext().resources.getColor(R.color.female_color))
            chart.setUsePercentValues(false)
            chart.setDrawMarkerViews(false)
        }




        val yvalues = ArrayList<PieEntry>()
        if (studentArrayList.size > 0) {
            yvalues.add(PieEntry(studentArrayList[0].boys, 0))
            yvalues.add(PieEntry(studentArrayList[0].girls, 1))
            val dataSet = PieDataSet(yvalues, "")
            dataSet.sliceSpace = 2f
            val xVals = ArrayList<String>()
            xVals.add("")
            xVals.add("")
            val data = PieData(dataSet)
            // data.setValueFormatter(new PercentFormatter());
            binding.chart.setData(data)
            dataSet.setColors(
                *intArrayOf(
                    resources.getColor(R.color.male_color),
                    resources.getColor(R.color.female_color)
                )
            )
            // circle_dot.setColorFilter(getContext().getResources().getColor(R.color.male_color));
            // circle_dot2.setColorFilter(getContext().getResources().getColor(R.color.female_color));
            data.setValueTextSize(13f)
            data.setDrawValues(false)
            binding.chart.getLegend().isEnabled = false
            binding.chart.animateXY(1400, 1400)
            val total_login: Int = studentArrayList.get(0).total
            val s = "$total_login\nStudents"
            val ss1 = SpannableString(s)
            val len = total_login.toString().length
            ss1.setSpan(RelativeSizeSpan(2f), 0, len, 0) // set size
            ss1.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black)),
                0,
                len,
                0
            ) // set color
            binding.chart.setCenterText(ss1)
            binding.chart.setCenterTextSize(16f)
            binding.chart.setCenterTextColor(resources.getColor(R.color.black))
            binding.chart.setCenterTextTypeface(DEFAULT_BOLD)
            binding.chart.setHoleRadius(70f)
            binding.chart.setDescription(null)
            val android: Float = studentArrayList.get(0).boys * 100
            val ios: Float = studentArrayList.get(0).girls * 100
            val s1: Int
            val s2: Int
            if (total_login > 0) {
                s1 = Math.round(android / total_login)
                s2 = Math.round(ios / total_login)
            } else {
                s1 = 0
                s2 = 0
            }
            binding.percentageA.setText("$s1%")
            binding.percentageA2.setText("$s2%")
            binding.totalAndroid.setText(java.lang.String.valueOf(studentArrayList.get(0).boys))
            binding.totalAndroid2.setText(java.lang.String.valueOf(studentArrayList.get(0).girls))
            /*percentage_a.setTypeface(BOLD);
            percentage_a2.setTypeface(BOLD);
            percentage_a.setTypeface(BOLD);
            total_android.setTypeface(BOLD);
            total_android2.setTypeface(BOLD);*/
        }
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                binding.chart.highlightValues(null)
            }

            override fun onNothingSelected() {}
        })
        return binding.root
    }
}

