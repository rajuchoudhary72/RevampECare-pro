 package com.app.ecarepro.ui.studentProfile.academic_performance

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAcademicPerformanceNavHostBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.circuler.PopUpListAdapter
import com.app.ecarepro.utils.listener.ItemListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


 @AndroidEntryPoint
 class AcademicPerformanceNavHostFragment(
     private val academicYears: List<AcademicYear>,
     private val studentID: Int) : Fragment() {

    private lateinit var binding: FragmentAcademicPerformanceNavHostBinding
    private val academicPerViewModel : AcademicPerViewModel by viewModels()
     private lateinit var selectedYearData: AcademicYear


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentAcademicPerformanceNavHostBinding.inflate(inflater,container,false)
        return binding.root
    }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         binding. ctvSelectYear.setOnClickListener {
             popUpSelectAcademicYears()
         }

         if (academicYears!=null){
             academicYears.forEach { a->
                 if (a.isCur){
                     selectedYearData=a
                 }
             }
             getAcademicPerf()
         }




     }


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
             getAcademicPerf()
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

     private fun getAcademicPerf() {

         lifecycleScope.launch {
             academicPerViewModel.studentProfileStateFlow.collectLatest {

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

                         if (it.data != null) {

                             if (it.data.examSystems!=null ) {

                                 val fragmentList : ArrayList<Fragment> = ArrayList()


                                 for (a in it.data.examSystems ){
                                     fragmentList.add( AcademicPerformanceFragment( a.subjets))
                                 }



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

                                     tab.text = it.data.examSystems[position].exmStmName

                                 }.attach()


                             }

                         }

                     }

                     else -> {}
                 }


             }
         }

         academicPerViewModel.getAcademicPerformance(studentID,selectedYearData.yrID)



     }

 }