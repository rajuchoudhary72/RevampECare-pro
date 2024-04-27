package com.app.ecarepro.ui.timetableviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ClassListItemBinding
import com.app.ecarepro.model.Classe
import com.app.ecarepro.utils.listener.ItemListener

class ClassTimeTableAdapter    (
    private var classeList: List<Classe>,
    private var classTimeTableFragment: ClassTimeTableFragment
) :
    RecyclerView.Adapter<ClassTimeTableAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: ClassListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            ClassListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = classeList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {

        val binding = DataBindingUtil.getBinding<ClassListItemBinding>(holder.itemView)

        with(binding!!) {
            val data= classeList[position]
            tvClassName.text = data.className
            cvMain.setOnClickListener {

            }


        }



     }


    class AssignmentListAdapter(itemView: ClassListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}