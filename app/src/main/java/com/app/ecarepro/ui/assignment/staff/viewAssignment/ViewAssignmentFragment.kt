package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.databinding.FragmentViewAssignmentBinding
import com.app.ecarepro.model.AssignSubmitStudent
import com.app.ecarepro.model.TeacherAssignment
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.studentProfile.PopUpListAdapterLibTrans
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewAssignmentFragment : Fragment() , ItemListener<AssignSubmitStudent> {

    private var assignmentDetails: TeacherAssignment? = null
    private var isLateSubmitted: Boolean=false
     private var viewAssignmentData: NetworkViewAssignment? = null
     private var assignmentId: String  = ""
    private lateinit var binding : FragmentViewAssignmentBinding
    private val viewAssignmentViewModel : ViewAssignmentViewModel by viewModels()
    private var submitType =1
    private var teacherTypeUser= true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentViewAssignmentBinding.inflate(inflater,container,false)
         try {
             assignmentId = requireArguments().getString(Constant.ASSIGNMENT_ID).toString()
             isLateSubmitted = requireArguments().getBoolean(Constant.IS_LATE_SUBMITTED)
             teacherTypeUser = requireArguments().getBoolean(Constant.USER_TEACHER)
             arguments?.getParcelable<TeacherAssignment>("TeacherAssignment").let { data ->
                 assignmentDetails= data!!

             }

         }catch (_:Exception){}
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!teacherTypeUser){
            binding.llSubmitNotSubmit.isVisible=false
            binding.rvSubmitList.isVisible=false
            binding.tvDetailsAssi.isVisible=false

        }

        binding.btnLateSubmit.isVisible=isLateSubmitted
         if (assignmentDetails!=null){
             binding.llEditDelete.isVisible= assignmentDetails!! .hasAttachment!!

         }

        binding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_submit -> {

                    submitType=1
                    viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false) 

                }
                R.id.btn_late_submit -> {
                    submitType=3
                    viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false)

                }

                else -> {
                    submitType=2
                    viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,true)
                }
            }
        }


        lifecycleScope.launch {
            viewAssignmentViewModel.viewAssignmentStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    val  data= it.data
                    viewAssignmentData= it.data

                    if (data!=null){
                         binding.tvTitle.text= data.title
                        binding.tvData.text= data.data
                        binding.tvAssignmentDate.text= data.asgDate
                        binding.tvSubmittedDate.text= data.submitDate
                    }



                }  }
            } }

        lifecycleScope.launch {
            viewAssignmentViewModel.assigSubRPTStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    when(submitType){
                        1 -> {
                            if (it.data!=null){
                                if (it.data.studentList!=null){

                                    binding.rvSubmitList.isVisible=true

                                    val noticeAdapter = SubmitAssignListAdapter(it.data.studentList ,
                                        this@ViewAssignmentFragment)

                                    binding.rvSubmitList.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = noticeAdapter
                                    }



                                }else{
                                    binding.rvSubmitList.isVisible=false
                                }
                            }

                            binding.tvDetailsAssi.text= buildString {
                                append("Submitted (")
                                append(it.data!!.submittedBy)
                                append("/")
                                append(it.data.totalStudent)
                                append("): Offline (")
                                append(it.data.offlineSubmitted)
                                append("): Online (")
                                append(it.data.submittedBy-it.data.offlineSubmitted )
                                append(")")

                            }
                        }
                        2 -> {
                            if (it.data!=null){
                                if (it.data.studentList!=null){

                                    binding.rvSubmitList.isVisible=true

                                    val noticeAdapter = NotSubmitAssignListAdapter(it.data.studentList ,
                                        this@ViewAssignmentFragment)

                                    binding.rvSubmitList.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = noticeAdapter
                                    }
                                }else{
                                    binding.rvSubmitList.isVisible=false
                                }
                            }

                            binding.tvDetailsAssi.text= buildString {
                                append("Not Submitted (")
                                append(it.data!!.totalStudent-it.data.submittedBy)
                                append("/")
                                append(it.data.totalStudent)
                                append(") ")


                            }
                        }
                        3 -> {
                            if (it.data!=null){
                                if (it.data.studentList!=null){

                                    binding.rvSubmitList.isVisible=true

                                    val lateList=it.data.studentList.filter { q ->
                                        q.isLateSubmitted
                                    }


                                    val noticeAdapter = LateSubmitAssignListAdapter(lateList ,
                                        this@ViewAssignmentFragment)

                                    binding.rvSubmitList.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = noticeAdapter
                                    }



                                }else{
                                    binding.rvSubmitList.isVisible=false
                                }
                            }

                            binding.tvDetailsAssi.text= buildString {
                                append("Submitted (")
                                append(it.data!!.submittedBy)
                                append("/")
                                append(it.data.totalStudent)
                                append("): Offline (")
                                append(it.data.offlineSubmitted)
                                append("): Online (")
                                append(it.data.submittedBy-it.data.offlineSubmitted )
                                append(")")

                            }
                        }
                    }


                }  }
            } }
        viewAssignmentViewModel.viewAssignment(assignmentId)

        if ( teacherTypeUser){
             viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false)
        }




        binding.llView.setOnClickListener {
            if (assignmentDetails!=null){
                if (assignmentDetails!!.asgFiles !=null){
                    popUpFileList(assignmentDetails!!.asgFiles!!)
                }
            }

        }




    }

    private fun popUpFileList(filelist: List<String>) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.popup_file_list,null)
        val  rvDetails = view.findViewById<RecyclerView>(R.id.rvDetails)
        val  ivCross = view.findViewById<ImageView>(R.id.ivCross)
        val  tvHeading = view.findViewById<TextView>(R.id.tvHeading)
        tvHeading.text="View File"

        builder.setView(view)


        val popUpFileListAdapter= PopUpFileListAdapter(filelist ){ t, pos ->
            builder.dismiss()
            when(pos){
                1 -> {
                    openFile(t)
                }
                2 -> {
                    downloadFile(t)
                }
        }}
        rvDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpFileListAdapter
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }



    override fun onItemClick(t: AssignSubmitStudent, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                openFile(t.asgFile)
            }
            2 -> {

                if (t.asgFile!=null){
                    downloadFile(t.asgFile)
                }
            }
            3 -> {
                submitType=2
                dateSelctedPoPUp(t)
            }
        }
    }

    private fun openFile(fileSource: String) {
        if (Constant.isPdfUrl(fileSource)) {
            findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        } else {
            findNavController().navigate(R.id.openImageFragment, Bundle().apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        }

    }

    private fun downloadFile(fileSource: String) {
        if (Constant.isPdfUrl(fileSource)) {
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(fileSource, getString(R.string.assessment))
        } else {
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
        }


    }
    private fun dateSelctedPoPUp(t: AssignSubmitStudent) {
        val tv_date: TextView
        val btn_canel: Button
        val btn_submit: Button
        val ll_date: LinearLayout
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
         dialog.setContentView(R.layout.date_dialog_alert_new)
        tv_date = dialog.findViewById(R.id.tv_date)
        btn_canel = dialog.findViewById<Button>(R.id.btn_canel)
        btn_submit = dialog.findViewById(R.id.btn_submit)
        ll_date = dialog.findViewById(R.id.ll_date)
        ll_date.setOnClickListener {


            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    tv_date.text = date
                }

            }).setMinDate(System.currentTimeMillis())
        }
        btn_canel.setOnClickListener { dialog.dismiss() }
        btn_submit.setOnClickListener {
            if (tv_date.text.toString() == "") {
                mainActivity().showMessage("Please Select Date")
            } else {

                 offlineSubmited(t, tv_date.text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }


    private fun offlineSubmited(t: AssignSubmitStudent, fromDt: String) {

        viewAssignmentViewModel.offlineSubmited(assignmentId,t.stID, fromDt)

        lifecycleScope.launch {
            viewAssignmentViewModel.offlineSubmitedStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false)

                    }

                }


            }

        }}



}