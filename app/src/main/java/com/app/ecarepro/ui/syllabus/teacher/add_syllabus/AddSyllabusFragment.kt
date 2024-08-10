package com.app.ecarepro.ui.syllabus.teacher.add_syllabus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddSyllabusBinding
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.postAssignment.ClassListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.lassi.data.media.MiMedia
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddSyllabusFragment : Fragment() {

    private lateinit var binding: FragmentAddSyllabusBinding
    private val addSyllabusViewModel: AddSyllabusViewModel by viewModels()
    private var classesList = mutableListOf<MyClasseItem>()
    private var subjectList = mutableListOf<MySubject>()
    private lateinit var classData: MyClasseItem
    private lateinit var subjectData: MySubject
    private var isClassSelected: Boolean = false
    private var isSubjectSelected: Boolean = false
    private var lastClickAttachmentType: AttachmentType? = null
    private val id= ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSyllabusBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getMyClasses()

        binding.tvSelectClass.setOnClickListener {
            popUpSelectClass()
        }
        binding.tvSelectSubject.setOnClickListener {
            popUpSelectSubject()
        }

        binding.tvAddAttac.setOnClickListener {
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
            }else
            if (binding.etDescription.text.toString().isEmpty()){
                isValidated=false
                binding.etDescription.error="Enter Title"
            }

            if (isValidated){
                addSyllabusViewModel.saveSyllabus(classData.classID!!,id,subjectData.subID,binding.etDescription.text.toString()).invokeOnCompletion {
                    mainActivity().showMessage("Submitted Successfully!!!")
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
        val subjectListAdapter = ClassListAdapter(classesList,false, object : ItemListener<MyClasseItem> {
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {

                classData = t
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


    private fun launchPdfPicker() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pdfLauncher.launch(intent)
    }


    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
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
                            addSyllabusViewModel.setAttachments(files)
                        }
                    }
                }
            }
        }

}