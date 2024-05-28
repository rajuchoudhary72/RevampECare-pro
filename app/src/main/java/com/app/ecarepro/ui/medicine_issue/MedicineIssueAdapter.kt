package com.app.ecarepro.ui.medicine_issue

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.MedicineIssueItemBinding

class MedicineIssueAdapter(private var leaveList: List<MedicineIssued>) :
    RecyclerView.Adapter<MedicineIssueAdapter.MedicineIssueViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineIssueViewHolder {
        val binding =
            MedicineIssueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineIssueViewHolder(binding)
    }

    override fun getItemCount(): Int = leaveList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedicineIssueViewHolder, position: Int) {
        val medicineIssued=leaveList[position]
        val mainBinding=DataBindingUtil.getBinding<MedicineIssueItemBinding>(holder.itemView)
        with(mainBinding!!){

            tvMedicineName.text = "Medicine: ${medicineIssued.medicine}"
            tvQuantity.text = "Dose: ${medicineIssued.qty}"
            tvDate.text = medicineIssued.receiptDate
            tvInTime.text = "In: " + medicineIssued.inTime
            tvOutTime.text = "Out: " + medicineIssued.outTime
            if (medicineIssued.attendedBy.equals("")) {
                tvAttdentName.text = "N/A"
            } else {
                tvAttdentName.text = medicineIssued.attendedBy
            }

            if (medicineIssued.informedParent.equals("")) {
                tvInfParent.text = "N/A"
            } else {
                tvInfParent.text=medicineIssued.informedParent
            }

            if (medicineIssued.remark.equals("")) {
                tvRemark.text = "N/A"
            } else {
                tvRemark.text = medicineIssued.remark
            }
            if (medicineIssued.reasontoVisitInfirmary.equals("")) {
                tvReason.text = "N/A"
            } else {
                tvReason.text = medicineIssued.reasontoVisitInfirmary
            }

            if (medicineIssued.diagnosis.equals("")) {
                tvDiagnosis.text = "N/A"
            } else {
                tvDiagnosis.text = medicineIssued.diagnosis
            }

            tvRemark.setOnClickListener(object : View.OnClickListener {
                private var isMaxLineOne = true
                override fun onClick(v: View) {
                    isMaxLineOne = if (isMaxLineOne) {
                        tvRemark.maxLines = 5
                        false
                    } else {
                        tvRemark.maxLines = 1
                        true
                    }
                }
            })
        }
    }

    class MedicineIssueViewHolder(itemView: MedicineIssueItemBinding) :
        RecyclerView.ViewHolder(itemView.root)


}