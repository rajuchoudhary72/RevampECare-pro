package com.app.ecarepro.ui.studentProfile.academic_performance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.model.Subject
import com.app.ecarepro.databinding.FragmentAcademicPerformanceBinding
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class AcademicPerformanceFragment(private val subjets: List<Subject>) : Fragment() {

    private lateinit var binding : FragmentAcademicPerformanceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAcademicPerformanceBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (subjets!=null){

            if (subjets.isNotEmpty()){

                val fragmentList : ArrayList<Fragment> = ArrayList()

                subjets.forEach { itemDat ->
                    fragmentList.add( AcademicPerSubFragment(itemDat  ))
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
                    tab.text = subjets[position].subjectName
                }.attach()




            }

        }

    }
}