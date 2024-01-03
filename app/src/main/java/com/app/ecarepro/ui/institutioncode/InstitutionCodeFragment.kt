package com.app.ecarepro.ui.institutioncode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentInstitutionCodeBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InstitutionCodeFragment : Fragment() {

    private var _binding: FragmentInstitutionCodeBinding? = null

    private val binding get() = _binding!!

    private val institutionCodeViewModel: InstitutionCodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstitutionCodeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInstitutionCode.setOtpCompletionListener {
            binding.btnContinue.isEnabled = true
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            institutionCodeViewModel.validateSchoolCode(binding.textInstitutionCode.text.toString()) {
                (requireActivity() as MainActivity).showLoader(false)
                if (it)
                    findNavController().navigate(R.id.signInFragment)
                else
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        binding.btnFindSchoolCollege.setOnClickListener {
            findNavController().navigate(R.id.searchInstitutionFragment)
        }
        binding.btnHelp.setOnClickListener {
            findNavController().navigate(R.id.helpFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}