package com.app.ecarepro.ui.discipline_log.infraction.add_infraction

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddInfractionBinding
import com.app.ecarepro.model.InfractionConsequence
import com.app.ecarepro.model.InfractionType
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.StudentDTL
import com.app.ecarepro.model.Type
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionCatPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionConsPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.SubInfractionPopUpListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddInfractionFragment : Fragment() {

    private var userID: Int = 0
    private lateinit var subInfractionCatData: Type
    private   var subInfractionSubCateList= mutableListOf<Type>()
    private lateinit var infractionConsequence: InfractionConsequence
    private var infrTypeSelected: Boolean = false
    private var SubInfrTypeSelected: Boolean = false
    private var infrConsSelected: Boolean = false
    private lateinit var infractionCatData: InfractionType
    private   var infractionConsequencesList = mutableListOf<InfractionConsequence>()
    private   var infractionTypeList= mutableListOf<InfractionType>()
    private lateinit var binding: FragmentAddInfractionBinding
    private val addInfractionViewModel : AddInfractionViewModel by viewModels()
    private var lastClickAttachmentType: AttachmentType? = null
    private var uType : Int= Constant.STUDENT_TYPE



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAddInfractionBinding.inflate(inflater,container,false)
        try {
            userID=  requireArguments().getInt(Constant.USER_ID)
            uType=  requireArguments().getInt(Constant.USER_TYPE)
        } catch (_: Exception) { }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.add_infraction)


        binding.tvSelectInfractionCate.setOnClickListener {
            popUpSelectInfractionCat()
        }

        binding.tvSelectSubInfraction.setOnClickListener {
            popUpSelectSubInfractionCat()
        }

        binding.tvSelectCons.setOnClickListener {
            popUpSelectInfractionCons()
        }

        binding.tvContinue.setOnClickListener {
            saveInfraction(1)
        }
        binding.tvContinueNoti.setOnClickListener {
            saveInfraction(2)
        }

        binding.cvAttachmentButton.setOnClickListener {
            showAttachmentDialog()
        }
        binding.ivRemoveAttachment.setOnClickListener {
            addInfractionViewModel.removeAttachment()
            binding.llAttachements.isVisible=false
        }

        getAddInfection()

    }

    private fun getSubInfection(infrTypeID: Int) {

        lifecycleScope.launch {
            addInfractionViewModel.subInfractionTypesStateFlow.collectLatest {
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
                        if (it.data != null) {
                            subInfractionSubCateList.clear()
                            if (!it.data.types.isNullOrEmpty()){

                                subInfractionSubCateList= it.data.types.toMutableList()

                            }



                        }

                    }


                }
            }
        }


        addInfractionViewModel.getSubInfractionTypes(infrTypeID)
    }

    private fun getAddInfection() {

        lifecycleScope.launch {
            addInfractionViewModel.addInfractionStateFlow.collectLatest {
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
                        if (it.data != null) {

                            if (uType==Constant.STUDENT_TYPE){
                                bindStudentDetails(it.data.studentDTL)
                            }else{
                                bindStaffDetails(it.data.stafftDTL)
                            }


                            infractionTypeList.clear()
                            infractionConsequencesList.clear()
                            if (!it.data.infractionTypes.isNullOrEmpty()){

                                infractionTypeList= it.data.infractionTypes.toMutableList()
                            }

                            if (!it.data.infractionConsequences.isNullOrEmpty()){

                                infractionConsequencesList= it.data.infractionConsequences.toMutableList()
                            }

                         }  }
                }
            }
        }

        if (uType == Constant.STUDENT_TYPE){
            addInfractionViewModel.addInfraction(userID)
        }else{
            addInfractionViewModel.addStaffInfraction(userID)
        }

    }

    private fun bindStaffDetails(staffDTL: Staff) {

        binding.tvStudentName.text= buildString {
            append(staffDTL.name)
        }

        Picasso.get().
        load(staffDTL.photo)
            .placeholder(R.drawable.default_profile)
            .  into(binding.circleImageViewProfile)

        binding.tvAdmissionNo.text= buildString {
            append(getString(R.string.designation_bold))
            append(" ")
            append(staffDTL.designation)
        }
        binding.tvClassName.text= buildString {
            append(getString(R.string.mobile_pun_bold))
            append(" ")
            append(staffDTL.mobile)
        }

        binding.tvFatherName.text= buildString {
            append(getString(R.string.doj_bold))
            append(" ")
            append(staffDTL.doj)
        }

        binding.tvContact.text= buildString {
            append(getString(R.string.email_id_pun_bold))
            append(" ")
            append(staffDTL.emailID)
        }
        binding.tvGender.text= buildString {
            append(getString(R.string.gender_pun_bold))
            append(" ")
            append(staffDTL.gender)
        }


    }

    private fun bindStudentDetails(studentDTL: StudentDTL) {

        Picasso.get().
        load(studentDTL.photo)
            .placeholder(R.drawable.default_profile)
            .  into(binding.circleImageViewProfile)

        binding.tvStudentName.text= buildString {
            append(studentDTL.name)
        }

        binding.tvAdmissionNo.text= buildString {
            append(getString(R.string.admission_no))
            append(studentDTL.admissionNo)
        }
        binding.tvClassName.text= buildString {
            append(getString(R.string.classes))
            append(studentDTL.`class`)
        }
        binding.tvFatherName.text= buildString {
            append(getString(R.string.contact_person))
            append(studentDTL.contactPerson)
        }
        binding.tvContact.text= buildString {
            append(getString(R.string.contact_no))
            append(studentDTL.contactMob)
        }
    }

    private fun popUpSelectInfractionCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_infraction_category)
        builder.setView(view)

        relOk.setOnClickListener {
           if (infrTypeSelected){
               binding.tvSelectInfractionCate.text= infractionCatData .infraction


               getSubInfection(infractionCatData.infrTypeID)

               builder.dismiss()

           }
        }

        val infractionCatPopUpListAdapter= InfractionCatPopUpListAdapter(infractionTypeList, object : ItemListener<InfractionType> {
            override fun onItemClick(t: InfractionType, pos: Int, boolean: Boolean) {
                infractionCatData = t
                infrTypeSelected=true
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

    private fun popUpSelectSubInfractionCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_infraction_subcategory)
        builder.setView(view)

        relOk.setOnClickListener {
             if (SubInfrTypeSelected){
                 binding.tvSelectSubInfraction.text= subInfractionCatData .infraction

                 addInfractionViewModel.getinfractionInstance(infractionCatData.infrTypeID,
                     subInfractionCatData.infrTypeID,userID,uType)
                 setInfrenceInstance()
                 builder.dismiss()
             }

        }

        val infractionCatPopUpListAdapter= SubInfractionPopUpListAdapter(subInfractionSubCateList, object : ItemListener<Type> {
            override fun onItemClick(t: Type, pos: Int, boolean: Boolean) {
                subInfractionCatData = t
                SubInfrTypeSelected=true
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

    private fun setInfrenceInstance() {

        lifecycleScope.launch {
            addInfractionViewModel.infractionInstanceStateFlow.collectLatest {
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
                         binding.tvInstance.text= it.data?.instance.toString()
                    }


                }
            }
        }

    }

    private fun popUpSelectInfractionCons(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_consequences)
        builder.setView(view)

        relOk.setOnClickListener {
           if (infrConsSelected){
               binding.tvSelectCons.text= infractionConsequence .consequences

               builder.dismiss()
           }

        }

        val infractionCatPopUpListAdapter= InfractionConsPopUpListAdapter(infractionConsequencesList, object : ItemListener<InfractionConsequence> {
            override fun onItemClick(t: InfractionConsequence, pos: Int, boolean: Boolean) {
                infractionConsequence = t
                infrConsSelected=true
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

    private fun saveInfraction(action:Int) {
        var isValidate= true
        if (!infrTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_infraction_category))
        }else
        if (!SubInfrTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_infraction_subcategory))
        }else
        if (!infrConsSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_consequences))
        }

        if (isValidate){
            addInfractionViewModel.saveInfraction(
                uType = uType,
                action,
                userID,
                subInfractionCatData.infrTypeID,
                infractionConsequence.consID,
                binding.tvInstance.text.toString().toInt(),
                Constant.getCurrentDateTimeSecond(),
                binding.etPlanName.text.toString(),
                isComplianceActive = binding.cbActiveCompliance.isChecked

            )

            lifecycleScope.launch {
                addInfractionViewModel.saveInfractionStateFlow.collectLatest {
                    when (it) {
                        is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                        } is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        mainActivity().showMessage(getString( R.string.submit_successfully))
                       findNavController().popBackStack()

                    }
                    }
                }
            }

        }

    }


    private fun showAttachmentDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_attachment_dialog, null)

        val tvBrowsePhoto = dialogView.findViewById<TextView>(R.id.tv_browse_photo)
        val tvBrowseFile = dialogView.findViewById<TextView>(R.id.tv_browse_file)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        tvBrowsePhoto.setOnClickListener {
            if (checkAndRequestPermissions()) {
                lastClickAttachmentType = AttachmentType.GALLERY
                launchPhotoPicker()
            }
            dialog.dismiss()
        }

        tvBrowseFile.setOnClickListener {
            lastClickAttachmentType = AttachmentType.PDF
            launchPdfPicker()
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun launchPdfPicker() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfLauncher.launch(intent)
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
                binding.llAttachements.isVisible=true

                addInfractionViewModel.setAttachments(selectedMedia)
            }
        }


    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        binding.llAttachements.isVisible=true
                        addInfractionViewModel.setAttachments(
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
                            binding.llAttachements.isVisible=true

                            addInfractionViewModel.setAttachments(files)
                        }
                    }
                }
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