package com.app.ecarepro.ui.statical

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaticalReportBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class StaticalReport : Fragment() {
    private lateinit var tab1: Fragment
    private lateinit var tab2: Fragment
    private lateinit var tab3: Fragment
    private lateinit var binding: FragmentStaticalReportBinding
    private val viewModel: StaticalReportViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statical_report, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         studentArrayList.clear()
        teachertArrayList.clear()
       transportArrayList.clear()
       deptWiseStaffArrayList.clear()
        tab1 = newInstance1(0)
        tab2 = newInstance1(1)
        tab3 = newInstance1(2)


        lifecycleScope.launch {
            viewModel.staticGraphResponseStateFlow.collectLatest {
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
                            it.data.studentStatistical?.let {
                                studentArrayList!!.add(it)
                            }
                            it.data.teacherStatistical?.let {
                                    teachertArrayList!!.add(it)
                            }
                            it.data.transporStatistical?.let {
                                transportArrayList!!.add(it)
                            }
                            it.data.staffStatistical?.let {
                                deptWiseStaffArrayList!!.addAll(it.deptwise)
                                TotalStaff=it.total
                            }

                           /* if (response.body().getStaffStatistical() != null) {
                                StaticalReport.TotalStaff =
                                    response.body().getStaffStatistical().getTotal()

                            }*/
                            setupViewPager(binding.viewpager)
                            binding.tabs.setupWithViewPager(binding.viewpager)
                            binding.viewpager.currentItem = 0
                            binding.tabs.setSelectedTabIndicatorColor(resources.getColor(R.color.brand_color))
                            binding.tabs.setTabTextColors(
                                resources.getColor(R.color.module),
                                resources.getColor(R.color.brand_color)
                            )

                        }
                    }


                }
            }
        }

        viewModel.statistical()
    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFrag(tab1, resources.getString(R.string.students))
        adapter.addFrag(tab2, "Staff")
        adapter.addFrag(tab3, resources.getString(R.string.transport))
        viewpager.adapter = adapter
    }

    inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(
        manager!!
    ) {
        private val mFragmentList: MutableList<Fragment?> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> tab1
                1 -> tab2
                2 -> tab3
                else->tab1

            }
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment?, title: String) {
            try {
                mFragmentList.add(fragment)
                mFragmentTitleList.add(title)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        var studentArrayList= mutableListOf<StudentStatistical>()
        var teachertArrayList= mutableListOf<TeacherStatistical>()
        var transportArrayList= mutableListOf<TransporStatistical>()
        var deptWiseStaffArrayList= mutableListOf<Deptwise>()
         var TotalStaff = 0
        fun newInstance1(fragValue: Int): Fragment {

            when (fragValue) {
                0 -> return StudentsFragment()
                1 -> return StaticalTeacherFragment()
                2 -> return TransportFragment()
            }
            return StudentsFragment()
        }
    }
}