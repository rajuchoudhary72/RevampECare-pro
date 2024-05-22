package com.app.ecarepro.ui.studentProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.StudentProfileAttendenceListItemBinding
import com.app.ecarepro.databinding.StudentProfileMedicineListItemBinding
import com.app.ecarepro.model.MedicineIssued

class StudentProfileMedicineListAdapter(
    private var medicineIssuedList: List<MedicineIssued>,
    private var activityCalenderFragment: StudentProfileMedicineIssuedFragment
) :
    RecyclerView.Adapter<StudentProfileMedicineListAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: StudentProfileMedicineListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            StudentProfileMedicineListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm.root)
    }

    override fun getItemCount(): Int = medicineIssuedList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
        bindingm.medicineData = medicineIssuedList[position]


     }


    class AssignmentListAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}