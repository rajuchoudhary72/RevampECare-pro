package com.app.ecarepro.ui.markAttendence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.databinding.ItemMarkAttendanceBinding
import com.app.ecarepro.model.StudentListMarkAtt
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class StudentListMarkAttAdapter(
    private var studentListArrayList: MutableList<StudentListMarkAtt>,
    private val isLateEnable: Boolean,
    private val hasMarked: Boolean,
    private val canEdit: Boolean,
    private var stuMarkAttendanceFragment: StuMarkAttendanceFragment,

    ) :
    RecyclerView.Adapter<StudentListMarkAttAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemMarkAttendanceBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm =
            ItemMarkAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = studentListArrayList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<ItemMarkAttendanceBinding>(holder.itemView)

        val data = studentListArrayList[position]


        Picasso.get().
        load(data.photo)
            .placeholder(R.drawable.default_profile)
            .  into(binding!!.userImg)



        binding.tvStuName.text = data.stName
        binding.tvAdmissionNo.text = buildString {
            append("Admission No. : ")
            append(data.otherDTL[0].value)
        }
        binding.tvRollNo.text = buildString {
            append("Roll No. : ")
            append(data.otherDTL[1].value)
        }

        binding.tvMarkLate.isVisible=isLateEnable



        if (canEdit) {
            binding.tvMarkPresent.isEnabled = true
            binding.tvMarkAbsent.isEnabled = true
            binding.tvMarkLeave.isEnabled = true
            binding.tvMarkLate.isEnabled = true
            binding.tvMarkNa.isEnabled = true
            binding.tvMarkWh.isEnabled = true
        } else {
            if (hasMarked) {
                binding.tvMarkPresent.isEnabled = false
                binding.tvMarkAbsent.isEnabled = false
                binding.tvMarkLeave.isEnabled = false
                binding.tvMarkLate.isEnabled = false
                binding.tvMarkNa.isEnabled = false
                binding.tvMarkWh.isEnabled = false
            }
        }



        when (data.status) {
            1 -> if (data.isLate == 0) {
                binding.tvApproveLeave.isVisible=false

                if (data.isConstant == 1) {
                    binding.tvMarkPresent.isEnabled = false
                    binding.tvMarkAbsent.isEnabled = false
                    binding.tvMarkLeave.isEnabled = false
                    binding.tvMarkLate.isEnabled = false
                    binding.tvMarkNa.isEnabled = false
                    binding.tvMarkWh.isEnabled = false
                }else{
                    binding.tvMarkPresent.isEnabled = true
                    binding.tvMarkAbsent.isEnabled = true
                    binding.tvMarkLeave.isEnabled = true
                    binding.tvMarkLate.isEnabled = true
                    binding.tvMarkNa.isEnabled = true
                    binding.tvMarkWh.isEnabled = true
                }

                /*binding.tvMarkPresent.text = "P"
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.tvMarkPresent.background = ResourcesCompat.getDrawable(
                    stuMarkAttendanceFragment.resources,
                    R.drawable.circle_present,
                    null
                )*/

                updateUI(1,binding,data,position)

            }
            else if (data.isLate == 1) {



                /*binding.tvMarkLate.text = "Lt"
                binding.tvMarkLate.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkLate.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_late)
*/
                binding.tvApproveLeave.isVisible=false
                updateUI(4, binding, data, position)

            }

            2 -> {
                binding.tvApproveLeave.isVisible=false
                if (data.isConstant == 1) {
                    binding.tvMarkPresent.isEnabled = false
                    binding.tvMarkAbsent.isEnabled = false
                    binding.tvMarkLeave.isEnabled = false
                    binding.tvMarkLate.isEnabled = false
                    binding.tvMarkNa.isEnabled = false
                    binding.tvMarkWh.isEnabled = false
                }else{
                    binding.tvMarkPresent.isEnabled = true
                    binding.tvMarkAbsent.isEnabled = true
                    binding.tvMarkLeave.isEnabled = true
                    binding.tvMarkLate.isEnabled = true
                    binding.tvMarkNa.isEnabled = true
                    binding.tvMarkWh.isEnabled = true
                }
                /*binding.tvMarkAbsent.text = "A"
                binding.tvMarkAbsent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.absent_circle2)*/

                updateUI(2, binding, data, position)
            }

            3 -> {
                binding.tvApproveLeave.isVisible=false

                if (data.isConstant == 1) {
                    binding.tvMarkPresent.isEnabled = false
                    binding.tvMarkAbsent.isEnabled = false
                    binding.tvMarkLeave.isEnabled = false
                    binding.tvMarkLate.isEnabled = false
                    binding.tvMarkNa.isEnabled = false
                    binding.tvMarkWh.isEnabled = false
                    binding.tvApproveLeave.isVisible=true
                }else{
                    binding.tvMarkPresent.isEnabled = true
                    binding.tvMarkAbsent.isEnabled = true
                    binding.tvMarkLeave.isEnabled = true
                    binding.tvMarkLate.isEnabled = true
                    binding.tvMarkNa.isEnabled = true
                    binding.tvMarkWh.isEnabled = true
                }
               /* binding.tvMarkLeave.text = "L"
                binding.tvMarkLeave.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_leave)*/

                updateUI(3, binding, data, position)
            }

            4 -> {
                binding.tvApproveLeave.isVisible=false
                if (data.isConstant == 1) {
                    binding.tvMarkPresent.isEnabled = false
                    binding.tvMarkAbsent.isEnabled = false
                    binding.tvMarkLeave.isEnabled = false
                    binding.tvMarkLate.isEnabled = false
                    binding.tvMarkNa.isEnabled = false
                    binding.tvMarkWh.isEnabled = false
                 }else{
                    binding.tvMarkPresent.isEnabled = true
                    binding.tvMarkAbsent.isEnabled = true
                    binding.tvMarkLeave.isEnabled = true
                    binding.tvMarkLate.isEnabled = true
                    binding.tvMarkNa.isEnabled = true
                    binding.tvMarkWh.isEnabled = true
                }
                /*binding.tvMarkNa.text = "NA"
                binding.tvMarkNa.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_na)*/

                updateUI(5, binding, data, position)
            }
            7 -> {
                binding.tvApproveLeave.isVisible=false

                if (data.isConstant == 1) {
                    binding.tvMarkPresent.isEnabled = false
                    binding.tvMarkAbsent.isEnabled = false
                    binding.tvMarkLeave.isEnabled = false
                    binding.tvMarkLate.isEnabled = false
                    binding.tvMarkNa.isEnabled = false
                    binding.tvMarkWh.isEnabled = false
                    binding.tvApproveLeave.isVisible=true
                }else{
                    binding.tvMarkPresent.isEnabled = true
                    binding.tvMarkAbsent.isEnabled = true
                    binding.tvMarkLeave.isEnabled = true
                    binding.tvMarkLate.isEnabled = true
                    binding.tvMarkNa.isEnabled = true
                    binding.tvMarkWh.isEnabled = true
                }
                /* binding.tvMarkLeave.text = "L"
                 binding.tvMarkLeave.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                 binding.tvMarkLeave.background =
                     stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_leave)*/

                updateUI(7, binding, data, position)
            }

            else -> {}
        }

        binding.tvMarkPresent.setOnClickListener(View.OnClickListener {
            updateUI(1, binding, data, position)
        })

        binding.tvMarkAbsent.setOnClickListener(View.OnClickListener {
            updateUI(2, binding, data, position)
        })

        binding.tvMarkLeave.setOnClickListener(View.OnClickListener {
            updateUI(3, binding, data, position)
        })
        binding.tvMarkLate.setOnClickListener(View.OnClickListener {
            updateUI(4, binding, data, position)
        })
        binding.tvMarkNa.setOnClickListener(View.OnClickListener {
            updateUI(5, binding, data, position)
        })
        binding.tvMarkWh.setOnClickListener(View.OnClickListener {
            updateUI(7, binding, data, position)
        })


    }

   private fun updateUI(
        action: Int,
        binding: ItemMarkAttendanceBinding,
        data: StudentListMarkAtt,
        absoluteAdapterPosition: Int
    ) {
        when (action) {
            1 -> {
                binding.tvMarkPresent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkWh.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_present)
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

                data.status=1
                data.isLate=0
                studentListArrayList[ absoluteAdapterPosition] = data

               // stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,1),absoluteAdapterPosition,false)

            }

            2 -> {

                binding.tvMarkAbsent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkWh.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.absent_circle2)
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

                data.status=2
                data.isLate=0
                studentListArrayList[absoluteAdapterPosition] = data

              //  stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,2),absoluteAdapterPosition,false)
            }

            3 -> {
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkWh.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.background_box_rectangle_blue)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

                data.status=3
                data.isLate=0
                studentListArrayList[absoluteAdapterPosition] = data
              //  stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,3),absoluteAdapterPosition,false)
            }

            4 -> {
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkWh.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_late)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)


                binding.tvMarkPresent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkWh.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_present)
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

                data.status=1
                data.isLate=1
                studentListArrayList[absoluteAdapterPosition] = data
              //  stuMarkAttendanceFragment.onItemClick(StudentAtt(1,data .stID,1),absoluteAdapterPosition,false)
            }

            5 -> {
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkWh.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_na)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                data.status=4
                data.isLate=0
                studentListArrayList[absoluteAdapterPosition] = data
              //  stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,4),absoluteAdapterPosition,false)
            }
            7 -> {
                binding.tvMarkPresent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkAbsent.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLeave.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkLate.setTextColor(
                    stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
                )
                binding.tvMarkNa.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkWh.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                binding.tvMarkAbsent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkPresent.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLeave.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkLate.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkNa.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
                binding.tvMarkWh.background =
                    stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_wh)
                data.status=7
                data.isLate=0
                studentListArrayList[absoluteAdapterPosition] = data
              //  stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,4),absoluteAdapterPosition,false)
            }
        }
    }


    inner class NoticeViewHolder(val binding: ItemMarkAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root)


}