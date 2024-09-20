package com.app.ecarepro.ui.staffProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.databinding.FragmentProfileSalaryStrBinding
import com.app.ecarepro.model.SalaryStructure


class ProfileSalaryStrFragment( private val salaryStructure: SalaryStructure?) : Fragment() {


    private lateinit var binding: FragmentProfileSalaryStrBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentProfileSalaryStrBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.salaryData=salaryStructure

    }
}