package com.app.ecarepro.ui.lessonPlan.add_lesson

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddLessonBinding
import com.app.ecarepro.model.AuditorLst
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.model.RequiredField
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.postAssignment.ClassListAdapter
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.listener.ItemListener
import com.lassi.data.media.MiMedia
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import android.webkit.MimeTypeMap

import kotlin.math.abs



@AndroidEntryPoint
class AddLessonFragment : Fragment(), ItemListener<AuditorLst> {

    private var backDate: Int=0
    private var isAuditorySelected: Boolean=false
    private lateinit var requiredFiled: RequiredField
    private var lPlanId: String= ""
    private var audID= ""
    private var auditor= ""
    private lateinit var classData: NetworkMyClass
    private var isClassSelected: Boolean = false
    private var isSubSelected: Boolean = false
    private lateinit var subjectList: List<MySubject>
    private lateinit var binding: FragmentAddLessonBinding
    private lateinit var selectSubjectData: MySubject
    private val addLessonViewModel : AddLessonViewModel by viewModels()
    private   var imageExt: String= ""
    private   var imageString: String=""
    var selectAll: Boolean = false
    var classIds = StringBuilder()

    var timestampOneDay = "86400000".toLong()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAddLessonBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            lPlanId= requireArguments().getString(Constant.LESSON_ID_ARGUMENT).toString()

        }catch (_:Exception){}
          return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ctvFromDate.setOnClickListener {
            val toDayDateInLong=Constant.getLongTimeDate(Constant.currentDate())

            val tempdate=backDate*timestampOneDay
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback  {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvFromDate.text=date
                }  }).setMinDate(toDayDateInLong-tempdate)
        }


        binding.ctvToDate.setOnClickListener {
            val toDayDateInLong=Constant.getLongTimeDate(Constant.currentDate())

            val tempdate=backDate*timestampOneDay
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback  {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvToDate.text=date
                } }).setMinDate(toDayDateInLong-tempdate)
        }

        binding.ctvSelectSubject.setOnClickListener {
            popUpSelectSubject( )
        }
        binding.ctvSelectClass.setOnClickListener {
            if (isSubSelected){
                if (classData!=null){
                    if (classData.myClasses!=null){
                        popUpSelectClass(classData.myClasses!!)
                    }
                }
            }else{
                Toast.makeText(requireContext(),"Select Subject ",Toast.LENGTH_SHORT).show()
            }


        }

        getCreateLessonDetails()



        binding.tvBrowsePhoto.setOnClickListener {
            selectImageOptionDialog()
        }

        binding.tvBrowseFile.setOnClickListener {
            launchPdfPicker()
        }


        binding.ivFileRemove.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        binding.btnSubmit.setOnClickListener {

            if (isValidate()) {
                addLessonViewModel.postLessonPlan(
                    imageString,
                    imageExt,
                    "",
                    audID,
                    classIds = classIds.toString(),
                    binding.etClosure.text.toString(),
                    binding.etExtension.text.toString(),
                    "",
                    binding.ctvFromDate.text.toString(),
                    binding.etIntroduction.text.toString(),
                    binding.etActivity.text.toString(),
                    0,
                    binding.etLearningOutcomes.text.toString(),
                    binding.etObjective.text.toString(),
                    binding.etOtherResources.text.toString(),
                    binding.etResources.text.toString(),
                    binding.cbOpenStudent.isChecked,
                    selectSubjectData.subID,
                    binding.ctvToDate.text.toString(),
                    binding.etTopic.text.toString(),
                    binding.etLink.text.toString()
                )

                 lifecycleScope.launch {
                     addLessonViewModel.postLessonStateFlow.collectLatest {
                         when (it) {
                             is NetworkResult.Loading -> {
                                 (requireActivity() as MainActivity).showLoader(true)
                             } is NetworkResult.Error -> {
                                 (requireActivity() as MainActivity).showLoader(false)
                             } is NetworkResult.Success -> {
                                 (requireActivity() as MainActivity).showLoader(false)

                             Toast.makeText(requireContext(),"Submitted Successfully!!!",Toast.LENGTH_SHORT).show()

                             findNavController().popBackStack()

                             }
                         }

                     }
                 }
            }


        }


        if (lPlanId.isNotEmpty()){
            getLessonPlanDTL(lPlanId,0)
        }


    }

    private fun launchPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            )
            // Removed EXTRA_ALLOW_MULTIPLE for single file selection
        }
        pdfLauncher.launch(intent)
    }

    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    binding.llFile.isVisible = true

                    val base64 = getBase64FromUri(uri)
                    val ext = getFileExtensionFromUri(uri)

                    if (base64 != null) {
                        imageString = base64
                        imageExt = ext.toString()
                    }
                }
            }
        }


    private fun getBase64FromUri(uri: Uri): String? {
        return try {
            val inputStream =  requireContext(). contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileExtensionFromUri(uri: Uri): String? {
        return try {
            val mime = requireContext().contentResolver.getType(uri)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        } catch (e: Exception) {
            null
        }
    }



    private fun getCreateLessonDetails() {

        lifecycleScope.launch {
            addLessonViewModel.createLessonStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                   if (it.data!=null){

                     binding.requiredData = it.data.requiredField
                        subjectList=it.data.subjects
                       requiredFiled=it.data.requiredField
                       backDate= abs(it.data.requiredField.backDate)


                       if (it.data.auditorLst != null) {

                           binding.rvAuditor.isVisible = true


                           val studentListMarkAttAdapter = AuditorListAdapter(
                               it.data.auditorLst ,
                               this@AddLessonFragment
                           )

                           binding.rvAuditor.apply {
                               setHasFixedSize(true)
                               layoutManager = LinearLayoutManager(activity)
                               adapter = studentListMarkAttAdapter
                           }
                       } else {
                           binding.rvAuditor.isVisible = false
                        }

                   }


                } }  } }
        addLessonViewModel.createLessonPlan( )
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
            FileAccess.checkPermission(this@AddLessonFragment)
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

                imageExt = FileAccess.getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)

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

    override fun onItemClick(t: AuditorLst, pos: Int, boolean: Boolean) {
        isAuditorySelected=true
        auditor=t.auditor
        audID=t.audID
    }

    private fun popUpSelectSubject( ) {

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text="Select Subject"
        builder.setView(view)

        relOk.setOnClickListener {
            if (isSubSelected){
                binding.ctvSelectSubject. text= selectSubjectData.subjectName
                 getClass(selectSubjectData)
                builder.dismiss()

            }

        }

        val yearAdapter= PopUpSubjectListAdapter(subjectList, object : ItemListener<MySubject>{
            override fun onItemClick(t: MySubject, pos: Int, boolean: Boolean) {
                selectSubjectData = t
                isSubSelected=true

            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = yearAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun getClass(subjectName: MySubject) {
        lifecycleScope.launch {
            addLessonViewModel.myClassStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    if (it.data!=null){

                        classData=it.data


                    }


                } }  } }
        addLessonViewModel.getMyClass(subjectName.subID,0 )
    }




    private fun popUpSelectClass(classesList: List<MyClasseItem>){

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
                classIds.clear()

                for (classeItem in classesList) {
                    if (classeItem.checked == true) {
                        if (classIds.toString().isEmpty()) {
                            classIds.append(classeItem.classID)
                            name.append(classeItem.className)
                        } else {
                            classIds.append(",").append(classeItem.classID)
                            name.append(",").append(classeItem.className)
                        }
                    }
                }

                binding.ctvSelectClass.text= name
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

            val name = StringBuilder()
            classIds.clear()

            for (classeItem in classesList) {
                if (classeItem.checked) {
                    if (classIds.toString().isEmpty()) {
                        classIds.append(classeItem.classID)
                        name.append(classeItem.className)
                    } else {
                        classIds.append(",").append(classeItem.classID)
                        name.append(",").append(classeItem.className)
                    }
                }
            }

            binding.ctvSelectClass.text= name
            isClassSelected=selectAll

        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun getLessonPlanDTL(
        id: String,
        teacherID: Int
    ) {

        lifecycleScope.launch {
            addLessonViewModel.viewLessonPlanStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    if (it.data != null) {



                        if (it.data.lessonPlans != null) {
                            val data= it.data.lessonPlans

                            auditor=data.auditoryTxt
                            audID=data.auditoryIds
                            classIds = StringBuilder(data.classIds)
                            binding.etClosure.setText(data.closure)
                            binding.etExtension.setText(data.extensionTopic)
                            lPlanId=data.id
                            binding.ctvFromDate.text=data.fromDate
                            binding.etIntroduction.setText(data.introduction)
                            binding.etActivity.setText(data.kinestheticActivity)
                            binding.etLearningOutcomes.setText(data.learningOutcomes)
                            binding.etObjective.setText(data.objective)
                            binding.etOtherResources.setText(data.otherResources)
                            binding.etResources.setText(data.resources)
                            binding.cbOpenStudent.isChecked=data.showToStudent
                            selectSubjectData=MySubject("",data.subID,data.subject)
                            binding.ctvToDate.text =data.tillDate
                            binding.etTopic.setText(data.topic)
                            binding.etLink.setText(data.youtubeLinks)
                            binding.ctvSelectSubject.text=data.subject
                            binding.ctvSelectClass.text=data.classesName

                        }

                    }

                } }  } }
        addLessonViewModel.getLessonPlanDTL( id, teacherID)


    }

    private fun isValidate() : Boolean {
        var isValidate=true

        if (requiredFiled.isAttachmentRequired){
            if (imageString==""){
                isValidate=false
                Toast.makeText(requireContext(),"Please select Attachment",Toast.LENGTH_SHORT).show()
            }
        }
        if (requiredFiled.isAuditoryRequired){
            if (!isAuditorySelected){
                isValidate=false
                Toast.makeText(requireContext(),"Please select Auditory",Toast.LENGTH_SHORT).show()
            }
        }
        if (requiredFiled.isClosureRequired){
            if (binding.etClosure.text.isEmpty()){
                isValidate=false
                binding.etClosure.error="Mandatory Field"
            }
        }
        if (requiredFiled.isExtensionTopicRequired){
            if (binding.etExtension.text.isEmpty()){
                isValidate=false
                binding.etExtension.error="Mandatory Field"
            }
        }
        if (requiredFiled.isIntroductionRequired){
            if (binding.etIntroduction.text.isEmpty()){
                isValidate=false
                binding.etIntroduction.error="Mandatory Field"
            }
        }
        if (requiredFiled.isLearningOutcomesRequired){
            if (binding.etLearningOutcomes.text.isEmpty()){
                isValidate=false
                binding.etLearningOutcomes.error="Mandatory Field"
            }
        }
        if (requiredFiled.isObjectiveRequired){
            if (binding.etObjective.text.isEmpty()){
                isValidate=false
                binding.etObjective.error="Mandatory Field"
            }
        }
        if (requiredFiled.isOtherResourcesRequired){
            if (binding.etOtherResources.text.isEmpty()){
                isValidate=false
                binding.etOtherResources.error="Mandatory Field"
            }
        }
        if (requiredFiled.isResourcesRequired){
            if (binding.etResources.text.isEmpty()){
                isValidate=false
                binding.etResources.error="Mandatory Field"
            }
        }
        if (requiredFiled.isTopicRequired){
            if (binding.etTopic.text.isEmpty()){
                isValidate=false
                binding.etTopic.error="Mandatory Field"
            }
        }
        if (requiredFiled.isYoutubeLinksRequired){
            if (binding.etLink.text.isEmpty()){
                isValidate=false
                binding.etLink.error="Mandatory Field"
            }
        }



        return isValidate

    }

    private fun getBase64StringFromUri(file: File): String? {
        val imageStream: InputStream
        return try {
            imageStream = FileInputStream(file)
            val bytes: ByteArray = readBytes(
                imageStream
            )
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len: Int
        while ((inputStream.read(buffer).also { len = it }) != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        return byteBuffer.toByteArray()
    }

    private fun isPdf(attachment: MiMedia) =
        mutableListOf(
            AttachmentType.PDF.name,
            AttachmentType.AUDIO.name,
            AttachmentType.RECORDING.name
        ).contains(attachment.name)


}