package com.app.ecarepro.ui.assignment

import android.app.AlertDialog
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
import com.app.ecarepro.databinding.FragmentAssignmentListBinding
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.TeacherAssignmentViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignmentListFragment(
    private val assignments: List<Assignment>?,
    val userType: String,
    val assignmentType: String,
    val assignmentID: String
) : Fragment(), ItemListener<Assignment> {

    private lateinit var binding : FragmentAssignmentListBinding
    private val teacherAssignmentViewModel : TeacherAssignmentViewModel by viewModels()
    private val assignmentNavHostViewModel : AssignmentNavHostViewModel by viewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAssignmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (assignments!=null){



            val assignmentListAdapter =
                AssignmentListAdapter(assignments,
                    this@AssignmentListFragment,userType)

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

    override fun onItemClick(t: Assignment, pos: Int, boolean: Boolean) {

        when (pos) {
            1 -> {
              /*  this@AssignmentListFragment.findNavController() .navigate(R.id.action_staffAssignmentsListFragment_to_viewAssignmentFragment,Bundle( ).apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                })*/
                findNavController().navigate(R.id.viewAssignmentFragment,Bundle().apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                })
            }
            2 -> {
                /*this@AssignmentListFragment.findNavController() .navigate(R.id.action_assignmentListFragment_to_submitAssignmentFragment,Bundle( ).apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                })*/
                if (t.isSubmissionOpened==true){
                    findNavController().navigate(R.id.submitAssignmentFragment,Bundle().apply {
                        putParcelable(Constant.ASSIGNMENT_ID, t)
                    })
                }else{
                    if (t.lateSubmission ){
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Are you sure ?")
                        builder.setMessage("The submission deadline for this assignment has passed. You may still submit your assignment, but it will be marked as a late submission")

                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            findNavController().navigate(R.id.submitAssignmentFragment,Bundle().apply {
                                putParcelable(Constant.ASSIGNMENT_ID, t)
                            })
                        }

                        builder.setNegativeButton(android.R.string.no) { dialog, which ->

                        }

                        builder.show()
                    }else{
                        mainActivity().showMessage("The submission deadline for this assignment has passed. ")

                    }
                 }

            }

        }


    }
}