package com.app.ecarepro.ui.forgotpassword

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.UserData
import com.app.ecarepro.databinding.BootomsheetServiceListBinding
import com.app.ecarepro.databinding.FragmentForgotPasswordBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.addSystemWindowInsetToMargin
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        binding.btnStudent.isGone = mViewModel.isStudentLoginBlocked ?: false.not()
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
                (requireActivity() as MainActivity).hideKeyBoard()
                (requireActivity() as MainActivity).showLoader(true)
                mViewModel.getCredentials(
                    binding.textFiled.text.toString()
                ) { it ->
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.errorCode == 0) {
                        findNavController().popBackStack()
                    } else if (it.errorCode == 404) {
                        mainActivity().showMessage("$value is not found registered with us.")
                    } else {
                        it.users?.let { users ->
                            list.clear()
                            list.addAll(users)
                            adapter.notifyDataSetChanged()
                            serviceHistoryBottomSheet()
                        }
                    }
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    val list = mutableListOf<UserData>()
    val adapter by lazy { UserAdapter(list) {} }
    private fun serviceHistoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        val inflater = layoutInflater
        val binding: BootomsheetServiceListBinding =
            DataBindingUtil.inflate(inflater, R.layout.bootomsheet_service_list, null, false)
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.recyclerView.adapter = adapter
        binding.btnSubmit.setOnClickListener {
            if (adapter.selected == -1)
                return@setOnClickListener
            bottomSheetDialog.dismiss()
            (requireActivity() as MainActivity).showLoader(true)
            mViewModel.forgotPassword(
                list.get(adapter.selected),
            ) { it ->
                (requireActivity() as MainActivity).showLoader(false)
                it.message?.let { it1 -> mainActivity().showMessage(it1) }
                if (it.status == "ok") {
                    mViewModel.sentPasswordChangeEvent(list.get(adapter.selected))
                    findNavController().popBackStack()
                }
            }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}