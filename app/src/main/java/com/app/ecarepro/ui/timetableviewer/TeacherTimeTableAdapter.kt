package com.app.ecarepro.ui.timetableviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.TeacherTimeTableListItemBinding
import com.app.ecarepro.model.Teacher
import com.squareup.picasso.Picasso

class TeacherTimeTableAdapter(
    private var teacherList: List<Teacher>,
    private var classTimeTableFragment: TeacherTimeTableFragment
) :
    RecyclerView.Adapter<TeacherTimeTableAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: TeacherTimeTableListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            TeacherTimeTableListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = teacherList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {

        val binding = DataBindingUtil.getBinding<TeacherTimeTableListItemBinding>(holder.itemView)

        with(binding!!) {
            val data= teacherList[position]
            tvTeacherName.text = data.name
            Picasso.get().
            load(data.photo)
                .placeholder(R.drawable.default_profile)
                .  into(ivProfile)
            cvMain.setOnClickListener {
                classTimeTableFragment.onItemClick(data,0,false)
            }


        }



     }


    class AssignmentListAdapter(itemView: TeacherTimeTableListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}