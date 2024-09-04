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
    private var subjectList = mutableListOf<MySubject>()
    private lateinit var classData: MyClasseItem
    private lateinit var subjectData: MySubject
    private var isClassSelected: Boolean = false
    private var isSubjectSelected: Boolean = false
    private var lastClickAttachmentType: AttachmentType? = null
    private var edit= false
    private var id= ""
    private var classID= 0
    private var classSTD= ""
    private var subID= 0
    private var subject= ""
    private var title= ""
    private var fileName= ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSyllabusBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            edit=  requireArguments().getBoolean("edit", false)
            id=  requireArguments().getString(Constant.ID,"")
            classID=  requireArguments().getInt("classID",0)
            classSTD=  requireArguments().getString("classSTD","")
            subID=  requireArguments().getInt("subID",0)
            subject=  requireArguments().getString("subject","")
            title=  requireArguments().getString("title","")
            fileName=  requireArguments().getString("fileName","")

        }catch (e:Exception){ }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (edit){
            binding.etDescription.setText(title)
            binding.tvSelectClass.text=classSTD
            binding.tvSelectSubject.text=subject

            isSubjectSelected=true
            isClassSelected=true
        }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
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
            }else
            if (binding.etDescription.text.toString().isEmpty()){
                isValidated=false
                binding.etDescription.error="Enter Title"
            }

            if (isValidated){
                addSyllabusViewModel.saveSyllabus(classID,id,subID,binding.etDescription.text.toString(),
                     if (pdfString.isNotEmpty()) BrowsedFile(pdfString,"pdf") else null
                ).invokeOnCompletion {
                    mainActivity().showMessage("Submitted Successfully!!!")
                    findNavController().popBackStack()
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
        tvHeading.text = getText(R.string.lbl_select_class)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isClassSelected) {
                binding.tvSelectClass.text = classData.className
                staffSubjects(classData.classID!!)
                builder.dismiss()

            }

        }
        val subjectListAdapter = ClassListAdapter(classesList,false, false, object : ItemListener<MyClasseItem> {
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {

                classData = t
                classID= classData.classID!!
                isClassSelected = true
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
        tvHeading.text = getText(R.string.select_subject)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isSubjectSelected) {
                binding.tvSelectSubject.text = subjectData.subjectName
                builder.dismiss()

            }

        }
        val subjectListAdapter = SubjectListAdapter(subjectList, object : ItemListener<MySubject> {
            override fun onItemClick(t: MySubject, pos: Int, boolean: Boolean) {

                subjectData = t
                subID= subjectData.subID!!
                isSubjectSelected = true
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

    }


