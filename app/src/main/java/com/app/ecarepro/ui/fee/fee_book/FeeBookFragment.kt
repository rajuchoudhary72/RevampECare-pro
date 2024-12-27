package com.app.ecarepro.ui.fee.fee_book

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.FeeBookModel
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeeBookBinding
import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.FeeReceiptSession
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fee.fee_receipt.FeeReceiptPopUpAdapter
import com.app.ecarepro.ui.fee.fee_receipt.FeeReportAdapter
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class FeeBookFragment : Fragment() , ItemListener <FeeBookModel> {


    private lateinit var binding: FragmentFeeBookBinding
    private val feeBookViewModel: FeeBookViewModel by viewModels()
    private var firstTime=true
    private val STORAGE_PERMISSION_REQUEST_CODE = 1001

    private var base64String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeeBookBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.fee_book)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            feeBookViewModel.feeBookStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("mainnnnnnnn", "ErrorRRRR$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

//                            if (firstTime){
//                                if (!it.data.session_data.isNullOrEmpty()){
//                                    sessionListData = it.data.session_data.toMutableList()
//                                    if (sessionListData.isNotEmpty()){
//                                        for (i in sessionListData){
//                                            if (i.active=="1"){
//                                                sessionSelectData=i
//                                            }
//                                        }
//
//                                        binding.tvSelectSession.text=sessionSelectData.yearname
//                                    }
//                                    firstTime=false
//                                }
//
//                            }


                            if (  !it.data.fee_data.isNullOrEmpty()) {

                                binding.recyclerFeeReceipt.isVisible = true
                                binding.tvNoData.isVisible = false



                                val feeReportAdapter = FeeBookAdapter(
                                    it.data.fee_data,
                                    this@FeeBookFragment
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

        feeBookViewModel.getFeeBookReportList()

    }



    override fun onItemClick(t: FeeBookModel, pos: Int, boolean: Boolean) {
         when(pos){
             1 ->{
                 getFeeCertificateDownload(t,1)
             }
             2 ->{
                 getFeeCertificateDownload(t,2)
             }
         }
         }


    private fun getFeeCertificateDownload(feeBookModel: FeeBookModel, i : Int){
        lifecycleScope.launch {
            feeBookViewModel.feeCertificateDownloadStateFlow.collectLatest {
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
                            base64String=it.data.bytedata
                            if (checkStoragePermission()) {
                                saveAndOpenPdf(it.data.bytedata,i,feeBookModel.installname )
                            } else {
                                requestStoragePermission()
                            }
                        }
                    } }
            }
        }

        feeBookViewModel.getFeeReceiptDownload(feeBookModel)

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



    private fun saveAndOpenPdf(base64String: String, i: Int, recdate: String?) {



        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)


           // val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FeeReceipt_"+"$recdate.pdf")
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "FeeBook$recdate.pdf"
            )
            try {

                val outputStream = FileOutputStream(file)
                outputStream.write(decodedBytes)
                outputStream.close()
                if (i==2){
                    Toast.makeText(requireContext(), "Fee Receipt saved to eCarePro Download", Toast.LENGTH_SHORT).show()
             }
             } catch (e: java.lang.Exception) {
                e.printStackTrace()
                 Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show()
            }


if (i==1){
    val intent = Intent(Intent.ACTION_VIEW)
    val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".myFileProvider", file)
    intent.setDataAndType(uri, "application/pdf")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(intent)
}

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exceptions appropriately (e.g., show an error message)
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 14 and above
            val imagePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            val videoPermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
            imagePermission && videoPermission
        } else {
            // Android 13 and below
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 14 (API level 33) and above
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            // For Android 13 (API level 32) and below
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with saving and opening the PDF
              //  saveAndOpenPdf(base64String, "FeeReceipt", i, recdate) // Assuming you have the Base64 string available
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
