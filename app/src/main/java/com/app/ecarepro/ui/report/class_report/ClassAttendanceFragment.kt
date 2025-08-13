package com.app.ecarepro.ui.report.class_report

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassAttendanceBinding
import com.app.ecarepro.model.AttReport
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClassAttendanceFragment : Fragment()  {

    private var classId: String  =  ""
    private var className: String  =  ""
    private var date: String  =  ""
    private lateinit var binding :  FragmentClassAttendanceBinding
    private val classAttViewModel : ClassAttViewModel by viewModels()
    private   var mMyClass= mutableListOf<MyClasseItem>()
     private   var mMyClassDataString:   ArrayList<String> =  ArrayList( )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentClassAttendanceBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        classId= requireArguments().getString(Constant.CLASS_ID_ARGUMENT).toString()
        className= requireArguments().getString(Constant.NAME).toString()
        date= requireArguments().getString(Constant.DATE).toString()
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startDate.setText(date)
        binding.autoCompleteClass.setText(className,false)

        binding.startDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.startDate.setText(Constant.dateToShow(date.toString()))
                    classAttViewModel.getClassAttendance(classId.toString(),Constant.toSystemDate(binding.startDate.text.toString()))
                }

            }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
        }



        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                classId= mMyClass[pos].id!!
                classAttViewModel.getClassAttendance(classId.toString(),Constant.toSystemDate(binding.startDate.text.toString()))

            }

        lifecycleScope.launch {

            mMyClass.clear()
            mMyClassDataString.clear()

            classAttViewModel._myClassStateFlow.collectLatest {

                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it)
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            if (it.data.myClasses!=null) {
                                mMyClass= it.data.myClasses as MutableList<MyClasseItem>
                                mMyClass.forEach { data ->
                                    mMyClassDataString.add(data.className.toString())
                                }
                                val arrayAdapter= ArrayAdapter(requireContext(),
                                    android.R.layout.simple_list_item_1 ,
                                    mMyClassDataString)
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
                            }
                        }
                    }  } } }

        classAttViewModel.getMyClass(0,1)

        getClassAttendance()

    }


    private fun getClassAttendance(){
        lifecycleScope.launch {
            classAttViewModel.classAttStateFlow.collectLatest {

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
                        if (it.data!=null){

                            if (it.data.attReport!=null) {

                                binding.tvNoData.isVisible=false
                                binding.tabLayout.isVisible=true
                                binding.viewPager.isVisible=true

                                val fragmentList = listOf(
                                    ClassAttSubFragment( it.data.attReport ),
                                    ClassAttSubFragment(getFilterList(it.data.attReport,1,false) ),
                                    ClassAttSubFragment(getFilterList(it.data.attReport, 2, false) ),
                                    ClassAttSubFragment(getFilterList(it.data.attReport, 3, false) ),
                                    ClassAttSubFragment(getFilterList(it.data.attReport, 1, true) )
                                )

                                val viewPagerAdapter = ViewPagerAdapter(
                                    fragmentList,
                                    activity?.supportFragmentManager!!,
                                    lifecycle
                                )
                                binding.viewPager.adapter = viewPagerAdapter


                                TabLayoutMediator(
                                    binding.tabLayout,
                                    binding.viewPager
                                ) { tab, position ->
                                    when (position) {
                                        0 -> {
                                            tab.text = getString(R.string.all)
                                        }
                                        1 -> {
                                            tab.text = getString(R.string.present)
                                        }
                                        2 -> {
                                            tab.text = getString(R.string.general_absent)
                                        }
                                        3 -> {
                                            tab.text = getString(R.string.leave)
                                        }
                                        4 -> {
                                            tab.text = getString(R.string.late)
                                        }
                                    }
                                }.attach()


                            }else{
                                binding.tvNoData.isVisible=true
                                binding.tabLayout.isVisible=false
                                binding.viewPager.isVisible=false
                            }

                        }
                    }  } } }

        classAttViewModel.getClassAttendance(classId.toString(),Constant.toSystemDate(binding.startDate.text.toString()))
    }

    private fun getFilterList(attReport: List<AttReport>, status: Int, isLate: Boolean): List<AttReport> {
        val finalAttReport: ArrayList<AttReport> = ArrayList()
        for ( data in attReport){
           if (status==data.status && isLate==data.isLate){
               finalAttReport.add(data)
           }
        }
        return finalAttReport
    }


}