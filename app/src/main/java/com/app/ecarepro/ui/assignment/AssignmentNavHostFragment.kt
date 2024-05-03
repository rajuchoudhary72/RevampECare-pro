package com.app.ecarepro.ui.assignment

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
import com.app.ecarepro.databinding.FragmentAssignmentNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AssignmentNavHostFragment : Fragment() {

    private lateinit var binding : FragmentAssignmentNavHostBinding
    private val assignmentNavHostViewModel : AssignmentNavHostViewModel by viewModels()
    private   var  Id: String=""
    private   var  assignmentType: String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentAssignmentNavHostBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            Id= requireArguments().getString(Constant.ID).toString()
            assignmentType= requireArguments().getString(Constant.ASSIGNMENT_TYPE).toString()
        }catch (_:Exception){}
         return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            assignmentNavHostViewModel.assignmentStateFlow.collectLatest {

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

                            if (it.data.subjectAssignments!=null ) {

                                val fragmentList : ArrayList<Fragment> = ArrayList()

                                 it.data.subjectAssignments.forEach { assignmentsData ->
                                    fragmentList.add(AssignmentListFragment(assignmentsData.assignments))
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

                                        tab.text = it.data.subjectAssignments[position].subject


                                }.attach()


                            }

                        }

                    }

                    else -> {}
                }


            }
        }
        if (assignmentType==Constant.CLASS_ASSIGNMENT){
            assignmentNavHostViewModel.getClassAssignment(Id)
        }else{
            assignmentNavHostViewModel.getAssignment()

        }


    }
}