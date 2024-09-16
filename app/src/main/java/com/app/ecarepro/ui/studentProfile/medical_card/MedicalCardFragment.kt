package com.app.ecarepro.ui.studentProfile.medical_card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMedicalCard2Binding
import com.app.ecarepro.model.MedicalCard


class MedicalCardFragment( val medicalCard: MedicalCard) : Fragment() {

    private lateinit var binding: FragmentMedicalCard2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentMedicalCard2Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.medicalCard=medicalCard
    }
}