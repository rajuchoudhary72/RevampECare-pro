package com.app.ecarepro.ui.assignment.staff.postAssignment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.post_question.Attachment
import com.app.ecarepro.databinding.FragmentPostAssignmentBinding
import com.app.ecarepro.model.ClassID_StID
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.listener.ItemListener
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class PostAssignmentFragment : Fragment() {

    private var isClassWise: Boolean=true
    private var viewAssignmentData: NetworkViewAssignment? = null
     private var isClassSelected: Boolean = false
    private var isStudentSelected: Boolean = false
    private lateinit var classesList: List<MyClasseItem>
    private lateinit var subjectData: MySubject
    private var isSubjectSelected: Boolean = false
    private lateinit var subjectList: List<MySubject>
    private lateinit var binding : FragmentPostAssignmentBinding
    private val postAssignmentViewModel : PostAssignmentViewModel by viewModels()
    private   var imageExt: String= ""
    private   var imageString: String=""
    var selectAll: Boolean = false
    var selectAllStudent: Boolean = false
    var ids = StringBuilder()
    var studentIds = StringBuilder()
    var attachmentsList = mutableListOf<Attachment>()
    var classID_StID = mutableListOf<ClassID_StID>()
    var isEdit: Boolean = false
    private var assignmentId: String  = ""
    private var studentList = mutableListOf<Student>()
    private var lastClickAttachmentType: AttachmentType? = null
    var submitDate=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
       binding = FragmentPostAssignmentBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.post_assignment)
       try {
           assignmentId = requireArguments().getString(Constant.ASSIGNMENT_ID).toString()
           isEdit = requireArguments().getBoolean(Constant.EDIT.toString())
       }catch (e:Exception){}
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.isSubmitDate.setOnCheckedChangeListener { _, isChecked ->
            binding.tvSubmissionDt.isVisible=isChecked
        }

        binding.radioGroupWisesubmission.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbClassWise -> {
                     binding.tvSelectstudent.isVisible=false
                    isClassWise=true

                }
                R.id.rbStudentWise -> {
                    binding.tvSelectstudent.isVisible=true
                    isClassWise=false
                }
            }
        }


        lifecycleScope.launch {
            postAssignmentViewModel.subjectsStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data!=null){
                        if (it.data.mySubjects!=null){
                             subjectList=it.data.mySubjects
                        }
                    }


                }  }
            } }
        postAssignmentViewModel.mySubjects(Constant.DEFAULT_ID)

        binding.tvSelectSubject.setOnClickListener { popUpSelectSub() }
        binding.tvSelectClass.setOnClickListener {
             if (isSubjectSelected){
                 popUpSelectClass()
             }else{
                 mainActivity().showMessage("Select Subject")
             }
        }

        binding.btnSubmit.setOnClickListener { uploadAssignment() }

        binding.tvBrowseFile.setOnClickListener {
            lastClickAttachmentType = AttachmentType.PDF
            launchPdfPicker()
        }
        binding.tvBrowsePhoto.setOnClickListener {
            if (checkAndRequestPermissions()) {
                lastClickAttachmentType = AttachmentType.GALLERY
               launchPhotoPicker()
            }
        }

        binding.tvSelectstudent.setOnClickListener {
            if (studentList!=null){
                popUpStudentListByClass(studentList)
            }
             }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

            postAssignmentViewModel.removeAttachment()

        }

        binding.ctvAssignmentDt.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvAssignmentDt.text = Constant.dateToShow(date.toString())
                }

            })
        }

        binding.tvSubmissionDt.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvSubmissionDt.text = Constant.dateToShow(date.toString())
                }

            })
        }

        if (isEdit){
            setUpViewAssignment()
        }


    }

    private fun launchPdfPicker() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfLauncher.launch(intent)
    }


    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        binding.llFile.isVisible=true
                        postAssignmentViewModel.setAttachments(
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
                            binding.llFile.isVisible=true
                            postAssignmentViewModel.setAttachments(files)
                        }
                    }
                }
            }
        }

    private fun uploadAssignment() {

        var isValidate= true

        if (!isClassSelected){
            isValidate=false
            mainActivity().showMessage("Select Class")


        }
        if (!isSubjectSelected){
            isValidate=false
            mainActivity().showMessage("Select Subject")
        }
        if ( binding.etTitle.text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage("Enter Title")
        }
        if ( binding.ctvAssignmentDt.text.toString()==getString(R.string.assignment_date)){
            isValidate=false
            mainActivity().showMessage("Select Assignment Date")
        }
        if (binding.isSubmitDate.isChecked){
            if ( binding.tvSubmissionDt.text.toString()==getString(R.string.submission_date)){
                isValidate=false
                mainActivity().showMessage("Select Submission Date")
            }
        }
        if ( binding.etDescription.text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage("Enter Data")
        }




       if (isValidate ){


               submitDate = if (binding.isSubmitDate.isChecked){
                   Constant.toSystemDate( binding.tvSubmissionDt.text.toString())
               }else{
                   getCurrentYearLastDate()
               }

                postAssignmentViewModel.createAssignment(
                   asgDate =  if (isEdit) binding.ctvAssignmentDt.text.toString() else Constant.toSystemDate(binding.ctvAssignmentDt.text.toString()),
                   asgID =  if (isEdit) viewAssignmentData!!.asgID else 0 ,
                   classID = if (isEdit) ids.toString().toInt()   else 0,
                   classIDs =  if (isEdit)  "" else if (isClassWise)  ids.toString()   else "" ,
                   data =  binding.etDescription.text.toString() ,
                   file = "",
                   id =if (isEdit) viewAssignmentData!!.id else  "" ,
                   isActive =binding.cbActive.isChecked,
                   isFileRemoved =false,
                   multipleSubmission = binding.cbMultipleActive.isChecked,
                   subjectID = if (isEdit) viewAssignmentData!!.subjectID else subjectData.subID,
                   submitDate =submitDate,
                   title = binding.etTitle.text.toString(),
                   lateSubmission = binding.cbLateSubmission.isChecked ,
                   attachments =attachmentsList,
                   classID_StID = classID_StID,
                   stIDs =   null
               )


           lifecycleScope.launch {
               postAssignmentViewModel.createAssignmentStateFlow.collectLatest {
                   when (it) {  is NetworkResult.Loading -> {
                       (requireActivity() as MainActivity).showLoader(true)
                   }  is NetworkResult.Error -> {
                       (requireActivity() as MainActivity).showLoader(false)
                   } is NetworkResult.Success -> {
                       (requireActivity() as MainActivity).showLoader(false)
                       mainActivity().showMessage("Assignment Uploaded Successfully")
                        findNavController().popBackStack()
                   }  }
               } }
       }


    }

    private fun getCurrentYearLastDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun getMyClasses(subID: Int){
        lifecycleScope.launch {
            postAssignmentViewModel.myClassStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data!=null){
                        if (it.data.myClasses!=null){
                            classesList=it.data.myClasses

                        }
                    }


                }  }
            } }
        postAssignmentViewModel.getMyClass(subID,Constant.MY_CLASS_ID)
    }

    private fun popUpSelectSub(){

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text= getText(R.string.select_subject)
        builder.setView(view)

        relOk.setOnClickListener {

                 try {
                     isSubjectSelected = true
                     binding.tvSelectSubject.text= subjectData .subjectName

                     getMyClasses(subjectData.subID)
                     builder.dismiss()
                 }catch (e:Exception){  }



        }

        val subjectListAdapter= SubjectListAdapter(subjectList, object : ItemListener<MySubject> {
            override fun onItemClick(t: MySubject, pos: Int, boolean: Boolean) {

                subjectData=t

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

    private fun popUpSelectClass(){

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
            if (isClassSelected){


                val name = StringBuilder()
                ids.clear()

                    for (classeItem in classesList) {
                        if (classeItem.checked == true) {
                            if (ids.toString().isEmpty()) {
                                ids.append(classeItem.classID)
                                name.append(classeItem.className)
                            } else {
                                ids.append(",").append(classeItem.classID)
                                name.append(",").append(classeItem.className)
                            }
                        }
                    }



                binding.tvSelectClass.text= name
               if (!isClassWise){
                   getStudentListByClass(1,ids.toString(),2,false)
               }
                builder.dismiss()
            }

        }

         val subjectListAdapter= ClassListAdapter(classesList, selectAll, true, object : ItemListener<MyClasseItem> {
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {

                 isClassSelected = true
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        llSelectAll.setOnClickListener {
            selectAll = !selectAll
            for (i in classesList) {
                i .checked=selectAll
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


    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_library),
            getString(R.string.cancel)

        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this@PostAssignmentFragment)
            if (items[item] == getString(R.string.take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.choose_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        })
        builder.show()
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

                attachmentsList.clear()
                attachmentsList.add(Attachment(imageString,imageExt,""))

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
                    attachmentsList.clear()
                    attachmentsList.add(Attachment(imageString,imageExt,""))

                }
            }
        }


    fun getStudentListByClass(
        recipientType: Int,
        classIDs: String,
        scholarType: Int,
        byRollNo: Boolean,
    ){
        lifecycleScope.launch {
            postAssignmentViewModel.studentParentCommsStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data!=null){
                        if (it.data.students!=null){
                            studentList= it.data.students.toMutableList()

                             if (isEdit){
                                 setUpselectedStudent( )
                             }else{
                                  popUpStudentListByClass(it.data.students)
                             }
                        }

                    }


                }  }
            } }
        postAssignmentViewModel.studentParentComms(recipientType, classIDs, scholarType, byRollNo)
    }

    private fun setUpselectedStudent( ) {

        val selectedStudents = viewAssignmentData!!.stIDs!!.split(",")
        for (student in studentList)  {
            student.isSelected = selectedStudents.contains(student.stID.toString())
        }

    }


    private fun popUpStudentListByClass(students: List<Student>) {

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text= getText(R.string.lbl_select_Student)
        val  llSelectAll = view.findViewById<LinearLayout>(R.id.llSelectAll)
        val  checkImage = view.findViewById<ImageView>(R.id.checkImage)
        llSelectAll.isVisible=false
        builder.setView(view)

        relOk.setOnClickListener {
            if (isStudentSelected){


                val name = StringBuilder()

                for (student in students) {
                    if (student.isSelected!!) {
                        if (studentIds.toString().isEmpty()) {
                            studentIds.append(student.stID)
                            name.append(student.recipientName)
                        } else {
                            studentIds.append(",").append(student.stID)
                            name.append(",").append(student.recipientName)
                        }

                    }
                }
                classID_StID= groupStudentsByClass(students.filter { it.isSelected!! }).toMutableList()


                binding.tvSelectstudent.text= name

                builder.dismiss()

            }

        }

         if (isEdit){
             val selectedStudents = mutableSetOf<String>()
             viewAssignmentData!!.stIDs!!.split(",").map {
                 selectedStudents.add(it)
             }
             for (student in students)  {
                 student.isSelected = selectedStudents.contains(student.stID.toString())
             }
         }




        val subjectListAdapter= StudentListAdapter(students, selectAll,  object : ItemListener<Student> {
            override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {
                isStudentSelected = true
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        llSelectAll.setOnClickListener {
            selectAllStudent = !selectAllStudent
            for (i in students) {
                i .isSelected=selectAllStudent
            }
            subjectListAdapter.notifyDataSetChanged()
            checkImage.setImageResource(if (selectAllStudent) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun groupStudentsByClass(students: List<Student>): List<ClassID_StID> {
        // Group by classID and collect student IDs
        val classIDMap = students.groupBy { it.classID }.mapValues { entry ->
            // Join the list of stIDs with commas
            entry.value.joinToString(", ") { it.stID.toString() }
        }

        // Convert map to a list of ClassIDStID
        return classIDMap.map { (classID, stIDs) ->
            ClassID_StID(classID!!, stIDs)
        }
    }

    private fun setUpViewAssignment(){




            lifecycleScope.launch {
                postAssignmentViewModel.viewAssignmentStateFlow.collectLatest {
                    when (it) {  is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }  is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        val  data= it.data
                        viewAssignmentData= it.data

                        isSubjectSelected=true
                        isClassSelected=true

                        if (data!=null){

                            submitDate=data.submitDate
                            binding.etTitle.setText(data.title)
                            binding.etDescription.setText(data.data)
                            binding.ctvAssignmentDt.text=  Constant.apiToSystemDate(data.asgDate)
                            binding.tvSubmissionDt.text= Constant.apiToSystemDate(data.submitDate)
                            binding.tvSubmissionDt.isVisible = data.submitDate!=null && data.submitDate.isNotEmpty()
                            binding.cbActive.isChecked= data.isActive
                            binding.cbMultipleActive.isChecked= data.multipleSubmission
                            binding.cbLateSubmission.isChecked= data.lateSubmission
                            // binding.tvSelectClass.text=data.classIDs.toString()
                            // binding.tvSelectSubject.text=data.su.toString()

                            if (it.data.stIDs!=null){
                                binding.tvSelectstudent.isVisible= it.data.stIDs.isNotEmpty()
                            }
                            binding.isSubmitDate.isChecked= data.submitDate.isNotEmpty()

                            postAssignmentViewModel.getMyClass(data.subjectID,Constant.MY_CLASS_ID)

                            ids=     StringBuilder(it.data.classID.toString())

                            binding.radioGroupWisesubmission.isVisible=false

                            if (it.data.stIDs  !=null){
                                if (it.data.stIDs!=null && it.data.stIDs!!.isEmpty()){
                                    binding.tvSelectstudent.isVisible=false
                                }else{
                                    studentIds= StringBuilder(it.data.stIDs)
                                    getStudentListByClass(1,it.data.classID.toString(),2,false)
                                }
                            }



                            getMyClasses(viewAssignmentData!!.subjectID)

                        }
                    }  }
                } }


        postAssignmentViewModel.viewAssignment(assignmentId)
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
                binding.llFile.isVisible=true
                postAssignmentViewModel.setAttachments(selectedMedia)
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

}