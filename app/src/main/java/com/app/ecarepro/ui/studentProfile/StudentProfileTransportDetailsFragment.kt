package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTransportDetailsBinding
import com.app.ecarepro.model.TransDetails


class StudentProfileTransportDetailsFragment(private val transDetails: TransDetails? ) : Fragment() {

    private lateinit var binding : FragmentTransportDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTransportDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transDetails=transDetails

    }
}