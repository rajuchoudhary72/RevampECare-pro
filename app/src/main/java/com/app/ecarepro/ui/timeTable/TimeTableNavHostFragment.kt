package com.app.ecarepro.ui.timeTable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentTimeTableNavHostBinding
import com.app.ecarepro.model.TimeTableData
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class TimeTableNavHostFragment : Fragment() {

    private lateinit var binding : FragmentTimeTableNavHostBinding
    private val timeTableNavHostViewModel : TimeTableNavHostViewModel by viewModels()
    private   var staffId: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentTimeTableNavHostBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            staffId= requireArguments().getString(Constant.STAFF_ID_ARGUMENT).toString()
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            timeTableNavHostViewModel.timeTableStateFlow.collectLatest {

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

                            if (it.data.data!=null ) {

                                val fragmentList : ArrayList<Fragment> = ArrayList()



                               // fragmentList.add( DayWiseTimeTableFragment( todayData(it.data.data)))
                                fragmentList.add( TimeTableDayWiseNavHostFragment(it.data))

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

                                  /* if (position==0){
                                       tab.text =  "Today"
                                   }else*/ if (position==0) {
                                       tab.text =  "Day Wise"
                                   }




                                }.attach()


                            }

                        }

                    }

                    else -> {}
                }


            }
        }

        timeTableNavHostViewModel.teachersTimetable(staffId)

    }

    private fun todayData(data: List<TimeTableData>) : TimeTableData {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        lateinit var  timeTableData : TimeTableData

        data.forEach {itemData ->
            if (dayOfTheWeek==itemData.day){
                timeTableData=itemData
            } }
        return timeTableData
    }
}