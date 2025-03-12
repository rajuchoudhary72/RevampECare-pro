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
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.studentProfile.StudentProfileAttendanceFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileDetailsFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileFeeSummaryFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileLibraryTransFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileMedicineIssuedFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileTransportDetailsFragment
import com.app.ecarepro.ui.studentProfile.appreciation.StudentProfileAppreciationFragment
import com.app.ecarepro.ui.studentProfile.infraction.StudentProfileInfractionFragment
import com.app.ecarepro.ui.studentProfile.medical_card.MedicalCardFragment
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
       try {
           staffId= requireArguments().getInt(Constant.STAFF_ID_ARGUMENT)
       }catch (e:Exception) {
       }
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

                            if (it.data.details!=null){
                                binding.userData=it.data.details

                                Picasso.get().
                                load(it.data.details.photo)
                                    .placeholder(R.drawable.default_profile)
                                    .  into(binding.civStuPic)
                            }

                            val fragmentList: ArrayList<Fragment> = ArrayList()
                            val fragmentName= mutableListOf<String>()

                            if (it.data.sectionControl!=null){
                            if (it.data.sectionControl.sections!=null){
                            if (it.data.sectionControl.sections.isNotEmpty()){
                                for (i in it.data.sectionControl.sections ){
                                    when(i.name){
                                        "PersonalDetails" -> {
                                            if (i.isShow){
                                                fragmentList.add(StaffProfileFragment(it.data.details))
                                                fragmentName.add(getString(R.string.personal_details))
                                            }
                                        }
                                        "Attendance" -> {
                                            if (i.isShow){
                                                fragmentList.add(ProfileAtteFragment(it.data.attendanceDTL))
                                                fragmentName.add(getString(R.string.attendance))
                                            }
                                        }
                                        "Salary" -> {
                                            if (i.isShow){
                                                fragmentList.add(ProfileSalaryStrFragment(it.data.salaryStructure))
                                                fragmentName.add(getString(R.string.salary))
                                            }
                                        }
                                        "Timetable" -> {
                                            if (i.isShow){
                                                fragmentList.add(ProfileTimeTableFragment(it.data.timetableSummary))
                                                fragmentName.add(getString(R.string.timetable))
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
                            }
                            }
                            }else{
                                mainActivity().showMessage(getString(R.string.general_no_data_found))
                            }


                           }

                    }
                }
            }
        }
        staffProfileNavHostViewModel.getStaffProfile(sId)


    }
}