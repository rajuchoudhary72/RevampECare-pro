package com.app.ecarepro.ui.medicalcard.medical_class

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StudentListItemBinding
import com.app.ecarepro.model.Student

class MedicalClassAdapter(private var studentList: List<Student>,  val callback: (poss:Int, student:Student) -> Unit) :
    RecyclerView.Adapter<MedicalClassAdapter.MedicalClassViewHolder>() {
    private var filteredList: List<Student> = studentList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalClassViewHolder {
        val binding =
            StudentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicalClassViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedicalClassViewHolder, position: Int) {
        val studentList=filteredList[position]
        val mainBinding= DataBindingUtil.getBinding<StudentListItemBinding>(holder.itemView)
        with(mainBinding!!){

            studentData=studentList
            rollno.text= buildString {
                append("Roll No : ")
                append(studentList.rollNumber)
            }

            admission.text= buildString {
                append("Admission No : ")
                append(studentList.admissionNumber)
            }
            llMain.setOnClickListener {
                callback.invoke(position,studentList)
            }
        }
    }

    class MedicalClassViewHolder(itemView: StudentListItemBinding) :
        RecyclerView.ViewHolder(itemView.root)
    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            studentList
        } else {
            studentList.filter {item-> item.toString().contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

}