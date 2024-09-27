package com.app.ecarepro.ui.staffProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentProfileAtteBinding
import com.app.ecarepro.databinding.FragmentProfileTimeTableBinding
import com.app.ecarepro.model.TimetableSummary


class ProfileTimeTableFragment(private val timetableSummary: TimetableSummary?) : Fragment() {

    private lateinit var binding: FragmentProfileTimeTableBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfileTimeTableBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timeTableData=timetableSummary
    }
}