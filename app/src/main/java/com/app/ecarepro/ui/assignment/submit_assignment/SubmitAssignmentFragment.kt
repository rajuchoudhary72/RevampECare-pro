package com.app.ecarepro.ui.assignment.submit_assignment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentSubmitAssignmentBinding
import com.app.ecarepro.model.AssignSubmitStudent
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.viewAssignment.PopUpFileListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import android.content.pm.PackageManager
import androidx.core.os.bundleOf
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment


@AndroidEntryPoint
class SubmitAssignmentFragment : Fragment() {

    private lateinit var assignmentDetails: Assignment
    private lateinit var binding : FragmentSubmitAssignmentBinding
    private val submitAssignmentViewModel : SubmitAssignmentViewModel  by viewModels( )
    private   var imageExt: String= ""
    private   var imageString: String=""
    private var studentSubmission= mutableListOf<AssignSubmitStudent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentSubmitAssignmentBinding.inflate(inflater,container,false)
        arguments?.getParcelable<Assignment>(Constant.ASSIGNMENT_ID).let { data ->
            assignmentDetails= data!!
            binding.assData=assignmentDetails

        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llView.isVisible= assignmentDetails.hasAttachment!!
        binding.llView.setOnClickListener {
            if (assignmentDetails!=null){
            if (assignmentDetails .asgFileNames!=null){
                popUpFileList(assignmentDetails .asgFileNames!!)
            }
            }

        }
        binding.llSubmitReport.setOnClickListener {
            popUpSubmitList()
        }
        binding.postAnswer.setOnClickListener {
            if (assignmentDetails.isSubmissionOpened==true){
                submitAssignment()
            }else{
                if (assignmentDetails.lateSubmission ){
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are you sure ?")
                    builder.setMessage("The submission deadline for this assignment has passed. You may still submit your assignment, but it will be marked as a late submission")

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        submitAssignment()
                    }

                    builder.setNegativeButton(android.R.string.no) { dialog, which ->

                    }

                    builder.show()
                }else{
                    mainActivity().showMessage("The submission deadline for this assignment has passed. ")

                }}


        }




        binding.etAnswer.doAfterTextChanged {
            if (it != null) {
                if (it.isNotEmpty()){
                    binding.postAnswer.isEnabled = true
                    binding.postAnswer.setImageResource(R.drawable.send_icon_enable)

                    

                }else{
                    binding.postAnswer.isEnabled = false
                    binding.postAnswer.setImageResource(R.drawable.send_icon_light)
                }

            }
        }




        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        binding.tvBrowsePhoto.setOnClickListener {
            selectImageOptionDialog()
        }

        binding.tvBrowseFile.setOnClickListener {
            pdfLauncher.launch(FileAccess.pickPdfFileIntent())
         }

