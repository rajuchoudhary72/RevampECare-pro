package com.app.ecarepro.ui.discipline_log.infraction.appreciation.students_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentListBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StudentListFragment : Fragment() , ItemListener<Student> {

    private lateinit var binding : FragmentStudentListBinding
    private val studentListViewModel: StudentListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentStudentListBinding.inflate(inflater,container,false)
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.apply {
                title = "Students List"

                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
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

                                val circularAdapter = StudentListAdapter(
                                    it.data.students,
                                    this@StudentListFragment
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

        studentListViewModel.getStudentList(2,false)

    }

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.action_studentListFragment2_to_addAppreciationFragment,Bundle( ).apply {
            putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
        })
    }
}