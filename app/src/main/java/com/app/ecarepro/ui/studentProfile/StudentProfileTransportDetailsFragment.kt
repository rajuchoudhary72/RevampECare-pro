package com.app.ecarepro.ui.studentProfile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTransportDetailsBinding
import com.app.ecarepro.model.Subject
import com.app.ecarepro.model.TransDetails
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment.Companion


class StudentProfileTransportDetailsFragment : Fragment() {

    private lateinit var binding : FragmentTransportDetailsBinding
    private var transDetails: TransDetails?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                transDetails = it.getParcelable(ARG_ITEM_DATA, TransDetails::class.java)
            }else{
                @Suppress("DEPRECATION")
                transDetails = it.getParcelable(ARG_ITEM_DATA)
            }

        }
    }

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

    companion object {
        private const val ARG_ITEM_DATA = "arg_item_data"

        fun newInstance( itemDat: TransDetails?)= StudentProfileTransportDetailsFragment().apply {
            arguments= Bundle().apply {
                putParcelable(ARG_ITEM_DATA,itemDat)
            }
        }

    }

}