        viewAssignmentDetails()

    }

    private fun viewAssignmentDetails() {
        submitAssignmentViewModel.viewAssignment(assignmentDetails.id.toString())

        lifecycleScope.launch {
            submitAssignmentViewModel.viewAssignmentStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    if (it.data !=null){
                        if(it.data.studentSubmission !=null){
                            studentSubmission= it.data.studentSubmission as MutableList<AssignSubmitStudent>
                        }
                        binding.llSubmitReport.isVisible=studentSubmission.isNotEmpty()
                        binding.llPostAnswer.isVisible=it.data.isSubmissionOpened
                    }
               }  }
            } }

    }

    private fun submitAssignment(){
        if (binding.etAnswer.text.isNotEmpty()){
            lifecycleScope.launch {
                submitAssignmentViewModel.submitAssignment(
                    id= assignmentDetails.id.toString(),
                    asgID = assignmentDetails.asgID!!,
                    data = binding.etAnswer.text.toString(),
                    attachment =  imageString,
                    fileName = "",
                    fileURL = "",
                    fileExt = imageExt

                ).invokeOnCompletion {
                    mainActivity().showMessage("Submitted Successfully!!!  " )
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun selectImageOptionDialog() {
        try {
            val items = arrayOf<CharSequence>(
                getString(R.string.take_photo),
                getString(R.string.choose_library),
                getString(R.string.cancel)

            )
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.add_photo))
            builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
                FileAccess.checkPermission(this@SubmitAssignmentFragment)
                try {
                    if (items[item] == getString(R.string.take_photo)) {
                        cameraLauncher.launch(FileAccess.cameraIntent())
                    } else if (items[item] == getString(R.string.choose_library)) {
                        galleryLauncher.launch(FileAccess.galleryIntent())
                    } else if (items[item] == getString(R.string.cancel)) {
                        dialog.dismiss()
                    }
                }catch (e:SecurityException){
                    e.message
                }

            })
            builder.show()
        }catch (e:SecurityException){
            e.printStackTrace()
        }

    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.llFile.isVisible=true


                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

            }
        }


    private val pdfLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.llFile.isVisible=true
                imageString = FileAccess.convertPdfToBase64(imgUri!!,requireContext())

                imageExt = "pdf"

            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.llFile.isVisible=true


                    imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                    imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()


                }
            }
        }

    private fun popUpFileList(filelist: List<String>) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.popup_file_list,null)
        val  rvDetails = view.findViewById<RecyclerView>(R.id.rvDetails)
        val  ivCross = view.findViewById<ImageView>(R.id.ivCross)
        val  tvHeading = view.findViewById<TextView>(R.id.tvHeading)
        tvHeading.text="View File"

        builder.setView(view)


        val popUpFileListAdapter= PopUpFileListAdapter(filelist ){ t, pos ->
            builder.dismiss()
            when(pos){
                1 -> {
                    openFile(t)
                }
                2 -> {
                    downloadFile(t)
                }
            }}
        rvDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpFileListAdapter
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun popUpSubmitList( ) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.popup_file_list,null)
        val  rvDetails = view.findViewById<RecyclerView>(R.id.rvDetails)
        val  ivCross = view.findViewById<ImageView>(R.id.ivCross)
        val  tvHeading = view.findViewById<TextView>(R.id.tvHeading)
        tvHeading.text=" Submitted Report"

        builder.setView(view)


        val submitReportAdapter= SubmitReportAdapter(studentSubmission ){ t, pos ->
            builder.dismiss()
            when(pos){
                1 -> {
                    openFile(t.asgFile)
                }
                2 -> {
                    downloadFile(t.asgFile)
                }
            }}
        rvDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = submitReportAdapter
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun isPdfViewerAvailable(context: Context): Boolean {
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(Uri.parse("file:///fakepath/sample.pdf"), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        val packageManager: PackageManager = context.packageManager
        val resolvedActivities = packageManager.queryIntentActivities(pdfIntent, PackageManager.MATCH_DEFAULT_ONLY)

        // Return true if there is at least one app that can handle PDF
        return resolvedActivities.isNotEmpty()
    }

    private fun openFile(fileSource: String) {
        when (Constant.isPdfUrl(fileSource)){
            1 -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(fileSource), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                // Check if there's an app that can handle PDFs
                val chooser = Intent.createChooser(intent, "Open PDF")
                requireContext().startActivity(chooser)
                if (isPdfViewerAvailable(requireContext())){
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse(fileSource), "application/pdf")
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                        // Check if there's an app that can handle PDFs
                        val chooser = Intent.createChooser(intent, "Open PDF")
                        requireContext().startActivity(chooser)
                    } catch (e: Exception) {
                        // Handle the exception (if no app is available to open PDFs)
                        findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                            putString(Constant.URL_ARGUMENT, fileSource)
                        })
                    }
                }else{
                    findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                        putString(Constant.URL_ARGUMENT, fileSource)
                    })
                }



            }
            2 -> {
                findNavController().navigate(
                    R.id.photoViewFragmentFragment,
                    bundleOf(PhotoViewFragmentFragment.PHOTO to fileSource)
                )
            }
            3 -> {

                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                    putString(Constant.URL_ARGUMENT, fileSource)
                })
             }else -> {
            findNavController().navigate(
                R.id.photoViewFragmentFragment,
                bundleOf(PhotoViewFragmentFragment.PHOTO to fileSource)
            )
        }
        }



    }

    private fun downloadFile(fileSource: String) {
        when (Constant.isPdfUrl(fileSource)) {
            1 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, getString(R.string.assessment))
            }
            2 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
            }
            3 -> {

                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, getString(R.string.assessment),"application/vnd.openxmlformats-officedocument.wordprocessingml.document")

//                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
//                    putString(Constant.URL_ARGUMENT, fileSource)
//                })
            }
            else -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
            }
        } }




}