package com.app.ecarepro.ui.transport_attendance.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentTransportAttendanceReportBinding
import com.app.ecarepro.model.RouteLST
import com.app.ecarepro.model.StopLST
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.transport_attendance.adapter.RouterPopUpListAdapter
import com.app.ecarepro.ui.transport_attendance.adapter.StoppersPopUpListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransportAttendanceReportFragment : Fragment() {

    private lateinit var binding: FragmentTransportAttendanceReportBinding
    private val transportAttReportViewModel: TransportAttReportViewModel by viewModels()

    private lateinit var routeLSTList: List<RouteLST>
    private lateinit var stopLSTList: List<StopLST>
    private lateinit var stoppersSelectData: StopLST
    private var routeSelected: Boolean = false
    private var stoppersSelected: Boolean = false
    private lateinit var routerSelectData: RouteLST

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransportAttendanceReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {

            tvSelectDate.setOnClickListener {
                ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback  {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        tvSelectDate.text=date
                    }
                })
            }

            tvSelectRoute.setOnClickListener {
                if (routeLSTList.isNotEmpty()) {
                    popUpRouter()
                } else {
                    mainActivity().showMessage("No Route Data")
                }

            }
            tvSelectStoppage.setOnClickListener {
                if (stopLSTList.isNotEmpty()) {
                    popUpStoppers()
                } else {
                    mainActivity().showMessage("No Stoppers Data")
                }
            }


        }
        getRouterList()

    }

    private fun getRouterList() {
        lifecycleScope.launch {
            transportAttReportViewModel.routesListStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            routeLSTList = it.data.routeLST
                        }
                    }
                }
            }
        }
        transportAttReportViewModel.getRoutesList()
    }

    private fun getStoppersList() {
        lifecycleScope.launch {
            transportAttReportViewModel.stoppageListStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            stopLSTList = it.data.stopLST
                        }
                    }
                }
            }
        }
        transportAttReportViewModel.getStoppageList(routerSelectData.routeID.toString(), 0)
    }

    private fun getStudentToMarkTransAttendance() {
        lifecycleScope.launch {
            transportAttReportViewModel.transAttendanceReportStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            if (it.data.stopLST != null) {

                                binding.recyclerTransportAttReport.isVisible = true
                                binding.tvNoData.isVisible = false

                                val transportAttReportAdapter = TransportAttReportAdapter(
                                    it.data.stopLST,
                                     this@TransportAttendanceReportFragment
                                )

                                binding.recyclerTransportAttReport.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = transportAttReportAdapter
                                }

                            } else {
                                binding.recyclerTransportAttReport.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }
                    }
                }
            }
        }
        var isValidate = true
        if (!routeSelected) {
            mainActivity().showMessage("Please Select Route")
            isValidate = false
        }
        if (!stoppersSelected) {
            mainActivity().showMessage("Please Select Route")
            isValidate = false
        }
        if (binding. tvSelectDate.text.toString() == getString(R.string.select_date)) {
            mainActivity().showMessage("Please Select Date")
            isValidate = false
        }

        if (isValidate) {
            transportAttReportViewModel.getTransAttendanceReport(
                routeID = routerSelectData.routeID,
                stopIDs = stoppersSelectData.stopID.toString(),
                attDate =  binding. tvSelectDate.text.toString()
            )
        }

    }

    private fun popUpRouter() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text = getString(R.string.select_route)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectRoute.text = routerSelectData.routeName
            routeSelected = true
            getStoppersList()
            builder.dismiss()
        }

        val routerPopUpListAdapter =
            RouterPopUpListAdapter(routeLSTList, object : ItemListener<RouteLST> {
                override fun onItemClick(t: RouteLST, pos: Int, boolean: Boolean) {
                    routerSelectData = t
                }
            })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = routerPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpStoppers() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text = getString(R.string.select_stoppae)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectStoppage.text = stoppersSelectData.stopName
            stoppersSelected = true
            getStudentToMarkTransAttendance()
            builder.dismiss()
        }

        val stoppersPopUpListAdapter =
            StoppersPopUpListAdapter(stopLSTList, object : ItemListener<StopLST> {
                override fun onItemClick(t: StopLST, pos: Int, boolean: Boolean) {
                    stoppersSelectData = t
                }
            })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = stoppersPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}