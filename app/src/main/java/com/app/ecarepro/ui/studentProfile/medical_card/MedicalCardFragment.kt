package com.app.ecarepro.ui.studentProfile.medical_card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMedicalCard2Binding
import com.app.ecarepro.model.MedicalCard
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MedicalCardFragment : Fragment() {

    private lateinit var binding: FragmentMedicalCard2Binding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentMedicalCard2Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            binding.medicalCard=it.medicalCard

        }

    }
}