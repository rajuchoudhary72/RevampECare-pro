package com.app.ecarepro.ui.staffProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentProfileAtteBinding
import com.app.ecarepro.databinding.FragmentStaffProfileBinding
import com.app.ecarepro.model.AttendanceDTL


class ProfileAtteFragment(private val attendanceDTL: AttendanceDTL) : Fragment() {

    private lateinit var binding: FragmentProfileAtteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentProfileAtteBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.atteData=attendanceDTL


    }
}