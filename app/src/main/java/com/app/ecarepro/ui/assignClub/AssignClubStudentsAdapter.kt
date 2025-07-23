package com.app.ecarepro.ui.assignClub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemAssignClubBinding
import com.app.ecarepro.ui.assign_home.Student
import com.squareup.picasso.Picasso

class AssignClubStudentsAdapter(private var studentList: List<Student>, private var clubsList: List<Clubs>, val callback: (poss:Int, student:Student) -> Unit) :
    RecyclerView.Adapter<AssignClubStudentsAdapter.AssignHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignHomeViewHolder {
        val binding =
            ItemAssignClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignHomeViewHolder(binding)
    }

    override fun getItemCount(): Int = studentList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AssignHomeViewHolder, position: Int) {
        val studentList=studentList[position]
        val mainBinding= DataBindingUtil.getBinding<ItemAssignClubBinding>(holder.itemView)
        with(mainBinding!!){
            tvStuName.text =  studentList.name
            tvKeyValue1.text = "Class: " + studentList.`class`
            tvKeyValue2.text = "Roll No.:" + studentList.rollNumber
            tvKeyValue3.text = "Admission No.:" + studentList.admissionNumber
            Picasso.get().load( studentList.photo)
                .into(civStuImg)

            try {
                val clubsId: Int = studentList.clubID ?:0
                for (clubs in clubsList) {
                    if (clubsId == clubs.clubID) {
                        if (studentList.clubName!!.isNotEmpty()) {
                            edtHouseName.text = clubs.clubName
                        } else {
                            edtHouseName.text = "No Club Assign"
                        }
                    }
                }
            } catch (ignored: Exception) {
            }


            edtHouseName.setOnClickListener { callback(position,studentList) }
        }
    }

    class AssignHomeViewHolder(itemView: ItemAssignClubBinding) :
        RecyclerView.ViewHolder(itemView.root)


}