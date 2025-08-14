package com.app.ecarepro.ui.students_list.students_new_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StudentListItemBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.utils.Constant

class StudentListNewAdapter(
    private var studentList: List<Student>,
    private var studentListFragment: StudentListSubFragment,
    private val toFragment: String
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


            if (toFragment== Constant.PROFILE_FRA_STU){
                llButton.isVisible=false
                bindingm.llMain.setOnClickListener {
                    studentListFragment.onItemClick(data,1,false)
                }
            }

            bindingm.rollno.text= buildString {
                append(studentListFragment.getString(R.string.general_roll_no_pun))
                append(data.rollNumber)
            }

            bindingm.tvClassName.text= buildString {
                append(studentListFragment.getString(R.string.general_classes_pun))
                append(data.`class`)
            }
            bindingm.tvClassName.isVisible=false


            bindingm.admission.text= buildString {
                append(studentListFragment.getString(R.string.general_admission_no_pun))
                append(data.admissionNumber)
            }




            bindingm.cvView.setOnClickListener {
                studentListFragment.onItemClick(data,0,false)
            }
            bindingm.cvAdd.setOnClickListener {
                studentListFragment.onItemClick(data,1,false)
            }
            bindingm.llMain.setOnClickListener {
                studentListFragment.onItemClick(data,3,false)
            }
        }

           }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}