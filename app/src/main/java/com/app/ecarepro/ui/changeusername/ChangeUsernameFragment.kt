package com.app.ecarepro.ui.changeusername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentChangeUsernameBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangeUsernameFragment : Fragment() {

    private var _binding: FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!

    private val changeUsernameViewModel: ChangeUsernameViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = changeUsernameViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            btnContinue.setOnClickListener {
                (requireActivity() as MainActivity).showLoader(true)
                changeUsernameViewModel.changeUsername { isSuccess, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage(message)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        changeUsernameViewModel.sendScreenEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}