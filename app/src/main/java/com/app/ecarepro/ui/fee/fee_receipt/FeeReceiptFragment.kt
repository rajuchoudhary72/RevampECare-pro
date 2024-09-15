package com.app.ecarepro.ui.fee.fee_receipt

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils.replace
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
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.databinding.FragmentFeeReceiptBinding
import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.FeeReceiptSession
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeeReceiptFragment : Fragment() , ItemListener <FeeReceipt> {


    private var isSessionSelected: Boolean = false
    private lateinit var sessionSelectData: FeeReceiptSession
    private var sessionListData = mutableListOf<FeeReceiptSession>()
    private lateinit var binding: FragmentFeeReceiptBinding
    private val feeReceiptViewModel: FeeReceiptViewModel by viewModels()
    private var firstTime=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeeReceiptBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSelectSession.setOnClickListener {
            popUpSessionList()
        }

        lifecycleScope.launch {
            feeReceiptViewModel.feeReceiptStateFlow.collectLatest {
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

                            if (firstTime){
                                sessionListData = it.data.session_data.toMutableList()
                                firstTime=false
                            }


                            if (  it.data.receipt_data.isNotEmpty()) {

                                binding.recyclerFeeReceipt.isVisible = true
                                binding.tvNoData.isVisible = false



                                val feeReportAdapter = FeeReportAdapter(
                                    it.data.receipt_data,
                                    this@FeeReceiptFragment
                                )

                                binding.recyclerFeeReceipt.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = feeReportAdapter
                                }


                            } else {
                                binding.recyclerFeeReceipt.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }
                    }

                    else -> {}
                }
            }
        }
        feeReceiptViewModel.getFeeReceipt(
            feeReceiptViewModel.feePaymentURL.replace("mlogin.aspx", "")+"/api/feereceipt",
             0
        )


    }

    private fun popUpSessionList() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)

        tvHeading.text = getString(R.string.select_session)
        builder.setView(view)


        relOk.setOnClickListener {

            if (isSessionSelected) {
                binding.tvSelectSession.text = sessionSelectData.yearname
                getFeeReceipt(sessionSelectData.yrid)
                builder.dismiss()
            }


        }

        val staffPopUpListAdapter =
            FeeReceiptPopUpAdapter(sessionListData, object : ItemListener<FeeReceiptSession> {
                override fun onItemClick(t: FeeReceiptSession, pos: Int, boolean: Boolean) {
                    isSessionSelected = true
                    sessionSelectData = t
                }
            })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = staffPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun getFeeReceipt( yearID : Int) {

        feeReceiptViewModel.getFeeReceipt(
            feeReceiptViewModel.feePaymentURL.replace("mlogin.aspx", "")+"/api/feereceipt",
            yearID
        )
    }

    override fun onItemClick(t: FeeReceipt, pos: Int, boolean: Boolean) {




        }



}