package com.app.ecarepro.ui.appointment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAppointmentReportBinding
import com.app.ecarepro.model.Appointment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AppointmentReportFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentReportBinding
    private val appointmentViewModel: AppointmentViewModel by viewModels()
    private var all = true
    private var appointType = Constant.TODAY
    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()
    private val arrayList = ArrayList<Appointment>()


    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            dateFrom.setOnClickListener { pickDateRange() }
            dateTo.setOnClickListener { pickDateRange() }

            toggleButtonAllMe.addOnButtonCheckedListener { _, checkedId, isChecked ->
                when (toggleButtonAllMe.checkedButtonId) {
                    R.id.btn_all -> {
                        all = true
                        getAppointments()
                    }

                    else -> {
                        all = false
                        getAppointments()
                    }
                }
            }
            toggleButtonTodayUpcoming.addOnButtonCheckedListener { _, checkedId, isChecked ->
                when (toggleButtonTodayUpcoming.checkedButtonId) {
                    R.id.btn_today -> {

                        getAppointments()
                    }

                    else -> {

                        getAppointments()
                    }
                }
            }

            setUpPager()


        }
    }

    private fun setUpPager() {

        val fragmentList: ArrayList<Fragment> = ArrayList()



        fragmentList.add(AppointmentSubFragment(arrayList, 0))
        fragmentList.add(AppointmentSubFragment(arrayList, 1))
        fragmentList.add(AppointmentSubFragment(arrayList, 2))


        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            activity?.supportFragmentManager!!,
            lifecycle
        )
        binding.viewPager.adapter = viewPagerAdapter


        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->

            when (position) {
                0 -> {
                    tab.text = "Approved"
                }

                1 -> {
                    tab.text = "Pending"
                }

                2 -> {
                    tab.text = "Reject"
                }
            }


        }.attach()


    }


    private fun pickDateRange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(dateFrom.timeInMillis, dateTo.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }
        appointType = Constant.DATE_RANGE
        getAppointments()


    }

    private fun getAppointments() {
        lifecycleScope.launch {
            appointmentViewModel.appointmentsStateFlow.collectLatest {

                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            if (it.data.appointments != null) {

                                val fragmentList: ArrayList<Fragment> = ArrayList()



                                fragmentList.add(AppointmentSubFragment(it.data.appointments, 0))
                                fragmentList.add(AppointmentSubFragment(it.data.appointments, 1))
                                fragmentList.add(AppointmentSubFragment(it.data.appointments, 2))


                                val viewPagerAdapter = ViewPagerAdapter(
                                    fragmentList,
                                    activity?.supportFragmentManager!!,
                                    lifecycle
                                )
                                binding.viewPager.adapter = viewPagerAdapter


                            }

                        }

                    }

                    else -> {}
                }


            }
        }
        when (appointType) {
            Constant.TODAY -> {
                appointmentViewModel.appointmentOverview(
                    Constant.currentDate(),
                    Constant.currentDate(),
                    all
                )
            }

            Constant.UP_COMING -> {
                appointmentViewModel.appointmentOverview(
                    Constant.currentDate(),
                    "",
                    all
                )
            }

            Constant.DATE_RANGE -> {
                appointmentViewModel.appointmentOverview(
                    binding.dateFrom.text.toString(),
                    binding.dateTo.text.toString(),
                    all
                )
            }
        }


    }

}