package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.app.ecarepro.databinding.FragmentStudentProfileDetailsBinding
import com.app.ecarepro.model.Profile
import com.app.ecarepro.model.SiblingDetails


class StudentProfileDetailsFragment(
    private val profile: Profile,
    private val  siblingDetails: List<SiblingDetails>?
) : Fragment() {


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
        if (profile.isBoarding){
            binding.tvBoarding.text = profile.classification
        }else{
            binding.tvBoarding.text =""
        }

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