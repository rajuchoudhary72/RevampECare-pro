package com.app.ecarepro.ui.staffList

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
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStaffListBinding
import com.app.ecarepro.model.Staff
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.students_list.StudentListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StaffListFragment : Fragment() , ItemListener<Staff> {

    private lateinit var binding: FragmentStaffListBinding
    private val staffListViewModel: StaffListViewModel by viewModels()
    private var toFragment: String= ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffListBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment= requireArguments().getString(Constant.TO).toString()
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


                            if (it.data.staffs.isNotEmpty()) {
                                binding.rvStaffList.isVisible = true
                                binding.tvNoData.isVisible = false

                                val circularAdapter = StaffListAdapter(
                                    it.data.staffs,
                                    this@StaffListFragment
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


            }

        }

        staffListViewModel.getStaffList()

    }

    override fun onItemClick(t: Staff, pos: Int, boolean: Boolean) {
        if (toFragment==Constant.PROFILE_FRA_STAFF){
            findNavController().navigate(R.id.action_staffListFragment_to_staffProfileNavHostFragment,Bundle( ).apply {
                putInt(Constant.STAFF_ID_ARGUMENT, t.sid)
            })
        }

    }
}