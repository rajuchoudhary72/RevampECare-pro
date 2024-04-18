package com.app.ecarepro.ui.report.class_report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentClassAttSubBinding
import com.app.ecarepro.model.AttReport
import com.app.ecarepro.ui.calender.CalenderListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener


class ClassAttSubFragment(val attReport: List<AttReport>) : Fragment() , ItemListener<AttReport> {

    private lateinit var binding : FragmentClassAttSubBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentClassAttSubBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (attReport !=null){



            val calenderListAdapter =
                ClassAttAdapter(attReport,
                    this@ClassAttSubFragment)

            binding.rvClassAtt.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = calenderListAdapter
            }
            binding.rvClassAtt.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvClassAtt.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    override fun onItemClick(t: AttReport, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.action_classAttendanceFragment_to_studentAttRepoFragment2).apply {
            Bundle().apply {
                putString(Constant.ID , t.id)
            }
        }
    }


}