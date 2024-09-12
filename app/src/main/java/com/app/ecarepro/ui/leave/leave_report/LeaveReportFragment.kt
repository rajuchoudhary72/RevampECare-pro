package com.app.ecarepro.ui.leave.leave_report

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLeaveReportBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveReportFragment  : Fragment(), ItemListener<Dtl> {

    private lateinit var leaveReportAdapter: LeaveReportAdapter
    private lateinit var binding:  FragmentLeaveReportBinding
    private val leaveReportViewModel : LeaveReportViewModel by viewModels()
    private var leaveReportList = mutableListOf<Dtl>()

    private var status = 0
    private var order = 2
    private var applType = 1
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private var toFragment: String= ""





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLeaveReportBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        leaveReportAdapter = LeaveReportAdapter(leaveReportList, this)
        with(binding) {
            recyclerLeaveReport.adapter = leaveReportAdapter
        }
        try {
            toFragment= requireArguments().getString(Constant.TO).toString()

            if (toFragment==Constant.FRA_STAFF_LEAVE){
                order=1
                applType=3
            }

        }catch (_:Exception){}
        return binding.root


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleViewPager()
        getLeaveReport()

        binding.toggleButtonTypeLeave.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeLeave.checkedButtonId) {
                R.id.btn_pen -> {
                    leaveReportAdapter.clearData()
                    status=0
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                }
                R.id.btn_app -> {
                    leaveReportAdapter.clearData()
                    status=1
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                }

                else -> {
                    leaveReportAdapter.clearData()
                    status=2
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                }
            }
        }



    }

    private fun getLeaveReport() {

        lifecycleScope.launch {
            leaveReportViewModel.leaveReportStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerLeaveReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerLeaveReport.isVisible = true

                        if (it.data!!.dtl != null) {

                            binding.recyclerLeaveReport.isVisible = true
                            binding.tvNoData.isVisible = false

                            if (pageIndex==1){
                                leaveReportAdapter.clearData()
                             }
                            leaveReportAdapter.setData(it.data.dtl.toMutableList(),it.data.canTalkeAction,applType,status)

                        }else{
                            if (pageIndex==1){
                                binding.recyclerLeaveReport.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }


                    }

                    else -> {}
                }
            }

        }
        leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
    }

    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {
        when (pos) {
            0 -> {
                findNavController().navigate(
                    R.id.photoViewFragmentFragment,
                    bundleOf(PhotoViewFragmentFragment.PHOTO to t.attachment)
                )
            }
            1 -> {
                leaveReportViewModel.leaveAction(applType,t.lvID,Constant.LEAVE_ACTION_APPROVE,0).invokeOnCompletion {
                    leaveReportAdapter.clearData()
                    status=0
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                }

            }
            2 -> {
                leaveReportViewModel.leaveAction(applType,t.lvID,Constant.LEAVE_ACTION_REJECT,0)
                    .invokeOnCompletion {
                        leaveReportAdapter.clearData()
                        status=0
                        pageIndex=1
                        leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                    }
            }
        }
     }


    private fun setupRecycleViewPager() {
        leaveReportAdapter.clearData()
             binding.recyclerLeaveReport.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (linearLayoutManager != null) {
                        if (dy > 0) {
                            visibleItemCount = linearLayoutManager.childCount;
                            totalItemCount = linearLayoutManager.itemCount;
                            pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                            if (isLoading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    isLoading = false
                                    pageIndex += 1
                                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex)
                                }
                            }

                        }
                    }
                }
            })



    }

}