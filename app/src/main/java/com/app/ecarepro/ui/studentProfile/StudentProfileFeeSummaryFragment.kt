package com.app.ecarepro.ui.studentProfile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentProfileFeeSummaryBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.model.PaidHistory
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.circuler.PopUpListAdapter
import com.app.ecarepro.ui.studentProfile.StudentProfileAttendanceFragment.Companion
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import com.app.ecarepro.utils.listener.ItemListener
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentProfileFeeSummaryFragment(

) : Fragment() {

    private lateinit var selectedYearData: AcademicYear

    private lateinit var binding: FragmentStudentProfileFeeSummaryBinding
    private val studentProfileFeeSummaryViewModel: StudentProfileFeeSummaryViewModel by viewModels()
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()
    private lateinit var academicYears: List<AcademicYear>
    private lateinit var feeSummery: FeeSummery
    private var studentID: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentID=it.getInt(STUDENT_ID)
        }
    }
    override fun onResume() {
        super.onResume()
        studentProfileFeeSummaryViewModel.sendScreenEvent()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileFeeSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
             feeSummery=it.feeSummery
             academicYears=it.academicYears

            if (academicYears.isNotEmpty()) {
                for (i in academicYears) {
                    if (i.isCur) {
                        selectedYearData=i
                        binding.ctvSelectYear.text = i.session
                        break
                    }}}

            if (feeSummery  != null){
                setupUi(feeSummery )
            }
        }


    }


    private fun popUpPaidHistory(paidHistory: List<PaidHistory>) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.layout_paid_history, null)
        val rvFeeTransaction = view.findViewById<RecyclerView>(R.id.rvFeeTransaction)
        val ivCross = view.findViewById<ImageView>(R.id.ivCross)

        builder.setView(view)


        val popUpListAdapterPaidFee = PopUpListAdapterPaidFee(paidHistory)
        rvFeeTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpListAdapterPaidFee
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun getBarChartModel(received: Int, outstanding: Int, concession: Int) = AAChartModel()

        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true)
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Fee")
                    .size("80%")
                    .innerSize("70%")
                    .borderWidth(0)
                    .allowPointSelect(false)

                    .data(
                        arrayOf(
                            arrayOf("Received ", received),
                            arrayOf("Outstanding ", outstanding),
                            arrayOf("Concession ", concession),

                            )
                    )
            )
        )

    private fun popUpSelectAcademicYears() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text = getString(R.string.select_academic_year)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.ctvSelectYear.text = selectedYearData.session
            getAtt()
            builder.dismiss()

        }

        val yearAdapter = PopUpListAdapter(academicYears, object : ItemListener<AcademicYear> {
            override fun onItemClick(t: AcademicYear, pos: Int, boolean: Boolean) {
                selectedYearData = t
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = yearAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun getAtt() {
        studentProfileFeeSummaryViewModel.getFeeSummaryYrID(studentID, selectedYearData.yrID)

        lifecycleScope.launch {
            studentProfileFeeSummaryViewModel.feeSummeryStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            setupUi(it.data)
                        }
                    }
                }
            }


        }
    }

    private fun setupUi(feeSummery: FeeSummery) {

        with(binding) {

            binding.pieChartView. isClearBackgroundColor = true
            ctvSelectYear.setOnClickListener {
                popUpSelectAcademicYears()
            }

            tvAllPaidHistory.setOnClickListener {
                popUpPaidHistory(feeSummery.paidHistory)
            }

            tvTotalFee.text = feeSummery.totalActualFee.toString()
            tvTotalConcession.text = feeSummery.totalConcession.toString()
            tvTotalOutstanding.text = feeSummery.totalOutstanding.toString()
            tvTotalReceived.text = feeSummery.totalReceived.toString()


           try {
               tvConcessionPer.text = buildString {
                   append("(")
                   append(setCalculatedPercentageToInt(feeSummery.totalConcession.toInt(),feeSummery.totalActualFee.toInt()))
                   append("%)")
               }
               tvOutstandingPer.text = buildString {
                   append("(")
                   append(setCalculatedPercentageToInt(feeSummery.totalOutstanding.toInt(), feeSummery.totalActualFee.toInt()))
                   append("%)")
               }
               tvReceivedPer.text = buildString {
                   append("(")
                   append(setCalculatedPercentageToInt(feeSummery.totalReceived.toInt(), feeSummery.totalActualFee.toInt()))
                   append("%)")
               }
           }catch (e:Exception){}

            if (feeSummery.feeInstallment != null) {
                val assignmentListAdapter =
                    StudentProfileFeeSummeryAdapter(
                        feeSummery.feeInstallment,
                        this@StudentProfileFeeSummaryFragment
                    )

                binding.rvFeePaid.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = assignmentListAdapter
                }
                binding.rlMain.isVisible = true
                binding.tvNoData.isVisible = false


            } else {
                binding.rlMain.isVisible = false
                binding.tvNoData.isVisible = true

            }
            tvAllPaidHistory.isVisible = feeSummery.paidHistory != null
             try {

                binding.pieChartView.aa_drawChartWithChartModel(
                    getBarChartModel(
                        ((feeSummery.totalReceived * 100 / feeSummery.totalActualFee * 100.0).roundToInt()),
                        ((feeSummery.totalOutstanding * 100 / feeSummery.totalActualFee * 100.0).roundToInt()),
                        ((feeSummery.totalConcession * 100 / feeSummery.totalActualFee * 100.0).roundToInt())
                    )
                )
                 showPieChart(feeSummery.totalReceived.toInt(),
                     feeSummery.totalOutstanding.toInt(),
                     feeSummery.totalConcession.toInt())
            } catch (_: Exception) {
            }


        }

    }

    private fun showPieChart(
         totalReceived: Int,
         totalOutstanding: Int,
         totalConcession: Int
    ) {
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setUsePercentValues(false)
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.setDrawMarkerViews(false)

        val yvalues = ArrayList<PieEntry>()
        yvalues.add(PieEntry(totalReceived.toFloat(), 0))
        yvalues.add(PieEntry(totalOutstanding.toFloat(), 1))
        yvalues.add(PieEntry(totalConcession.toFloat(), 2))

        val dataSet = PieDataSet(yvalues, "")
        dataSet.sliceSpace = 2f
        val xVals = ArrayList<String>()
        xVals.add("")
        xVals.add("")
        val data = PieData(dataSet)
        // data.setValueFormatter(new PercentFormatter());
        binding.pieChart.setData(data)
         dataSet.setColors(
            resources.getColor(R.color.green, null),
            resources.getColor(R.color.red, null),
            resources.getColor(R.color.att_na_color, null)
        )


        data.setValueTextSize(13f)
        data.setDrawValues(false)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.animateXY(1400, 1400)

//        val s = """
//            ${totalPresent + totalAbsent + totalLeave}
//            Student(s)
//            """.trimIndent()
//        val length = (totalPresent + totalAbsent + totalLeave).toString() + ""
//        val ss1 = SpannableString(s)
//        ss1.setSpan(RelativeSizeSpan(2f), 0, length.length, 0) // set size
//        ss1.setSpan(
//            ForegroundColorSpan(resources.getColor(R.color.deep_black)),
//            0,
//            3,
//            0
//        ) // set color
//
//        binding.pieChart.centerText = ss1
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.setCenterTextColor(resources.getColor(R.color.deep_black))
        binding.pieChart.holeRadius = 70f
        binding.pieChart.description = null
    }

    private fun setCalculatedPercentageToInt(day: Int, totalDay: Int): Double {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00)
    }

    companion object {
        private const val STUDENT_ID = "student_id_int"

        fun newInstance(   studentID: Int)= StudentProfileFeeSummaryFragment().apply {
            arguments= Bundle().apply {
                putInt(STUDENT_ID,studentID)
            }
        }

    }

}