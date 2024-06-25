package com.app.ecarepro.ui.assign_roll_no

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemAssignRollnoBinding
import com.app.ecarepro.model.StudentRllNo
import com.squareup.picasso.Picasso

class AssignRollNoListAdapter(
    private var studentRllNoList: MutableList<StudentRllNo>,
    private var assignRollNoFragment: AssignRollNoFragment
) :
    RecyclerView.Adapter<AssignRollNoListAdapter.AssignRollNoViewHolder>() {

    private lateinit var bindings: ItemAssignRollnoBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignRollNoViewHolder {
        bindings =
            ItemAssignRollnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignRollNoViewHolder(bindings)
    }

    override fun getItemCount(): Int = studentRllNoList.size

    override fun onBindViewHolder(holder: AssignRollNoViewHolder, position: Int) {

        holder.bind(studentRllNoList[position])


    }


    inner class AssignRollNoViewHolder(private val binding: ItemAssignRollnoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentRllNo) {
            binding.apply {
                tvStuName.text = buildString {
                    append("Name : ")
                    append(student.name)
                }
                tvAdmission.text = buildString {
                    append("Admission no : ")
                    append(student.admissionNumber)
                }
                edtRoll.setText(student.rollNumber)
                Picasso.get().load(student.photo)
                    .placeholder(R.drawable.default_profile)
                    .into(circleImageViewProfile)
            }


        }

        init {
            binding.apply {
                edtRoll.doAfterTextChanged {
                    if (edtRoll.text.toString().isNotEmpty()) {

                        if (edtRoll.text.isNotEmpty()) {
                            if (edtRoll.text.toString().toInt() > 0) {
                                edtRoll.error = null
                            } else {
                                edtRoll.error = "Invalid Roll No"
                            }
                        } else {
                            edtRoll.error = null
                        }

                        studentRllNoList.forEach { d ->
                            studentRllNoList[absoluteAdapterPosition].rollNumber =
                                edtRoll.text.toString()

                            if (getCountNumber(edtRoll.text.toString()) > 1) {
                                edtRoll.error = "Already Assigned"
                            }
                        }

                    }





                }
            }
        }


    }


    fun getCountNumber(rollNo: String?): Int {
        var count = 0
        for (student in studentRllNoList) {
            if (student.rollNumber == rollNo) {
                count += 1
            }
        }
        return count
    }

    fun setData(lessonList: MutableList<StudentRllNo>) {
        studentRllNoList = lessonList
        notifyDataSetChanged()
    }

    fun clearData() {
        studentRllNoList.clear()
        notifyDataSetChanged()
    }


}