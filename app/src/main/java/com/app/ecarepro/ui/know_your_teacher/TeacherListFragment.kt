package com.app.ecarepro.ui.know_your_teacher

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
import com.app.ecarepro.databinding.FragmentTeacherListBinding
import com.app.ecarepro.model.Staff
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TeacherListFragment : Fragment() , ItemListener<Staff> {

    private lateinit var binding: FragmentTeacherListBinding
    private val teacherListViewModel: TeacherListViewModel by viewModels()
    private var toFragment: String= ""

    private   var teacherList: List<Staff>? = null
    private lateinit var teacherListFilter: List<Staff>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = teacherListViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment= requireArguments().getString(Constant.TO).toString()
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        try {
            lifecycleScope.launch {
                teacherListViewModel.searchQuery.collectLatest {

                    if (it.isNotEmpty() && teacherList!=null){
                        teacherListFilter = teacherList!!.filter { s ->   s .name.lowercase().contains(it.lowercase()) || s .designation.lowercase().contains(it.lowercase()) || s .teachersSubject.lowercase().contains(it.lowercase())  }
                        setupRecycleViewStudentList(teacherListFilter)
                    }else{
                        teacherList?.let { it1 -> setupRecycleViewStudentList(it1) }
                    }


                }
            }
        } catch (_: Exception) { }

        getTeachersList()


    }

    private fun getTeachersList() {

        lifecycleScope.launch {
            teacherListViewModel.staffListStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvStaffList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStaffList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStaffList.isVisible = true

                        if (it.data!=null){
                            teacherList = it.data.staffs
                            setupRecycleViewStudentList(it.data.staffs)
                        }

                    }


                    else -> {}
                }


            }

        }

       // staffListViewModel.getStaffList()

    }

    override fun onItemClick(t: Staff, pos: Int, boolean: Boolean) {
        if (toFragment==Constant.PROFILE_FRA_STAFF){
            findNavController().navigate(R.id.action_staffListFragment_to_staffProfileNavHostFragment,Bundle( ).apply {
                putInt(Constant.STAFF_ID_ARGUMENT, t.sid)
            })
        }

    }

    private fun setupRecycleViewStudentList(staffs: List<Staff>) {
        if ( staffs != null) {

         if ( staffs.isNotEmpty()) {
                binding.rvStaffList.isVisible = true
                binding.tvNoData.isVisible = false

                val circularAdapter = TeacherListAdapter(
                     staffs,
                    this@TeacherListFragment
                )

                binding.rvStaffList.apply {
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(activity, 2)
                    adapter = circularAdapter
                }
            } else {
                binding.rvStaffList.isVisible = false
                binding.tvNoData.isVisible = true
            }

        }
    }
}