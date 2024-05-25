package com.app.ecarepro.ui.transport_attendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.TransportAttStudentListItemBinding
import com.app.ecarepro.model.StuLst
import com.app.ecarepro.ui.transport_attendance.TransportAttendanceFragment
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso

class StudentListToMarkAttAdapter(
    private val stuLstList: List<StuLst>,
    private val tripType: Int,
    private val transportAttendanceFragment: TransportAttendanceFragment
) :
    RecyclerView.Adapter<StudentListToMarkAttAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: TransportAttStudentListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = TransportAttStudentListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = stuLstList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding =
            DataBindingUtil.getBinding<TransportAttStudentListItemBinding>(holder.itemView)
        val data = stuLstList[position]

        holder.bind(data)
        binding?.apply {


            if (tripType == Constant.UP_TRIP) {
                llTransportAtt.isVisible = true
                llDrop.isVisible = false
                llConformCancel.isVisible = false
                tvTripTypeStatus.text = "Up Trip Status"
                opView.visibility=View.GONE
                tvMarkLeave.visibility=View.GONE
                updateUI(data.pickupStatus, binding)
            } else if (tripType == Constant.DROP_STUDENT_TRIP) {
                llTransportAtt.isVisible = false
                tvTripTypeStatus.text = "Drop  Status"
                if (data.isdropped) {
                    llDrop.isVisible = true
                    tvDrop.isVisible = false
                    tvDropped.isVisible = true
                } else {
                    llConformCancel.isVisible = true
                    llDrop.isVisible = false

                    tvDrop.setOnClickListener {
                        llDrop.isVisible = false
                        llConformCancel.isVisible = true
                    }
                    tvCancel.setOnClickListener {
                        llDrop.isVisible = true
                        tvDrop.isVisible = true
                        tvDropped.isVisible = true

                        llConformCancel.isVisible = false
                    }
                    tvConform.setOnClickListener {
                        llDrop.isVisible = true
                        tvDrop.isVisible = false
                        tvDropped.isVisible = true

                        llConformCancel.isVisible = false


                    }

                }
            } else if (tripType == Constant.DOWN_TRIP) {
                llTransportAtt.isVisible = true
                llDrop.isVisible = false
                llConformCancel.isVisible = false
                tvTripTypeStatus.text = "Down Trip Status"
                opView.visibility=View.VISIBLE
                tvMarkLeave.visibility=View.VISIBLE
                updateUI(data.dropStatus, binding)
            }

            studentData = data
            tvMarkPresent.setOnClickListener {
                updateUI(Constant.PRESENT, binding)
                transportAttendanceFragment.onItemClick(data, position, Constant.PRESENT)
            }
            tvMarkAbsent.setOnClickListener {
                updateUI(Constant.ABSENT, binding)
                transportAttendanceFragment.onItemClick(data, position, Constant.ABSENT)
            }
            tvMarkLeave.setOnClickListener {
                updateUI(Constant.OP, binding)
                transportAttendanceFragment.onItemClick(data, position, Constant.OP)
            }
            tvConform.setOnClickListener {
                transportAttendanceFragment.onItemClick(data, position, Constant.DROP_CONFORM)
            }


        }

    }

    private fun updateUI(actionType: Int, binding: TransportAttStudentListItemBinding) {
        when (actionType) {
            Constant.PRESENT -> {
                binding.tvMarkPresent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))
                binding.tvMarkLeave.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))

                binding.tvMarkPresent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_present)
                binding.tvMarkAbsent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            }

            Constant.ABSENT -> {
                binding.tvMarkPresent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))
                binding.tvMarkAbsent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkLeave.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))

                binding.tvMarkPresent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkAbsent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.absent_circle2)
                binding.tvMarkLeave.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            }

            Constant.OP -> {
                binding.tvMarkPresent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))
                binding.tvMarkAbsent.setTextColor(transportAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt))
                binding.tvMarkLeave.setTextColor(transportAttendanceFragment.resources.getColor(R.color.white))

                binding.tvMarkPresent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkAbsent.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    transportAttendanceFragment.resources.getDrawable(R.drawable.circle_leave)

            }
        }
    }


    class NoticeViewHolder(val bin: TransportAttStudentListItemBinding) :
        RecyclerView.ViewHolder(bin.root) {

        fun bind(data: StuLst) {
            bin.apply {
                tvStuName.text = "Name: " + data.stName
                tvRollNo.text = "Roll No: " + data.rollNo
                tvClassName.text = "Class: " + data.className
                tvAdmissionNo.text = "Admission No: " + data.admissionNo

                Picasso.get().load(data.photo)
                    .placeholder(R.drawable.default_profile)
                    .into(circleImageViewProfile)

            }
        }

    }


}