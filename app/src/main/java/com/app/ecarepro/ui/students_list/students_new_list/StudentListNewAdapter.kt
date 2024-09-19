package com.app.ecarepro.ui.students_list.students_new_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StudentListItemBinding
import com.app.ecarepro.databinding.TeacherItemBinding
import com.app.ecarepro.model.Student

class StudentListNewAdapter(private var studentList: List<Student>,
                            private var studentListFragment: StudentListSubFragment
) :
    RecyclerView.Adapter<StudentListNewAdapter.CircularViewHolder>() {

        private lateinit var binding :   StudentListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        binding=StudentListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CircularViewHolder(binding.root)
    }

    override fun getItemCount(): Int = studentList.size

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {

        val bindingm = DataBindingUtil.getBinding<StudentListItemBinding>(holder.itemView)
        bindingm?.apply {
            bindingm.studentData=studentList[position]
            val data= studentList[position]

            bindingm.rollno.text= buildString {
                append("Roll No : ")
                append(data.rollNumber)
            }

            bindingm.tvClassName.text= buildString {
                append("Class : ")
                append(data.`class`)
            }
            bindingm.tvClassName.isVisible=false


            bindingm.admission.text= buildString {
                append("Admission No : ")
                append(data.admissionNumber)
            }

            bindingm.llMain.setOnClickListener {
                studentListFragment.onItemClick(data,1,false)
            }
        }

           }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}