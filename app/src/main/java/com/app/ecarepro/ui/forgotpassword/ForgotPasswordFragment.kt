package com.app.ecarepro.ui.forgotpassword

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentForgotPasswordBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.addSystemWindowInsetToMargin
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.img.addSystemWindowInsetToMargin(topWindowInsetToMargin = true)

        binding.toggleButtonPasswordRecoverFor.addOnButtonCheckedListener { _, checkedId, _ ->

            mViewModel.userType = when (checkedId) {
                R.id.btn_parent -> {
                    2
                }

                R.id.btn_staff -> {
                    3
                }

                else -> {
                    1
                }
            }
        }
        binding.toggleButtonUsing.addOnButtonCheckedListener { _, checkedId, _ ->
            binding.textFiled.setText("")
            mViewModel.rcvOn = when (checkedId) {
                R.id.btn_mobile -> {
                    binding.tilTextFiled.hint = "Mobile Number"
                    "mob"
                }
                else -> {
                    binding.tilTextFiled.hint = "Email Address"
                    "email"
                }
            }
        }


        binding.textFiled.doAfterTextChanged {
            if (mViewModel.rcvOn == "mob") {
                binding.btnNext.isEnabled = it?.length == 10
            } else {
                binding.btnNext.isEnabled = isValidEmail(it)
            }

        }

        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        binding.btnNext.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            mViewModel.getCredentials(
                binding.textFiled.text.toString()
            ) {
                (requireActivity() as MainActivity).showLoader(false)

                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                if (it.errorCode == 0) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}