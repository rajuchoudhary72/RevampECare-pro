package com.app.ecarepro.ui.lessonPlan.add_lesson

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddLessonFragment : Fragment(), ItemListener<AuditorLst> {

    private var lPlanId: String= ""
    private lateinit var auditorSelectDat: AuditorLst
    private lateinit var classData: NetworkMyClass
    private var isClassSelected: Boolean = false
    private lateinit var selectClassData: MyClasseItem
    private var isSubSelected: Boolean = false
    private lateinit var subjectList: List<MySubject>
    private lateinit var binding: FragmentAddLessonBinding
    private lateinit var selectSubjectData: MySubject
    private val addLessonViewModel : AddLessonViewModel by viewModels()
    private   var imageExt: String= ""
    private   var imageString: String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAddLessonBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         lPlanId= requireArguments().getString(Constant.LESSON_ID_ARGUMENT).toString()
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ctvFromDate.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback  {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvFromDate.text=date
                }
            })
        }
        binding.ctvToDate.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback  {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvToDate.text=date
                } }).setMinDate(Constant.getLongTimeDate(binding.ctvFromDate.text.toString()))
        }

        binding.ctvSelectSubject.setOnClickListener {
            popUpSelectSubject( )
        }
        binding.ctvSelectClass.setOnClickListener {
            if (classData!=null){
                if (classData.myClasses!=null){
                    popUpSelectClass(classData.myClasses!!)
                }
            }

        }

        getCreateLessonDetails()



        binding.tvAddFile.setOnClickListener {
            selectImageOptionDialog()
        }
        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        binding.btnSubmit.setOnClickListener {
             addLessonViewModel.postLessonPlan(
            imageString,
            imageExt,
            "",
                 auditorSelectDat.auditor,
                 selectClassData.classID.toString(),
                 binding.etClosure.text.toString(),
                 binding.tvExtensionReq.text.toString(),
                 "",
                 binding.ctvFromDate.text.toString(),
                 binding.etIntroduction.text.toString(),
                 binding.etActivity.text.toString(),
                 0,
                 binding.tvLearningOutcomesReq.text.toString(),
                 binding.etObjective.text.toString(),
                 binding.etOtherResources.text.toString(),
                 binding.etResources.text.toString(),
                 binding.cbOpenStudent.isChecked,
                 selectSubjectData.subID,
                 binding.ctvToDate.text.toString(),
                 binding.etTopic.text.toString(),
                 binding.etLink.text.toString()
             )
        }


        if (lPlanId.isNotEmpty()){
            getLessonPlanDTL(lPlanId,0)
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

    override fun onItemClick(t: AuditorLst, pos: Int, boolean: Boolean) {
        auditorSelectDat=t
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


    private fun popUpSelectClass(myClasses: List<MyClasseItem>) {

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text="Select Class"
        builder.setView(view)

        relOk.setOnClickListener {
            if (isClassSelected){
                binding.ctvSelectClass. text= selectClassData.className
                 builder.dismiss()

            }

        }

        val popUpClassListAdapter= PopUpClassListAdapter(myClasses!!, object : ItemListener<MyClasseItem>{
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {
                selectClassData = t
                isClassSelected=true

            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpClassListAdapter
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
                            auditorSelectDat=AuditorLst( data.auditoryIds, data.auditoryTxt )
                            selectClassData=MyClasseItem(0,data.classesName,data.classIds,false   )
                            binding.etClosure.setText(data.closure)
                            binding.tvExtensionReq.text = data.extensionTopic
                            lPlanId=data.id
                            binding.ctvFromDate.text=data.fromDate
                            binding.etIntroduction.setText(data.introduction)
                            binding.etActivity.setText(data.kinestheticActivity)
                            binding.tvLearningOutcomesReq.text=data.learningOutcomes
                            binding.etObjective.setText(data.objective)
                            binding.etOtherResources.setText(data.otherResources)
                            binding.etResources.setText(data.resources)
                            binding.cbOpenStudent.isChecked=data.showToStudent
                            selectSubjectData=MySubject("",data.subID,data.subject)
                            binding.ctvToDate.text =data.tillDate
                            binding.etTopic.setText(data.topic)
                            binding.etLink.setText(data.youtubeLinks)

                        }

                    }

                } }  } }
        addLessonViewModel.getLessonPlanDTL( id, teacherID)


    }


}