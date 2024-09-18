package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentStudentProfileMedicineIssuedBinding
import com.app.ecarepro.model.MedicineIssued


class StudentProfileMedicineIssuedFragment(private val medicineIssued: List<MedicineIssued>) : Fragment() {

    private lateinit var binding: FragmentStudentProfileMedicineIssuedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentStudentProfileMedicineIssuedBinding.inflate(inflater,container,false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            if (medicineIssued!=null) {
            if (medicineIssued.isNotEmpty()) {

                binding.rvMedicineIssue.isVisible = true
                binding.tvNoData.isVisible = false

                val profileMedicineListAdapter =
                    StudentProfileMedicineListAdapter(medicineIssued, this@StudentProfileMedicineIssuedFragment)

                binding.rvMedicineIssue.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = profileMedicineListAdapter
                }
            } else {
                binding.rvMedicineIssue.isVisible = false
                binding.tvNoData.isVisible = true
            }
            } else {
                binding.rvMedicineIssue.isVisible = false
                binding.tvNoData.isVisible = true
            }

    }
}