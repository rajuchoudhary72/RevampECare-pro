package com.app.ecarepro.ui.report.lessonplan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAllStaffListBinding
import com.app.ecarepro.databinding.FragmentStaffListBinding
import com.app.ecarepro.model.Staff
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.message.chat.MessageType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AllStaffListFragment : Fragment(), ItemListener<Staff> {

    private lateinit var binding: FragmentAllStaffListBinding
    private val staffListViewModel: AllStaffListViewModel by viewModels()
    private var toFragment: String = ""

    private var teacherList: List<Staff>? = null
    private lateinit var teacherListFilter: List<Staff>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllStaffListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = staffListViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment = requireArguments().getString(Constant.TO).toString()
        } catch (_: Exception) {
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {args ->
            if (args.getString("ID")=="Menu"){

            }else{
                if(args.getString("ID").isNullOrEmpty().not()){
                    findNavController().navigate(
                        R.id.action_lessonPlanListFragment_to_viewLessonPlanFragment,
                        bundleOf(
                            Constant.LESSON_ID_ARGUMENT to args.getString("ID"),
                            Constant.LESSONPLAN_HARDCCODE_KEY to args.getString("HARDCODE"),
                           "reportLessonPlan" to args.getString("HARDCODE")
                        )
                    )
                    args.remove("ID")
                }
            }
        }

        lifecycleScope.launch {
            staffListViewModel.searchQuery.collectLatest {

                if (it.isNotEmpty() && teacherList!=null){
                    teacherListFilter = teacherList!!.filter { s ->   s .name.lowercase().contains(it.lowercase())|| s .designation.lowercase().contains(it.lowercase()) }
                    setupRecycleViewStudentList(teacherListFilter)
                }else{
                    teacherList?.let { it1 -> setupRecycleViewStudentList(it1) }
                }


            }
        }

        getStaffList()


    }

    private fun getStaffList() {

        lifecycleScope.launch {
            staffListViewModel.staffListStateFlow.collectLatest {
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

                        if (it.data != null) {
                            teacherList = it.data.staffs
                            setupRecycleViewStudentList(it.data.staffs)
                        }
                    }

                    else -> {}
                }
            }

        }
    }

    override fun onItemClick(t: Staff, pos: Int, boolean: Boolean) {
            findNavController().navigate(
                R.id.action_allStaffListFragment_to_lessonPlanListFragment,
                Bundle().apply {
                    putString("reportLessonPlan", t.id)
                })
    }

    private fun setupRecycleViewStudentList(staffs: List<Staff>) {
        if (staffs != null) {

            if (staffs.isNotEmpty()) {
                binding.rvStaffList.isVisible = true
                binding.tvNoData.isVisible = false

                val circularAdapter = AllStaffListAdapter(
                    staffs,
                    this@AllStaffListFragment
                )

                binding.rvStaffList.apply {
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(activity, 1)
                    adapter = circularAdapter
                }
            } else {
                binding.rvStaffList.isVisible = false
                binding.tvNoData.isVisible = true
            }

        }
    }
}