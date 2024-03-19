package com.app.ecarepro.ui.class_promo

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.CustomPopupSelectClassBinding
import com.app.ecarepro.databinding.ItemClassPromotionBinding
import com.app.ecarepro.model.NextSessionClasse

import com.app.ecarepro.model.Section

import com.app.ecarepro.model.Student
import com.squareup.picasso.Picasso


class ClassPromotionsAdapter(
    private var studentMutableList: MutableList<Student>
) :
    RecyclerView.Adapter<ClassPromotionsAdapter.ClassPromotionsHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassPromotionsHolder {
        val mBinding =
            ItemClassPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassPromotionsHolder(mBinding)
    }

    override fun getItemCount(): Int = studentMutableList.size

    override fun onBindViewHolder(holder: ClassPromotionsHolder, position: Int) {
        val mBinding = DataBindingUtil.getBinding<ItemClassPromotionBinding>(holder.itemView)
        val student = studentMutableList[position]
        val nextSessionClasses = getSelectedClass(student.nextSessionClasses!!)

        with(mBinding!!) {
            tvStuName.text = "Name: ${student.name}"
            tvKeyValue1.text = "Class: ${student.`class`}"
            tvKeyValue2.text = "Roll No.:${student.rollNumber}"
            tvKeyValue3.text = "Admission No.:${student.admissionNumber}"
            tvKeyValue4.text = "Father Name:${student.fatherName}"
            Picasso.get().load(student.photo).into(civStuImg)
            if (nextSessionClasses != -1) {
                edtRoll.text = student.nextSessionClasses[nextSessionClasses]?.className
            } else {
                edtRoll.text = "N/A"
            }

            if (student.selected != null) {
                edtSection.text = student.selected!!.secName
            } else {
                edtSection.text = "New Section"
            }
            edtSection.setOnClickListener {
                showSection(
                    edtSection.context,
                    position,
                    nextSessionClasses
                )
            }
            edtRoll.setOnClickListener {
                showNewClass(
                    edtRoll.context,
                    position
                )
            }
        }

    }

    var selectedPoss = 0
    private fun getSelectedClass(sessionClasses: MutableList<NextSessionClasse?>): Int {
        for (i in sessionClasses.indices) {
            if (sessionClasses[i]?.isSelected!!) return i
        }
        return -1
    }

    private fun showNewClass(
        context: Context,
        poss: Int
    ) {
        selectedPoss = -1
        val dialogBinding =
            CustomPopupSelectClassBinding.inflate(LayoutInflater.from(context), null, false)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvHeading.text = "Select Class"

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        dialogBinding.rvYear.layoutManager = linearLayoutManager
        val homeSelectAdapter = NewClassAdapter(
            studentMutableList[poss].nextSessionClasses!!
        ) { house, position -> selectedPoss = position }
        dialogBinding.rvYear.adapter = homeSelectAdapter
        dialog.show()
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.tvOk.setOnClickListener {
            if (selectedPoss != -1) {
                setSelectedClass(studentMutableList[poss].nextSessionClasses!!)
                studentMutableList[poss].selected = null
                notifyItemChanged(poss)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Add Message Here", Toast.LENGTH_LONG).show()
            }
        }
    }

    var selected: Section? = null
    private fun showSection(
        context: Context,
        poss: Int,
        nextSessionClassesPoss: Int

    ) {
        selected = null
        val dialogBinding =
            CustomPopupSelectClassBinding.inflate(LayoutInflater.from(context), null, false)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvHeading.text = "Select Section"

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        dialogBinding.rvYear.layoutManager = linearLayoutManager
        val homeSelectAdapter = AssignStudentSelectAdapter(
            studentMutableList[poss].nextSessionClasses!![nextSessionClassesPoss]!!.sections!!
        ) { house, position -> selected = house }
        dialogBinding.rvYear.adapter = homeSelectAdapter
        dialog.show()
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.tvOk.setOnClickListener {
            if (selected != null) {
                studentMutableList[poss].selected = selected
                studentMutableList[poss].selected?.classID = studentMutableList[poss].nextSessionClasses?.get(nextSessionClassesPoss)?.classID
                notifyItemChanged(poss)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Add Message Here", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setSelectedClass(sessionClasses: MutableList<NextSessionClasse?>) {
        for (i in sessionClasses.indices) {
            sessionClasses[i]!!.isSelected = selectedPoss == i
        }
    }

    class ClassPromotionsHolder(itemView: ItemClassPromotionBinding) :
        RecyclerView.ViewHolder(itemView.root)


}