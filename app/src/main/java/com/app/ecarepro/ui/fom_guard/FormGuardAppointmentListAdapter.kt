package com.app.ecarepro.ui.fom_guard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AppointmentListItemBinding
import com.app.ecarepro.databinding.GuardAppointmentListItemBinding
import com.app.ecarepro.ui.fom_guard.model.Data
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso

class FormGuardAppointmentListAdapter(
    private val appointmentList: List<Data>,
    private val appointmentSubFragment: FomGuardFragment,

) :
    RecyclerView.Adapter<FormGuardAppointmentListAdapter.AppointmentsListAdapter>() {

    private lateinit var bindingm: GuardAppointmentListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsListAdapter {
        bindingm =
            GuardAppointmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentsListAdapter(bindingm)
    }

    override fun getItemCount(): Int = appointmentList.size

    override fun onBindViewHolder(holder: AppointmentsListAdapter, position: Int) {

        val binding= DataBindingUtil.getBinding<GuardAppointmentListItemBinding>(holder.itemView)
        val data= appointmentList[position]



        binding!!.apply {

            appointmentData=data

            btnCheckOut.isVisible = data.checkInTime.isEmpty()

           if(data.visitorPhoto.isNullOrEmpty().not()){
               Picasso.get().
               load(data.visitorPhoto)
                   .placeholder(R.drawable.default_profile)
                   .  into(userImg)
           }



            btnCheckOut.setOnClickListener {
                appointmentSubFragment.onItemClick(data,Constant.CHECK_OUT,false)
            }



        }

     }


    class AppointmentsListAdapter(itemView: GuardAppointmentListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}