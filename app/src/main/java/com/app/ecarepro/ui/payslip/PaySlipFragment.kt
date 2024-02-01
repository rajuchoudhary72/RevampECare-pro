package com.app.ecarepro.ui.payslip

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.Year
import com.app.ecarepro.databinding.FragmentPaySlipBinding
import com.app.ecarepro.model.MonthlyPaySlip
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.notice.CustomDropDownAdapter
import com.app.ecarepro.utils.AndroidDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaySlipFragment : Fragment() {


    private lateinit var downloadFileUrl: String
    private lateinit var monthSelectedData: MonthlyPaySlip
    private lateinit var monthData: List<MonthlyPaySlip>
    private lateinit var yearData: List<Year>
    private val paySlipViewModel : PaySlipViewModel by viewModels()
    private lateinit var binding : FragmentPaySlipBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=FragmentPaySlipBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {

                monthData= yearData[position].monthlyPaySlip

                if (monthData!=null) {
                    val spinnerAdapter = CustomDropDownAdapterMonth(requireContext(),monthData)
                    binding.spinnerMont.adapter = spinnerAdapter
                }


            }

        }

        binding.spinnerMont.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {

                monthSelectedData =monthData[position]
                downloadFileUrl=monthSelectedData.protectedFilePath

                if (monthSelectedData.filePath.isNotEmpty()){
                    binding.wvPdf.loadUrl("https://docs.google.com/gview?embedded=true&url="+monthSelectedData.filePath)
                }

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
                                 val spinnerAdapter = CustomDropDownAdapterYear(requireContext(), it.data.years)
                                binding.spinnerYear.adapter = spinnerAdapter
                            }


                        }


                    }

                    else -> {}
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
            androidDownloader.downloadFile(downloadFileUrl, "PaySlip")
        }

    }
}