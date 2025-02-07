package com.app.ecarepro.ui.dashbord.fee_defaulter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeeDefaulterUIBinding
import com.app.ecarepro.model.FeeType
import com.app.ecarepro.model.Installment
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FeeDefaulterUI : Fragment() {

    private lateinit var binding: FragmentFeeDefaulterUIBinding
    private val feeDefaulterViewModel: FeeDefaulterViewModel by viewModels()
    private var feeTypeId: Int? = 0
    private var installIds: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentFeeDefaulterUIBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.fee_defaulter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFeeDefaulters(feeTypeId, installIds)

    }

    private fun getFeeDefaulters(feeTypeId: Int?,
                                 installIds: Int?) {

        lifecycleScope.launch {
            feeDefaulterViewModel.feeDefaulterStateFlow.collectLatest {
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
                        if (it.data != null) {

                            binding.tvTotalStudent.text=it.data.totalStudent.toString()
                            binding.tvTotalDefaulter.text=it.data.totalDefaulter.toString()
                            binding.defaultAmount.text= it.data.totalAmount

                            if (it.data.feeTypes!=null){
                                buildFeeType(it.data.feeTypes)
                            }
                            if (it.data.installments!=null){
                                buildFeeInstallment(it.data.installments)
                            }

                            if (it.data.feeDefaulters!=null) {
                                if (it.data.feeDefaulters.isNotEmpty()) {

                                    binding.rvFeeDefaulter.isVisible = true
                                    binding.tvNoData.isVisible = false

                                    val outPassReportAdapter = FeeDefaulterAdapter(
                                        it.data.feeDefaulters
                                    )

                                    binding.rvFeeDefaulter.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = outPassReportAdapter
                                    }


                                } else {
                                    binding.rvFeeDefaulter.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }
                            } else {
                                binding.rvFeeDefaulter.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }
                    }
                }
            }
        }

        feeDefaulterViewModel.getFeeDefaulters(feeTypeId, installIds)

    }


    private fun buildFeeType(feeType: List<FeeType>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            feeType.map { it.feeTypeName })
        binding.feeType.setAdapter(adapter)

        binding.feeType.setOnItemClickListener { _, _, position, _ ->
            feeTypeId=feeType[position].feeTypeID
            getFeeDefaulters(feeTypeId,installIds)
        }
    }

    private fun buildFeeInstallment(installment: List<Installment>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            installment.map { it.installName })
        binding.installments.setAdapter(adapter)

        binding.installments.setOnItemClickListener { _, _, position, _ ->
            installIds=installment[position].installID
            getFeeDefaulters(feeTypeId,installIds)
        }
    }
}