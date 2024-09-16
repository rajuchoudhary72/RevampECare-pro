package com.app.ecarepro.ui.reportCard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
 import com.app.ecarepro.databinding.FragmentReportCardStudentListBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.students_list.StudentListViewModel
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ReportCardStudentListFragment : Fragment(), ItemListener<Student> {

    private lateinit var binding : FragmentReportCardStudentListBinding
    private val studentListViewModel: StudentListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentReportCardStudentListBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            studentListViewModel.studentListStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvStudentList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentList.isVisible = true

                        if (it.data!=null){



                            if (it.data.students.isNotEmpty()){
                                binding.rvStudentList.isVisible=true
                                binding.tvNoData.isVisible=false

                                val circularAdapter = ReportCardStudentListAdapter(
                                    it.data.students,
                                    this@ReportCardStudentListFragment
                                )

                                binding.rvStudentList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = GridLayoutManager(activity,2)
                                    adapter = circularAdapter
                                }
                            }else{
                                binding.rvStudentList.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }


                }


            }

        }

        studentListViewModel.getStudentList(2,"")


    }

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {



    }
}