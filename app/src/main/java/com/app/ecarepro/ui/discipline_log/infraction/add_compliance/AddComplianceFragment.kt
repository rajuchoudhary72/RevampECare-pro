package com.app.ecarepro.ui.discipline_log.infraction.add_compliance

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddComplianceBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.ShareViewModelDiscipline
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.text.lowercase
import kotlin.text.substringAfterLast


@AndroidEntryPoint
class AddComplianceFragment : Fragment() {

    private lateinit var binding: FragmentAddComplianceBinding
    private val addComplianceViewModel: AddComplianceViewModel by viewModels()
    private val sharedViewModel: ShareViewModelDiscipline by activityViewModels()
    private var lastClickAttachmentType: AttachmentType? = null
    private var infractionId= ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAddComplianceBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.add_compliance)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedViewModel.getRecentInfraction().observe(this.viewLifecycleOwner){ infraction ->
            if (infraction!=null){
                infractionId=infraction.id
                binding.apply {

                    binding.cvResolvedButton.isVisible=infraction.showResolvedButton

                    tvInfractionCategory.text=infraction.infraction
                    tvInfractionSubcategory.text=infraction.subInfraction
                    tvCorrectiveAction.text=infraction.correctiveAction
                    tvConsequences.text=infraction.consequences
                    tvInfractionOn.text=infraction.infractionOn
                    tvInstance.text=infraction.instance
                   if (infraction.isComplianceActive){



                       tvComplianceStaus.text="Active"
                       llComplineSection.visibility=View.VISIBLE
                       rlButtons.isVisible=!infraction.isResolved

                       if (infraction.isResolved){
                           llAttachment.visibility=View.GONE
                           tvAttTextHolder.visibility=View.GONE
                           etCompline.isEnabled=false
                       }

                           if (!infraction.complianceAttachment.isNullOrEmpty()){
                              llAttachedFile.isVisible=true

                               tvAttchmentName.text=getFilenameFromUrl(infraction.complianceAttachment)
                               llAttachedFile.setOnClickListener {
                                   try {
                                       val androidDownloader = AndroidDownloader(requireContext())
                                       if (isPdfUrlByExtension(infraction.complianceAttachment)){
                                           androidDownloader.downloadFile(infraction.complianceAttachment ,getString(R.string.compliance))
                                       }else{
                                           androidDownloader.downloadFile(infraction.complianceAttachment, "Photo", "image/jpeg")
                                       }
                                   }catch (e:SecurityException){
                                       e.printStackTrace()
                                   }
                               }
                           }

                       binding.tvComplianceUpdate.text= buildString {
        append("Compliance Modified by ")
        append(infraction.staffName)
        append(", ")
        append(infraction.designation)
        append(" Modified on ")
        append(infraction.compCreatedOn)
        append(".")
    }

                   }else{
                       tvComplianceStaus.text="Inactive"
                       llComplineSection.visibility=View.GONE
                   }

                }
            }
        }

        binding.cvAttchmentButton.setOnClickListener {
            showAttachmentDialog()
        }
        binding.ivRemoveAttachment.setOnClickListener {
            addComplianceViewModel.removeAttachment()
            binding.llPickedFile.isVisible=false
        }


        binding.cvSaveComplineButton.setOnClickListener {
            postCompliance()
        }
        binding.cvResolvedButton.setOnClickListener {
            resolvedCompliance()
        }

    }


    private fun showAttachmentDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_attachment_dialog, null)

        val tvBrowsePhoto = dialogView.findViewById<TextView>(R.id.tv_browse_photo)
        val tvBrowseFile = dialogView.findViewById<TextView>(R.id.tv_browse_file)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        tvBrowsePhoto.setOnClickListener {
            if (checkAndRequestPermissions()) {
                lastClickAttachmentType = AttachmentType.GALLERY
                launchPhotoPicker()
            }
            dialog.dismiss()
        }

        tvBrowseFile.setOnClickListener {
            lastClickAttachmentType = AttachmentType.PDF
            launchPdfPicker()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun postCompliance() {
        lifecycleScope.launch {
            addComplianceViewModel.postComplianceStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data!=null){
                        mainActivity().showMessage(it.data.message.toString())
                    }
                    findNavController().popBackStack()
                }  }
            } }
        addComplianceViewModel.postCompliance(binding.etCompline.toString(),infractionId)
    }

    private fun resolvedCompliance() {
        lifecycleScope.launch {
            addComplianceViewModel.resolvedComplianceStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data!=null){
                        mainActivity().showMessage(it.data.message.toString())
                    }
                    findNavController().popBackStack()
                }  }
            } }
        addComplianceViewModel.resolvedCompliance(infractionId)
    }

    private fun launchPdfPicker() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfLauncher.launch(intent)
    }

    private fun launchPhotoPicker() {

        val intent = Lassi(requireContext())
            .with(LassiOption.CAMERA_AND_GALLERY)
            .setMediaType(MediaType.IMAGE)
            .setMaxCount(7)
            .setGridSize(3)
            .setMinFileSize(0) // Restrict by minimum file size
            .setMaxFileSize(65535) // Restrict by maximum file size
            .setCompressionRatio(10) // compress image for single item selection (can be 0 to 100)
            .setAlertDialogNegativeButtonColor(R.color.black)
            .setAlertDialogPositiveButtonColor(R.color.md_theme_light_primary)
            .setStatusBarColor(R.color.md_theme_light_primary)
            .setToolbarColor(R.color.md_theme_light_primary)
            .setToolbarResourceColor(android.R.color.white)
            .setProgressBarColor(R.color.red)
            .setGalleryBackgroundColor(R.color.white)
            .build()
        receiveData.launch(intent)
    }

    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                binding.llPickedFile.isVisible=true

                addComplianceViewModel.setAttachments(selectedMedia)
            }
        }


    private fun getFilenameFromUrl(url: String): String {
        return url.substringAfterLast("/")
    }


    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        binding.llPickedFile.isVisible=true
                        addComplianceViewModel.setAttachments(
                            listOf(
                                MiMedia(
                                    path = mImageUri.toString(),
                                    name = lastClickAttachmentType?.name
                                )
                            )
                        )
                    } else {
                        if (data.clipData != null) {
                            val count: Int = data.clipData!!.itemCount
                            val files = mutableListOf<MiMedia>()
                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                                files.add(
                                    MiMedia(
                                        path = imageUri.toString(),
                                        name = lastClickAttachmentType?.name
                                    )
                                )
                            }
                            binding.llPickedFile.isVisible=true

                            addComplianceViewModel.setAttachments(files)
                        }
                    }
                }
            }
        }


    private fun checkAndRequestPermissions(): Boolean {
        val permissionList = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        return if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionList.toTypedArray(),
                1001
            )
            false
        } else {
            true
        }
    }

    fun isPdfUrlByExtension(url: String): Boolean {
        val extension = url.substringAfterLast(".").lowercase()
        return extension == "pdf"
    }


}