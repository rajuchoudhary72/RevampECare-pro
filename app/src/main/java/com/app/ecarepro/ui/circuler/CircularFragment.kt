package com.app.ecarepro.ui.circuler

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCirculerBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.Circular
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.leave.leave_report.LeaveReportAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch



@AndroidEntryPoint
class CircularFragment : Fragment(), ItemListener<Circular> {

    private var isYearSelected: Boolean=false
    private var selectedYearID: Int=0
    private var yearList: List<AcademicYear> = ArrayList<AcademicYear>()
    private val circularViewModel :CircularViewModel   by viewModels()
    private lateinit var fragmentCircularBinding: FragmentCirculerBinding
    var selectedYearData: AcademicYear? =null
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private lateinit var   circularListAdapter: CircularListAdapter
    private var isFirst=true


    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCircularBinding = FragmentCirculerBinding.inflate(inflater, container, false)
        fragmentCircularBinding.includeToolbar.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(
                this
            ).popBackStack() }
        fragmentCircularBinding.includeToolbar.toolbarTitle.text = getString(R.string.circular)
        return fragmentCircularBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentCircularBinding.tvSelectSession.setOnClickListener {
            popUpSelectAcademicYears()
        }

        circularListAdapter = CircularListAdapter( this@CircularFragment)

            fragmentCircularBinding.recyclerCircular.adapter = circularListAdapter


        fragmentCircularBinding.edSearch.doAfterTextChanged { text ->
            val query = text?.toString()?.trim() ?: ""
            if (query != circularViewModel.lastSearchQuery) {
                circularViewModel.lastSearchQuery = query

                // Cancel the previous search request
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                // Schedule a new search request with a delay
                searchRunnable = Runnable {
                    pageIndex=1
                    circularViewModel.pageIndex=1
                    circularViewModel.getCirculars(pageIndex, selectedYearID, query)
                }
                searchHandler.postDelayed(searchRunnable!!, 500) // 500ms delay
            }
        }

        lifecycleScope.launch {
            circularViewModel._circularsStateFlowStateFlow.observe(viewLifecycleOwner) {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        fragmentCircularBinding.recyclerCircular.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentCircularBinding.recyclerCircular.isVisible = false
                        Log.d("main", "Error" + it )
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentCircularBinding.recyclerCircular.isVisible = true

                        if (it.data!=null){
                            if (it.data.academicYears!=null){
                                yearList=it.data.academicYears
                                if (it.data.academicYears.isNotEmpty()){
                                    if (isFirst){
                                        circularViewModel.academicYear=it.data.academicYears[0].session
                                        fragmentCircularBinding.tvSelectSession.text= it.data.academicYears[0].session
                                        isFirst=false
                                    }
                                }
                            }

                            if (it.data.circularList!=null  ){

                                if (it.data.circularList.isNotEmpty()){
                                    fragmentCircularBinding.recyclerCircular.isVisible=true
                                    fragmentCircularBinding.tvNoData.isVisible=false
                                    isLoading=true
                                    if (pageIndex==1){

                                        circularListAdapter.clearData()
                                    }
                                   circularViewModel.cacheListData.addAll(it.data.circularList)
                                    circularListAdapter.setData(it.data.circularList.toMutableList())

                                    fragmentCircularBinding.includeToolbar.toolbarTitle.text= "All Circular" + "( " + it.data.totalCirculer + "/" + it.data.unreadCirculer + ")"

                                }else{
                                    if (pageIndex==1){
                                        fragmentCircularBinding.recyclerCircular.isVisible=false
                                        fragmentCircularBinding.tvNoData.isVisible=true
                                    }

                                }


                            }else{
                                if (pageIndex==1){
                                    fragmentCircularBinding.recyclerCircular.isVisible=false
                                    fragmentCircularBinding.tvNoData.isVisible=true
                                }
                            }

                        }

                    }


                    else -> {}
                }
            }
        }
        setupRecycleViewPager()

        if (circularViewModel.isFirst ) {
            circularViewModel.getCirculars(pageIndex,selectedYearID,"")
            circularViewModel.isFirst=false
        }else{
            pageIndex=circularViewModel.pageIndex
            circularListAdapter.setData(circularViewModel.cacheListData)
            fragmentCircularBinding.tvSelectSession.text= circularViewModel.academicYear

        }


    }

    override fun onItemClick(t: Circular, pos: Int, boolean: Boolean) {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_circularFragment_to_circularDetailsFragment, Bundle().apply {
                putString(Constant.CIRCULAR_ID, t.id)
            })
     }

    private fun popUpSelectAcademicYears(){

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text= getString(R.string.select_academic_year)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isYearSelected){
                pageIndex=1
                fragmentCircularBinding.tvSelectSession.text= selectedYearData!!.session
                circularViewModel.academicYear=selectedYearData!!.session
                circularViewModel.cacheListData.clear()
                circularViewModel.getCirculars(pageIndex, selectedYearID,"")
                builder.dismiss()
            }


        }

        val yearAdapter= PopUpListAdapter(yearList, object : ItemListener<AcademicYear>{
            override fun onItemClick(t: AcademicYear, pos: Int, boolean: Boolean) {
               isYearSelected=true
                selectedYearData = t
                selectedYearID=t.yrID
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = yearAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun setupRecycleViewPager() {
        circularListAdapter.clearData()
        fragmentCircularBinding.recyclerCircular.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null) {
                    if (dy > 0) {
                        visibleItemCount = linearLayoutManager.childCount;
                        totalItemCount = linearLayoutManager.itemCount;
                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                        if (isLoading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                isLoading = false
                                pageIndex += 1
                                circularViewModel.pageIndex=pageIndex
                                circularViewModel.getCirculars(pageIndex,selectedYearID,fragmentCircularBinding.edSearch.text.toString())

                            }
                        }

                    }
                }
            }
        })



    }

}