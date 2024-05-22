package com.app.ecarepro.ui.con_report

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentConverReportBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.medicine_issue.MedicineIssueAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConversationReportFragment : Fragment() {

    private lateinit var binding: FragmentConverReportBinding
    private val conversationReportViewModel : ConversationReportViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConverReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun getConversationReport(){

        lifecycleScope.launch {
            conversationReportViewModel.convReportStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSmsUsageReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSmsUsageReport.isVisible = true

                        if (it.data != null) {
                             binding.recyclerSmsUsageReport.isVisible = true
                            binding.tvNoData.isVisible = false

                            val leaveHistoryAdapter = ConversationReportAdapter(
                                it.data.conversation
                            )

                            binding.recyclerSmsUsageReport.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }


                    }
                }
            }

        }

       // conversationReportViewModel.getConversationReport( )

    }


}