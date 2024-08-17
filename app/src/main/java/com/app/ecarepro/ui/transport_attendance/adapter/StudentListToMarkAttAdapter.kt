package com.app.ecarepro.ui.transport_attendance.adapter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
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
    private val transportAttendanceFragment: TransportAttendanceFragment,
    private val freezDrop: Boolean,
    private val freezPickup: Boolean
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


        holder.bind(stuLstList[position])


    }




  inner  class NoticeViewHolder(val bin: TransportAttStudentListItemBinding) :
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

            bin.apply {


                if (tripType == Constant.UP_TRIP) {
                    statusNew.isVisible = false

                    llTransportAtt.isVisible = true
                    llDrop.isVisible = false
                    llConformCancel.isVisible = false
                    tvTripTypeStatus.text = " "
                    opView.visibility=View.GONE
                    tvMarkLeave.visibility=View.GONE
                    updateUI(stuLstList[absoluteAdapterPosition].pickupStatus, bin)
                    if (freezPickup){
                        tvMarkPresent.isEnabled=false
                        tvMarkAbsent.isEnabled=false
                    }else{
                        tvMarkPresent.isEnabled=true
                        tvMarkAbsent.isEnabled=true
                    }

                } else if (tripType == Constant.DROP_STUDENT_TRIP) {
                    statusNew.isVisible = false

                    llTransportAtt.isVisible = false
                    tvTripTypeStatus.text = "Drop  Status"
                    if (stuLstList[absoluteAdapterPosition].isdropped) {
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
                            llConformCancel.isVisible = false  }

                    }
                } else if (tripType == Constant.DOWN_TRIP) {
                    if (freezDrop){
                        tvMarkPresent.isEnabled=false
                        tvMarkAbsent.isEnabled=false
                        tvMarkLeave.isEnabled=false
                    }else{
                        tvMarkPresent.isEnabled=true
                        tvMarkAbsent.isEnabled=true
                        tvMarkLeave.isEnabled=true
                    }
                    llTransportAtt.isVisible = true
                    llDrop.isVisible = false
                    llConformCancel.isVisible = false
                    statusNew.isVisible = true
                    if (stuLstList[absoluteAdapterPosition].pickupStatus==Constant.PRESENT){
                        statusNew.setTextColor(transportAttendanceFragment.resources.getColor(R.color.green))
                        statusNew.text = "Present"
                    } else if (stuLstList[absoluteAdapterPosition].pickupStatus==Constant.ABSENT){
                        statusNew.setTextColor(transportAttendanceFragment.resources.getColor(R.color.red))
                        statusNew.text = "Absent"
                    }
                    tvTripTypeStatus.text = "Up Trip Status: "
                    opView.visibility=View.VISIBLE
                    tvMarkLeave.visibility=View.VISIBLE
                    updateUI(stuLstList[absoluteAdapterPosition].dropStatus, bin)
                }

                studentData = stuLstList[absoluteAdapterPosition]
                tvMarkPresent.setOnClickListener {
                    if (tripType == Constant.UP_TRIP) {
                        stuLstList[absoluteAdapterPosition].pickupStatus=Constant.PRESENT
                        transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.PRESENT)
                        updateUI(Constant.PRESENT, bin)
                    }else  if (tripType == Constant.DOWN_TRIP) {
                        if (stuLstList[absoluteAdapterPosition].pickupStatus==Constant.PRESENT) {
                            stuLstList[absoluteAdapterPosition].dropStatus=Constant.PRESENT
                            transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.PRESENT)
                            updateUI(Constant.PRESENT, bin)
                        }else{
                            SuccessAlertPopup("Alert", "Absent student  status can not be change ")


                        }

                    }


                }
                tvMarkAbsent.setOnClickListener {
                    if (tripType == Constant.UP_TRIP) {
                        stuLstList[absoluteAdapterPosition].pickupStatus=Constant.ABSENT
                    }else  if (tripType == Constant.DOWN_TRIP) {
                        stuLstList[absoluteAdapterPosition].dropStatus=Constant.ABSENT
                    }

                    transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.ABSENT)
                    updateUI(Constant.ABSENT, bin)
                }
                tvMarkLeave.setOnClickListener {
                    if (tripType == Constant.UP_TRIP) {
                        stuLstList[absoluteAdapterPosition].pickupStatus=Constant.OP
                        transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.OP)
                        updateUI(Constant.OP, bin)
                    }else  if (tripType == Constant.DOWN_TRIP) {
                        if (stuLstList[absoluteAdapterPosition].pickupStatus==Constant.PRESENT) {
                            stuLstList[absoluteAdapterPosition].dropStatus=Constant.OP
                            transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.OP)
                            updateUI(Constant.OP, bin)
                        }else{
                            SuccessAlertPopup("Alert", "Absent student  status can not be change ")

                        }

                    }

                }
                tvConform.setOnClickListener {
                    transportAttendanceFragment.onItemClick(stuLstList[absoluteAdapterPosition], absoluteAdapterPosition, Constant.DROP_CONFORM)

                }


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


    fun SuccessAlertPopup(headingString: String?, subHeading: String?) {
        val tv_done: TextView
        val tv_sub_text: TextView
        val tv_main_text: TextView
        val tv_heading: TextView
        val ll_yes_no: LinearLayout
        val dialog = Dialog(transportAttendanceFragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.custom_popup_attention)
        ll_yes_no = dialog.findViewById<LinearLayout>(R.id.ll_yes_no)
        tv_done = dialog.findViewById(R.id.tv_done)
        tv_sub_text = dialog.findViewById<TextView>(R.id.tv_sub_text)
        tv_main_text = dialog.findViewById<TextView>(R.id.tv_main_text)
        tv_heading = dialog.findViewById(R.id.tv_heading)
        tv_heading.setText(R.string.attention)
        tv_main_text.text = headingString
        tv_sub_text.text = subHeading
        tv_done.visibility = View.VISIBLE
        ll_yes_no.visibility = View.GONE
        tv_done.setOnClickListener {
            dialog.dismiss()
            //   finish();
        }
        dialog.show()
    }

}