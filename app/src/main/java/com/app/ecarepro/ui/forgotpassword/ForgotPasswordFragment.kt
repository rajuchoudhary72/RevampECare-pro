package com.app.ecarepro.ui.forgotpassword

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentForgotPasswordBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
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

        binding.toggleButtonPasswordRecoverFor.addOnButtonCheckedListener { _, checkedId, isChecked ->
            mViewModel.userType = when (binding.toggleButtonPasswordRecoverFor.checkedButtonId) {
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
        binding.toggleButtonUsing.addOnButtonCheckedListener { _, checkedId, isChecked ->
            binding.textFiled.setText("")
            mViewModel.rcvOn = when (binding.toggleButtonUsing.checkedButtonId) {
                R.id.btn_mobile -> {
                    binding.tilTextFiled.hint = "Mobile Number"
                    binding.textFiled.inputType = InputType.TYPE_CLASS_PHONE
                    "mob"
                }

                else -> {
                    binding.tilTextFiled.hint = "Email Address"
                    binding.textFiled.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    "email"
                }
            }
        }



        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        binding.btnNext.setOnClickListener {
            val value = binding.textFiled.text.toString()
            if (mViewModel.rcvOn == "mob" && value.length != 10) {
                (requireActivity() as MainActivity).showMessage("Please enter a valid 10 digit mobile number.")
            } else if (mViewModel.rcvOn == "email" && !isValidEmail(value)) {
                (requireActivity() as MainActivity).showMessage("Please enter a valid email address.")
            } else {
                (requireActivity() as MainActivity).showLoader(true)
                mViewModel.getCredentials(
                    binding.textFiled.text.toString()
                ) {
                    (requireActivity() as MainActivity).showLoader(false)

                    if (it.errorCode == 404) {
                        mainActivity().showMessage("Invalid credentials so please check again")
                    } else {
                        mainActivity().showMessage(it.message ?: "")
                    }

                    if (it.errorCode == 0) {
                        findNavController().popBackStack()
                    }
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