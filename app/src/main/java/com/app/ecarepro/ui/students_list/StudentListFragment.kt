package com.app.ecarepro.ui.students_list

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

    private   var studentList: List<Student>? = null
    private lateinit var studentListFilter: List<Student>
    private var fromFragment: String= ""
    private lateinit var binding : FragmentStudentListBinding
    private val studentListViewModel: StudentListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentStudentListBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = studentListViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
           try {
               fromFragment= requireArguments().getString(Constant.FROM).toString()
           }catch (_:Exception){}
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            studentListViewModel.searchQuery.collectLatest {

                if (it.isNotEmpty() && studentList!=null){
                    studentListFilter = studentList!!.filter { s -> s.name.contains(it)   }
                    setupRecycleViewStudentList(studentListFilter)
                }else{
                    studentList?.let { it1 -> setupRecycleViewStudentList(it1) }
                }


            }
        }

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

                            setupRecycleViewStudentList(it.data.students )



                        }

                    }


                    else -> {}
                }


            }

        }

        studentListViewModel.getStudentList(2,false)

    }


    private fun setupRecycleViewStudentList(students: List<Student>) {
        if (  students.isNotEmpty()){
            binding.rvStudentList.isVisible=true
            binding.tvNoData.isVisible=false

            studentList=  students

            val circularAdapter = StudentListAdapter(
                  students,
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

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {

        findNavController().navigate(R.id.action_studentListFragment2_to_studentProfileNavHostFragment,Bundle( ).apply {
            putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
        })

        when (fromFragment) {
           /* getString(R.string.add_appreciation) -> {
                findNavController().navigate(R.id.action_studentListFragment2_to_addAppreciationFragment,Bundle( ).apply {
                    putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                })
            }
            getString(R.string.view_appreciation) -> {
                findNavController().navigate(R.id.action_studentListFragment2_to_appreciationListFragment,Bundle( ).apply {
                    putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                })
            }
            getString(R.string.add_infraction) -> {
                findNavController().navigate(R.id.action_studentListFragment2_to_addInfractionFragment,Bundle( ).apply {
                    putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                })
            }
            getString(R.string.view_infraction) -> {
                findNavController().navigate(R.id.action_studentListFragment2_to_infractionListFragment,Bundle( ).apply {
                    putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                })
            }
            */
           /* Constant.PROFILE_FRA_STU -> {
                findNavController().navigate(R.id.action_studentListFragment2_to_studentProfileNavHostFragment,Bundle( ).apply {
                    putInt(Constant.STUDENT_ID_ARGUMENT, t.stID)
                })
            }*/
        }

    }
}