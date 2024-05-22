package com.app.ecarepro.ui.appuserreport

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.ReportPreviewWebBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.statical.StaticalReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class AppUserReportWebFragment : Fragment() {
    private val viewModel: StaticalReportViewModel by viewModels()
    private lateinit var binding: ReportPreviewWebBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.report_preview_web, container, false)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.fab.setOnClickListener {
            printPDF()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            viewModel.appUserWebResponseStateFlow.collectLatest {
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
binding.fab.show()
                            // webView.loadData(response.body().getHtmlData(), "text/html", "UTF-8");
                            binding.webView.loadDataWithBaseURL(
                                ":http://pro.franciscan.in",
                                it.data.htmlData,
                                "text/html",
                                "UTF-8",
                                ""
                            )

                        }
                    }


                }
            }
        }
        arguments?.let {
            viewModel.appUserReportWebResponse(it.getString("userType", "0"))
        }

    }

    private fun createWebPrintJob(webView: WebView) {

        //create object of print manager in your device
        var printManager: PrintManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printManager =
                requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager?
            //create object of print adapter
            val printAdapter = webView.createPrintDocumentAdapter()

            //provide name to your newly generated pdf file
            val jobName = getString(R.string.app_name) + " Print Report"

            //open print dialog
            printManager!!.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }

    private fun printPDF() {
        createWebPrintJob(binding.webView)
    }


}