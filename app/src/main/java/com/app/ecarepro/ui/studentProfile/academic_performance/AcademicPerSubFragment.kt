package com.app.ecarepro.ui.studentProfile.academic_performance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentAcademicPerSubBinding
import com.app.ecarepro.model.Subject
import com.app.ecarepro.ui.timeTable.DayWiseListAdapter


class AcademicPerSubFragment( val itemDat: Subject) : Fragment() {

    private lateinit var binding: FragmentAcademicPerSubBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAcademicPerSubBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (itemDat!=null){



            val assignmentListAdapter =
                AcademicPerfListAdapter(itemDat.marks)

            binding.rvExamList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = assignmentListAdapter
            }
            binding.rvExamList.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvExamList.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }
}