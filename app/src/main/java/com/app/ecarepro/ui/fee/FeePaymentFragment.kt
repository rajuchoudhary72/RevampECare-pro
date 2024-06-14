package com.app.ecarepro.ui.fee

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeePaymentBinding
import com.app.ecarepro.databinding.SmsRechargeLogItemBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fee_report.collection.CollectionFeeReportListAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FeePaymentFragment : Fragment() {

    private lateinit var binding: FragmentFeePaymentBinding
    private val feePaymentViewModel : FeePaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentFeePaymentBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getGenToken()

    }


    private fun getGenToken(){
        lifecycleScope.launch {
            feePaymentViewModel.genTokenStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                     }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){

                            setUpFeePayWebView(it.data.tokenKey)

                        }

                    }

                    else -> {}
                }
            }
        }
        feePaymentViewModel.getGenerateToken(Constant.DEVICE_TYPE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpFeePayWebView(tokenKey: String) {

       Log.i("paymentUrl",feePaymentViewModel.feePayemtURL + "?token=" + tokenKey)

        binding.apply {

            wvFeePayment.settings.javaScriptEnabled = true
            wvFeePayment.settings.setSupportZoom(true)
            wvFeePayment.webViewClient= object  : WebViewClient(){
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    (requireActivity() as MainActivity).showLoader(true)
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    (requireActivity() as MainActivity).showLoader(false)
                    super.onPageFinished(view, url)
                }
            }


            wvFeePayment.loadUrl( feePaymentViewModel.feePayemtURL + "?token=" + tokenKey   )
        }




    }

}