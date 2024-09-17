package com.app.ecarepro.ui.fee.fee_receipt

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeeReceiptBinding
import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.FeeReceiptSession
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                                if (!it.data.session_data.isNullOrEmpty()){
                                    sessionListData = it.data.session_data.toMutableList()
                                    if (sessionListData.isNotEmpty()){
                                        sessionSelectData=it.data.session_data[sessionListData.size-1]
                                        binding.tvSelectSession.text=sessionSelectData.yearname
                                    }
                                    firstTime=false
                                }

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
         when(pos){
             1 ->{
                 getFeeCertificateDownload(t.recid.toString(),1)
             }
             2 ->{
                 getFeeCertificateDownload(t.recid.toString(),2)
             }
         }
         }


    fun getFeeCertificateDownload(recid: String, i: Int){
        lifecycleScope.launch {
            feeReceiptViewModel.feeCertificateDownloadStateFlow.collectLatest {
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
                            /*try {
                                val file: File? =  FileAccess.writeResponseBodyToDisk(
                                    it.data.bytedata,
                                    Constant.currentDate()
                                )

                                if (null != file) {


                                        val pdfUri =
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                FileProvider.getUriForFile(
                                                    requireContext(),
                                                    "com.franciscan.ecare_pro.provider",
                                                    file
                                                )
                                            } else {
                                                Uri.fromFile(file)
                                            }
                                        val pdfIntent = Intent(Intent.ACTION_VIEW)
                                        pdfIntent.setDataAndType(pdfUri, "application/pdf")
                                        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                        try {
                                            startActivity(pdfIntent)
                                        } catch (e: ActivityNotFoundException) {
                                            Toast.makeText(
                                                context,
                                                "No Application available to view PDF",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                }

                            } catch (e: Exception) {
                                // Handle exception
                            }*/

                            generatePDFFromBase64(it.data.bytedata,"FeeReceipt" )
                        }
                    } }
            }
        }

        feeReceiptViewModel.getFeeReceiptDownload(recid,sessionSelectData.yrid)

    }
    fun generatePDFFromBase64(base64: String, fileName: String) {
        try {
            val decodedBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            val fos = FileOutputStream(getFilePath(fileName))
            fos.write(decodedBytes)
            fos.flush()
            fos.close()

            openDownloadedPDF(fileName)
        } catch (e: IOException) {
            Log.e("TAG", "Faild to generate pdf from base64: ${e.localizedMessage}")
        }
    }

    private fun openDownloadedPDF(fileName: String) {
        val file = File(getFilePath(fileName))

        if (file.exists()) {
            val fileProviderAuthority = "com.franciscan.ecare_pro.myFileProvider"
            val path: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(requireContext(), fileProviderAuthority, file)
            } else {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            val chooserIntent = Intent.createChooser(intent, "Open with")
            try {
                startActivity(chooserIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e("TAG", "Failed to open PDF  ${e.localizedMessage}")
            }
        }
    }

    fun getFilePath(filename: String): String {
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath.toString() + "/" + filename + ".pdf"
    }


    private fun openPdfFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".myFileProvider",file
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
}