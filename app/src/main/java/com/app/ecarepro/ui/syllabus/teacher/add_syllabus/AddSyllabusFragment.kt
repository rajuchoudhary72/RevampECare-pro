package com.app.ecarepro.ui.syllabus.teacher.add_syllabus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.ClassSection
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.create_syllabus.BrowsedFile
import com.app.ecarepro.databinding.FragmentAddSyllabusBinding
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.postAssignment.ClassListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.getFile
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class AddSyllabusFragment : Fragment() {

    private   var pdfString: String=""
    private lateinit var binding: FragmentAddSyllabusBinding
    private val addSyllabusViewModel: AddSyllabusViewModel by viewModels()
    private var classesList = mutableListOf<MyClasseItem>()
    private  var sectionList= mutableListOf<ClassSection>()

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
                binding.tvAddAttac.isVisible=false
                binding.ivFileRemove.isVisible=false
            }
            isSubjectSelected=true
            isClassSelected=true

        }


        binding.ivFileRemove.setOnClickListener {
            binding.llFile.isVisible=false
            binding.tvAddAttac.isVisible=true
            pdfString=""
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

        binding.tvAddAttac.setOnClickListener {
            pickPdf()
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
                if (pdfString.isEmpty()){
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
                addSyllabusViewModel.saveSyllabus(
                    classID,
                    sectionIDs.toString().ifEmpty { null },
                    id,
                    subID,
                    binding.etDescription.text.toString(),
                    if (pdfString.isNotEmpty()) BrowsedFile(pdfString,"pdf") else null,
                    if (pdfString.isEmpty()) fileName else null,
                ).invokeOnCompletion {
                    mainActivity().showMessage("Submitted Successfully!!!")
                    findNavController().popBackStack()
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

            binding.tvSelectSubject.text = "All Subject"
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




    fun pickPdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfPickerLauncher.launch(intent)
    }



    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let { pdfUri ->
                val file = requireContext().getFile(pdfUri)
                pdfString = getBase64StringFromUri(file!!.toUri()).toString()
                binding.llFile.isVisible=true

             }
        }
    }

    private fun getBase64StringFromUri(uri: Uri): String? {
        val imageStream: InputStream
        return try {
            imageStream = requireNotNull(requireContext().contentResolver.openInputStream(uri))
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

    }


