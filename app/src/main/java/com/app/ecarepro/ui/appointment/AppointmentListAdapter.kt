package com.app.ecarepro.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.appendPlaceholders
import com.app.ecarepro.R
import com.app.ecarepro.model.Appointment
import com.app.ecarepro.databinding.AppointmentListItemBinding
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso

class AppointmentListAdapter(
    private val appointmentList: List<Appointment>,
    private val appointmentSubFragment: AppointmentSubFragment,
    private val  appType: Int
) :
    RecyclerView.Adapter<AppointmentListAdapter.AppointmentsListAdapter>() {

    private lateinit var bindingm: AppointmentListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsListAdapter {
        bindingm =
            AppointmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentsListAdapter(bindingm)
    }

    override fun getItemCount(): Int = appointmentList.size

    override fun onBindViewHolder(holder: AppointmentsListAdapter, position: Int) {

        val binding= DataBindingUtil.getBinding<AppointmentListItemBinding>(holder.itemView)
        val data= appointmentList[position]



        binding!!.apply {

            appointmentData=data

            Picasso.get().
            load(data.visitorPhoto)
                .placeholder(R.drawable.default_profile)
                .  into(userImg)

            when (appType) {
                Constant.APPOINTMENT_APPROVE -> {
                    llCheckoutCheckIn.isVisible=true
                    actionDivider.isVisible=false
                    llApproveReject.isVisible=false


                    if (data.checkInTime==null){
                        btnCheckIn.isVisible=true
                        btnCheckOut.isVisible=false
                        llCheckIn.isVisible=false
                        llCheckOut.isVisible=false
                    }else if (data.checkOutTime==null){
                        btnCheckOut.isVisible=true
                        btnCheckIn.isVisible=false
                        llCheckIn.isVisible=true
                        llCheckOut.isVisible=false
                    }else{
                        llCheckoutCheckIn.isVisible=false
                        llCheckIn.isVisible=true
                        llCheckOut.isVisible=true
                    }

                }
                Constant.APPOINTMENT_PENDING -> {
                    llCheckoutCheckIn.isVisible=false
                    actionDivider.isVisible=true
                    llApproveReject.isVisible=true
                }
                Constant.APPOINTMENT_REJECT -> {
                    llCheckoutCheckIn.isVisible=false
                    actionDivider.isVisible=false
                    llApproveReject.isVisible=false
                }
            }
            btnCheckIn.setOnClickListener {
                appointmentSubFragment.onItemClick(data,Constant.CHECK_IN,false)
            }
            btnCheckOut.setOnClickListener {
                appointmentSubFragment.onItemClick(data,Constant.CHECK_OUT,false)
            }

            btnApprove.setOnClickListener {
                appointmentSubFragment.onItemClick(data,Constant.APPROVE,false)
            }
            btnReject.setOnClickListener {
                appointmentSubFragment.onItemClick(data,Constant.REJECT,false)
            }


        }

     }


    class AppointmentsListAdapter(itemView: AppointmentListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}