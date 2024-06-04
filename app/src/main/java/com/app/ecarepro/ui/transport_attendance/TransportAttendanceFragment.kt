package com.app.ecarepro.ui.transport_attendance

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_trans_att.StuAtt
import com.app.ecarepro.databinding.FragmentTransportAttendanceBinding
import com.app.ecarepro.model.RouteLST
import com.app.ecarepro.model.StopLST
import com.app.ecarepro.model.StuLst
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.transport_attendance.adapter.RouterPopUpListAdapter
import com.app.ecarepro.ui.transport_attendance.adapter.StoppersPopUpListAdapter
import com.app.ecarepro.ui.transport_attendance.adapter.StudentListToMarkAttAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.app.ecarepro.utils.listener.OnClickItemValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransportAttendanceFragment : Fragment() , OnClickItemValue<StuLst>, MenuProvider  {


    private lateinit var studentListToMarkAtt: List<StuLst>
    private lateinit var stoppersSelectData: StopLST
    private var routeSelected: Boolean = false
    private var stoppersSelected: Boolean = false
    private lateinit var routerSelectData: RouteLST
    private lateinit var binding : FragmentTransportAttendanceBinding
    private val transportAttendanceViewModel : TransportAttendanceViewModel by viewModels()

    private lateinit var routeLSTList: List<RouteLST>
    private   var stopLSTList: List<StopLST> = ArrayList()

    private var tripType = 0
    private var p  = 0
    private var a  = 0
    private var l  = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentTransportAttendanceBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
       // from= requireArguments().getString(Constant.TO).toString()
       // binding.toolbar.title=from
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.apply {
            tvSelectRoute.setOnClickListener {
                if (routeLSTList.isNotEmpty()){
                    popUpRouter()
                }else{
                    mainActivity().showMessage("No Route Data")
                }

            }
            tvSelectStoppage.setOnClickListener {
                if (stopLSTList.isNotEmpty()){
                    popUpStoppers()
                }else{
                    mainActivity().showMessage("No Stoppers Data")
                }
            }

             val spinnerTripTypeAdapter = ArrayAdapter(requireActivity(),
                 android.R.layout.simple_spinner_item,resources.getStringArray(R.array.tripType))
            spinnerSelectTrip.adapter=spinnerTripTypeAdapter

            spinnerSelectTrip.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                     when(position){
                         1 -> {
                          tripType=Constant.UP_TRIP
                             getStoppersList()

                         }
                         2 -> {
                             tripType=Constant.DOWN_TRIP
                             getStoppersList()
                         }
                         3 -> {
                             tripType=Constant.DROP_STUDENT_TRIP
                         }
                     }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                 }
            }  }
        getRouterList()
    }

    private fun getRouterList() {
        lifecycleScope.launch {
            transportAttendanceViewModel.routesListStateFlow.collectLatest {
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
                            routeLSTList=it.data.routeLST
                        }
                    }
                }
            }
        }
        transportAttendanceViewModel.getRoutesList()
    }

    private fun getStoppersList() {
        lifecycleScope.launch {
            transportAttendanceViewModel.stoppageListStateFlow.collectLatest {
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
                            stopLSTList=it.data.stopLST
                         }
                    }
                }
            }
        }
        transportAttendanceViewModel.getStoppageList(routerSelectData.routeID.toString(),tripType)
    }

    private fun getStudentToMarkTransAttendance() {
        lifecycleScope.launch {
            transportAttendanceViewModel.studentToMarkTransAttendanceStateFlow.collectLatest {
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
                        if (it.data!=null){

                            if (it.data.stuLst!=null){

                                binding.recyclerStudentAttMark.isVisible=true
                                binding.tvNoData.isVisible=false

                                val studentListToMarkAttAdapter =  StudentListToMarkAttAdapter(
                                    it.data.stuLst  ,
                                    tripType,
                                    this@TransportAttendanceFragment)

                                binding.recyclerStudentAttMark.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = studentListToMarkAttAdapter
                                }

                                studentListToMarkAtt=it.data.stuLst

                            }else{
                                binding.recyclerStudentAttMark.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }
                    }
                }
            }
        }
        var isValidate= true
        if (!routeSelected ){
            mainActivity().showMessage("Please Select Route")
            isValidate= false
        }
        if (!stoppersSelected ){
            mainActivity().showMessage("Please Select Route")
            isValidate= false
        }
        if (tripType==0 ){
            mainActivity().showMessage("Please Select Route")
            isValidate= false
        }
        if (isValidate){
            if (tripType==Constant.DROP_STUDENT_TRIP){
                transportAttendanceViewModel.getStudentToDrop(
                    routeID = routerSelectData.routeID,
                    stopID = stoppersSelectData.stopID,
                    attDate = Constant.currentDate().toString()
                )
            }else{
                transportAttendanceViewModel.getStudentToMarkTransAttendance(
                    routeIDs =   routerSelectData.routeID.toString(),
                    stopID= stoppersSelectData.stopID,
                    trip = tripType,
                    attDate =  Constant.currentDate().toString(),
                    stopIDs = stoppersSelectData.stopID.toString()

                )
            }

        }

    }

    private fun popUpRouter(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_route)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectRoute.text= routerSelectData.routeName
            routeSelected=true

            builder.dismiss()
        }

        val routerPopUpListAdapter= RouterPopUpListAdapter(routeLSTList, object : ItemListener<RouteLST> {
            override fun onItemClick(t: RouteLST, pos: Int, boolean: Boolean) {
                routerSelectData = t
            }  })

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

    private fun popUpStoppers(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_stoppae)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectStoppage.text= stoppersSelectData.stopName
            stoppersSelected=true
            getStudentToMarkTransAttendance()
            builder.dismiss()
        }

        val stoppersPopUpListAdapter= StoppersPopUpListAdapter(stopLSTList, object : ItemListener<StopLST> {
            override fun onItemClick(t: StopLST, pos: Int, boolean: Boolean) {
                stoppersSelectData = t
            }  })

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



    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_save, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_save -> {
              popUpDetailsMarkAttendance()
                true
            }
            else -> false
        }
    }

    override fun onItemClick(t: StuLst, pos: Int, action: Int) {
        if (action==Constant.DROP_CONFORM){
            transportAttendanceViewModel.dropToStudent(t.stID,Constant.currentDate(),true).invokeOnCompletion {
                mainActivity().showMessage("Updated Successfully!!!")
            }
        }else{
            studentListToMarkAtt[pos].isSelected=true
            studentListToMarkAtt[pos].status=action

        }

     }


    private fun popUpDetailsMarkAttendance() {


        val tv_cancel: TextView
        val tv_ok: TextView
        val tv_present_count: TextView
        val tv_absent_count: TextView
        val tv_leave_count: TextView
        val tvLateCount: TextView
        val na_day: TextView
        val llLate: LinearLayout
        val ll_leave: LinearLayout
        val NALL: LinearLayout


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.pop_up_mark_details_attendance)
        tv_cancel = dialog.findViewById(R.id.tv_cancel)
        tv_ok = dialog.findViewById(R.id.tv_ok)
        llLate = dialog.findViewById(R.id.llLate)
        tv_present_count = dialog.findViewById(R.id.tv_present_count)
        tv_absent_count = dialog.findViewById(R.id.tv_absent_count)
        tv_leave_count = dialog.findViewById(R.id.leave_day)
        ll_leave = dialog.findViewById(R.id.ll_leave)
        NALL = dialog.findViewById(R.id.NALL)
        if (tripType==Constant.UP_TRIP){
            ll_leave.visibility=View.GONE
            NALL.visibility=View.GONE
        }else{
            NALL.visibility=View.GONE
        }

        na_day = dialog.findViewById(R.id.na_day)
        tvLateCount = dialog.findViewById(R.id.late_day)
        for (i in   studentListToMarkAtt) {

            if (tripType==Constant.UP_TRIP){
                when (i.status) {
                    1 -> {
                        p++
                    }
                    0 -> {
                        a++
                    }
                }

            }else if (tripType==Constant.DOWN_TRIP){
                when (i.status) {
                    1 -> {
                        p++
                    }
                    0 -> {
                        a++
                    }
                    2 -> {
                        l ++
                    }
                }
            }

        }

        tv_present_count.text = p.toString() + ""
        tv_absent_count.text = a.toString() + ""
        tv_leave_count.text = l.toString() + ""
         dialog.show()
        tv_cancel.setOnClickListener { dialog.dismiss() }
        tv_ok.setOnClickListener {
            saveMarkAttendance()
            dialog.dismiss()
        }


    }

    private fun saveMarkAttendance() {

        val requestList = mutableListOf<StuAtt>()
        studentListToMarkAtt.forEach { d ->
            requestList.add(StuAtt(d.stID,d.status,d.stopID))
        }

        transportAttendanceViewModel.postTransAttendance(
            Constant.currentDate().toString(),
            routerSelectData.routeID,
            stoppersSelectData.stopID,
            requestList,
            tripType
        ).invokeOnCompletion {
            mainActivity().showMessage("Attendance Marked Successfully")
        }


    }

}