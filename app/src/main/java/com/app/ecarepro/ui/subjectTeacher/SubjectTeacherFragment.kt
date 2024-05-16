package com.app.ecarepro.ui.subjectTeacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentSubjectTeacherBinding
import com.app.ecarepro.model.AllTeacher

import com.app.ecarepro.ui.MainActivity
import com.squareup.picasso.Picasso

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubjectTeacherFragment : Fragment() {

    private val listSubject = mutableListOf<AllTeacher>()
    private val listAll = mutableListOf<AllTeacher>()
    private val list = mutableListOf<AllTeacher>()
    private val subjectAdapter by lazy { TeacherAdapter(list) { allTeacher, poss -> } }
    private lateinit var binding: FragmentSubjectTeacherBinding
    private val viewModel: TeacherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSubjectTeacherBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        } catch (e: IllegalStateException) {

        }
        binding.all.text = getString(R.string.subject_teacher_s)
        binding.my.text = getString(R.string.all_teachers)

        with(binding) {
            gridview.adapter=subjectAdapter
            relAllVisible.setOnClickListener(View.OnClickListener {
                relAllVisible.setBackground(resources.getDrawable(R.drawable.background_shape_left_section_selected))
                all.setTextColor(resources.getColor(R.color.brand_color))
                myVisibleRel.setBackground(resources.getDrawable(R.drawable.background_shape_right_section_unselected))
                my.setTextColor(resources.getColor(R.color.white))
                list.clear()
                list.addAll(listAll)
                subjectAdapter.notifyDataSetChanged()

                gridview.smoothScrollToPosition(0)
            })
            myVisibleRel.setOnClickListener(View.OnClickListener {
                myVisibleRel.setBackground(resources.getDrawable(R.drawable.background_shape_right_section_selected))
                my.setTextColor(resources.getColor(R.color.brand_color))
                relAllVisible.setBackground(resources.getDrawable(R.drawable.background_shape_left_section_unselected))
                all.setTextColor(resources.getColor(R.color.white))
                list.clear()
                list.addAll(listSubject)
                subjectAdapter.notifyDataSetChanged()
                gridview.smoothScrollToPosition(0)
            })
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       lifecycleScope.launch {

            viewModel._studentTeacherResponseMutableStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        list.clear()
                        listAll.clear()
                        listSubject.clear()
                        if (it.data != null) {
                            it.data.let {respose->
                                list.addAll(respose.allTeacher)
                                listAll.addAll(respose.allTeacher)
                                listSubject.addAll(respose.subjectTeacher)
                            }


                        }
                        subjectAdapter.notifyDataSetChanged()

                    }

                }
            }
        }
        viewModel.getSubjectTeacher()

    }


}