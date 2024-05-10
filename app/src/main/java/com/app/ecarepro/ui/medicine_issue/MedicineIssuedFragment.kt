package com.app.ecarepro.ui.medicine_issue

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
import com.app.ecarepro.databinding.FragmentMedicineIssueBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicineIssuedFragment : Fragment() , ItemListener<Dtl> {

    private lateinit var binding: FragmentMedicineIssueBinding
    private val mViewModel: MedicineIssueViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicineIssueBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel.leaveHistoryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvMedicineIssue.isVisible = true

                        if (it.data != null) {

                            binding.rvMedicineIssue.isVisible = true
                            binding.tvNoData.isVisible = false

                            val leaveHistoryAdapter = MedicineIssueAdapter(
                                it.data.medicineIssued
                            )

                            binding.rvMedicineIssue.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }


                    }
                }
            }

        }

        mViewModel.medicineIssue()

    }

    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {
    }
}