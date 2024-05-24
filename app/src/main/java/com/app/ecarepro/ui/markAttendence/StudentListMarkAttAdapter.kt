package com.app.ecarepro.ui.markAttendence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        private lateinit var binding :   ItemMarkAttendanceBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        binding=ItemMarkAttendanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(binding.root)
    }

    override fun getItemCount(): Int = studentListArrayList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
       val data= studentListArrayList[position]




        Picasso.get()
            .load( data.photo )
            .placeholder(R.drawable.default_profile)
            .networkPolicy(NetworkPolicy.OFFLINE).into(holder.user_img)



        holder.tv_stu_name.text=data.stName
        holder.tv_admission_no.text= buildString {
            append("Admission No. : ")
            append(data.otherDTL[0].value)
        }
        holder.tv_roll_no.text= buildString {
            append("Roll No. : ")
            append(data.otherDTL[1].value)
         }



        if (canEdit) {
            holder.tv_mark_present.isEnabled = true
            holder.tv_mark_absent.isEnabled = true
            holder.tv_mark_leave.isEnabled = true
            holder.tv_mark_late.isEnabled = true
            holder.tv_mark_na.isEnabled = true
        } else {
            if (hasMarked) {
                holder.tv_mark_present.isEnabled = false
                holder.tv_mark_absent.isEnabled = false
                holder.tv_mark_leave.isEnabled = false
                holder.tv_mark_late.isEnabled = false
                holder.tv_mark_na.isEnabled = false
            }
        }



        when (data.status) {
            1 -> if (data.isLate  == 0) {
                if (data.isConstant  == 1) {
                    holder.tv_mark_present.isEnabled = false
                    holder.tv_mark_absent.isEnabled = false
                    holder.tv_mark_leave.isEnabled = false
                    holder.tv_mark_late.isEnabled = false
                    holder.tv_mark_na.isEnabled = false
                }
                holder.tv_mark_present.text = "P"
                holder.tv_mark_present.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_present)
            } else if (data.isLate  == 1) {
                holder.tv_mark_late.text = "Lt"
                holder.tv_mark_late.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_late)
            }

            2 -> {
                if (data.isConstant == 1) {
                    holder.tv_mark_present.isEnabled = false
                    holder.tv_mark_absent.isEnabled = false
                    holder.tv_mark_leave.isEnabled = false
                    holder.tv_mark_late.isEnabled = false
                    holder.tv_mark_na.isEnabled = false
                }
                holder.tv_mark_absent.text = "A"
                holder.tv_mark_absent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.absent_circle2)
            }

            3 -> {
                if (data.isConstant == 1) {
                    holder.tv_mark_present.isEnabled = false
                    holder.tv_mark_absent.isEnabled = false
                    holder.tv_mark_leave.isEnabled = false
                    holder.tv_mark_late.isEnabled = false
                    holder.tv_mark_na.isEnabled = false
                }
                holder.tv_mark_leave.text = "L"
                holder.tv_mark_leave.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_leave)
            }

            4 -> {
                if (data.isConstant  == 1) {
                    holder.tv_mark_present.isEnabled = false
                    holder.tv_mark_absent.isEnabled = false
                    holder.tv_mark_leave.isEnabled = false
                    holder.tv_mark_late.isEnabled = false
                    holder.tv_mark_na.isEnabled = false
                }
                holder.tv_mark_na.text = "NA"
                holder.tv_mark_na.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
                holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_na)
            }

            else -> {}
        }

        holder.tv_mark_present.setOnClickListener(View.OnClickListener {
            holder.tv_mark_present.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
            holder.tv_mark_absent.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_leave.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_late.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_na.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_present)
            holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            data.status=1
            data.isLate=0
            studentListArrayList[holder.absoluteAdapterPosition] = data

            stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,1),position,false)


        })
        holder.tv_mark_absent.setOnClickListener(View.OnClickListener {

            holder.tv_mark_absent.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
            holder.tv_mark_present.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_leave.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_late.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_na.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.absent_circle2)
            holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            data.status=2
            data.isLate=0
            studentListArrayList[holder.absoluteAdapterPosition] = data

            stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,2),position,false)

        })

        holder.tv_mark_leave.setOnClickListener(View.OnClickListener {
            holder.tv_mark_present.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_absent.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_late.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_na.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_leave.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
            holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_leave)
            holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            data.status=3
            data.isLate=0
            studentListArrayList[holder.absoluteAdapterPosition] = data
            stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,3),position,false)

        })
        holder.tv_mark_late.setOnClickListener(View.OnClickListener {
            holder.tv_mark_present.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_absent.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_leave.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_na.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_late.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
            holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_late)
            holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)

            data.status=1
            data.isLate=1
            studentListArrayList[holder.absoluteAdapterPosition] = data
            stuMarkAttendanceFragment.onItemClick(StudentAtt(1,data .stID,1),position,false)

        })
        holder.tv_mark_na.setOnClickListener(View.OnClickListener {
            holder.tv_mark_present.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_absent.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_leave.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_late.setTextColor(
                stuMarkAttendanceFragment.resources.getColor(R.color.grey_light_compose_msg_headr_txt)
            )
            holder.tv_mark_na.setTextColor(stuMarkAttendanceFragment.resources.getColor(R.color.white))
            holder.tv_mark_absent.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_present.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_leave.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_late.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_gray_att)
            holder.tv_mark_na.background = stuMarkAttendanceFragment.resources.getDrawable(R.drawable.circle_na)
            data.status=4
            data.isLate=0
            studentListArrayList[holder.absoluteAdapterPosition] = data
            stuMarkAttendanceFragment.onItemClick(StudentAtt(0,data .stID,4),position,false)

        })

    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

 
   val   tv_mark_present = itemView.findViewById<TextView?>(R.id.tv_mark_present)
   val   tv_mark_absent = itemView.findViewById<TextView?>(R.id.tv_mark_absent)
   val   tv_mark_leave = itemView.findViewById<TextView?>(R.id.tv_mark_leave)
   val   tv_stu_name = itemView.findViewById<TextView?>(R.id.tv_stu_name)
   val   tv_admission_no = itemView.findViewById<TextView?>(R.id.tv_admission_no)
   val   tv_roll_no = itemView.findViewById<TextView?>(R.id.tv_roll_no)
    val   tv_mark_late = itemView.findViewById<TextView?>(R.id.tv_mark_late)
   val   tv_mark_na = itemView.findViewById<TextView?>(R.id.tv_mark_na)
   val   user_img = itemView.findViewById<ImageView?>(R.id.user_img)




    }


}