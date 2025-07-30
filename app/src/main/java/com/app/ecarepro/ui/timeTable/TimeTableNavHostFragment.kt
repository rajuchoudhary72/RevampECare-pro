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
import com.app.ecarepro.R
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
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class TimeTableNavHostFragment : Fragment() {

    private lateinit var binding: FragmentTimeTableNavHostBinding
    private val timeTableNavHostViewModel: TimeTableNavHostViewModel by viewModels()
    private var id: String = ""
    private var toFragment: String = ""
    private var name: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeTableNavHostBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            id = requireArguments().getString(Constant.ID).toString()
            toFragment = requireArguments().getString(Constant.TIME_TABLE_TYPE).toString()
            name = requireArguments().getString(Constant.NAME).toString()

            if (name.isEmpty() || name == "null") {
                binding.includeToolbar.toolbarTitle.text = getString(R.string.timetable)
            } else {
                binding.includeToolbar.toolbarTitle.text = getString(R.string.timetable_of, name)
            }

        } catch (_: Exception) {
        }
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
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            val data = it.data.data

                            if (data != null) {

                                if (data.isNotEmpty()) {

                                    binding.tabLayout.visibility = View.VISIBLE
                                    binding.viewPager.visibility = View.VISIBLE
                                    binding.tvNoData.visibility = View.GONE

                                    val fragmentList: ArrayList<Fragment> = ArrayList()

                                    data.forEach { itemDat ->
                                        fragmentList.add(
                                            DayWiseTimeTableFragment.newInstance(
                                                itemDat,
                                                toFragment
                                            )
                                        )
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
                                        tab.text = data[position].day
                                    }.attach()

                                      // Now set the default tab to today
                                    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                                    val tabIndex = when (today) {
                                        Calendar.MONDAY -> 0
                                        Calendar.TUESDAY -> 1
                                        Calendar.WEDNESDAY -> 2
                                        Calendar.THURSDAY -> 3
                                        Calendar.FRIDAY -> 4
                                        Calendar.SATURDAY -> 5
                                        Calendar.SUNDAY -> 6
                                        else -> 0
                                    }

                                    binding.viewPager.setCurrentItem(tabIndex, false)


                                } else {
                                    binding.tabLayout.visibility = View.GONE
                                    binding.viewPager.visibility = View.GONE
                                    binding.tvNoData.visibility = View.VISIBLE
                                }

                            } else {
                                binding.tabLayout.visibility = View.GONE
                                binding.viewPager.visibility = View.GONE
                                binding.tvNoData.visibility = View.VISIBLE
                            }
                        } else {
                            binding.tabLayout.visibility = View.GONE
                            binding.viewPager.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }


                    }

                }


            }
        }

        if (toFragment == Constant.CLASS_TIME_TABLE) {
            if (id == "null") {
                timeTableNavHostViewModel.classTimetable(null)
            } else {
                timeTableNavHostViewModel.classTimetable(id)
            }

        } else {

            timeTableNavHostViewModel.teachersTimetable(id)

        }


        /*to redirect  tab  at today day*/


    }

    private fun todayData(data: List<TimeTableData>): TimeTableData {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        lateinit var timeTableData: TimeTableData

        data.forEach { itemData ->
            if (dayOfTheWeek == itemData.day) {
                timeTableData = itemData
            }
        }
        return timeTableData
    }
}