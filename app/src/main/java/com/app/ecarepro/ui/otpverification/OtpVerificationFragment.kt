package com.app.ecarepro.ui.otpverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentOtpVerificationBinding
import com.app.ecarepro.ui.mainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: OtpVerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        mViewModel.remainingTime.observe(viewLifecycleOwner) { timeRemaining ->
            if (timeRemaining > 0) {
                // Display the remaining time
                binding.resendTimer.text = buildString {
                    append(getString(R.string.please_wait))
                    append("$timeRemaining ")
                    append(
                        getString(
                            R.string.seconds_to_resend_otp
                        )
                    )
                }
                binding.btnResendOtp.isEnabled =
                    false // Disable resend button while timer is running
            } else {
                // Timer finished, enable the resend button
                binding.resendTimer.isGone = true
                binding.btnResendOtp.isEnabled = true
            }
        }


    }

    private fun initViews() {
        binding.textOtpView.doAfterTextChanged {
            binding.btnContinue.isEnabled = it?.length == binding.textOtpView.itemCount
        }
        binding.btnResendOtp.setOnClickListener {
            binding.textOtpView.text?.clear()
            mainActivity().showLoader(true)
            mViewModel.resendOtp { _, errorMessage ->
                mainActivity().showLoader(false)
                errorMessage?.let {
                    mainActivity().showMessage(it)
                }
            }
        }
        binding.btnContinue.setOnClickListener {
            mainActivity().showLoader(true)
            mViewModel.validateOtp(binding.textOtpView.text.toString()) { isSuccess, errorMessage, userDTL ->
                mainActivity().showLoader(false)
                if (userDTL != null) {
                    setFragmentResult(
                        REQUEST_TYPE_OPT_VERIFICATION,
                        bundleOf(IS_OTP_VERIFIED to true)
                    )
                    findNavController().popBackStack()
                }
                errorMessage?.let {
                    mainActivity().showMessage(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_TYPE_OPT_VERIFICATION = "REQUEST_TYPE_OPT_VERIFICATION"
        const val IS_OTP_VERIFIED = "IS_OTP_VERIFIED"
        const val MAX_ATTEMPTS_DONE = "MAX_ATTEMPTS_DONE"
    }
}