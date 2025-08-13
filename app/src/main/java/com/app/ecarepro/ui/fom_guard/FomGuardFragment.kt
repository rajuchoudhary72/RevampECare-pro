package com.app.ecarepro.ui.fom_guard

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFomGuardBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fom_guard.model.Data
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FomGuardFragment : Fragment() , ItemListener<Data> {

    private val viewModel: FomGuardAppointmentsViewModel by viewModels()
    private lateinit var binding: FragmentFomGuardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentFomGuardBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.cvCheckIn.setOnClickListener {
            findNavController().navigate(R.id.FOMGuardVerificationCodeFragment)
        }
        binding.cvWalkIn.setOnClickListener {
            findNavController().navigate(R.id.verifyPhoneFragment)
        }


        binding.startDate.setText( Constant.currentDate() )
        binding.startDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.startDate.setText(Constant.dateToShow(date.toString()))
                    getFomGuardAppointments(date)
                }

            })
        }


        getFomGuardAppointments(binding.startDate.text.toString())


    }

    fun getFomGuardAppointments(date: String?) {
        lifecycleScope.launch {
            viewModel.fomGuardAppointmentsStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            if (!it.data.data.isNullOrEmpty() ) {

                                val assignmentListAdapter =
                                    FormGuardAppointmentListAdapter(it.data.data,
                                        this@FomGuardFragment
                                         )

                                binding.rvAppointment.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = assignmentListAdapter
                                }
                                binding.rvAppointment.isVisible=true
                                binding.tvNoData.isVisible=false


                            }else{
                                binding.rvAppointment.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }


                        }


                    }


                }
            }
        viewModel.getFomGuardAppointments(date)

    }

    override fun onItemClick(t: Data, pos: Int, boolean: Boolean) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.general_are_you_sure))
        builder.setMessage(getString(R.string.are_you_sure_you_want_to_check_out))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            viewModel.updateappointmentcheckout(t.appointmentid.toString()).invokeOnCompletion {
                getFomGuardAppointments(binding.startDate.text.toString())
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }

        builder.show()




    }
}