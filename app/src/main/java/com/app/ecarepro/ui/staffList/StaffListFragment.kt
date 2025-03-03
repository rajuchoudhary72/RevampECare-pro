package com.app.ecarepro.ui.staffList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.databinding.FragmentStaffListBinding
import com.app.ecarepro.model.Staff
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StaffListFragment : Fragment(), ItemListener<Staff> {

    private var _binding: FragmentStaffListBinding? = null
    private val binding get() = _binding!!

    private val staffListViewModel: StaffListViewModel by viewModels()
    private var targetFragment: String? = null
    private var staffList: List<Staff> = emptyList()
    private var filteredStaffList: List<Staff> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = staffListViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        parseArguments()
        observeSearchQuery()
        observeStaffList()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun parseArguments() {
        targetFragment = arguments?.getString(Constant.TO)
    }

    private fun observeSearchQuery() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                staffListViewModel.searchQuery.collectLatest { query ->
                    filterStaffList(query)
                }
            }
        }
    }

    private fun filterStaffList(query: String) {
        if (query.isNotEmpty()) {
            filteredStaffList = staffList.filter { staff ->
                staff.name.lowercase().contains(query.lowercase()) ||
                        staff.mobile.lowercase().contains(query.lowercase()) ||
                        staff.designation.lowercase().contains(query.lowercase())
            }
            setupRecyclerView(filteredStaffList)
        } else {
            setupRecyclerView(staffList)
        }
    }

    private fun observeStaffList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                staffListViewModel.staffListState.collectLatest { result ->
                    handleStaffListResult(result)
                }
            }
        }
    }

    private fun handleStaffListResult(result: NetworkResult<NetworkStaffList>) {
        when (result) {
            is NetworkResult.Loading -> {
                showLoadingState()
            }

            is NetworkResult.Error -> {
                showErrorState(result.message)
            }

            is NetworkResult.Success -> {
                showSuccessState(result)
            }
        }
    }

    private fun showLoadingState() {
        (requireActivity() as? MainActivity)?.showLoader(true)
        binding.rvStaffList.isVisible = false
        binding.tvNoData.isVisible = false
    }

    private fun showErrorState(errorMessage: String?) {
        (requireActivity() as? MainActivity)?.showLoader(false)
        binding.rvStaffList.isVisible = false
        binding.tvNoData.isVisible = false
        Log.e("StaffListFragment", "Error: $errorMessage")
        //Show error message to user.
    }

    private fun showSuccessState(result: NetworkResult.Success<NetworkStaffList>) {
        (requireActivity() as? MainActivity)?.showLoader(false)
        binding.rvStaffList.isVisible = true

        if (result.data != null) {
            staffList = result.data.staffs
            filterStaffList(staffListViewModel.searchQuery.value)
        } else {
            binding.tvNoData.isVisible = true
            binding.rvStaffList.isVisible = false
        }
    }


    override fun onItemClick(item: Staff, position: Int, isSelected: Boolean) {
        if (targetFragment == Constant.PROFILE_FRA_STAFF) {
            findNavController().navigate(
                R.id.action_staffListFragment_to_staffProfileNavHostFragment,
                Bundle().apply {
                    putInt(Constant.STAFF_ID_ARGUMENT, item.sid)
                }
            )
        }
    }

    private fun setupRecyclerView(staffs: List<Staff>) {
        binding.tvNoData.isVisible = staffs.isEmpty()
        binding.rvStaffList.isVisible = staffs.isNotEmpty()

        if (staffs.isNotEmpty()) {
            val staffListAdapter = StaffListAdapter(staffs, this@StaffListFragment)
            binding.rvStaffList.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = staffListAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}