package com.app.ecarepro.ui.staffAttendence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAttendenceBinding
import com.app.ecarepro.model.MonthModel
import com.app.ecarepro.model.YearModel
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.syllabus.SyllabusListAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var yearSelected: Int = 0
    private var monthSelected: Int = 0
    private var monthModelArrayList = ArrayList<MonthModel>()
    private var yearModelArrayList = ArrayList<YearModel>()
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private lateinit var binding: FragmentAttendenceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAttendenceBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.autoCompleteMonth.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                monthSelected = monthModelArrayList[pos].monthID

                attendanceViewModel.staffAttendance(monthSelected, yearSelected)

            }

        binding.autoCompleteYear.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                yearSelected = yearModelArrayList[pos].yearId.toInt()
                attendanceViewModel.staffAttendance(monthSelected, yearSelected)

            }

        lifecycleScope.launch {
            attendanceViewModel.staffAttendenceStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAttendence.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendence.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendence.isVisible = true

                        if (it.data != null) {




                             if (it.data.startYear!=0){
                                 bindYearArray(it.data.startYear)
                                 bindMonthArray()
                             }


                             if (yearModelArrayList != null) {

                                 val monthDataString: ArrayList<String> = ArrayList()


                                 monthDataString.clear()

                                 yearModelArrayList.forEach { data ->
                                     monthDataString.add(data.yearId.toString())
                                 }

                                 val arrayAdapter = ArrayAdapter(
                                     requireContext(),
                                     android.R.layout.simple_list_item_1,
                                     monthDataString
                                 )
                                 binding.autoCompleteYear.setAdapter(arrayAdapter)
                             }

                             if (monthModelArrayList != null) {

                                 val monthDataString: ArrayList<String> = ArrayList()


                                 monthDataString.clear()
                                 monthModelArrayList.forEach { data ->
                                     monthDataString.add(data.month.toString())
                                 }

                                 val arrayAdapter = ArrayAdapter(
                                     requireContext(),
                                     android.R.layout.simple_list_item_1,
                                     monthDataString
                                 )
                                 binding.autoCompleteMonth.setAdapter(arrayAdapter)
                             }


                             if (it.data.attendance != null) {

                                 if (it.data.attendance.isNotEmpty()) {

                                     binding.rvAttendence.isVisible = true
                                     binding.tvNoData.isVisible = false

                                     val noticeAdapter =
                                         StaffAttendenceListAdapter(
                                             it.data.attendance,
                                             this@AttendanceFragment
                                         )

                                     binding.rvAttendence.apply {
                                         setHasFixedSize(true)
                                         layoutManager = LinearLayoutManager(activity)
                                         adapter = noticeAdapter
                                     }

                                 } else {
                                     binding.rvAttendence.isVisible = false
                                     binding.tvNoData.isVisible = true
                                 }

                             } else {
                                 binding.rvAttendence.isVisible = false
                                 binding.tvNoData.isVisible = true
                             }
                       }

                    }
                  }
            }
        }

        attendanceViewModel.staffAttendance(monthSelected, yearSelected)

    }

    private fun bindMonthArray() {
        monthModelArrayList = ArrayList<MonthModel>()

        val monthMode = MonthModel(0, "Select Month")
        monthModelArrayList.add(monthMode)

        val monthModel1 = MonthModel(1, "January")
        monthModelArrayList.add(monthModel1)

        val monthModel2 = MonthModel(2, "February")
        monthModelArrayList.add(monthModel2)

        val monthModel3 = MonthModel(3, "March")
        monthModelArrayList.add(monthModel3)

        val monthModel4 = MonthModel(4, "April")
        monthModelArrayList.add(monthModel4)

        val monthModel5 = MonthModel(5, "May")
        monthModelArrayList.add(monthModel5)

        val monthModel6 = MonthModel(6, "June")
        monthModelArrayList.add(monthModel6)

        val monthModel7 = MonthModel(7, "July")
        monthModelArrayList.add(monthModel7)

        val monthMode8 = MonthModel(8, "August")
        monthModelArrayList.add(monthMode8)

        val monthMode9 = MonthModel(9, "September")
        monthModelArrayList.add(monthMode9)

        val monthMode10 = MonthModel(10, "October")
        monthModelArrayList.add(monthMode10)

        val monthMode11 = MonthModel(11, "November")
        monthModelArrayList.add(monthMode11)

        val monthMode12 = MonthModel(12, "December")
        monthModelArrayList.add(monthMode12)


    }

    private fun bindYearArray(year: Int = 0) {
        yearModelArrayList = ArrayList<YearModel>()
        val now = Calendar.getInstance()
        val yearModel = YearModel("Select Year")
        yearModelArrayList.add(yearModel)
        for (i in year..now[Calendar.YEAR]) {
            val yearModel1 = YearModel(i.toString() + "")
            yearModelArrayList.add(yearModel1)
        }
    }


}