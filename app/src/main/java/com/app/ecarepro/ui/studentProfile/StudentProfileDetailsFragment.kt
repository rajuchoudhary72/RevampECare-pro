package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentStudentProfileDetailsBinding
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionListAdapter
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import com.app.ecarepro.ui.studentProfile.student_profile.StudentProfileSiblingListAdapter
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
                        val siblingListAdapter = StudentProfileSiblingListAdapter(
                            siblingDetails,
                        )

                        binding.rvSeblingList.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(activity)
                            adapter = siblingListAdapter
                        }
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