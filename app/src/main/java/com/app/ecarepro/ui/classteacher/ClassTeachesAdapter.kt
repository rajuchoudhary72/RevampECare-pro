package com.app.ecarepro.ui.classteacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.StudentListItemBinding
import com.app.ecarepro.databinding.TeacherListItemBinding
import com.app.ecarepro.model.Teacher
import com.squareup.picasso.Picasso

class ClassTeachesAdapter(private var teacherList: List<Teacher>,
                          private var classTeacherFragment: ClassTeacherFragment
) :
    RecyclerView.Adapter<ClassTeachesAdapter.CircularViewHolder>() {

        private lateinit var bindingm:   TeacherListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        bindingm=TeacherListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CircularViewHolder(bindingm)
    }

    override fun getItemCount(): Int = teacherList.size

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<TeacherListItemBinding>(holder.itemView)

        with(binding!!) {
             staffData=teacherList[position]
            val data= teacherList[position]

             tvClassName.text= buildString {
                append("( ")
                append(data.designation)
                append(" )")
            }

            Picasso.get().
            load(data.photo)
                .placeholder(R.drawable.default_profile)
                .  into(circleImageViewProfile)

        }



           }

    class CircularViewHolder(itemView: TeacherListItemBinding) : RecyclerView.ViewHolder(itemView.root){
  }


}