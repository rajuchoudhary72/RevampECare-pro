package com.app.ecarepro.ui.leave

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLeaveListBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LeaveHistoryFragment : Fragment() , ItemListener<Dtl>{

    private lateinit var binding: FragmentLeaveListBinding
    private val leaveHistoryViewModel: LeaveHistoryViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaveListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            leaveHistoryViewModel.leaveHistoryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvLeaveHistory.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvLeaveHistory.isVisible = true

                        if (it.data != null) {

                            binding.rvLeaveHistory.isVisible = true
                            binding.tvNoData.isVisible = false

                            val leaveHistoryAdapter = LeaveHistoryAdapter(
                                it.data.dtl,
                                this@LeaveHistoryFragment
                            )

                            binding.rvLeaveHistory.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }


                    }
                }
            }

        }

        leaveHistoryViewModel.leaveHistory()


        binding.fbApplyForLeave.setOnClickListener {
            findNavController().navigate(R.id.leaveSettingFragment)
        }
    }

    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {

        if (pos==1){
            lifecycleScope.launch {
                leaveHistoryViewModel.leaveDeleteStateFlow.collectLatest {
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
                            leaveHistoryViewModel.leaveHistory()
                              }  }  }
            }

            leaveHistoryViewModel.leaveDelete(t.lvID)

        }
    }
}