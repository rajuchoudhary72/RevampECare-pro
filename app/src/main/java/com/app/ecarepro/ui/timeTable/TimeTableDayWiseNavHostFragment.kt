package com.app.ecarepro.ui.timeTable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.databinding.FragmentTiemTableDayWiseNavHostBinding
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class TimeTableDayWiseNavHostFragment(val data: NetworkTeachersTimetable, val toFragment: String) : Fragment() {


    private lateinit var binding : FragmentTiemTableDayWiseNavHostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentTiemTableDayWiseNavHostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (data!=null){

            if (data.data.isNotEmpty()){

                val fragmentList : ArrayList<Fragment> = ArrayList()

                data.data.forEach { itemDat ->
                    fragmentList.add( DayWiseTimeTableFragment(itemDat, toFragment  ))
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
                    tab.text = data.data[position].day
                }.attach()
                



            }

        }











    }
}