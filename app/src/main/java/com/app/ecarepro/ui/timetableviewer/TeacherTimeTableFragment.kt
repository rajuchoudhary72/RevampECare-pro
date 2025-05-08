package com.app.ecarepro.ui.timetableviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTeacherTimeTableBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.model.Teacher
import com.app.ecarepro.ui.students_list.students_new_list.StudentListNewAdapter
import com.app.ecarepro.ui.timetableviewer.view_model.TeacherTimeTableViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TeacherTimeTableFragment(private val teachers: List<Teacher>, private val toFragment: String) : Fragment(), ItemListener<Teacher> {

    private lateinit var binding : FragmentTeacherTimeTableBinding
    private val teacherTimeTableViewModel: TeacherTimeTableViewModel by viewModels()
    private lateinit var teacherListFilter: List<Teacher>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentTeacherTimeTableBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = teacherTimeTableViewModel
        }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (teachers!=null){
            lifecycleScope.launch {
                teacherTimeTableViewModel.searchQuery.collectLatest {

                    if (it.isNotEmpty() && teachers != null) {
                        teacherListFilter = teachers .filter { s ->
                            s.name.lowercase().contains(it.lowercase())
                        }
                        setupRecycleViewStudentList(teacherListFilter)
                    } else {
                        setupRecycleViewStudentList(teachers)
                    }


                }
            }

        }else{
            binding.rvTeacherTimeTable.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    private fun setupRecycleViewStudentList(teachers: List<Teacher>) {
        if (teachers.isNotEmpty()) {

            val classTimeTableAdapter =
                TeacherTimeTableAdapter(teachers,
                    this@TeacherTimeTableFragment)

            binding.rvTeacherTimeTable.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = classTimeTableAdapter
            }

            binding.rvTeacherTimeTable.isVisible=true
            binding.tvNoData.isVisible=false

        } else {
            binding.rvTeacherTimeTable.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }

    override fun onItemClick(t: Teacher, pos: Int, boolean: Boolean) {

        when (toFragment) {
            Constant.FRA_TIMETABLE -> {
                NavHostFragment.findNavController(this).navigate(
                    R.id.action_classAndTeacherListFragment_to_timeTableNavHostFragment,
                    Bundle().apply {
                        putString(Constant.ID, t.id)
                        putString(Constant.TIME_TABLE_TYPE, Constant.TEACHER_TIME_TABLE)
                        putString(Constant.NAME, t.name)

                    })
            }
            Constant.FRA_ASSI -> {
                NavHostFragment.findNavController(this).navigate(
                    R.id.action_classAndTeacherListFragment_to_staffAssignmentsListFragment,
                    Bundle().apply {
                        putString(Constant.STAFF_ID_ARGUMENT, t.id)
                    })
            }
            Constant.FRA_LESSON_PLAN -> {
                NavHostFragment.findNavController(this).navigate(
                    R.id.action_classAndTeacherListFragment_to_lessonPlanListFragment5,
                    Bundle().apply {
                        putString(Constant.STAFF_ID_ARGUMENT, t.id)
                    })
            }
        }


    }


}