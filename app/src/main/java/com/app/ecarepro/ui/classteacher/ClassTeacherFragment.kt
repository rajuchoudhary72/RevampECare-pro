package com.app.ecarepro.ui.classteacher

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
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassTeacherBinding
import com.app.ecarepro.model.Teacher
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClassTeacherFragment : Fragment() {
    private lateinit var binding: FragmentClassTeacherBinding
    private val classTeacherViewModel: ClassTeacherViewModel by viewModels()
    private var teacherList: List<Teacher>? = null
    private lateinit var teacherListFilter: List<Teacher>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassTeacherBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = classTeacherViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            classTeacherViewModel.searchQuery.collectLatest {

                if (it.isNotEmpty() && teacherList != null) {
                    teacherListFilter = teacherList!!.filter { s ->
                        s.name.lowercase().contains(it.lowercase()) || s.`class`.lowercase()
                            .contains(it.lowercase())
                    }
                    setupRecycleViewStudentList(teacherListFilter)
                } else {
                    teacherList?.let { it1 -> setupRecycleViewStudentList(it1) }
                }


            }
        }

        lifecycleScope.launch {
            classTeacherViewModel.classTeachersStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvClassTeacher.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvClassTeacher.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvClassTeacher.isVisible = true

                        if (it.data != null) {
                            teacherList = it.data.teachers
                            it.data.teachers?.let { it1 -> setupRecycleViewStudentList(it1) }

                        }

                    }


                    else -> {}
                }


            }

        }
        classTeacherViewModel.getClassTeacher()
    }

    private fun setupRecycleViewStudentList(teachers: List<Teacher>) {
        if (teachers.isNotEmpty()) {
            binding.rvClassTeacher.isVisible = true
            binding.tvNoData.isVisible = false


            val teachesAdapter = ClassTeachesAdapter(
                teachers,
                this@ClassTeacherFragment
            )
            binding.rvClassTeacher.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = teachesAdapter
            }
        } else {
            binding.rvClassTeacher.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }

}