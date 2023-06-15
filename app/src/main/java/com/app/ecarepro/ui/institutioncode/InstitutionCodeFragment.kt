package com.app.ecarepro.ui.institutioncode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentInstitutionCodeBinding
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InstitutionCodeFragment : Fragment() {

    private var _binding: FragmentInstitutionCodeBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: InstitutionCodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstitutionCodeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.addSystemWindowInsetToPadding(topWindowInsetToPadding = true)

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.signInFragment)
        }

        binding.btnFindSchoolCollege.setOnClickListener {
            findNavController().navigate(R.id.searchInstitutionFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}