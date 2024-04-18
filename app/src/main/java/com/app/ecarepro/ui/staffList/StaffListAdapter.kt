package com.app.ecarepro.ui.staffList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StaffListItemBinding
import com.app.ecarepro.model.Staff

class StaffListAdapter(private var staffList: List<Staff>,
                       private var staffListFragment: StaffListFragment
) :
    RecyclerView.Adapter<StaffListAdapter.StaffListViewHolder>() {

        private lateinit var bindingm:   StaffListItemBinding





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {
        bindingm=StaffListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StaffListViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = staffList.size

    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {

        bindingm.staffData=staffList[position]
        val data= staffList[position]

        bindingm.tvClassName.text= buildString {
            append("( ")
            append(data.designation)
            append(" )")
        }



        bindingm.llMain.setOnClickListener {
            staffListFragment.onItemClick(data,1,false)
        }
           }

    class StaffListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}