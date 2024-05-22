package com.app.ecarepro.ui.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.model.Appointment
import com.app.ecarepro.databinding.FragmentAppointmentSubBinding
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppointmentSubFragment(private val appointments: List<Appointment>, private val appType: Int) : Fragment(), ItemListener<Appointment> {

    private lateinit var binding: FragmentAppointmentSubBinding
    private val appointmentSubViewModel: AppointmentSubViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAppointmentSubBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (appointments!=null && appointments.isNotEmpty()){

            val appList=  ArrayList<Appointment>()

            appointments.forEach { d->
                if (d.statusID==appType){
                    appList.add(d)
                }
            }



            val appointmentListAdapter =
                AppointmentListAdapter(appList,
                    this@AppointmentSubFragment,appType)

            binding.rvAppointment.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = appointmentListAdapter
            }
            binding.rvAppointment.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvAppointment.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    override fun onItemClick(t: Appointment, pos: Int, boolean: Boolean) {
        when(pos){
            Constant.CHECK_IN -> {
                appointmentSubViewModel.appointmentAction(Constant.CHECK_IN,t.appId)
            }
            Constant.CHECK_OUT-> {
                appointmentSubViewModel.appointmentAction(Constant.CHECK_OUT,t.appId)
            }
            Constant.APPROVE ->{
                appointmentSubViewModel.approveAppointment( t.appId.toString() )
            }

        }
    }
}