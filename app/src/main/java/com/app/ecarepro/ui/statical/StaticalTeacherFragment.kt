package com.app.ecarepro.ui.statical

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.app.ecarepro.R

import com.app.ecarepro.databinding.ViewpagerTeacherBinding
import com.app.ecarepro.ui.statical.StaticalReport.Companion.deptWiseStaffArrayList
import com.app.ecarepro.ui.statical.StaticalReport.Companion.teachertArrayList

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class StaticalTeacherFragment : Fragment() {
    private lateinit var binding: ViewpagerTeacherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.viewpager_teacher, container, false)



        val deptWiseStaffStatisticalAdapter = DeptWiseStaffStatisticalAdapter(
            requireContext()
        )
        binding.chart.setUsePercentValues(false)
        binding.chart.setDrawMarkerViews(false)
        binding.rvData.isNestedScrollingEnabled = false
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.adapter = deptWiseStaffStatisticalAdapter
        val yvalues = ArrayList<PieEntry>()
        if (deptWiseStaffArrayList.size > 0) {
            for (i in 0 until deptWiseStaffArrayList.size) {
                yvalues.add(PieEntry(deptWiseStaffArrayList.get(i).count, i))
            }
            // yvalues.add(new PieEntry(teachertArrayList.get(0).getMale(), 0));
            //yvalues.add(new PieEntry(teachertArrayList.get(0).getFemale(), 1));
            val dataSet = PieDataSet(yvalues, "")
            dataSet.sliceSpace = 2f
            val xVals = ArrayList<String>()
            xVals.add("")
            xVals.add("")
            val data = PieData(dataSet)
            // data.setValueFormatter(new PercentFormatter());
            binding.chart.setData(data)
            dataSet.setColors(requireContext().resources.getIntArray(R.array.multi_color_array).toList())
            data.setValueTextSize(13f)
            data.setDrawValues(false)
            binding.chart.getLegend().isEnabled = false
            binding.chart.animateXY(2000, 2000)
            val totalStaff: Int = teachertArrayList.get(0).total
            val s = "$totalStaff\nStaff"
            val ss1 = SpannableString(s)
            val len = totalStaff.toString().length
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
            binding.chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            binding.chart.setHoleRadius(70f)
            binding.chart.setDescription(null)
            /*float android = teachertArrayList.get(0).getMale() * 100;
            float ios = teachertArrayList.get(0).getFemale() * 100;

            int s1, s2;
            if (total_login > 0) {
                s1 = Math.round(android / total_login);
                s2 = Math.round(ios / total_login);
            } else {
                s1 = 0;
                s2 = 0;
            }*/
        }
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                binding.chart.highlightValues(null)
            }

            override fun onNothingSelected() {}
        })
        return binding.root
    } /*public static final int[] MY_COLORS = {
            Color.rgb(84, 124, 101), Color.rgb(64, 64, 64), Color.rgb(153, 19, 0),
            Color.rgb(38, 40, 53), Color.rgb(215, 60, 55)
    };*/
}

