package com.app.ecarepro.ui.timeTable

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentDayWiseTimeTableBinding
import com.app.ecarepro.model.TimeTableData

class DayWiseTimeTableFragment() : Fragment() {


    private lateinit var binding : FragmentDayWiseTimeTableBinding
    private var timeTableData: TimeTableData? = null
    private var toFragment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                timeTableData = it.getParcelable(ARG_ITEM_DATA, TimeTableData::class.java)
            }else{
                @Suppress("DEPRECATION")
                timeTableData = it.getParcelable(ARG_ITEM_DATA)
            }
            toFragment = it.getString(ARG_ITEM_TO_FRAGMENT, "")
        }
    }

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
                DayWiseListAdapter(
                    timeTableData!!.timeTable,
                    this@DayWiseTimeTableFragment,toFragment)

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

    companion object {
        private const val ARG_ITEM_DATA = "item_timeTableData"
        private const val ARG_ITEM_TO_FRAGMENT = "item_toFragment"

        fun newInstance( timeTableData: TimeTableData,  toFragment: String)= DayWiseTimeTableFragment().apply {
            arguments= Bundle().apply {
                putParcelable(ARG_ITEM_DATA,timeTableData)
                putString(ARG_ITEM_TO_FRAGMENT,toFragment)

            }
        }

    }



}