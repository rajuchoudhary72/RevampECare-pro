package com.app.ecarepro.ui.circuler

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCirculerBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.Circular
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CircularFragment : Fragment(), ItemListener<Circular> {

    private var selectedYearID: Int = 0
    private var yearList: List<AcademicYear> = ArrayList<AcademicYear>()
    private val circularViewModel: CircularViewModel by viewModels()
    private lateinit var fragmentCircularBinding: FragmentCirculerBinding
    var selectedYearData: AcademicYear? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentCircularBinding = FragmentCirculerBinding.inflate(inflater, container, false)
        fragmentCircularBinding.tvSelectSession.setOnClickListener {
            popUpSelectAcademicYears()
        }

        fragmentCircularBinding.ivSearch.setOnClickListener {
            if (fragmentCircularBinding.edSearch.text.isNotEmpty()) {
                circularViewModel.getCirculars(
                    Constant.PAGE_INDEX,
                    selectedYearID,
                    fragmentCircularBinding.edSearch.text.toString()
                )
            } else {
                Toast.makeText(requireContext(), "Please enter title!!!", Toast.LENGTH_LONG).show()
            }
        }





        return fragmentCircularBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentCircularBinding.edSearch.doAfterTextChanged {
            if (fragmentCircularBinding.edSearch.text.isNotEmpty()) {
                circularViewModel.getCirculars(
                    Constant.PAGE_INDEX,
                    selectedYearID,
                    fragmentCircularBinding.edSearch.text.toString()
                )
            } else {
                circularViewModel.getCirculars(Constant.PAGE_INDEX, selectedYearID, "")

            }
        }

        lifecycleScope.launch {
            circularViewModel._circularsStateFlowStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        fragmentCircularBinding.recyclerCircular.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentCircularBinding.recyclerCircular.isVisible = false
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentCircularBinding.recyclerCircular.isVisible = true

                        if (it.data != null) {
                            if (it.data.academicYears != null) {
                                yearList = it.data.academicYears
                            }

                            if (it.data.circularList != null) {

                                if (it.data.circularList.isNotEmpty()) {
                                    fragmentCircularBinding.recyclerCircular.isVisible = true
                                    fragmentCircularBinding.tvNoData.isVisible = false

                                    val circularAdapter = CircularListAdapter(
                                        it.data.circularList,
                                        this@CircularFragment
                                    )

                                    fragmentCircularBinding.recyclerCircular.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = circularAdapter
                                    }
                                } else {
                                    fragmentCircularBinding.recyclerCircular.isVisible = false
                                    fragmentCircularBinding.tvNoData.isVisible = true
                                }


                            } else {
                                fragmentCircularBinding.recyclerCircular.isVisible = false
                                fragmentCircularBinding.tvNoData.isVisible = true
                            }

                        }

                    }


                }
            }
        }

        circularViewModel.getCirculars(Constant.PAGE_INDEX, selectedYearID, "")

    }

    override fun onItemClick(t: Circular, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.action_circularFragment_to_circularDetailsFragment,
            Bundle().apply {
                putInt(Constant.CIRCULAR_ID, t.cirID)
            })
    }

    private fun popUpSelectAcademicYears() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text = "Select Academic Year"
        builder.setView(view)

        relOk.setOnClickListener {
            fragmentCircularBinding.tvSelectSession.text = selectedYearData!!.session
            circularViewModel.getCirculars(1, selectedYearID, "")
            builder.dismiss()

        }

        val yearAdapter = PopUpListAdapter(yearList, object : ItemListener<AcademicYear> {
            override fun onItemClick(t: AcademicYear, pos: Int, boolean: Boolean) {
                selectedYearData = t
                selectedYearID = t.yrID
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


}