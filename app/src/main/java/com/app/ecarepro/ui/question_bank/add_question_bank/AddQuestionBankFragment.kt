package com.app.ecarepro.ui.question_bank.add_question_bank

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.postQuestionBank.BrowsedImg
import com.app.ecarepro.data.network.model.postQuestionBank.NetworkPostQuestionBank
import com.app.ecarepro.data.network.model.question_bank.Chapter
import com.app.ecarepro.data.network.model.question_bank.MyClasse
import com.app.ecarepro.data.network.model.question_bank.QuestionType
import com.app.ecarepro.data.network.model.question_bank.Subject
import com.app.ecarepro.databinding.FragmentAddQuestionBankBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.question_bank.adapter.ChapterPopUpListAdapter
import com.app.ecarepro.ui.question_bank.adapter.ClassPopUpListAdapter
import com.app.ecarepro.ui.question_bank.adapter.QuesTypePopUpListAdapter
import com.app.ecarepro.ui.question_bank.adapter.SubjectsClassPopUpListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddQuestionBankFragment : Fragment() {

    private var isQuestType: Boolean = false
    private var isClassSelected: Boolean = false
    private var isSubjectSelected: Boolean = false
    private var isChapterSelected: Boolean = false
    private lateinit var questionType: QuestionType
    private lateinit var selectedClass: MyClasse
    private lateinit var selectedSubject: Subject
    private lateinit var selectedChapter: Chapter
    private   var questionTypes = mutableListOf<QuestionType>()
    private   var myClasses = mutableListOf<MyClasse>()
    private   var subjectLists = mutableListOf<Subject>()
    private   var chapterLists = mutableListOf<Chapter>()
     private lateinit var binding : FragmentAddQuestionBankBinding
    private val addQuestionBankViewModel : AddQuestionBankViewModel by viewModels()
    private lateinit var imageExt: String
    private lateinit var imageString: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAddQuestionBankBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSelectQueType.setOnClickListener {
            popUpQuestionBankQuesType()
        }
        binding.tvSelectClass.setOnClickListener {
            popUpQuestionBankClass()
        }
        binding.tvSelectSub.setOnClickListener {
            popUpQuestionBankSubjects()
        }
        binding.tvSelectChapter.setOnClickListener {
            popUpQuestionBankChapter()
        }
        binding.btAdd.setOnClickListener {
            saveAddQuestionBank()
        }

        binding.tvAddAttac.setOnClickListener {
            selectImageOptionDialog()
        }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        getQuestionBankCreate()

    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.general_take_photo),
            getString(R.string.general_choose_library),
            getString(R.string.general_cancel)

        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.general_add_photo))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this@AddQuestionBankFragment)
            if (items[item] == getString(R.string.general_take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.general_choose_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.general_cancel)) {
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

    private fun saveAddQuestionBank() {

        var isValidate=true

        if (!isChapterSelected){
            Toast.makeText(requireContext(),getString(R.string.select_chapter),Toast.LENGTH_SHORT).show()
            isValidate=false
        }
        if (!isQuestType){
            Toast.makeText(requireContext(),
                getString(R.string.select_question_type),Toast.LENGTH_SHORT).show()
            isValidate=false
        }
        if (!isClassSelected){
            Toast.makeText(requireContext(),getString(R.string.select_class),Toast.LENGTH_SHORT).show()
            isValidate=false
        }
        if (!isSubjectSelected){
            Toast.makeText(requireContext(),getString(R.string.select_subject),Toast.LENGTH_SHORT).show()
            isValidate=false
        }

        if (isValidate){
            addQuestionBankViewModel.submitPostQuestion(NetworkPostQuestionBank(
                BrowsedImg(imageString,"",imageExt),
                binding.tvSelectChapter.text.toString(),
                selectedChapter.chapterID,
                binding.tvSelectClass.text.toString(),
                selectedClass.classID,
                Constant.currentDate(),
                "",
                true,
                "",
                false,
                0,
                "",
                binding.tvSelectQueType.text.toString(),
                0,
                question = binding.etQuestion.text.toString(),
                selectedSubject.subID,
                binding.tvSelectSub.text.toString(),
                0,
                binding.etWeightage.text.toString()
            ))
        }



    }

    private fun getQuestionBankCreate() {

        lifecycleScope.launch {
            addQuestionBankViewModel.questionBankCreateStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                         Log.d("main", "Error$it")
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            if(!it.data.questionTypes.isNullOrEmpty()){
                                questionTypes = it.data.questionTypes .toMutableList()


                            }
                              if(!it.data.myClasses.isNullOrEmpty()){
                                 myClasses = it.data.myClasses .toMutableList()

                            }
                        }

                    } } } }

        addQuestionBankViewModel.getMyQuestionBankCreate()

    }

    private fun getQuestionBankSubject(classID: Int) {

        lifecycleScope.launch {
            addQuestionBankViewModel.questionBankSubjectStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            if(!it.data.subjects.isNullOrEmpty()){
                                subjectLists = it.data.subjects.toMutableList()

                            }


                        }

                    } } } }

        addQuestionBankViewModel.getQuestionBankSubject(classID)

    }

    private fun getQuestionBankChapters(
        classID: Int,
        subID: Int
    ) {

        lifecycleScope.launch {
            addQuestionBankViewModel.questionBankChaptersStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){


                            if(!it.data.chapters.isNullOrEmpty()){
                                chapterLists = it.data.chapters.toMutableList()

                            }

                        }

                    } } } }

        addQuestionBankViewModel.getQuestionBankChapters(classID,subID)

    }

    private fun popUpQuestionBankQuesType(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_que_type)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isQuestType){
                binding.tvSelectQueType.text= questionType .queType
                builder.dismiss()
            }


        }

        val infractionCatPopUpListAdapter= QuesTypePopUpListAdapter(questionTypes, object : ItemListener<QuestionType> {
            override fun onItemClick(t: QuestionType, pos: Int, boolean: Boolean) {
                  isQuestType=true
                questionType = t
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpQuestionBankClass(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.general_select_class)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isClassSelected){
                binding.tvSelectClass.text= selectedClass .className
                getQuestionBankSubject(selectedClass.classID)
                builder.dismiss()
            } }

        val infractionCatPopUpListAdapter= ClassPopUpListAdapter(myClasses, object : ItemListener<MyClasse> {
            override fun onItemClick(t: MyClasse, pos: Int, boolean: Boolean) {
                isClassSelected=true
                selectedClass = t
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpQuestionBankSubjects(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_subject)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isSubjectSelected){
                binding.tvSelectSub.text= selectedSubject .subjectName
                 builder.dismiss()
            } }

        val infractionCatPopUpListAdapter= SubjectsClassPopUpListAdapter(subjectLists, object : ItemListener<Subject> {
            override fun onItemClick(t: Subject, pos: Int, boolean: Boolean) {
                isSubjectSelected=true
                selectedSubject = t
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpQuestionBankChapter(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_chapter)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isChapterSelected){
                binding.tvSelectChapter.text= selectedChapter .chapterName
                builder.dismiss()
            } }

        val infractionCatPopUpListAdapter= ChapterPopUpListAdapter(chapterLists, object : ItemListener<Chapter> {
            override fun onItemClick(t: Chapter, pos: Int, boolean: Boolean) {
                isChapterSelected=true
                selectedChapter = t
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}