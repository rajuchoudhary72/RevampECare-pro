package com.app.ecarepro.ui.students_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StudentListItemBinding
import com.app.ecarepro.databinding.TeacherItemBinding
import com.app.ecarepro.model.Student

class StudentListAdapter(private var studentList: List<Student>,
                         private var studentListFragment: StudentListFragment
) :
    RecyclerView.Adapter<StudentListAdapter.CircularViewHolder>() {

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