package com.app.ecarepro.ui.assignment.staff.postAssignment

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentPostAssignmentBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.circuler.PopUpListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PostAssignmentFragment : Fragment() {

     private lateinit var classData: MyClasseItem
    private var isClassSelected: Boolean = false
    private lateinit var classesList: List<MyClasseItem>
    private lateinit var subjectData: MySubject
    private var isSubjectSelected: Boolean = false
    private lateinit var subjectList: List<MySubject>
    private lateinit var binding : FragmentPostAssignmentBinding
    private val postAssignmentViewModel : PostAssignmentViewModel by viewModels()
    private   var imageExt: String= ""
    private   var imageString: String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
       binding = FragmentPostAssignmentBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.isSubmitDate.setOnCheckedChangeListener { _, isChecked ->
            binding.tvSubmissionDt.isVisible=isChecked
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
        binding.tvSelectClass.setOnClickListener { popUpSelectClass() }

        binding.btnSubmit.setOnClickListener { uploadAssignment() }

        binding.tvAddAttac.setOnClickListener { selectImageOptionDialog() }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        binding.ctvAssignmentDt.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.ctvAssignmentDt.text = date
                }

            })
        }

        binding.tvSubmissionDt.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvSubmissionDt.text = date
                }

            })
        }

    }

    private fun uploadAssignment() {

        var isValidate= true

        if (!isClassSelected){
            isValidate=false
            Toast.makeText(requireContext(),"Select Class",Toast.LENGTH_LONG).show()


        }
        if (!isSubjectSelected){
            isValidate=false
            Toast.makeText(requireContext(),"Select Subject",Toast.LENGTH_LONG).show()
        }
        if ( binding.etTitle.text.toString().isEmpty()){
            isValidate=false
            Toast.makeText(requireContext(),"Enter Title",Toast.LENGTH_LONG).show()
        }
        if ( binding.ctvAssignmentDt.text.toString()==getString(R.string.assignment_date)){
            isValidate=false
            Toast.makeText(requireContext(),"Select Assignment Date",Toast.LENGTH_LONG).show()
        }
        if (binding.isSubmitDate.isChecked){
            if ( binding.tvSubmissionDt.text.toString()==getString(R.string.submission_date)){
                isValidate=false
                Toast.makeText(requireContext(),"Select Submission Date",Toast.LENGTH_LONG).show()
            }
        }
        if ( binding.etDescription.text.toString().isEmpty()){
            isValidate=false
            Toast.makeText(requireContext(),"Enter Data",Toast.LENGTH_LONG).show()
        }




       if (isValidate ){
           var submitDate=""
           submitDate = if (binding.isSubmitDate.isChecked){
               binding.tvSubmissionDt.text.toString()
           }else{
               ""
           }
           classData .classID?.let {
               postAssignmentViewModel.createAssignment(
                   binding.ctvAssignmentDt.text.toString(),
                   0,
                   imageString,imageExt,"",
                   it,
                   it.toString(),
                   binding.etDescription.text.toString() ,
                   "",
                   "",
                   binding.cbActive.isChecked,
                   false,
                   binding.cbMultipleActive.isChecked,
                   subjectData.subID,
                   submitDate,
                   binding.etTitle.text.toString()  )
           }

           lifecycleScope.launch {
               postAssignmentViewModel.createAssignmentStateFlow.collectLatest {
                   when (it) {  is NetworkResult.Loading -> {
                       (requireActivity() as MainActivity).showLoader(true)
                   }  is NetworkResult.Error -> {
                       (requireActivity() as MainActivity).showLoader(false)
                   } is NetworkResult.Success -> {
                       (requireActivity() as MainActivity).showLoader(false)
                       Toast.makeText(requireContext(),"Assignment Uploaded Successfully",Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                   }  }
               } }
       }


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
            binding.tvSelectSubject.text= subjectData .subjectName
            isSubjectSelected = true
            getMyClasses(subjectData.subID)
            builder.dismiss()

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
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectClass.text= subjectData .subjectName
            isClassSelected = true
             builder.dismiss()

        }

        val subjectListAdapter= ClassListAdapter(classesList, object : ItemListener<MyClasseItem> {
            override fun onItemClick(t: MyClasseItem, pos: Int, boolean: Boolean) {

                classData=t
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

}