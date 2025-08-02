package com.app.ecarepro.ui.leave.leave_report

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.app.ecarepro.model.MyReporting
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.leave.leave_report.adapter.MyReportingListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveReportFragment  : Fragment(), ItemListener<Dtl> {

    private lateinit var selectedReporter: MyReporting
    private  var mLeaveList = mutableListOf<Dtl>()
    private  var myReporting = mutableListOf<MyReporting>()
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
    private var isReportingSelected: Boolean = false
    private var isRejectionReasonReq: Boolean = false
    private var toFragment: String= ""
    private var leaveListIds = mutableListOf<Int>()
    private var showAttPer=false
    private var scrollYPosition: Int = 0
    private var isDataLoaded: Boolean = false




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLeaveReportBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.leave_report)
        leaveReportAdapter = LeaveReportAdapter(leaveReportList, this)
        with(binding) {
            recyclerLeaveReport.adapter = leaveReportAdapter
        }
        try {
            toFragment= requireArguments().getString(Constant.TO).toString()

            if (toFragment==Constant.FRA_STAFF_LEAVE){
                order=1
                applType=3
                binding.llAllApproveRej.isVisible=false
                binding.cbAllSelect.isVisible=false
                binding.btnCancel.isVisible=true
            }

        }catch (_:Exception){}
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            scrollYPosition = scrollY


            // Check if the NestedScrollView has reached the bottom
            if (binding.nestedScrollView.getChildAt(0).bottom <= (v.height + scrollY)) {
                if (isLoading && isDataLoaded) { // Add isDataLoaded check to prevent initial multiple calls
                    isLoading = false
                    pageIndex += 1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                }
            }
        }

        getLeaveReport()
      //  setupRecycleViewPager()


        binding.toggleButtonTypeLeave.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeLeave.checkedButtonId) {
                R.id.btn_pen -> {
                    leaveReportAdapter.clearData()
                    status=0
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    if (applType!=3){
                        binding.cbAllSelect.isVisible=true
                        binding.llAllApproveRej.isVisible=true
                    }
                }
                R.id.btn_app -> {
                    leaveReportAdapter.clearData()
                    status=1
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    binding.cbAllSelect.isVisible=false
                    binding.llAllApproveRej.isVisible=false
                }
                R.id.btn_cancel -> {
                    leaveReportAdapter.clearData()
                    status=Constant.LEAVE_ACTION_CANCEL
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    binding.cbAllSelect.isVisible=false
                    binding.llAllApproveRej.isVisible=false
                }

                else -> {
                    leaveReportAdapter.clearData()
                    status=2
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    binding.cbAllSelect.isVisible=false
                    binding.llAllApproveRej.isVisible=false
                }
            }
        }

        binding.tvApprove.setOnClickListener {
            if (leaveListIds.isNotEmpty()){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are You Sure")
                builder.setMessage("Are you sure you want to proceed?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    val leaveIds=leaveListIds.joinToString(",")
                    leaveReportViewModel.leaveAction(applType,null,leaveIds,Constant.LEAVE_ACTION_APPROVE,0,"",null,null,null).invokeOnCompletion {
                        leaveReportAdapter.clearData()
                        status=0
                        pageIndex=1
                        leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            }else{
                mainActivity().showMessage("Please select at least one leave by click on Check Box")
            }
        }
        binding.tvReject.setOnClickListener {
            if (leaveListIds.isNotEmpty()){
                popUpRemark(null,true)
            }else{
            mainActivity().showMessage("Please select at least one leave by click on Check Box")
        }
        }

        binding.cbAllSelect.setOnCheckedChangeListener { _, isChecked ->
          if (isChecked){
              leaveListIds.clear()
              leaveReportAdapter.setAllSelect(true)
              mLeaveList.forEach {
                  leaveListIds.add(it.lvID)
              }
          }else{
              leaveReportAdapter.setAllSelect(false)
              leaveListIds.clear()
          }
        }

        binding.cbShowAttPer.setOnCheckedChangeListener { _, isChecked ->
            showAttPer=isChecked
            leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
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
                        isDataLoaded=true
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        isDataLoaded=true
                        binding.recyclerLeaveReport.isVisible = true

                        if (it.data!!.dtl != null) {
                            isLoading=true
                            binding.recyclerLeaveReport.isVisible = true
                            binding.tvNoData.isVisible = false
                            isRejectionReasonReq=it.data .isRejectionReasonReq
                            if (pageIndex==1){
                                mLeaveList.clear()
                                leaveReportAdapter.clearData()

                             }
                            if (status==0){
                                if (applType!=3){
                                    binding.cbAllSelect.isVisible=it.data.canTalkeAction
                                    binding.llAllApproveRej.isVisible=it.data.canTalkeAction
                                }
                            }
                            mLeaveList=it.data.dtl.toMutableList()
                           if (!it.data.myReporting.isNullOrEmpty()){
                               myReporting=it.data.myReporting.toMutableList()
                           }
                            leaveReportAdapter.setData(it.data.dtl.toMutableList(),it.data.canTalkeAction,applType,status)
                        } else{
                            if (pageIndex==1){
                                binding.recyclerLeaveReport.isVisible = false
                                binding.tvNoData.isVisible = true
                                binding.llAllApproveRej.isVisible=false
                                binding.cbAllSelect.isVisible=false
                            }
                        }

                    }
                    else -> {}
                }
            }

        }
        leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
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

                leaveReportViewModel.leaveAction(applType,t.lvID,null,Constant.LEAVE_ACTION_APPROVE,0,"",null,null,null).invokeOnCompletion {
                    leaveReportAdapter.clearData()
                    status=0
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                }

            }
            2 -> {
                popUpRemark(t, false)
            }
            3 ->{
                if (boolean){
                    leaveListIds.add(t.lvID)
                }else{
                    leaveListIds.remove(t.lvID)
                }
            }
            4 ->{
                if (!myReporting.isNullOrEmpty()){
                    popUpForward(t.lvID)
                }else{
                    mainActivity().showMessage("No Reporting Found")
                }
            }
            5 ->{
                leaveReportViewModel.leaveAction(applType,t.lvID,null,Constant.LEAVE_ACTION_CANCEL,0,"",null,null,null).invokeOnCompletion {
                    leaveReportAdapter.clearData()
                    status=1
                    pageIndex=1
                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                    binding.cbAllSelect.isVisible=false
                    binding.llAllApproveRej.isVisible=false
                }
            }
            6 -> {
                popUpPartialLeaveApprove(t)
            }
        }
     }

    private fun popUpPartialLeaveApprove(t: Dtl) {
        val tvFromDate: TextView
        val tvTODate: TextView
        val btn_canel: Button
        val btn_submit: Button
        val llStartDate: LinearLayout
        val llEndDate: LinearLayout
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.setContentView(R.layout.pop_up_partial_leave_approve)
        tvFromDate = dialog.findViewById(R.id.tv_from_date)
        tvTODate = dialog.findViewById(R.id.tv_to_date)
        btn_canel = dialog.findViewById<Button>(R.id.btn_canel)
        btn_submit = dialog.findViewById(R.id.btn_submit)
        llStartDate = dialog.findViewById(R.id.ll_start_date)
        llEndDate = dialog.findViewById(R.id.ll_end_date)

        tvFromDate.text=t.fromDate
        tvTODate.text=t.tillDate

        llStartDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    tvFromDate.text = Constant.dateToShowSec(date!!)
                }
            },Constant.getLongTimeDateSec(t.fromDate),Constant.getLongTimeDateSec(t.tillDate))
        }
        llEndDate.setOnClickListener {
            val timestampBack = Constant.getLongTimeDateSec(tvFromDate.text.toString())
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    tvTODate.text = Constant.dateToShowSec(date!!)
                }

            },timestampBack,Constant.getLongTimeDateSec(t.tillDate))
        }
        btn_canel.setOnClickListener { dialog.dismiss() }

        btn_submit.setOnClickListener {
            leaveReportViewModel.leaveAction(applType,t.lvID,null,Constant.LEAVE_ACTION_APPROVE,0,"",
                true,
                Constant.strToApiDate( tvFromDate.text.toString()),
                Constant.strToApiDate( tvTODate.text.toString())
            )
                .invokeOnCompletion {
                leaveReportAdapter.clearData()
                status=0
                pageIndex=1
                leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun popUpForward(lvID: Int) {
            val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
            val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
            val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
            val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
            val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
            val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
            tvHeading.text= getText(R.string.lbl_forward_to)
            val  llSelectAll = view.findViewById<LinearLayout>(R.id.llSelectAll)
            val  checkImage = view.findViewById<ImageView>(R.id.checkImage)
            llSelectAll.isVisible=false
            builder.setView(view)

            relOk.setOnClickListener {
                if (isReportingSelected) {
                    leaveReportViewModel.leaveAction(
                        applType,
                        lvID,
                        null,
                        Constant.LEAVE_ACTION_FORWARD,
                        selectedReporter.teacherID,
                        "",null,null,null
                    ).invokeOnCompletion {
                        leaveReportAdapter.clearData()
                        status=0
                        pageIndex=1
                        leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                        showActionMessage()

                    }
                    builder.dismiss()
                }

            }

            val subjectListAdapter= MyReportingListAdapter(myReporting, false, false, object : ItemListener<MyReporting> {
                override fun onItemClick(t: MyReporting, pos: Int, boolean: Boolean) {
                    selectedReporter=t
                    isReportingSelected = true
                }

            })
            rvYears.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = subjectListAdapter
            }



            relCancel.setOnClickListener {
                builder.dismiss()
            }

            builder.setCanceledOnTouchOutside(false)
            builder.show()

    }

    private fun showActionMessage() {

        lifecycleScope.launch {
            leaveReportViewModel.leaveActionStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data!!.message?.let { it1 -> mainActivity().showMessage(it1) }

                    }
                    else -> {}
                }
            }

        }
    }


    private fun popUpRemark(t: Dtl?, multiLeave: Boolean) {
             val tv_done: TextView
            val tv_cancel: TextView
            val textInputEditText: TextInputEditText


            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            dialog.window!!.attributes.windowAnimations = R.style.Animations
            dialog.setContentView(R.layout.custom_popup_leave_reject)
            tv_done = dialog.findViewById(R.id.tv_done)
            tv_cancel = dialog.findViewById(R.id.tv_cancel)
            textInputEditText = dialog.findViewById(R.id.textFiledReason)
            tv_done.setOnClickListener {

                if (isRejectionReasonReq){
                    if ( textInputEditText.text.toString().isNotEmpty()){

                        if (multiLeave){
                            val leaveIds=leaveListIds.joinToString(",")
                            leaveReportViewModel.leaveAction(applType, null,leaveIds,Constant.LEAVE_ACTION_REJECT,0,textInputEditText.text.toString(),null,null,null)
                                .invokeOnCompletion {
                                    leaveReportAdapter.clearData()
                                    status=0
                                    pageIndex=1
                                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                                }
                        }else{
                            leaveReportViewModel.leaveAction(applType,
                                t!!.lvID,null,Constant.LEAVE_ACTION_REJECT,0,textInputEditText.text.toString(),null,null,null)
                                .invokeOnCompletion {
                                    leaveReportAdapter.clearData()
                                    status=0
                                    pageIndex=1
                                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                                }
                        }

                        dialog.dismiss()
                    }else{
                        textInputEditText.error="Rejection Reason is mandatory field"
                    }
                }else{

                    if (multiLeave){
                        val leaveIds=leaveListIds.joinToString(",")
                        leaveReportViewModel.leaveAction(applType,
                            null,leaveIds,Constant.LEAVE_ACTION_REJECT,0,textInputEditText.text.toString(),null,null,null)
                            .invokeOnCompletion {
                                leaveReportAdapter.clearData()
                                status=0
                                pageIndex=1
                                leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                            }
                    }else{
                        leaveReportViewModel.leaveAction(applType,
                            t!!.lvID,null,Constant.LEAVE_ACTION_REJECT,0,textInputEditText.text.toString(),null,null,null)
                            .invokeOnCompletion {
                                leaveReportAdapter.clearData()
                                status=0
                                pageIndex=1
                                leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                            }
                    }

                    dialog.dismiss()
                }


            }
            tv_cancel.setOnClickListener { dialog.dismiss() }
            dialog.show()

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
                                    leaveReportViewModel.leaveReport(status,order,applType,pageIndex,showAttPer)
                                }
                            }

                        }
                    }
                }
            })



    }

    override fun onResume() {
        super.onResume()
        binding.nestedScrollView.post {
            binding.nestedScrollView.scrollTo(0, scrollYPosition)
        }
    }

}