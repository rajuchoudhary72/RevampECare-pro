package com.app.ecarepro.ui.appuserreport

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaticalReportBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.statical.StaticalReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AppUserReportFragment : Fragment() {
    private val viewModel: StaticalReportViewModel by viewModels()


    private lateinit var binding: FragmentStaticalReportBinding
    private val tabHeaderArrayList = ArrayList<TabHeaderModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statical_report, container, false)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        header()
        return binding.root
    }

    private fun header() {
        val model = TabHeaderModel(0, "Overall")
        tabHeaderArrayList.add(model)
        val model2 = TabHeaderModel(1, "Parent")
        tabHeaderArrayList.add(model2)
        val model3 = TabHeaderModel(2, "Staff")
        tabHeaderArrayList.add(model3)
        val model4 = TabHeaderModel(3, "Student")
        tabHeaderArrayList.add(model4)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            viewModel.appUserReportResponseStateFlow.collectLatest {
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
                            it.data.deviceUsers.let {


                                binding.viewpager.adapter = setVPAdapter(it)
                                binding.tabs.setupWithViewPager(binding.viewpager)
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
        }

        viewModel.appUserReportResponse()
    }

    private fun setVPAdapter(deviceUsersArrayList: ArrayList<DeviceUser>): PagerAdapter {
        binding.viewpager.offscreenPageLimit = 0
        return AppUserReportPagerAdapter(childFragmentManager, tabHeaderArrayList, deviceUsersArrayList)
    }


}

