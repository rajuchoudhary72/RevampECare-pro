package com.app.ecarepro.ui.studentProfile.report_card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.app.ecarepro.databinding.FragmentReportCardNavHostBinding
import com.app.ecarepro.model.ReportClasse
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.reportCard.ReportCardDetailsFragment
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StudentProfileReportCardFragment : Fragment() {

    private lateinit var binding:  FragmentReportCardNavHostBinding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentReportCardNavHostBinding.inflate(inflater,container,false)
        binding.toolbar.isVisible=false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).showLoader(false)
        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            val reportCardDTLs=it.reportCardDTLs
            if (!reportCardDTLs.isNullOrEmpty()) {

                val fragmentList : ArrayList<Fragment> = ArrayList()

                reportCardDTLs. forEach { itemDat ->
                    fragmentList.add( ReportCardDetailsFragment.newInstance(itemDat))
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

                    tab.text = reportCardDTLs[position].className

                }.attach()


            }else{
                binding.viewPager.isVisible = false
                binding.tvNoData.isVisible = true
                binding.tabLayout.isVisible = false
            }
        }

    }
}