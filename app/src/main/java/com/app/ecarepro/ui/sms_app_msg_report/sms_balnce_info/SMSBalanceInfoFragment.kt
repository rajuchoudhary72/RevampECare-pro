package com.app.ecarepro.ui.sms_app_msg_report.sms_balnce_info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentSMSBalnceInfoBinding
import com.app.ecarepro.databinding.SmsConsumptionItemBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.sms_app_msg_report.sms_consumption.SmsConsumptionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class SMSBalanceInfoFragment : Fragment() {

    private lateinit var binding: FragmentSMSBalnceInfoBinding
    private val smsBalanceInfoViewModel : SmsBalanceInfoViewModel by viewModels()

    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSMSBalnceInfoBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack() }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSMSBalanceInfo()

    }

    private fun getSMSBalanceInfo(  ) {
        lifecycleScope.launch {
            smsBalanceInfoViewModel.smsBalanceInfoStateFlow.collectLatest {
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
                            binding.tvSMSCommitment.text=it.data.smsCommitment
                            binding.tvSMSBalance.text=it.data.smsBalance.toString()
                            binding.tvSMSAsOn.text=it.data.balanceOn

                            if (it.data .alertNotification != null){
                                val alertData=it.data .alertNotification
                                binding.tvAlertHeading.text=alertData.tiltle
                                binding.tvAlertDescription.text=alertData.description
                                binding.tvAlertEmail.text=alertData.emailIds
                                binding.tvAlertPhoneNo.text=alertData.mobileNumber
                            }

                        }
                    }
                }
            }
        }
        smsBalanceInfoViewModel.getSMSBalanceInfo()

    }

}