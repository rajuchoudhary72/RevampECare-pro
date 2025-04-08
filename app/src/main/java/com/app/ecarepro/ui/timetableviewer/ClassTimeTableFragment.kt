package com.app.ecarepro.ui.timetableviewer

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
import com.app.ecarepro.databinding.FragmentClassTimeTableBinding
import com.app.ecarepro.model.Classe
import com.app.ecarepro.model.Teacher
import com.app.ecarepro.ui.timetableviewer.view_model.ClassTimeTableViewModel
import com.app.ecarepro.ui.timetableviewer.view_model.TeacherTimeTableViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClassTimeTableFragment(private val classes: List<Classe>, private val toFragment: String) : Fragment(), ItemListener<Classe> {

    private lateinit var binding : FragmentClassTimeTableBinding
    private val classTimeTableViewModel: ClassTimeTableViewModel by viewModels()
    private lateinit var classListFilter: List<Classe>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentClassTimeTableBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = classTimeTableViewModel
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (classes!=null){

            lifecycleScope.launch {
                classTimeTableViewModel.searchQuery.collectLatest {

                    if (it.isNotEmpty() && classes != null) {
                        classListFilter = classes .filter { s ->
                            s.className.lowercase().contains(it.lowercase())
                        }
                        setupRecycleViewStudentList(classListFilter)
                    } else {
                        setupRecycleViewStudentList(classes)
                    }


                }
            }

        }else{
            binding.rvClassTimeTable.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    private fun setupRecycleViewStudentList(classes: List<Classe>) {
        if (classes.isNotEmpty()) {

            val classTimeTableAdapter =
                ClassTimeTableAdapter(classes,
                    this@ClassTimeTableFragment)

            binding.rvClassTimeTable.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = classTimeTableAdapter
            }

            binding.rvClassTimeTable.isVisible=true
            binding.tvNoData.isVisible=false

        } else {
            binding.rvClassTimeTable.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }


    override fun onItemClick(t: Classe, pos: Int, boolean: Boolean) {
        when (toFragment) {
            Constant.FRA_TIMETABLE -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_timeTableNavHostFragment, Bundle().apply {
                    putString(Constant.ID, t.id)
                    putString(Constant.TIME_TABLE_TYPE, Constant.CLASS_TIME_TABLE)
                    putString(Constant.NAME, t.className)

                })
            }
            Constant.FRA_ASSI -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_assignmentNavHostFragment,Bundle( ).apply {
                    putString(Constant.ID, t.id)
                    putString(Constant.ASSIGNMENT_TYPE, Constant.CLASS_ASSIGNMENT)

                })
            }
            Constant.FRA_LESSON_PLAN -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_lessonPlanListFragment5,Bundle( ).apply {
                    putString(Constant.STAFF_ID_ARGUMENT, t.id)
                })
            }
        }
    }
}