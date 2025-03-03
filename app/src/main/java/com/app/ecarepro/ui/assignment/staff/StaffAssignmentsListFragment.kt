package com.app.ecarepro.ui.assignment.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
class StaffAssignmentsListFragment : Fragment(), ItemListener<TeacherAssignment> {

    private var _binding: FragmentStaffAssignmentsListBinding? = null
    private val binding get() = _binding!!

    private val teacherAssignmentViewModel: TeacherAssignmentViewModel by viewModels()

    // Use companion object for creating instances with arguments
    companion object {
        private const val ARG_ASSIGNMENTS = "assignments"
        private const val ARG_CLASS_NAME = "className"
        private const val ARG_TEACHER_TYPE_USER = "teacherTypeUser"
        private const val ARG_STAFF_ID = "staffId"

        fun newInstance(
            assignments: List<TeacherAssignment>,
            className: String,
            teacherTypeUser: Boolean,
            staffId: String
        ): StaffAssignmentsListFragment {
            val fragment = StaffAssignmentsListFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_ASSIGNMENTS, ArrayList(assignments))
                putString(ARG_CLASS_NAME, className)
                putBoolean(ARG_TEACHER_TYPE_USER, teacherTypeUser)
                putString(ARG_STAFF_ID, staffId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    // Arguments as private properties, nullable by default.
    private val assignments: List<TeacherAssignment>? by lazy {
        arguments?.getParcelableArrayList<TeacherAssignment>(ARG_ASSIGNMENTS)?.toList()
    }
    private val className: String? by lazy { arguments?.getString(ARG_CLASS_NAME) }
    private val teacherTypeUser: Boolean by lazy {
        arguments?.getBoolean(ARG_TEACHER_TYPE_USER) ?: false
    }
    private val staffId: String? by lazy { arguments?.getString(ARG_STAFF_ID) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffAssignmentsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(assignment: TeacherAssignment, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> navigateToViewAssignment(assignment)
            2 -> navigateToPostAssignment(assignment)
            3 -> deleteAssignment(assignment)
            else -> {
                // Handle unknown position or do nothing
                println("Unknown position in onItemClick: $pos")
            }
        }
    }

    private fun navigateToViewAssignment(assignment: TeacherAssignment) {
        findNavController().navigate(R.id.viewAssignmentFragment, Bundle().apply {
            putString(Constant.ASSIGNMENT_ID, assignment.id)
            assignment.lateSubmission?.let { putBoolean(Constant.IS_LATE_SUBMITTED, it) }
            putBoolean(Constant.USER_TEACHER, teacherTypeUser)
            putParcelable(
                "AssignmentShareModel",
                AssignmentShareModel(asgFiles = assignment.asgFiles, hasAttachment = assignment.hasAttachment)
            )
        })
    }

    private fun navigateToPostAssignment(assignment: TeacherAssignment) {
        findNavController().navigate(R.id.postAssignmentFragment, Bundle().apply {
            putString(Constant.ASSIGNMENT_ID, assignment.id)
            putBoolean(Constant.EDIT.toString(), true)
        })
    }

    private fun deleteAssignment(assignment: TeacherAssignment) {
        assignment.id?.let { assignmentId ->
            teacherAssignmentViewModel.deleteAssignment(assignmentId)
            lifecycleScope.launch {
                teacherAssignmentViewModel.deleteAssignmentState.collectLatest { result ->
                    when (result) {
                        is NetworkResult.Loading -> showLoader(true)
                        is NetworkResult.Error -> {
                            showLoader(false)
                            //Handle error, show a message etc
                        }
                        is NetworkResult.Success -> {
                            showLoader(false)
                            staffId?.let { teacherAssignmentViewModel.fetchTeachersAssignments(it) }
                            //Handle success, maybe show a message
                        }
                    }
                }
            }
        } ?: run {
            //Handle case where assignment ID is null
            println("Assignment ID is null")
        }
    }

    private fun showLoader(show: Boolean){
        (requireActivity() as? MainActivity)?.showLoader(show) ?: println("MainActivity not found or showLoader not implemented")
    }
}