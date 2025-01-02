package com.app.ecarepro.ui.assignment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAssignmentNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AssignmentNavHostFragment : Fragment() {

    private   var assignmentType: String=""
    private   var assignmentID: String=""
    private lateinit var binding : FragmentAssignmentNavHostBinding
    private var isClassAssignment=false
    private val assignmentNavHostViewModel : AssignmentNavHostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentAssignmentNavHostBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.assignment)

        try {
            assignmentType= requireArguments().getString(Constant.ASSIGNMENT_TYPE).toString()
            assignmentID= requireArguments().getString(Constant.ID).toString()
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

                            if (!it.data.subjectAssignments.isNullOrEmpty()) {

                                binding.tabLayout.visibility=View.VISIBLE
                                binding.viewPager.visibility=View.VISIBLE
                                binding.tvNoData.visibility=View.GONE

                                val fragmentList : ArrayList<Fragment> = ArrayList()

                                 it.data.subjectAssignments.forEach { assignmentsData ->
                                     fragmentList.add(AssignmentListFragment.newInstance(assignmentsData.assignments, isClassAssignment))
                                 }

                                val viewPagerAdapter = ViewPagerAdapter(
                                    fragmentList,
                                    activity?.supportFragmentManager!!,
                                    lifecycle
                                )
                                binding.viewPager.adapter = viewPagerAdapter


                                TabLayoutMediator(
                                    binding.tabLayout,
                                    binding.viewPager,
                                ) { tab, position ->

                                        tab.text = it.data.subjectAssignments[position].subject
                                      val badgeDrawable : BadgeDrawable = tab.orCreateBadge

                                     badgeDrawable.backgroundColor=resources.getColor(R.color.att_leave_color)
                                     badgeDrawable.isVisible=it.data.subjectAssignments[position].showAlert
                                     if (it.data.subjectAssignments[position].assignments!=null){
                                         var count=0
                                         for (assignment in it.data.subjectAssignments[position].assignments){
                                             if (assignment.isSubmitted!!){
                                                 count++
                                             }
                                         }
                                         badgeDrawable.badgeGravity=BadgeDrawable.TOP_START

                                         badgeDrawable.number = it.data.subjectAssignments[position].assignments.size-count

                                     }


                                }.attach()




                            }else{
                                binding.tabLayout.visibility=View.GONE
                                binding.viewPager.visibility=View.GONE
                                binding.tvNoData.visibility=View.VISIBLE
                            }

                        }

                    }

                    else -> {}
                }


            }
        }

        if (assignmentType==Constant.CLASS_ASSIGNMENT){
            isClassAssignment=true
            assignmentNavHostViewModel.getClassAssignment(assignmentID)
        }else{
            isClassAssignment=false
            assignmentNavHostViewModel.getAssignment()
        }





    }
}