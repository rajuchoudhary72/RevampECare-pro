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
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ViewpagerStudentBinding
import com.app.ecarepro.ui.statical.StaticalReport.Companion.transportArrayList
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class TransportFragment : Fragment() {
    private lateinit var binding: ViewpagerStudentBinding
    private lateinit var pieChart: PieChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.viewpager_student, container, false)

        binding.android.setText(resources.getString(R.string.school_bus))
        val female = requireActivity().resources.getDrawable(R.drawable.school_bus_st)
        binding.android.setCompoundDrawablesWithIntrinsicBounds(female, null, null, null)

        binding.viewBoxColor1.setBackgroundColor(requireContext().resources.getColor(R.color.approved))
        binding.viewBoxColor2.setBackgroundColor(requireContext().resources.getColor(R.color.Self_transprt_color))

        binding.android2.setText(resources.getString(R.string.self_bus))
        //        ios.setTextSize(15);
        val male = requireActivity().resources.getDrawable(R.drawable.self_st)
        binding.android2.setCompoundDrawablesWithIntrinsicBounds(male, null, null, null)
        //ios.setTypeface(regular);
        pieChart = binding.chart
        pieChart.setUsePercentValues(false)
        //
        pieChart.setDrawMarkerViews(false)
        //
        val yvalues = ArrayList<PieEntry>()
        if (transportArrayList.size > 0) {
            yvalues.add(PieEntry(transportArrayList.get(0).schoolBus, 0))
            yvalues.add(PieEntry(transportArrayList.get(0).selfConvene, 1))
            val dataSet = PieDataSet(yvalues, "")
            dataSet.sliceSpace = 2f
            val xVals = ArrayList<String>()
            xVals.add("")
            xVals.add("")
            val data = PieData(dataSet)
            // data.setValueFormatter(new PercentFormatter());
            pieChart.setData(data)
            dataSet.setColors(
                *intArrayOf(
                    resources.getColor(R.color.android),
                    resources.getColor(R.color.android)
                )
            )
            data.setValueTextSize(13f)
            data.setDrawValues(false)
            pieChart.getLegend().isEnabled = false
            pieChart.animateXY(1400, 1400)
            val total_login: Int = transportArrayList.get(0).total
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
            pieChart.setCenterText(ss1)
            pieChart.setCenterTextSize(16f)
            pieChart.setCenterTextColor(resources.getColor(R.color.black))
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            pieChart.setHoleRadius(70f)
            pieChart.setDescription(null)
            val android: Float = transportArrayList.get(0).schoolBus * 100
            val ios: Float = transportArrayList.get(0).selfConvene * 100
            val s1: Int
            val s2: Int
            if (total_login > 0) {
                s1 = Math.round(android / total_login)
                s2 = Math.round(ios / total_login)
            } else {
                s1 = 0
                s2 = 0
            }
            binding.percentageA2.setText("$s1%")
            binding.percentageA2.setText("$s2%")
            binding.totalAndroid.setText(
                java.lang.String.valueOf(
                    transportArrayList.get(0).schoolBus
                )
            )
            binding.totalAndroid2.setText(
                java.lang.String.valueOf(
                    transportArrayList.get(0).selfConvene
                )
            )
            /* percentage_a.setTypeface(BOLD);
            percentage_a2.setTypeface(BOLD);
            percentage_a.setTypeface(BOLD);
            total_android.setTypeface(BOLD);
            total_android2.setTypeface(BOLD);*/
        }
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                pieChart.highlightValues(null)
            }

            override fun onNothingSelected() {}
        })
        return binding.root
    }

    companion object {
        const val DIALOG_LOADING = 1
    }
}

