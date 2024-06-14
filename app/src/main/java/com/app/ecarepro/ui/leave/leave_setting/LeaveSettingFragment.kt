package com.app.ecarepro.ui.leave.leave_setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLeaveSettingBinding
import com.app.ecarepro.model.LeaveDetail
import com.app.ecarepro.model.LeaveTypes
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.leave.LeaveHistoryAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LeaveSettingFragment : Fragment() {

    private var isSelected: Boolean=false
    private lateinit var binding : FragmentLeaveSettingBinding
    private val leaveSettingViewModel : LeaveSettingViewModel by viewModels()
    private lateinit var selectedLeaveTypeData: LeaveDetail
    private var leaveTypesDataString: ArrayList<String> = ArrayList()
    private lateinit var leaveTypeList: List<LeaveDetail>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentLeaveSettingBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            leaveSettingViewModel.leaveSettingStateFlow.collectLatest {
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
                        leaveTypesDataString.clear()
                        if (it.data !=null) {
                            leaveTypeList=it.data.leaveDetails
                            it.data.leaveDetails.forEach { data ->
                                leaveTypesDataString.add(data.leaveType .toString())
                            }
                            val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,
                                leaveTypesDataString)
                            binding.autoCompleteReason.setAdapter(arrayAdapter)

                            val leaveHistoryAdapter = LeaveSettingDetailsAdapter(
                                it.data.leaveDetails,
                                this@LeaveSettingFragment
                            )

                            binding.rvLeaveDetail.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }

                    }  }  }  }

        binding.autoCompleteReason.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedLeaveTypeData=leaveTypeList[position]
                isSelected=true
            }

        leaveSettingViewModel.leaveSetting()



        binding.tvDone.setOnClickListener {
            if (isSelected){
                findNavController().navigate(
                    R.id.action_leaveSettingFragment_to_staffApplyLeaveFragment,
                    Bundle().apply {
                        putInt(Constant.LEAVE_ID_ARGUMENT, selectedLeaveTypeData.leaveID)
                        putString(Constant.NAME, selectedLeaveTypeData.leaveType)
                    })
            }else{
                Toast.makeText(requireContext(),"Select Leave Type",Toast.LENGTH_LONG).show()
            }

        }

    }
}