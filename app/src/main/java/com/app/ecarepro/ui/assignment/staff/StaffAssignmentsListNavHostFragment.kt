package com.app.ecarepro.ui.assignment.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaffAssignmentNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant

import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class StaffAssignmentsListNavHostFragment : Fragment()  {

    private   var staffId: String=""
    private   lateinit var binding : FragmentStaffAssignmentNavHostBinding
    private val teacherAssignmentViewModel : TeacherAssignmentViewModel by viewModels()
    private var teacherTypeUser= true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignmentNavHostBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            staffId= requireArguments().getString(Constant.STAFF_ID_ARGUMENT).toString()
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ( staffId.isNotEmpty()) {
            binding.fbPostAssignment.isVisible=false
            teacherTypeUser=false
        }


        binding.fbPostAssignment.setOnClickListener {
            findNavController().navigate(R.id.postAssignmentFragment)
        }

        lifecycleScope.launch {
            teacherAssignmentViewModel.teacAssignmentStateFlow.collectLatest { response ->

                when (response) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + response)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (response.data != null) {

                            if (response.data.assignments!=null ) {

                                try {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val classList = mutableListOf<String> ()
                                        val fragmentList : ArrayList<Fragment> = ArrayList()

                                        withContext(Dispatchers.Default) {
                                            response.data.assignments.forEach {
                                                if (!classList.contains(it.`class`)) {
                                                    classList.add(it.`class`!!)
                                                }
                                            }
                                            classList. forEach { itemDat ->
                                                fragmentList.add( StaffAssignmentsListFragment(response.data.assignments, itemDat,teacherTypeUser,staffId  ))
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
                                            tab.text = classList[position]
                                        }.attach()



                                    }
                                }catch (_:Exception){ }

                            } else{
                                binding.viewPager.isVisible=false
                                binding.tvNoData.isVisible=true

                            }

                            }

                        }


                }


            }
        }

        teacherAssignmentViewModel.teachersAssignment(staffId )


    }


}