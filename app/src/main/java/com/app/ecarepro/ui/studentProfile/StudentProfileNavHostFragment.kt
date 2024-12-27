package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentProfileNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerformanceNavHostFragment
import com.app.ecarepro.ui.studentProfile.appreciation.StudentProfileAppreciationFragment
import com.app.ecarepro.ui.studentProfile.infraction.StudentProfileInfractionFragment
import com.app.ecarepro.ui.studentProfile.medical_card.MedicalCardFragment
import com.app.ecarepro.ui.studentProfile.report_card.StudentProfileReportCardFragment
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StudentProfileNavHostFragment : Fragment() {

    private lateinit var binding: FragmentStudentProfileNavHostBinding
    private val studentProfileNavHostViewModel: StudentProfileNavHostViewModel by viewModels()
    private val sharedViewModel: SharedViewModelProfile  by activityViewModels()
    private var studentID: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileNavHostBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.students_profile)

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

                            if (it.data.profile!=null){
                                binding.userData = it.data.profile
                                binding.civStuPic.setOnClickListener { _ ->
                                    try {
                                        findNavController().navigate(R.id.openImageFragment, Bundle().apply {
                                            putString(Constant.URL_ARGUMENT, it.data.profile.photo)
                                        })
                                    } catch (_: Exception) { }
                                }

                                sharedViewModel.setNetworkStudentProfile(it.data)

                                sharedViewModel.setProfile(it.data.profile)
                                sharedViewModel.setSiblingDetails(it.data.siblingDetails)

                            }


                            val fragmentList: ArrayList<Fragment> = ArrayList()
                            val fragmentName= mutableListOf<String>()

                            if (it.data.sectionControl!=null){
                                if (it.data.sectionControl.sections!=null){
                                    if (it.data.sectionControl.sections.isNotEmpty()){
                                try {
                                    for (i in it.data.sectionControl.sections ){
                                        when(i.name){
                                            "PersonalDetails" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileDetailsFragment())
                                                    fragmentName.add("Personal Details")
                                                }
                                            }
                                            "Attendance" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileAttendanceFragment.newInstance(studentID))
                                                    fragmentName.add("Attendance")
                                                }
                                            }
                                            "AcademicPerformance" -> {
                                                if (i.isShow){
                                                    fragmentList.add(
                                                        AcademicPerformanceNavHostFragment.newInstance(studentID))
                                                    fragmentName.add("Academic Performance")
                                                }
                                            }
                                            "ReportCard" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileReportCardFragment())
                                                    fragmentName.add("Report Card")
                                                }
                                            }

                                            "FeeDetails" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileFeeSummaryFragment.newInstance(studentID))
                                                    fragmentName.add("Fee Details")
                                                }
                                            }
                                            "Infirmary" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileMedicineIssuedFragment())
                                                    fragmentName.add("Infirmary")
                                                }
                                            }
                                            "Library" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileLibraryTransFragment())
                                                    fragmentName.add("Library")
                                                }
                                            }
                                            "Transport" -> {
                                                if (i.isShow){
                                                    fragmentList.add(StudentProfileTransportDetailsFragment())
                                                    fragmentName.add("Transport Details")
                                                }
                                            }
                                            "Infraction" -> {
                                                if (i.isShow){

                                                    fragmentList.add(StudentProfileInfractionFragment())
                                                    fragmentName.add("Infraction Details")

                                                }
                                            }
                                            "Appreciation" -> {
                                                if (i.isShow){

                                                    fragmentList.add(StudentProfileAppreciationFragment())
                                                    fragmentName.add("Appreciation Details")


                                                }
                                            }
                                            "MedicalCard" -> {
                                                if (i.isShow){
                                                    fragmentList.add(MedicalCardFragment())
                                                    fragmentName.add("Medical Card")
                                                }
                                            }
                                        }
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

                                        tab.text = fragmentName[position]


                                    }.attach()
                                    binding.viewPager.offscreenPageLimit = fragmentList.size // Adjust based on your tab count
                                }catch (e:Exception){ }
                            }
                                }
                            }else{
                                mainActivity().showMessage("No Record Found")
                            }





                    }
                }
            }}
        }
        studentProfileNavHostViewModel.getStudentProfile(sId)
    }

    override fun onResume() {
        super.onResume()
        studentProfileNavHostViewModel.sendScreenEvent()
    }
}