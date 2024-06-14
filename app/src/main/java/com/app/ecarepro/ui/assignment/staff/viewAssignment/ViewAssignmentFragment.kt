package com.app.ecarepro.ui.assignment.staff.viewAssignment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.databinding.FragmentViewAssignmentBinding
import com.app.ecarepro.model.AssignSubmitStudent
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.formatDate
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewAssignmentFragment : Fragment() , ItemListener<AssignSubmitStudent> {

    private var viewAssignmentData: NetworkViewAssignment? = null
    private var submitList: Boolean=true
    private var assignmentId: String  = ""
    private lateinit var binding : FragmentViewAssignmentBinding
    private val viewAssignmentViewModel : ViewAssignmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentViewAssignmentBinding.inflate(inflater,container,false)
        assignmentId = requireArguments().getString(Constant.ASSIGNMENT_ID).toString()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_submit -> {

                    submitList=true
                    viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false)

                }

                else -> {
                    submitList=false
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
                        binding.tvSubject.text= ""
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



                    if (submitList){
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

                    }else{

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



                }  }
            } }

        viewAssignmentViewModel.viewAssignment(assignmentId)
        viewAssignmentViewModel.assignmnetSubmissionRPT(assignmentId,false)

        binding.llView.setOnClickListener {
            openFile(viewAssignmentData!!.asgFile)
        }
        binding.llDownload.setOnClickListener {
            downloadFile(viewAssignmentData!!.asgFile)
        }



    }

    private fun openFile(fileSource:String){
        findNavController().navigate(R.id.action_viewAssignmentFragment_to_openPdfFragment,Bundle( ).apply {
            putString(Constant.URL_ARGUMENT, fileSource)
        })
    }

    private fun downloadFile(fileSource:String){
        val androidDownloader = AndroidDownloader(requireContext())
        androidDownloader.downloadFile(fileSource, getString(R.string.assessment))
    }

    override fun onItemClick(t: AssignSubmitStudent, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                openFile(t.asgFile)
            }
            2 -> {
                downloadFile(t.asgFile)
            }
            3 -> {
                dateSelctedPoPUp(t)
            }
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


            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    tv_date.text = date
                }

            }).setMinDate(System.currentTimeMillis())
        }
        btn_canel.setOnClickListener { dialog.dismiss() }
        btn_submit.setOnClickListener {
            if (tv_date.text.toString() == "") {
                Toast.makeText(context, "Please Select Date", Toast.LENGTH_SHORT).show()
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