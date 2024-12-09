package com.app.ecarepro.ui.assignment.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaffAssignmentNavHostBinding
import com.app.ecarepro.model.AssignmentShareModel
import com.app.ecarepro.model.TeacherAssignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StaffAssignmentsListNavHostFragment : Fragment() , ItemListener<TeacherAssignment> {

    private var isReportView: Boolean=false
    private   var staffId: String=""
    private   lateinit var binding : FragmentStaffAssignmentNavHostBinding
    private val teacherAssignmentViewModel : TeacherAssignmentViewModel by viewModels()
    private lateinit var assignmentListFilter: List<TeacherAssignment>
    private   var assignmentList: List<TeacherAssignment>? = null
    private var filterType= Constant.FILTER_BY


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignmentNavHostBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = teacherAssignmentViewModel
        }
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
        }


        binding.fbPostAssignment.setOnClickListener {
            findNavController().navigate(R.id.postAssignmentFragment)
        }


        binding.spinnerSelectFilterType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Check if view is null before proceeding
                if (view == null) {
                    Log.e("SpinnerListener", "View is null")
                    return
                }

                when (position) {
                    1 -> {
                        filterType = Constant.FILTER_SUBJECT
                        binding.searchBar.setText("")
                    }
                    2 -> {
                        filterType = Constant.FILTER_CLASS
                        binding.searchBar.setText("")
                    }
                    3 -> {
                        filterType = Constant.FILTER_TEACHER
                        binding.searchBar.setText("")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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
                            if (response.data.assignments!=null   ) {
                                assignmentList=response.data.assignments
                                isReportView=response.data.isReportView
                                setupRecycleViewStudentList(response.data.assignments)
                                if (isReportView){
                                    val spinnerTripTypeAdapter = ArrayAdapter(requireActivity(),
                                        android.R.layout.simple_list_item_1,resources.getStringArray(R.array.filterTypeReport))
                                    binding.spinnerSelectFilterType.adapter=spinnerTripTypeAdapter
                                }else{
                                    val spinnerTripTypeAdapter = ArrayAdapter(requireActivity(),
                                        android.R.layout.simple_list_item_1,resources.getStringArray(R.array.filterTYPE))
                                    binding.spinnerSelectFilterType.adapter=spinnerTripTypeAdapter
                                }
                            }else{
                                binding.rvAssignment.isVisible=false
                                binding.tvNoData.isVisible=true

                            }
                        }

                        }
                }


            }
        }

        lifecycleScope.launch {
            teacherAssignmentViewModel.searchQuery.collectLatest {
                if (it.isNotEmpty() && assignmentList!=null){
                if (filterType!=Constant.FILTER_BY){
                    when(filterType){
                        Constant.FILTER_SUBJECT ->{
                            assignmentListFilter = assignmentList!!.filter { s ->   s.subject!!.lowercase().contains(it.lowercase())   }
                            setupRecycleViewStudentList(assignmentListFilter)

                        }
                        Constant.FILTER_CLASS ->{
                            assignmentListFilter = assignmentList!!.filter { s ->   s.`class`!!.lowercase().contains(it.lowercase())   }
                            setupRecycleViewStudentList(assignmentListFilter)

                        }
                        Constant.FILTER_TEACHER ->{
                            assignmentListFilter = assignmentList!!.filter { s ->   s.assignmentBy!!.lowercase().contains(it.lowercase())   }
                            setupRecycleViewStudentList(assignmentListFilter)

                        }
                    }
                }else{
                    assignmentList?.let { it1 -> setupRecycleViewStudentList(it1) }
                }
                }else{
                    assignmentList?.let { it1 -> setupRecycleViewStudentList(it1) }
                }


            }
        }
    }




    override fun onItemClick(t: TeacherAssignment, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                findNavController().navigate(R.id.viewAssignmentFragment,Bundle( ).apply {
                    putString(Constant.ASSIGNMENT_ID, t.id)
                    putBoolean(Constant.IS_LATE_SUBMITTED, t.lateSubmission!!)
                    putBoolean(Constant.IS_MINE, t.isMine!!)
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

    private fun setupRecycleViewStudentList(assignments: List<TeacherAssignment>) {
        if ( assignments != null) {

            if (assignments.isNotEmpty()) {
                val assignmentListAdapter =
                    StaffAssignmentListAdapter(assignments,
                        this@StaffAssignmentsListNavHostFragment, isReportView)

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


}