package com.app.ecarepro.ui.assign_home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemAssignHomeBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class AssignHomeAdapter(private var leaveList: List<Student>,private var houseList: List<House>,
                        private val getContext: AssignHomeFragment,
                        val callback: (poss:Int,student:Student) -> Unit) :
    RecyclerView.Adapter<AssignHomeAdapter.AssignHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignHomeViewHolder {
        val binding =
            ItemAssignHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignHomeViewHolder(binding)
    }

    override fun getItemCount(): Int = leaveList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AssignHomeViewHolder, position: Int) {
        val studentList=leaveList[position]
        val mainBinding= DataBindingUtil.getBinding<ItemAssignHomeBinding>(holder.itemView)
        with(mainBinding!!){
            tvStuName.text = getContext.getString(R.string.general_name)+" "+ studentList.name
            tvKeyValue1.text = "Class: " + studentList.`class`
            tvKeyValue2.text = "Roll No.:" + studentList.rollNumber
            tvKeyValue3.text = "Admission No.:" + studentList.admissionNumber
            Picasso.get().load( studentList.photo)
                .into(civStuImg)

            try {
                val houseid: Int = studentList.houseID ?:0
                for (house in houseList) {
                    if (houseid == house.houseID) {
                        if (studentList.houseName!!.isNotEmpty()) {
                            edtHouseName.text = house.houseName
                        } else {
                            edtHouseName.text = getContext.getString(R.string.no_house_assign)
                        }
                    }
                }
            } catch (ignored: Exception) {
            }


            edtHouseName.setOnClickListener { callback(position,studentList) }
        }
    }

    class AssignHomeViewHolder(itemView: ItemAssignHomeBinding) :
        RecyclerView.ViewHolder(itemView.root)


}