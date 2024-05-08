package com.app.ecarepro.ui.staffProfile

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
import com.app.ecarepro.databinding.FragmentStaffProfileNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StaffProfileNavHostFragment : Fragment() {

    private var staffId: Int = 0
    private lateinit var binding: FragmentStaffProfileNavHostBinding
    private val staffProfileNavHostViewModel: StaffProfileNavHostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffProfileNavHostBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        staffId= requireArguments().getInt(Constant.STAFF_ID_ARGUMENT)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        staffProfile(staffId)

    }


    private fun staffProfile(sId: Int) {
        lifecycleScope.launch {
            staffProfileNavHostViewModel.staffProfileStateFlow.collectLatest {
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

                            binding.userData=it.data.details

                            Picasso.get().
                            load(it.data.details.photo)
                                .placeholder(R.drawable.default_profile)
                                .  into(binding.civStuPic)

                            var fragmentList : ArrayList<Fragment> = ArrayList();


                                fragmentList.add(StaffProfileFragment(it.data.details))
                                fragmentList.add(ProfileAtteFragment(it.data.attendanceDTL))
                                fragmentList.add(ProfileTimeTableFragment(it.data.timetableSummary))
                                fragmentList.add(ProfileSalaryStrFragment(it.data.salaryStructure))


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
                                        tab.text = "Teacher's Profile"
                                    } 1 -> {
                                        tab.text = "Attendance"
                                    }   2 -> {
                                        tab.text = "TimeTable"
                                    } 3 -> {
                                        tab.text = "Current Salary Structure"
                                    } }
                            }.attach()
                           }

                    }
                }
            }
        }
        staffProfileNavHostViewModel.getStaffProfile(sId)


    }
}