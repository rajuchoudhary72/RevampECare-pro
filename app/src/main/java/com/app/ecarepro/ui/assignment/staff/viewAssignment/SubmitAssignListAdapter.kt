package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R

import com.app.ecarepro.databinding.SubmittedStuListBinding
 import com.app.ecarepro.model.AssignSubmitStudent


class SubmitAssignListAdapter(
    private var activityLST: List<AssignSubmitStudent>,
    private var viewAssignmentFragment:  ViewAssignmentFragment,
    private val itemValue : (remark : String, pos : Int)-> Unit
) :
    RecyclerView.Adapter<SubmitAssignListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: SubmittedStuListBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            SubmittedStuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = activityLST.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.stuData = activityLST[position]
         val data = activityLST[position]

        if (data.isOfflineSubmitted){
            bindingm.tvSubmittedBy.text=  viewAssignmentFragment.getString(R.string.offline)
            bindingm.tvSubmittedBy.setTextColor(Color.parseColor("#000000"))
        }else{
            bindingm.tvSubmittedBy.text= viewAssignmentFragment.getString(R.string.online)
            bindingm.tvSubmittedBy.setTextColor(Color.parseColor("#4DAC3C"))

        }


        if (data.asgFile==null){
            bindingm.llView.isVisible=false
            bindingm.llDownload.isVisible=false
        }else{
            bindingm.llView.isVisible=true
            bindingm.llDownload.isVisible=true
        }

        if (data.remark!=null){
            bindingm.textFiledRemark.setText(data.remark)
        }
        activityLST[position].remark= bindingm.textFiledRemark.text.toString()
//        bindingm.textFiledRemark.doAfterTextChanged {
//            itemValue(bindingm.textFiledRemark.text.toString(),position)
//            activityLST[position].remark= bindingm.textFiledRemark.text.toString()
//        }
//        bindingm.textFiledRemark.setOnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus){
//                itemValue(bindingm.textFiledRemark.text.toString(),position)
//                activityLST[position].remark= bindingm.textFiledRemark.text.toString()
//
//            }
//        }

        bindingm.textFiledRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                 itemValue(s.toString(),position)
            }
        })

        bindingm.llView.setOnClickListener {
            viewAssignmentFragment.onItemClick(data,1,false)
        }
        bindingm.llDownload.setOnClickListener {
            viewAssignmentFragment.onItemClick(data,2,false)
        }
    }





    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}