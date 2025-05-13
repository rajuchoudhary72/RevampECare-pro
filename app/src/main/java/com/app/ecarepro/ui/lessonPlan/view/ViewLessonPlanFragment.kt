package com.app.ecarepro.ui.lessonPlan.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import android.util.Base64
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentViewLessonPlanBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.lessonPlan.LessonPlanListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewLessonPlanFragment : Fragment() {


    private var lPlanId: String = ""
    private var lPlanPush: String = ""
    private var lPlanIdnew: Int = 0
    private lateinit var binding: FragmentViewLessonPlanBinding
    private val viewLessonPlanViewModel: ViewLessonPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewLessonPlanBinding.inflate(inflater, container, false)
        lPlanId = requireArguments().getString(Constant.LESSON_ID_ARGUMENT).toString()
        lPlanPush = requireArguments().getString(Constant.LESSONPLAN_HARDCCODE_KEY ).toString()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        if (Constant.LESSONPLAN_HARDCCODE_KEY.isEmpty()) {
            binding.llCheck.isVisible = false
            binding.llAction.isVisible = false
        } else {
            binding.llCheck.isVisible = false
            binding.llAction.isVisible = true
        }
        binding.tvReject.setOnClickListener {
            popUpRemark(true)
        }
        binding.tvApprove.setOnClickListener {
            popUpRemark(false)
        }
        return binding.root
    }

    private fun popUpRemark(optionalReason: Boolean) {
        val tv_done: TextView
        val tv_cancel: TextView
        val tv_heading: TextView
        val alertmsg: TextView
        val textInputEditText: TextInputEditText


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.custom_popup_leave_reject)
        alertmsg = dialog.findViewById(R.id.alertmsg)
        tv_heading = dialog.findViewById(R.id.tv_heading)
        tv_done = dialog.findViewById(R.id.tv_done)
        tv_cancel = dialog.findViewById(R.id.tv_cancel)
        textInputEditText = dialog.findViewById(R.id.textFiledReason)
        if (optionalReason) {
            tv_done.setBackgroundColor(resources.getColor(R.color.red))
            tv_heading.text = getString(R.string.reject_plan)
            alertmsg.text = getString(R.string.are_you_sure_you_want_to_reject_the_lesson_plan)
        }else{
            tv_done.text = getString(R.string.approve)
            tv_done.setBackgroundColor(resources.getColor(R.color.green))
            tv_heading.text = getString(R.string.approve_plan)
            alertmsg.text = getString(R.string.are_you_sure_you_want_to_approve_the_lesson_plan)
        }



        tv_done.setOnClickListener {
            val reason: String = textInputEditText.text.toString().trim()

            if (optionalReason) {
                if (textInputEditText.text.toString().isNotEmpty()) {
                    //do reject  code here
                    viewLessonPlanViewModel.lessonPlanAction(lPlanIdnew, 2, reason).invokeOnCompletion {
                        getLessonPlanDTL(lPlanId, 0)
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(),"Rejection Reason is mandatory field",Toast.LENGTH_SHORT).show()
                    // textInputEditText.error = "Rejection Reason is mandatory field"
                }
            } else {
                //do approve code here

                viewLessonPlanViewModel.lessonPlanAction(lPlanIdnew, 1, reason).invokeOnCompletion {
                    getLessonPlanDTL(lPlanId, 0)
                }
                dialog.dismiss()
            }

        }
        tv_cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLessonPlanDTL(lPlanId, 0)
    }


    private fun getLessonPlanDTL(
        id: String,
        teacherID: Int
    ) {

        lifecycleScope.launch {
            viewLessonPlanViewModel.viewLessonPlanStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.e("ytututut", it.message.toString())
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            if (it.data.lessonPlans != null) {
                                lPlanIdnew= it.data.lessonPlans.lPlnID

                                binding.lessonData = it.data.lessonPlans

                                binding.tvPlanDuration.text = buildString {
                                    append(it.data.lessonPlans.fromDate)
                                    append(" to ")
                                    append(it.data.lessonPlans.tillDate)
                                }

                                binding.tvStatusBy.text = buildString {
                                    append(" by ")
                                    append(it.data.lessonPlans.actionTakenBy)
                                }
                                if (it.data.lessonPlans.status == 0) {
                                    binding.tvStatus.setTextColor(
                                        this@ViewLessonPlanFragment.resources.getColor(
                                            R.color.att_late_color,
                                            null
                                        )
                                    )
                                    binding.tvStatus.text = "Pending"
                                } else  if (it.data.lessonPlans.status == 2) {
                                    binding.tvStatus.setTextColor(
                                        this@ViewLessonPlanFragment.resources.getColor(
                                            R.color.red,
                                            null
                                        )
                                    )
                                    binding.tvStatus.text = "Rejected"
                                } else {
                                    binding.tvStatus.setTextColor(
                                        this@ViewLessonPlanFragment.resources.getColor(
                                            R.color.green,
                                            null
                                        )
                                    )
                                    binding.tvStatus.text = "Approved"
                                }
                                try {
                                    if (it.data.lessonPlans.attachment != null) {
                                        if (it.data.lessonPlans.attachment.fileURL != null) {
                                            binding.llFile.setOnClickListener { _ ->
                                                downloadFile(it.data.lessonPlans.attachment.fileURL)
                                            }
                                        } else {
                                            binding.llFile.isVisible = false
                                        }
                                    } else {
                                        binding.llFile.isVisible = false
                                    }
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                                if (it.data.lessonPlans.status==0){
                                    binding.cvDetailsApprove.isVisible = it.data.lessonPlans.status ==0
                                }else{
                                    binding.cvDetailsApprove.isVisible = false
                                }
                            }
                        }
                    }


                }
            }
        }
        viewLessonPlanViewModel.getLessonPlanDTL(id, teacherID)
    }

    private fun downloadFile(fileSource: String) {
        try {
            when (Constant.isPdfUrl(fileSource)) {

                1 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, getString(R.string.lesson))
                }

                2 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
                }

                3 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(
                        fileSource,
                        getString(R.string.lesson),
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )

                }

                else -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


    }
}