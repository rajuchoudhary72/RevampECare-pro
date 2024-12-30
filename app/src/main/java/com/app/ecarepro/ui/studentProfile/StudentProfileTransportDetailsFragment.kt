package com.app.ecarepro.ui.studentProfile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTransportDetailsBinding
import com.app.ecarepro.model.Subject
import com.app.ecarepro.model.TransDetails
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment.Companion
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentProfileTransportDetailsFragment : Fragment() {

    private lateinit var binding : FragmentTransportDetailsBinding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTransportDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            binding.transDetails=it.transDetails

        }

    }



}