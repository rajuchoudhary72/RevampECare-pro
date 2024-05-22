package com.app.ecarepro.ui.transport_attendance.out_pass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentOutPassReportBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OutPassReportFragment : Fragment() {

    private lateinit var binding : FragmentOutPassReportBinding
    private val outPassReportViewModel : OutPassReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOutPassReportBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }

    private fun getOutPassReport(attDate:String) {
        lifecycleScope.launch {
            outPassReportViewModel.outPassReportStateFlow.collectLatest {
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
                        if (it.data!=null){

                            if (it.data.stuLst!=null){

                                binding.recyclerOutPassReport.isVisible=true
                                binding.tvNoData.isVisible=false

                                val outPassReportAdapter =  OutPassReportAdapter(
                                    it.data.stuLst  ,
                                     this@OutPassReportFragment)

                                binding.recyclerOutPassReport.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = outPassReportAdapter
                                }


                            }else{
                                binding.recyclerOutPassReport.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }
                    }
                }
            }
        }
        outPassReportViewModel.getOutPassReport(attDate)

    }

}