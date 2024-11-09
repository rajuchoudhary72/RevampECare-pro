package com.app.ecarepro.ui.fee_report.collection.estimate

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
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentDefaulterReportBinding

import com.app.ecarepro.filter
import com.app.ecarepro.istimatedata
import com.app.ecarepro.model.defaulter_report_filter.Classess
import com.app.ecarepro.model.defaulter_report_filter.FeeType
import com.app.ecarepro.model.defaulter_report_filter.Installment
import com.app.ecarepro.model.defaulter_report_filter.School
import com.app.ecarepro.model.defaulter_report_filter.Section
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.currentDate
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class EstimateReportFragment : Fragment() {


    private var classesListData: ArrayList<String> = ArrayList()
    private var schoolListData: ArrayList<String> = ArrayList()
    private var feeTypeListData: ArrayList<String> = ArrayList()
    private var installmentListData: ArrayList<String> = ArrayList()
    private var sectionListData: ArrayList<String> = ArrayList()

    private lateinit var classes: List<Classess>
    private lateinit var schoolsLists: List<School>
    private lateinit var feetTypeLists: List<FeeType>
    private lateinit var installmentLists: List<Installment>
    private lateinit var sectionsLists: List<Section>

    private var DateFrom: String = ""
    private var DateTo: String = ""
    private var schoolid: String = "0"
    private var feetypeid: String = "0"
    private var classid: String = "0"
    private var sectionid: String = "0"
    private var installid: String = "0"

    private var dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

    private lateinit var binding: FragmentDefaulterReportBinding
    private val defaulterFeeReportViewModel: EstimateFeeReportViewModel by viewModels()
    var shortDescending = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaulterReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        DateFrom = currentDate()
        DateTo = currentDate()
        binding.dateFrom.text = DateFrom
        binding.dateTo.text = DateTo
        binding.ivFilter.setOnClickListener {
            binding.ivFilter.isVisible = false
            binding.groupFilter.isVisible = true
            binding.recyclerDefaulterReport.isVisible=false
            binding.tvNoData.isVisible=false
        }
        binding.toolbar.setTitle("Estimate Report")
        binding.ivOrder.setOnClickListener {
            shortDescending = !shortDescending
            binding.recyclerDefaulterReport.clear()
            getDefaultReport()
        }
        binding.apply {
            dateFrom.setOnClickListener { pickDateRange() }
            dateTo.setOnClickListener { pickDateRange() }
        }

        binding.cardView.setOnClickListener {
            binding.rvSelectClass.isVisible = !binding.rvSelectClass.isVisible
            binding.ivClass.animate().rotation(if (!binding.rvSelectClass.isVisible) 180f else 270f)
        }
        binding.cardViewSelectSchool.setOnClickListener {
            binding.rvSelectSchool.isVisible = !binding.rvSelectSchool.isVisible
            binding.ivSelectSchool.animate()
                .rotation(if (!binding.rvSelectSchool.isVisible) 180f else 270f)
        }
        binding.cardViewSelectSection.setOnClickListener {
            binding.rvSelectSection.isVisible = !binding.rvSelectSection.isVisible
            binding.ivSelectSection.animate()
                .rotation(if (!binding.rvSelectSection.isVisible) 180f else 270f)
        }
        binding.cardViewSelectFeeType.setOnClickListener {
            binding.rvSelectFeeType.isVisible = !binding.rvSelectFeeType.isVisible
            binding.ivfeeType.animate()
                .rotation(if (!binding.rvSelectFeeType.isVisible) 180f else 270f)
        }
        binding.cardViewSelectInstallment.setOnClickListener {
            binding.rvSelectInstallment.isVisible = !binding.rvSelectInstallment.isVisible
            binding.ivSelectInstallment.animate()
                .rotation(if (!binding.rvSelectInstallment.isVisible) 180f else 270f)
        }
        /*  binding.autoCompleteClass.onItemClickListener=
              AdapterView.OnItemClickListener { parent, view, pos, id ->
                  classid = classes[pos].classid
              }*/
        /* binding.autoCompleteSelectSchool.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, pos, id ->
                 schoolid = schoolsLists[pos].schoolid
             }
         binding.autoCompleteFeeType.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, pos, id ->
                 feetypeid = feetTypeLists[pos].feetypeid
             }
         binding.autoCompleteSelectInstallment.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, pos, id ->
                 installid = installmentLists[pos].installid
             }*/
        /* binding.autoCompleteClass.onItemClickListener=
             AdapterView.OnItemClickListener { parent, view, pos, id ->
                 sectionid = schoolsLists[pos].schoolid
             }*/
        defaulterFilters()
        defaulterFeeReportViewModel.defaulterFilters()
        binding.tvCancel.setOnClickListener { }
        binding.tvSubmit.setOnClickListener {
            binding.groupFilter.isVisible = false
            getDefaultReport()
           /* binding.ivFilter.isVisible = true
            binding.ivOrder.isVisible = true*/
        }

    }

    private fun pickDateRange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(dateFrom.timeInMillis, dateTo.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }


    }


    private fun defaulterFilters() {
        lifecycleScope.launch {
            defaulterFeeReportViewModel.defaultFilterStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerDefaulterReport.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerDefaulterReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerDefaulterReport.isVisible = true

                        if (it.data != null) {

                            classes = it.data.classes


                            binding.rvSelectClass.withModels {
                                it.data.classes.forEachIndexed { position, data ->
                                    classesListData.add(data.classname)
                                    filter {
                                        id(data.classid)
                                        value(data.classname)
                                        selected(classid == data.classid)
                                        onClickContent { _ ->
                                            classid = data.classid
                                            binding.rvSelectClass.requestModelBuild()
                                        }
                                    }
                                }
                            }

                            schoolsLists = it.data.schools
                            binding.rvSelectSchool.withModels {
                                it.data.schools.forEach { data ->
                                    schoolListData.add(data.schoolname)
                                    filter {
                                        id(data.schoolid)
                                        value(data.schoolname)
                                        selected(schoolid == data.schoolid)
                                        onClickContent { _ ->

                                            schoolid = data.schoolid
                                            binding.rvSelectSchool.requestModelBuild()
                                        }
                                    }


                                }
                            }

                            feetTypeLists = it.data.feetype

                            binding.rvSelectFeeType.withModels {
                                it.data.feetype.forEach { data ->
                                    feeTypeListData.add(data.feetypename)
                                    filter {
                                        id(data.feetypeid)
                                        value(data.feetypename)
                                        selected(feetypeid == data.feetypeid)
                                        onClickContent { _ ->

                                            feetypeid = data.feetypeid
                                            binding.rvSelectFeeType.requestModelBuild()
                                        }
                                    }
                                }
                            }
                            installmentLists = it.data.installment
                            if (installmentLists.isNotEmpty())
                                installid = installmentLists[0].installid
                            binding.rvSelectInstallment.withModels {
                                it.data.installment.forEach { data ->
                                    installmentListData.add(data.installmentname)
                                    filter {
                                        id(data.installid)
                                        value(data.installmentname)
                                        selected(installid == data.installid)
                                        onClickContent { _ ->

                                            installid = data.installid
                                            binding.rvSelectInstallment.requestModelBuild()
                                        }
                                    }
                                }
                            }
                            sectionsLists = it.data.sections


                            binding.rvSelectSection.withModels {
                                it.data.sections.forEach { data ->
                                    sectionListData.add(data.sectionname)
                                    filter {
                                        id(data.sectionid)
                                        value(data.sectionname)
                                        selected(sectionid == data.sectionid)
                                        onClickContent { _ ->

                                            sectionid = data.sectionid
                                            binding.rvSelectSection.requestModelBuild()
                                        }
                                    }
                                }
                            }

                        }

                    }

                    else -> {}
                }
            }
        }

    }


    private fun getDefaultReport() {
        lifecycleScope.launch {
            defaulterFeeReportViewModel.estimateDataStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerDefaulterReport.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerDefaulterReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerDefaulterReport.isVisible = true

                        if (it.data != null) {


                            binding.recyclerDefaulterReport.isVisible = it.data.isNotEmpty()
                            binding.tvNoData.isVisible = it.data.isEmpty()

                            binding.recyclerDefaulterReport.withModels {

                                val list =
                                    if (shortDescending) it.data.sortedByDescending { it.duesamount } else it.data.sortedBy { it.duesamount }
                                it.data.forEach {
                                    istimatedata {
                                        id(it.ActualAmount)
                                        estimateData(it)
                                    }
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }
        }


        var isValidated = true
        if (binding.dateFrom.text.toString() == "From Date") {
            isValidated = false
            mainActivity().showMessage("Select From Date")
        }
        if (binding.dateTo.text.toString() == "To Date") {
            isValidated = false
            mainActivity().showMessage("Select To Date")
        }
        if (sectionid == "") {
            isValidated = false
            mainActivity().showMessage("Please Select Section")
        }
        if (installid == "") {
            isValidated = false
            mainActivity().showMessage("Please Select Installment")
        }
        if (feetypeid == "") {
            isValidated = false
            mainActivity().showMessage("Please Select Fee Type")
        }
        if (schoolid == "") {
            isValidated = false
            mainActivity().showMessage("Please Select School")
        }
        if (classid == "") {
            isValidated = false
            mainActivity().showMessage("Please Select Class")
        }


        if (isValidated) {
            defaulterFeeReportViewModel.getEstimateReport(
                binding.dateFrom.text.toString(),
                binding.dateTo.text.toString(),
                schoolid,
                feetypeid,
                classid,
                sectionid,
                installid
            )
        }

    }

}