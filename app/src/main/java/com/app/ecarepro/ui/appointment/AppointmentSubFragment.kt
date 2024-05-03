package com.app.ecarepro.ui.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.model.Appointment
import com.app.ecarepro.databinding.FragmentAppointmentSubBinding


class AppointmentSubFragment(private val appointments: List<Appointment>, val appType: Int) : Fragment() {

    private lateinit var binding: FragmentAppointmentSubBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAppointmentSubBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (appointments!=null){

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
}