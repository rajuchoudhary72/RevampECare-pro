package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.app.ecarepro.databinding.FragmentStudentProfileDetailsBinding
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentProfileDetailsFragment(

) : Fragment() {

    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    private lateinit var binding : FragmentStudentProfileDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentStudentProfileDetailsBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sharedViewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            binding.studentData=profile
            if (profile.isBoarding){
                binding.tvBoarding.text = profile.classification
            }else{
                binding.tvBoarding.text =""
            }
        }

        sharedViewModel.getSiblingDetails().observe(viewLifecycleOwner) { siblingDetails ->
            if (siblingDetails!=null){
                if (siblingDetails.isNotEmpty()){
                    if (siblingDetails.size>0){

                        binding.siblingDetails=siblingDetails[0]
                        binding.tvClasses.text=siblingDetails[0].`class`

                    }else{
                        binding.llSiblingDetails.isVisible=false
                    }
                }else{
                    binding.llSiblingDetails.isVisible=false
                }
            }else{
                binding.llSiblingDetails.isVisible=false
            }
        }


    }
}