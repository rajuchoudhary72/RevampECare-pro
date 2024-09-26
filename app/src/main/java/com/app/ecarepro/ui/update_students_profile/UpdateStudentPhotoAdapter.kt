package com.app.ecarepro.ui.update_students_profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemAssignRollnoBinding
import com.app.ecarepro.databinding.UpdateStudentProfitStudentsListItemBinding
import com.app.ecarepro.model.StudentRllNo
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlin.random.Random

class UpdateStudentPhotoAdapter(
    private var studentRllNoList: MutableList<StudentRllNo>,
     val itemValue : (StudentRllNo) -> Unit

) :
    RecyclerView.Adapter<UpdateStudentPhotoAdapter.AssignRollNoViewHolder>() {

    private lateinit var bindings: UpdateStudentProfitStudentsListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignRollNoViewHolder {
        bindings =
            UpdateStudentProfitStudentsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignRollNoViewHolder(bindings)
    }

    override fun getItemCount(): Int = studentRllNoList.size

    override fun onBindViewHolder(holder: AssignRollNoViewHolder, position: Int) {

        holder.bind(studentRllNoList[position])


    }

    fun generateRandomNumber(from: Int, to: Int): Int {
        return Random.nextInt(from, to + 1)
    }


    inner class AssignRollNoViewHolder(private val binding: UpdateStudentProfitStudentsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentRllNo) {
            binding.apply {
                tvStudentName.text = buildString {
                     append(student.name)
                }
                tvAdmissionNo.text = buildString {
                     append(student.admissionNumber)
                }
                tvRollNo.text = student.rollNumber

                Picasso.get().load(student.photo)
                    .placeholder(R.drawable.default_profile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(circleImageViewStudent)



                circleImageViewStudent.setOnClickListener {
                    itemValue.invoke(student)
                }
            }


        }




    }





}