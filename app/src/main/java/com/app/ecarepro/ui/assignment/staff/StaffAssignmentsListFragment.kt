package com.app.ecarepro.ui.assignment.staff

import android.os.Bundle
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
import com.app.ecarepro.model.AssignmentShareModel
import com.app.ecarepro.model.TeacherAssignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant

import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StaffAssignmentsListFragment(
    val assignments: List<TeacherAssignment>,
    val className: String,
    private val teacherTypeUser: Boolean,
    private val staffId: String
) : Fragment(), ItemListener<TeacherAssignment> {

     private   lateinit var binding : FragmentStaffAssignmentsListBinding
    private val teacherAssignmentViewModel : TeacherAssignmentViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignmentsListBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




            if (assignments!=null   ) {
            if (assignments.isNotEmpty()) {
                val classAssignments = assignments.filter { it.`class` == className }
                val assignmentListAdapter =
                    StaffAssignmentListAdapter(classAssignments,
                        this@StaffAssignmentsListFragment,
                        teacherTypeUser)

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
            }else{
                binding.rvAssignment.isVisible=false
                binding.tvNoData.isVisible=true

            }



    }

    override fun onItemClick(t: TeacherAssignment, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                findNavController().navigate(R.id.viewAssignmentFragment,Bundle( ).apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                    putBoolean(Constant.IS_LATE_SUBMITTED, t.lateSubmission!!)
                    putBoolean(Constant.USER_TEACHER, teacherTypeUser)
                    putParcelable("AssignmentShareModel", AssignmentShareModel(asgFiles = t.asgFiles, hasAttachment = t.hasAttachment))


                })
            }
            2 -> {

                findNavController().navigate(R.id.postAssignmentFragment, Bundle().apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                    putBoolean(Constant.EDIT.toString(), true)
                })
            }
            3 -> {
                teacherAssignmentViewModel.deleteAssignment(t.id!!)
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