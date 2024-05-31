package com.app.ecarepro.ui.fee_report.collection.defaulter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentDefaulterReportBinding
import com.app.ecarepro.model.defaulter_report_filter.Classess
import com.app.ecarepro.model.defaulter_report_filter.FeeType
import com.app.ecarepro.model.defaulter_report_filter.Installment
import com.app.ecarepro.model.defaulter_report_filter.School
import com.app.ecarepro.model.defaulter_report_filter.Section
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class DefaulterReportFragment : Fragment() {


    private   var classesListData:   ArrayList<String> =  ArrayList( )
    private   var schoolListData:   ArrayList<String> =  ArrayList( )
    private   var feeTypeListData:   ArrayList<String> =  ArrayList( )
    private   var installmentListData:   ArrayList<String> =  ArrayList( )
    private   var sectionListData:   ArrayList<String> =  ArrayList( )

    private lateinit var classes: List<Classess>
    private lateinit var schoolsLists: List<School>
    private lateinit var feetTypeLists: List<FeeType>
    private lateinit var installmentLists: List<Installment>
    private lateinit var sectionsLists: List<Section>

    private var DateFrom : String = ""
    private var DateTo : String = ""
    private var schoolid : String = ""
    private var feetypeid : String = ""
    private var classid : String = ""
    private var sectionid : String = ""
    private var installid : String = ""

    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

    private lateinit var binding : FragmentDefaulterReportBinding
    private val defaulterFeeReportViewModel : DefaulterFeeReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaulterReportBinding.inflate(inflater,container,false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            dateFrom.setOnClickListener { pickDateRange() }
            dateTo.setOnClickListener { pickDateRange() }
        }

        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                classid = classes[pos].classid
            }
        binding.autoCompleteSelectSchool.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                schoolid = schoolsLists[pos].schoolid
            }
        binding.autoCompleteFeeType.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                feetypeid = feetTypeLists[pos].feetypeid
            }
        binding.autoCompleteSelectInstallment.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                installid = installmentLists[pos].installid
            }
        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                sectionid = schoolsLists[pos].schoolid
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



    private fun getFeeCollection(){
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

                        if (it.data!=null){

                            if (it.data !=null){

                                if(it.data.classes!=null){
                                    classes=it.data.classes

                                    it.data.classes.forEach { data ->
                                        classesListData.add(data.classname)
                                    }
                                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,classesListData)
                                    binding.autoCompleteClass.setAdapter(arrayAdapter)
                                }
                                if(it.data.schools!=null){
                                    schoolsLists=it.data.schools

                                    it.data.schools.forEach { data ->
                                        schoolListData.add(data.schoolname)
                                    }
                                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,schoolListData)
                                    binding.autoCompleteSelectSchool.setAdapter(arrayAdapter)
                                }
                                if(it.data.feetype!=null){
                                    feetTypeLists=it.data.feetype

                                    it.data.feetype.forEach { data ->
                                        feeTypeListData.add(data.feetypename)
                                    }
                                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,feeTypeListData)
                                    binding.autoCompleteFeeType.setAdapter(arrayAdapter)
                                }
                                if(it.data.installment!=null){
                                    installmentLists=it.data.installment

                                    it.data.installment.forEach { data ->
                                        installmentListData.add(data.installmentname)
                                    }
                                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,installmentListData)
                                    binding.autoCompleteSelectInstallment.setAdapter(arrayAdapter)
                                }
                                if(it.data.sections!=null){
                                    sectionsLists=it.data.sections

                                    it.data.sections.forEach { data ->
                                        sectionListData.add(data.sectionname)
                                    }
                                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,sectionListData )
                                    binding.autoCompleteSelectSection.setAdapter(arrayAdapter)
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }
        }
        defaulterFeeReportViewModel.defaulterFilters("")
    }


    private fun getDefaultReport(){
        lifecycleScope.launch {
            defaulterFeeReportViewModel.defaultersDataStateFlow.collectLatest {
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

                        if (it.data!=null){

                            if (it.data!=null){

                                binding.recyclerDefaulterReport.isVisible=true
                                binding.tvNoData.isVisible=false

                                val defaulterReportListAdapter = DefaulterReportListAdapter(it.data  ,
                                    this@DefaulterReportFragment)

                                binding.recyclerDefaulterReport.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = defaulterReportListAdapter
                                }
                            }else{
                                binding.recyclerDefaulterReport.isVisible=false
                                binding.tvNoData.isVisible=true
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


        if (isValidated){
            defaulterFeeReportViewModel.getDefaulterReport(
                "",
                "",
                binding.dateFrom.text.toString(),
                binding.dateTo.text.toString(),
                schoolid,
                feetypeid,
                classid,
                sectionid,
                installid )
        }

    }

}