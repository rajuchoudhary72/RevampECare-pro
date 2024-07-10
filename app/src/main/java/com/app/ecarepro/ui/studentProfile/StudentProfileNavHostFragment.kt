package com.app.ecarepro.ui.studentProfile

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
import com.app.ecarepro.databinding.FragmentStudentProfileNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerformanceNavHostFragment
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StudentProfileNavHostFragment : Fragment() {

    private lateinit var binding: FragmentStudentProfileNavHostBinding
    private val studentProfileNavHostViewModel: StudentProfileNavHostViewModel by viewModels()
    private var studentID: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileNavHostBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        try {
            studentID = requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)
        } catch (_: Exception) {
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            studentProfile(studentID)

        }


    }

    private fun studentProfile(sId: Int) {
        lifecycleScope.launch {
            studentProfileNavHostViewModel.studentProfileStateFlow.collectLatest {
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

                            binding.userData = it.data.profile

                            val fragmentList: ArrayList<Fragment> = ArrayList();


                            fragmentList.add(StudentProfileDetailsFragment(it.data.profile))
                            fragmentList.add(StudentProfileAttendanceFragment(it.data.attendanceDTL,it.data.academicYears,studentID))
                            fragmentList.add(StudentProfileFeeSummaryFragment(it.data.feeSummery,it.data.academicYears,studentID))
                            fragmentList.add(StudentProfileLibraryTransFragment(it.data.library))
                            fragmentList.add(StudentProfileMedicineIssuedFragment(it.data.medicineIssued))
                            fragmentList.add(StudentProfileTransportDetailsFragment(it.data.transDetails))
                            fragmentList.add(AcademicPerformanceNavHostFragment(it.data.academicYears,studentID ))


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
                                        tab.text = "Full Profile"
                                    }

                                    1 -> {
                                        tab.text = "Attendance"
                                    }

                                    2 -> {
                                        tab.text = "Fee Details"
                                    }

                                    3 -> {
                                        tab.text = "Library Transaction Details"
                                    }

                                    4 -> {
                                        tab.text = "Infirmary Visit"
                                    }
                                    5 -> {
                                        tab.text = "Transport Details"
                                    }
                                    6 -> {
                                        tab.text = "Academic Performance"
                                    }

                                }
                            }.attach()
                        }

                    }
                }
            }
        }
        studentProfileNavHostViewModel.getStudentProfile(sId)


    }

}