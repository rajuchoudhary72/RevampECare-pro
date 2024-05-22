package com.app.ecarepro.ui.transport_attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSelectTransportTypeBinding
import com.app.ecarepro.utils.Constant


class SelectTransportTypeFragment : Fragment() {

    private lateinit var binding : FragmentSelectTransportTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSelectTransportTypeBinding.inflate(inflater,container,false)
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvMarkTransAtt.setOnClickListener {
                findNavController().navigate(R.id.transportAttendanceFragment)
                }
            tvViewAtt.setOnClickListener {
                findNavController().navigate(R.id.transportAttendanceReportFragment)
            }
            tvOutPass.setOnClickListener {
                findNavController().navigate(R.id.outPassReportFragment)
            }


            }



    }
}