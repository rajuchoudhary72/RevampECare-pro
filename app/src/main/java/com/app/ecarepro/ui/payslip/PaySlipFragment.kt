package com.app.ecarepro.ui.payslip

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.Year
import com.app.ecarepro.databinding.FragmentPaySlipBinding
import com.app.ecarepro.model.MonthlyPaySlip
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaySlipFragment : Fragment() {


    private lateinit var downloadFileUrl: String
    private lateinit var monthSelectedData: MonthlyPaySlip
    private lateinit var monthData: List<MonthlyPaySlip>
    private lateinit var yearData: List<Year>
    private   var yearDataString:   ArrayList<String> =  ArrayList( )
    private   var monthDataString:   ArrayList<String> =  ArrayList( )
    private val paySlipViewModel : PaySlipViewModel by viewModels()
    private lateinit var binding : FragmentPaySlipBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=FragmentPaySlipBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.autoCompleteMonth.onItemClickListener= OnItemClickListener{parent,view,pos,id ->

            monthSelectedData =monthData[pos]
            downloadFileUrl=monthSelectedData.protectedFilePath

            if (monthSelectedData.filePath.isNotEmpty()){
                binding.wvPdf.loadUrl(Constant.WEBVIEW_PDF_BASE_URL+monthSelectedData.filePath)
            }

        }

        binding.autoCompleteYear.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->

                monthData= yearData[position].monthlyPaySlip

                if (monthData!=null) {
                    monthDataString.clear()
                    monthData.forEach { data ->
                        monthDataString.add(data.month.toString())
                    }

                    val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,monthDataString)
                    binding.autoCompleteMonth.setAdapter(arrayAdapter)
                }

            }


        lifecycleScope.launch {
            paySlipViewModel._payslipStateFlow.collectLatest {
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

                            if (it.data.years!=null) {
                                yearData=it.data.years



                                it.data.years.forEach { data ->
                                    yearDataString.add(data.year.toString())
                                }

                                val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,yearDataString)
                                binding.autoCompleteYear.setAdapter(arrayAdapter)




                            }


                        }


                    }


                }
            }
        }

        paySlipViewModel.getPayslip()

        binding.wvPdf.zoomIn()
         binding.wvPdf.settings .loadWithOverviewMode = true
        binding.wvPdf.settings.javaScriptEnabled = true
        binding.wvPdf.settings.supportZoom()
        binding.wvPdf.settings.builtInZoomControls=true
         binding.wvPdf.webViewClient= object  : WebViewClient(){



            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                (requireActivity() as MainActivity).showLoader(true)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                (requireActivity() as MainActivity).showLoader(false)
                super.onPageFinished(view, url)
            }
        }

        binding.fbDowload.setOnClickListener {
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(downloadFileUrl, getString(R.string.payslip))
            Toast.makeText(
                requireContext(),
                "Download started, check you status bar for more information.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}