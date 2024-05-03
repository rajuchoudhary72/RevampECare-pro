package com.app.ecarepro.ui.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentAttedanceReportBinding
import com.app.ecarepro.model.ClassSummary
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentAttendanceSummeryFragment : Fragment() , ItemListener<ClassSummary> {

    private lateinit var binding: FragmentStudentAttedanceReportBinding
    private val studentAttRepoViewModel: StudentAttSummeryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentAttedanceReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text=Constant.currentDate()

        binding.tvDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvDate.text = date
                    studentAttRepoViewModel.getAttendanceSummary( binding.tvDate.text.toString() )
                }

            })
        }

        getStudentAttRepo()

    }

    private fun getStudentAttRepo() {

        lifecycleScope.launch {
            studentAttRepoViewModel.attSummeryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAttReport.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttReport.isVisible = true

                        if (it.data != null) {

                            if (it.data.classSummary != null){
                                setupAttDeatils(it.data)
                                if (it.data.classSummary.isNotEmpty()) {
                                    binding.rvAttReport.isVisible = true

                                    val studentRepoAttAdapter = StudentRepoAttAdapter(
                                        it.data.classSummary,
                                        this@StudentAttendanceSummeryFragment
                                    )

                                    binding.rvAttReport.apply {
                                        setHasFixedSize(true)
                                        layoutManager = GridLayoutManager(activity, 2)
                                        adapter = studentRepoAttAdapter
                                    }
                                } else {
                                    binding.rvAttReport.isVisible = false
                                }
                            }   } }  }    }
                         }

        studentAttRepoViewModel.getAttendanceSummary( binding.tvDate.text.toString() )

    }

    private fun setupAttDeatils(data: NetworkAttedanceSummary) {
        val totalStudent: Double =
            (data.totalPresent + data.totalAbsent + data.totalLeave + data.totalLate).toDouble()


        with(binding) {

            tvAbsentCount.text = buildString {
                 append(data.totalAbsent)
            }
            tvLateCount.text = buildString {
                 append(data.totalLate)
            }
            tvLeaveCount.text = buildString {
                 append(data.totalLeave)
            }
            tvPresentCount.text = buildString {
                 append(data.totalPresent)
            }

            try{
                tvPresentPer.text = buildString {
                    append(
                        ((data.totalPresent * 100 / totalStudent * 100.0).roundToInt() / 100.0).toString()
                    )
                    append("%")
                }

                tvAbsentPer.text = buildString {
                    append(
                        ((data.totalAbsent * 100 / totalStudent * 100.0).roundToInt() / 100.0).toString()
                    )
                    append("%")
                }

                tvLeavePer.text = buildString {
                    append(
                        ((data.totalLeave * 100 / totalStudent * 100.0).roundToInt() / 100.0).toString()
                    )
                    append("%")
                }
                tvLatePer.text = buildString {
                    append(
                        ((data.totalLate * 100 / totalStudent * 100.0).roundToInt() / 100.0).toString()
                    )
                    append("%")
                }

                binding.pieChartView.aa_drawChartWithChartModel(getBarChartModel(
                    ((data.totalPresent * 100 / totalStudent * 100.0).roundToInt()) ,
                    ((data.totalLeave * 100 / totalStudent * 100.0).roundToInt())  ,
                    ((data.totalAbsent * 100 / totalStudent * 100.0).roundToInt()) ,
                    ((data.totalLate * 100 / totalStudent * 100.0).roundToInt() )
                ))


            } catch (_: Exception){

            }


        }


    }

    private fun getBarChartModel(present: Int,leave: Int,absent: Int,late: Int) = AAChartModel()

        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true)
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Student")
                    .size("80%")
                    .innerSize("70%")
                    .borderWidth(0)
                    .allowPointSelect(false)

                    .data(
                        arrayOf(
                            arrayOf("Present", present),
                            arrayOf("Leave", leave),
                            arrayOf("Absent", absent),
                            arrayOf("Late", late)
                        )
                    )
            )
        )

    override fun onItemClick(t: ClassSummary, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.action_studentAttendanceReportFragment_to_classAttendanceFragment,
            Bundle().apply {
                putString(Constant.CLASS_ID_ARGUMENT , t.id)
            })
    }


}