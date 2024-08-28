package com.app.ecarepro.ui.fom_guard.verification_code

import android.os.Bundle
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
import com.app.ecarepro.databinding.FragmentFOMGuardVerificationCodeBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fom_guard.FormGuardAppointmentListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FOMGuardVerificationCodeFragment : Fragment() {


    private val viewModel: FomGuardVerfyCodeViewModel by viewModels()
    private lateinit var binding: FragmentFOMGuardVerificationCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFOMGuardVerificationCodeBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInstitutionCode.setOtpCompletionListener {
            binding.btnContinue.isEnabled = true
        }

        binding.btnContinue.setOnClickListener {
            binding.textInstitutionCode.setItemBackground(resources.getDrawable(R.drawable.bg_outline_round_corner_green))

            lifecycleScope.launch {
                viewModel.updateappointmentCheckInTimeStateFlow.collectLatest {
                    when (it) {
                        is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                        } is NetworkResult.Error -> {
                            (requireActivity() as MainActivity).showLoader(false)
                        } is NetworkResult.Success -> {
                            (requireActivity() as MainActivity).showLoader(false)
                            if (it.data != null) {
                                if (it.data.data.status) {
                                    viewModel.setAppointmentData(it.data.data.appdetails)
                                     findNavController().navigate(
                                        R.id.action_FOMGuardVerificationCodeFragment_to_printOutAppointenentFragment,
                                         Bundle().apply {
                                            putParcelable("appointmentData",it.data.data.appdetails)
                                         }

                                    )


                                }else{
                                    binding.textInstitutionCode.setItemBackground(resources.getDrawable(R.drawable.bg_outline_round_corner_red))
                                    mainActivity().showMessage(it.data.data.message)
                                }

                            }  } } }  }

            viewModel.updateappointmentCheckInTime(binding.textInstitutionCode.text.toString())

        }

    }
}