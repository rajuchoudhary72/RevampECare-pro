package com.app.ecarepro.ui.students_list.students_new_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentStudentListSubBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.students_list.StudentListAdapter
import com.app.ecarepro.ui.students_list.StudentListViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentListSubFragment(val students: List<Student>?,val className: String,val toFragment: String) : Fragment(),
    ItemListener<Student> {

    private lateinit var binding: FragmentStudentListSubBinding
    private val studentListViewModel: StudentListViewModel by viewModels()
    private lateinit var studentListFilter: List<Student>
    private var rollNoFilterAsc=true
    private var admissionFilterAsc=true
    private var nameFilterAsc=true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentStudentListSubBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = studentListViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (students!=null) {
        if (students.isNotEmpty()) {

             var studentList = students.filter { it.`class` == className }
            binding.tvTotalCount.text = studentList.size.toString()
            binding.tvBoysCount.text = studentList.filter { it.gender == "Male" }.size.toString()
            binding.tvGirlsCount.text = studentList.filter { it.gender == "Female" }.size.toString()

            lifecycleScope.launch {
                studentListViewModel.searchQuery.collectLatest {

                    if (it.isNotEmpty() && studentList != null) {
                        studentListFilter = studentList .filter { s ->
                            s.name.lowercase().contains(it.lowercase())
                                    || s.name.lowercase().contains(it.lowercase())
                                    || s.admissionNumber.lowercase().contains(it.lowercase())
                                    || s.`class`.lowercase().contains(it.lowercase())
                                    || s.fatherName.lowercase().contains(it.lowercase())
                                    || s.contactMob.lowercase().contains(it.lowercase())


                        }
                        setupRecycleViewStudentList(studentListFilter)
                    } else {
                        setupRecycleViewStudentList(studentList)
                    }


                }
            }


            binding.tvSortByRollNo.setOnClickListener {
                rollNoFilterAsc = !rollNoFilterAsc
                studentList =
                    if (rollNoFilterAsc) studentList.sortedBy { it.rollNumber }.toMutableList()
                    else studentList.sortedByDescending { it.rollNumber }.toMutableList()
                setupRecycleViewStudentList(studentList)

            }
            binding.tvSortByAdmission.setOnClickListener {
                admissionFilterAsc = !admissionFilterAsc
                studentList = if (admissionFilterAsc) studentList.sortedBy { it.admissionNumber }
                    .toMutableList()
                else studentList.sortedByDescending { it.admissionNumber }.toMutableList()
                setupRecycleViewStudentList(studentList)
            }

            binding.tvSortByName.setOnClickListener {
                nameFilterAsc = !nameFilterAsc
                studentList = if (nameFilterAsc) studentList.sortedBy { it.name.trim().lowercase() }
                    .toMutableList()
                else studentList.sortedByDescending { it.name.trim().lowercase() }.toMutableList()
                setupRecycleViewStudentList(studentList)
            }


        } else {
            binding.rvStudentList.isVisible = false
            binding.tvNoData.isVisible = true
        }
        } else {
            binding.rvStudentList.isVisible = false
            binding.tvNoData.isVisible = true
        }

    }

    private fun setupRecycleViewStudentList(students: List<Student>) {
        if (students.isNotEmpty()) {
            binding.rvStudentList.isVisible = true
            binding.tvNoData.isVisible = false


            val circularAdapter = StudentListNewAdapter(
                students,
                this@StudentListSubFragment
            )
            binding.rvStudentList.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = circularAdapter
            }

        } else {
            binding.rvStudentList.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {


        when (toFragment) {
            Constant.FRA_ADD_APPRE -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_addAppreciationFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                    })
            }

            Constant.FRA_VIEW_APPRE -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_appreciationListFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                    })
            }

            Constant.FRA_ADD_INFE -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_addInfractionFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                    })
            }

            Constant.FRA_VIEW_INFE -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_infractionListFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                    })
            }

            Constant.PROFILE_FRA_STU -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_studentProfileNavHostFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                    })
            }
        }

    }

}