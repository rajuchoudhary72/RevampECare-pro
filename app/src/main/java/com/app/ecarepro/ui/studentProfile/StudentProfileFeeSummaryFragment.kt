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
import com.app.ecarepro.utils.listener.ItemListener
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentProfileFeeSummaryFragment(
    private val feeSummery: FeeSummery,
    private val academicYears: List<AcademicYear>,
    private val studentID: Int
) : Fragment() {

    private lateinit var selectedYearData: AcademicYear

    private lateinit var binding: FragmentStudentProfileFeeSummaryBinding
    private val studentProfileFeeSummaryViewModel: StudentProfileFeeSummaryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileFeeSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi(feeSummery)

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
        tvHeading.text = "Select Academic Year"
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


            ctvSelectYear.setOnClickListener {
                popUpSelectAcademicYears()
            }

            tvTotalFee.text = feeSummery.totalActualFee.toString()
            tvTotalConcession.text = feeSummery.totalConcession.toString()
            tvTotalOutstanding.text = feeSummery.totalOutstanding.toString()
            tvTotalReceived.text = feeSummery.totalReceived.toString()

            tvConcessionPer.text = feeSummery.totalConcession.toString()
            tvOutstandingPer.text = feeSummery.totalOutstanding.toString()
            tvReceivedPer.text = feeSummery.totalReceived.toString()

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
            } catch (_: Exception) {
            }


        }

    }

}