package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.BindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentStudentProfileDetailsBinding
import com.app.ecarepro.model.Profile


class StudentProfileDetailsFragment(private val profile: Profile) : Fragment() {


    private lateinit var binding : FragmentStudentProfileDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentStudentProfileDetailsBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.studentData=profile
    }
}