package com.app.ecarepro.ui.syllabus.teacher.add_syllabus

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.ClassSection
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkPushNotificationRequest
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.create_syllabus.BrowsedFile
import com.app.ecarepro.databinding.FragmentAddSyllabusBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.postAssignment.ClassListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.ui.syllabus.teacher.TeacherSyllabusViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.getFile
import com.app.ecarepro.utils.listener.ItemListener
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AddSyllabusFragment : Fragment() {

    private var isGallery: Boolean=false
    private  var imageExt: String =""
    private  var imageString: String =""
    private lateinit var binding: FragmentAddSyllabusBinding
    private val addSyllabusViewModel: AddSyllabusViewModel by viewModels()
    private var classesList = mutableListOf<MyClasseItem>()
    private  var sectionList= mutableListOf<ClassSection>()
    private var imageUri: Uri? = null
    private var subjectList = mutableListOf<MySubject>()
    private lateinit var classData: MyClasseItem
    private lateinit var subjectData: MySubject
    private var isClassSelected: Boolean = false
    private var isSectionSelected: Boolean = false
    private var isSubjectSelected: Boolean = false
    private var lastClickAttachmentType: AttachmentType? = null
    private var edit= false
    private var id= ""
    private var classID= 0
    private var classSTD= ""
    private var subID= 0
    private var subject= ""
    private var sections= ""
    private var title= ""
    private var fileName= ""
    var selectAll: Boolean = false
    private var sectionIDs = StringBuilder()
    private var sylabussType=Constant.CLASS_WISE
    private val teacherSyllabusViewModel: TeacherSyllabusViewModel by activityViewModels()
    private var isFileAttached =false




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSyllabusBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.add_syllabus)
        try {
            edit=  requireArguments().getBoolean("edit", false)
            id=  requireArguments().getString(Constant.ID,"")
            classID=  requireArguments().getInt("classID",0)
            classSTD=  requireArguments().getString("classSTD","")
            sections=  requireArguments().getString("sections","")
            subID=  requireArguments().getInt("subID",0)
            subject=  requireArguments().getString("subject","")
            title=  requireArguments().getString("title","")
            fileName=  requireArguments().getString("fileName","")

        }catch (_:Exception){ }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (edit){
            binding.etDescription.setText(title)
            binding.tvSelectClass.text=classSTD
            binding.tvSelectSubject.text=subject
            if (sections.isNotEmpty()){
                binding.rbSectionWise.isChecked=true
                binding.tvSelectSection.text=sections
                binding.tvSelectSection.isVisible=true
                sylabussType=Constant.SECTION_WISE
            }
            if (fileName.isNotEmpty()){
                binding.llFile.isVisible=true
                binding.llAttachemntFile.isVisible=false
                binding.ivFileRemove.isVisible=false
            }
            isSubjectSelected=true
            isClassSelected=true

        }


        binding.ivFileRemove.setOnClickListener {
            binding.llFile.isVisible=false
            binding.llAttachemntFile.isVisible=true
            isFileAttached=false
            addSyllabusViewModel.removeAttachment()
        }

        getMyClasses()

        binding.tvSelectClass.setOnClickListener {
            popUpSelectClass()
        }
        binding.tvSelectSubject.setOnClickListener {
             if (isClassSelected){
                 popUpSelectSubject()
             }
        }
        binding.tvSelectSection.setOnClickListener {
            if (isClassSelected){
                if (!sectionList.isNullOrEmpty()){
                    popUpSelectSection()
                }
            }else{
                mainActivity().showMessage("Select Class")
            }
        }

        binding.tvBrowsePhoto.setOnClickListener {
            if (checkAndRequestPermissions()) {
                lastClickAttachmentType = AttachmentType.GALLERY
                selectImageOptionDialog()
            }
        }

        binding.tvBrowseFile.setOnClickListener {
            lastClickAttachmentType = AttachmentType.PDF
            launchPdfPicker()
        }



        binding.btnSubmit.setOnClickListener {
            var isValidated=true
            if (!isClassSelected){
                isValidated=false
                mainActivity().showMessage("Select Class")
            }else
            if (!isSubjectSelected){
                isValidated=false
                mainActivity().showMessage("Select Subject")
            } else
            if (binding.etDescription.text.toString().isEmpty()){
                isValidated=false
                binding.etDescription.error="Enter Title"
            } else if (!edit){
                if (!isFileAttached){
                    isValidated=false
                    mainActivity().showMessage("Select File")
                }
            }
            if (sylabussType==Constant.SECTION_WISE) {
                if (!isSectionSelected) {
                    isValidated = false
                    mainActivity().showMessage("Select Section")
                }
            }

            if (isValidated){
                mainActivity().showLoader(true)
                addSyllabusViewModel.saveSyllabus(
                    classID,
                    sectionIDs.toString().ifEmpty { null },
                    id,
                    subID,
                    binding.etDescription.text.toString(),
                    if (isFileAttached) fileName else null,
                    BrowsedFile(
                        attachment = imageString,
                        fileExt = imageExt,
                    ),
                    isGallery
                ).invokeOnCompletion {
                    mainActivity().showLoader(false)
                    mainActivity().showMessage("Submitted Successfully!!!")
                    popUpSendNotification()
                }

            }
         }
        binding.rbGroupSyllabusType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbClassWise -> {
                    binding.tvSelectSection.visibility = View.GONE
                    sylabussType=Constant.CLASS_WISE
                }
                R.id.rb_section_wise -> {
                    binding.tvSelectSection.visibility = View.VISIBLE
                    sylabussType=Constant.SECTION_WISE
                }
            }
        }


    }



    private fun getMyClasses() {
        lifecycleScope.launch {
            addSyllabusViewModel.myClassStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.myClasses != null) {
                                classesList = it.data.myClasses as MutableList<MyClasseItem>
                            }
                        }


                    }
                }
            }
        }
        addSyllabusViewModel.getMyClass(0 )
    }

    private fun getClassSection(classID: Int) {
        lifecycleScope.launch {
            addSyllabusViewModel.classSectionStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.sections != null) {
                                sectionList = it.data.sections.toMutableList()
                            }
                        }


                    }
                }
            }
        }
        addSyllabusViewModel.getClassSection(classID )
    }


    private fun staffSubjects(classSTD: Int) {
        lifecycleScope.launch {
            addSyllabusViewModel.subjectsStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.mySubjects != null) {
                                subjectList = it.data.mySubjects as MutableList<MySubject>
                            }
                        }


                    }
                }
            }
        }
        addSyllabusViewModel.staffSubjects(classSTD)
    }


    private fun popUpSelectClass() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        val llOkCancel = view.findViewById<LinearLayout>(R.id.llOkCancel)
        llOkCancel.isVisible = false
        tvHeading.text = getText(R.string.lbl_select_class)
        builder.setView(view)

        val subjectListAdapter = ClassListAdapter(classesList,false, false, object : ItemListener<MyClasseItem> {
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {

                classData = t
                classID= classData.classID!!
                isClassSelected = true

                if (isClassSelected) {
                    binding.tvSelectClass.text = classData.className
                    staffSubjects(classData.classID!!)
                    getClassSection(classData.classID!!)
                    builder.dismiss()

                }
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun popUpSelectSubject() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        val tvSelectAll = view.findViewById<TextView>(R.id.tv_select_all_two)
        val llOkCancel = view.findViewById<LinearLayout>(R.id.llOkCancel)
        llOkCancel.isVisible = false
        tvSelectAll.isVisible = true
        tvHeading.text = getText(R.string.select_subject)
        builder.setView(view)


        tvSelectAll.setOnClickListener {

            binding.tvSelectSubject.text = "All"
            isSubjectSelected=true
            subID=0

            builder.dismiss()
        }


        val subjectListAdapter = SubjectListAdapter(subjectList, object : ItemListener<MySubject> {
            override fun onItemClick(t: MySubject, pos: Int, boolean: Boolean) {

                subjectData = t
                subID= subjectData.subID
                isSubjectSelected = true
                if (isSubjectSelected) {
                    binding.tvSelectSubject.text = subjectData.subjectName
                    builder.dismiss()

                }


            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }




    private fun popUpSelectSection(){

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text= getText(R.string.lbl_select_class)
        val  llSelectAll = view.findViewById<LinearLayout>(R.id.llSelectAll)
        val  checkImage = view.findViewById<ImageView>(R.id.checkImage)
        llSelectAll.isVisible=true
        builder.setView(view)

        relOk.setOnClickListener {
            if (isSectionSelected){


                val name = StringBuilder()
                sectionIDs.clear()

                for (item in sectionList) {
                    if (item.isSelected) {
                        if (sectionIDs.toString().isEmpty()) {
                            sectionIDs.append(item.classID)
                            name.append(item.secName)
                        } else {
                            sectionIDs.append(",").append(item.classID)
                            name.append(",").append(item.secName)
                        }
                    }
                }

                binding.tvSelectSection.text= name

                builder.dismiss()
            }

        }

        val subjectListAdapter= SectionListAdapter(sectionList, selectAll, true, object : ItemListener<ClassSection> {
            override fun onItemClick(t: ClassSection, pos: Int, boolean: Boolean) {

                isSectionSelected = true
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        llSelectAll.setOnClickListener {
            isSectionSelected=true
            selectAll = !selectAll
            for (i in sectionList) {
                i .isSelected=selectAll
            }
            subjectListAdapter.notifyDataSetChanged()
            checkImage.setImageResource(if (selectAll) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
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


    private fun popUpSendNotification() {
        val btn_canel: Button
        val btn_submit: Button
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.setContentView(R.layout.pop_up_alert_send_push_notifaction)
        btn_canel = dialog.findViewById<Button>(R.id.btn_canel)
        btn_submit = dialog.findViewById(R.id.btn_submit)

        btn_canel.setOnClickListener {
            teacherSyllabusViewModel.isDataLoaded=false

            dialog.dismiss()
            findNavController().popBackStack()
        }

        btn_submit.setOnClickListener {
            mainActivity().showLoader(true)
            val sub = binding.tvSelectSubject.text.toString()
            val classname = binding.tvSelectClass.text.toString()
            if (sylabussType==Constant.CLASS_WISE) {
                addSyllabusViewModel.sendPushNotification(
                    NetworkPushNotificationRequest(
                        ClassSTD=classID,
                        chMenuID=0,
                        classIDs=  null,
                        menuID=5,
                        message= "Syllabus has been posted of $sub in Grade $classname",
                        recipientType=2,
                        title= "Syllabus",
                    )
                ) {  isSuccess, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    if (isSuccess){
                        teacherSyllabusViewModel.isDataLoaded=false
                        mainActivity().showLoader(false)
                        mainActivity().showMessage("Notification Sent Successfully")
                        findNavController().popBackStack()
                    }else{
                        mainActivity().showLoader(false)
                        mainActivity().showMessage(message)
                    }
                }
            }else{
                val classname = binding.tvSelectClass.text.toString()
                 val classSectionName = StringBuilder()
                for (item in sectionList) {
                    if (item.isSelected) {
                        if (sectionIDs.toString().isEmpty()) {
                            classSectionName.append(classname).append(item.secName)
                        } else {
                            classSectionName.append(",").append(classname).append(item.secName)
                        }
                    }
                }

                addSyllabusViewModel.sendPushNotification(
                    NetworkPushNotificationRequest(
                        ClassSTD=null,
                        chMenuID=0,
                        classIDs=  sectionIDs.toString().ifEmpty { null },
                        menuID=5,
                        message= "Syllabus has been posted of $sub in Grade $classSectionName",
                        recipientType=2,
                        title= "Syllabus",
                    )
                ) {  isSuccess, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    if (isSuccess){
                        mainActivity().showLoader(false)
                        mainActivity().showMessage("Notification Sent Successfully")
                        findNavController().popBackStack()
                    }else{
                        mainActivity().showLoader(false)
                        mainActivity().showMessage(message)
                    }
                }
            }

            dialog.dismiss()
        }
        dialog.show()
    }


    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                // binding.ivAddedImage.setImageURI(imgUri)
                try {
                    val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                     imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                     imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                    /*val imageExt =
                        FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()*/
                    isFileAttached=true
                    isGallery=true
                    binding.llFile.isVisible=true


                } catch (e: NullPointerException) {
                    e.message
                }


            }
        }

    fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
        return when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.PNG -> "png"
            Bitmap.CompressFormat.WEBP -> "webp"
            else -> "unknown"
        }
    }



    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            FileAccess.checkPermission(this)
            if (items[item] == "Take Photo") {
                if (isCameraPermissionGranted(requireContext())) {
                    launchCamera()
                } else {
                    mainActivity().showMessage("Please allow camera permission, go to settings and enable.")
                }
            } else if (items[item] == "Choose from Library") {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun launchCamera() {
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.myFileProvider",
            imageFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraLauncher.launch(intent)
    }

    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && imageUri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)

                    isFileAttached = true
                    isGallery = true
                    binding.llFile.isVisible = true
                    // binding.ivAddedImage.setImageBitmap(bitmap) // Optional: show preview

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    private fun launchPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow any file type
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pdfLauncher.launch(intent)
    }




    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        addSyllabusViewModel.setAttachments(
                            listOf(
                                MiMedia(
                                    path = mImageUri.toString(),
                                    name = lastClickAttachmentType?.name
                                )
                            )
                        )
                        isFileAttached=true
                        binding.llFile.isVisible=true
                        isGallery=false
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
                            isFileAttached=true
                            binding.llFile.isVisible=true
                            addSyllabusViewModel.setAttachments(files)
                            isGallery=false
                        }
                    }
                }
            }
        }


    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    }


