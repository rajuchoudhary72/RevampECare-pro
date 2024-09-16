package com.app.ecarepro.ui.fee_certificate

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentFeeCertificateBinding
import com.app.ecarepro.model.FeeCertificateListItem
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class FeeCertificateFragment : Fragment() {


     private lateinit var binding: FragmentFeeCertificateBinding
    private val feeCertificateViewModel: FeeCertificateViewModel by viewModels()
    private lateinit var yearData: List<FeeCertificateListItem>
    private   var yearDataString:   ArrayList<String> =  ArrayList( )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentFeeCertificateBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.autoCompleteYear.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                getFeeCertificateDownload(yearData[position].yrid,yearData[position].yearname)
            }

        fetchFeeCer()
    }

    fun fetchFeeCer(){
        lifecycleScope.launch {
            feeCertificateViewModel.feeCertificateStateFlow.collectLatest {
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

                            if (it.data !=null) {
                                yearData=it.data



                                it.data. forEach { data ->
                                    yearDataString.add(data.yearname.toString())
                                }

                                val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,yearDataString)
                                binding.autoCompleteYear.setAdapter(arrayAdapter)




                            }


                        }


                    }


                }
            }
        }

        feeCertificateViewModel.getFeeCertificate()


    }

    fun getFeeCertificateDownload(sessionId : Int,sessionName: String ){
        lifecycleScope.launch {
            feeCertificateViewModel.feeCertificateDownloadStateFlow.collectLatest {
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

                            generatePDFFromBase64(it.data.bytedata,"Certificate "+Constant.currentDate())
                        }
  } }
            }
        }

        feeCertificateViewModel.getFeeCertificateDownload(sessionId,sessionName)

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

}