package com.app.ecarepro.ui.timeTable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentDayWiseTimeTableBinding
import com.app.ecarepro.model.TimeTableData
import com.app.ecarepro.ui.assignment.AssignmentListAdapter


class DayWiseTimeTableFragment(private val timeTableData: TimeTableData   ) : Fragment() {


    private lateinit var binding : FragmentDayWiseTimeTableBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentDayWiseTimeTableBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (timeTableData!=null){



            val assignmentListAdapter =
                DayWiseListAdapter(timeTableData.timeTable,
                    this@DayWiseTimeTableFragment)

            binding.rvTimeTable.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = assignmentListAdapter
            }
            binding.rvTimeTable.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvTimeTable.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }
}