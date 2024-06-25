package com.app.ecarepro.ui.calender

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCalenderActivityNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalenderActivityNavHost : Fragment() {

    private lateinit var binding: FragmentCalenderActivityNavHostBinding
    private val activityCalenderViewModel: ActivityCalenderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCalenderActivityNavHostBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            activityCalenderViewModel._calenderStateFlow.collectLatest {

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

                            if (it.data.activityMonth.isNotEmpty()) {
                                val fragmentList = listOf(
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[0],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[1],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[2],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[3],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[4],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[5],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[6],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[7],
                                        it.data.session
                                    ), 
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[8],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[9],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[10],
                                        it.data.session
                                    ),
                                    ActivityCalenderFragment(
                                        it.data.activityMonth[11],
                                        it.data.session
                                    ),
                                    Fragment()
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
                                            tab.text = it.data.activityMonth[0].monthName
                                        }

                                        1 -> {
                                            tab.text = it.data.activityMonth[1].monthName
                                        }

                                        2 -> {
                                            tab.text = it.data.activityMonth[2].monthName
                                        }

                                        3 -> {
                                            tab.text = it.data.activityMonth[3].monthName
                                        }

                                        4 -> {
                                            tab.text = it.data.activityMonth[4].monthName
                                        }
                                        5 -> {
                                            tab.text = it.data.activityMonth[5].monthName
                                        }
                                        6 -> {
                                            tab.text = it.data.activityMonth[6].monthName
                                        }
                                        7 -> {
                                            tab.text = it.data.activityMonth[7].monthName
                                        }
                                        8 -> {
                                            tab.text = it.data.activityMonth[8].monthName
                                        }
                                        9 -> {
                                            tab.text = it.data.activityMonth[9].monthName
                                        }
                                        10 -> {
                                            tab.text = it.data.activityMonth[10].monthName
                                        }
                                        11 -> {
                                            tab.text = it.data.activityMonth[11].monthName
                                        }
                                    }
                                }.attach()


                            }

                        }

                    }

                    else -> {}
                }


            }
        }

        activityCalenderViewModel.getActivityCaledar()


    }
}