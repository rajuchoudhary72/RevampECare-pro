package com.app.ecarepro.ui.fee

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeePaymentBinding
import com.app.ecarepro.databinding.SmsRechargeLogItemBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fee_report.collection.CollectionFeeReportListAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FeePaymentFragment : Fragment() {

    private lateinit var binding: FragmentFeePaymentBinding
    private val feePaymentViewModel : FeePaymentViewModel by viewModels()
    @Inject
    lateinit var userDataStore: UserDataStore
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

     private fun setUpFeePayWebView(tokenKey: String) {


//         lifecycleScope.launch {
//             userDataStore.getSchoolData()?.run {
//                 binding.apply {
//                     (requireActivity() as MainActivity).showLoader(true)
//                     wvFeePayment.settings.javaScriptEnabled = true
//                     wvFeePayment.settings.setSupportZoom(true)
//                     wvFeePayment.webViewClient= object  : WebViewClient(){
//
//                         override fun shouldOverrideUrlLoading(
//                             view: WebView?,
//                             request: WebResourceRequest?
//                         ): Boolean {
//                             val uri = request!!.url
//                             if (uri.scheme == "upi") {
//                                 // Handle UPI URL
//                                 val intent = Intent(Intent.ACTION_VIEW, uri)
//                                 startActivity(intent)
//                                 return true // Indicate WebView to not load the URL
//                             }
//                             return super.shouldOverrideUrlLoading(view, request)
//                         }
//
//                         override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//
//                             super.onPageStarted(view, url, favicon)
//
//                         }
//
//                         override fun onPageFinished(view: WebView?, url: String?) {
//
//                             super.onPageFinished(view, url)
//                             (requireActivity() as MainActivity).showLoader(false)
//                         }
//                     }
//
//
//
//                     if (! feePayemtURL.isNullOrEmpty()){
//                         wvFeePayment.loadUrl("$feePayemtURL?token=$tokenKey")
//                      }else{
//                 Toast.makeText(requireContext(), "Payment Url not found", Toast.LENGTH_SHORT).show()
//             }
//
//                 }
//             }
//         }

         lifecycleScope.launch {
             userDataStore.getSchoolData()?.run {
                 val tabIntent = CustomTabsIntent.Builder()
                     .setToolbarColor( (requireActivity() as MainActivity).getColor(R.color.green)).build()
                 openCustomTab(tabIntent, Uri.parse(feePayemtURL + "?token=" + tokenKey))

             }}

           }

    fun openCustomTab(customTabsIntent: CustomTabsIntent, uri: Uri?) {
        val packageName = "com.android.chrome"
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl((requireActivity() as MainActivity), uri!!)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
             }
        }

}