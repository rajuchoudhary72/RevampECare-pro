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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaffAssignmentsListBinding
 import com.app.ecarepro.model.TeacherAssignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant

import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StaffAssignmentsListFragment : Fragment(), ItemListener<TeacherAssignment> {

    private   var staffId: String=""
    private   lateinit var binding : FragmentStaffAssignmentsListBinding
    private val teacherAssignmentViewModel : TeacherAssignmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignmentsListBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            staffId= requireArguments().getString(Constant.STAFF_ID_ARGUMENT).toString()
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ( teacherAssignmentViewModel. userType == Constant.PRINCIPAL || teacherAssignmentViewModel. userType ==  Constant.MANAGEMENT) {
            binding.fbPostAssignment.isVisible=false
        }

        binding.fbPostAssignment.setOnClickListener {
            findNavController().navigate(R.id.postAssignmentFragment)
        }

        lifecycleScope.launch {
            teacherAssignmentViewModel.teacAssignmentStateFlow.collectLatest {

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

                            if (it.data.assignments!=null ) {

                                val assignmentListAdapter =
                                    StaffAssignmentListAdapter(it.data.assignments,
                                        this@StaffAssignmentsListFragment,
                                        teacherAssignmentViewModel.userType)

                                binding.rvAssignment.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = assignmentListAdapter
                                }
                                binding.rvAssignment.isVisible=true
                                binding.tvNoData.isVisible=false


                            }else{
                                binding.rvAssignment.isVisible=false
                                binding.tvNoData.isVisible=true

                            }

                            }

                        }



                    else -> {}
                }


            }
        }

        teacherAssignmentViewModel.teachersAssignment(staffId )


    }

    override fun onItemClick(t: TeacherAssignment, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                findNavController().navigate(R.id.action_staffAssignmentsListFragment_to_viewAssignmentFragment,Bundle( ).apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                    putBoolean(Constant.IS_LATE_SUBMITTED, t.lateSubmission)

                })
            }
            2 -> {

                findNavController().navigate(R.id.postAssignmentFragment, Bundle().apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                    putBoolean(Constant.EDIT.toString(), true)
                })
            }
            3 -> {
                teacherAssignmentViewModel.deleteAssignment(t.id)
                lifecycleScope.launch {
                    teacherAssignmentViewModel.deleteAssignmentStateFlow.collectLatest {
                        when (it) {  is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                        }  is NetworkResult.Error -> {
                            (requireActivity() as MainActivity).showLoader(false)
                        } is NetworkResult.Success -> {
                            (requireActivity() as MainActivity).showLoader(false)
                            teacherAssignmentViewModel.teachersAssignment(staffId)
                        }  }
                    } }
            }
        }

    }
}