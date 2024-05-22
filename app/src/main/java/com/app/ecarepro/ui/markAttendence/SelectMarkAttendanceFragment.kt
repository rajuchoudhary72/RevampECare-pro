package com.app.ecarepro.ui.markAttendence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
 import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSelectMarkAttendanceBinding
import com.app.ecarepro.utils.Constant


class SelectMarkAttendanceFragment : Fragment() {

    private lateinit var binding : FragmentSelectMarkAttendanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSelectMarkAttendanceBinding.inflate(inflater,container,false)

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSubAtt.setOnClickListener {
            findNavController().navigate(R.id.action_selectMarkAttendanceFragment_to_stuMarkAttendanceFragment,Bundle( ).apply {
                putString(Constant.TO,  getString(R.string.subject_attendance))
            })
        }
        binding.tvClassAtt.setOnClickListener {
            findNavController().navigate(R.id.action_selectMarkAttendanceFragment_to_stuMarkAttendanceFragment,Bundle( ).apply {
                putString(Constant.TO,  getString(R.string.class_attendance))
            })
        }

    }
}