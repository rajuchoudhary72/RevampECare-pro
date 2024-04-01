package com.app.ecarepro.ui.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentChangePasswordBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = changePasswordViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            btnContinue.setOnClickListener {
                (requireContext() as MainActivity).showLoader(true)
                changePasswordViewModel.changePassword { isSuccess, message ->
                    (requireContext() as MainActivity).showLoader(false)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}