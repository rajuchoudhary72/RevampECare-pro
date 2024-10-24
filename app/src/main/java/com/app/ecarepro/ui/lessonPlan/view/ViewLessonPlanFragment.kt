package com.app.ecarepro.ui.lessonPlan.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.lessonPlan.LessonPlanListAdapter
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewLessonPlanFragment : Fragment() {


    private var lPlanId: String= ""
    private lateinit var binding : FragmentViewLessonPlanBinding
    private val viewLessonPlanViewModel : ViewLessonPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentViewLessonPlanBinding.inflate(inflater,container,false)
        lPlanId = requireArguments().getString(Constant.LESSON_ID_ARGUMENT).toString()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        getLessonPlanDTL(lPlanId,0)

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
                        Log.e("ytututut",it.message.toString())
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            if (it.data.lessonPlans != null) {

                                binding.lessonData=it.data.lessonPlans

                                binding.tvPlanDuration.text= buildString {
                                    append(it.data.lessonPlans.fromDate)
                                    append(" to ")
                                    append(it.data.lessonPlans.tillDate)
                                }

                                binding.tvStatusBy.text= buildString {
                                    append(" by ")
                                    append(it.data.lessonPlans.actionTakenBy)
                                }

                                if (it.data.lessonPlans.status==0){
                                    binding. tvStatus.setTextColor( this@ViewLessonPlanFragment.resources.getColor(R.color.att_late_color,null))
                                    binding. tvStatus.text="Pending"

                                }else{
                                    binding. tvStatus.setTextColor( this@ViewLessonPlanFragment.resources.getColor(R.color.green,null))
                                    binding. tvStatus.text="Approved"

                                }
                                if (it.data.lessonPlans.attachment.fileURL!=null){
                                    binding.llFile.setOnClickListener { _ ->
                                        downloadFile(it.data.lessonPlans.attachment.fileURL)
                                    }
                                }else{
                                    binding.llFile.isVisible=false
                                }

                                binding.cvDetailsApprove.isVisible = it.data.lessonPlans.status != 1




                            }

                        }

                    }


                }
            }
        }



        viewLessonPlanViewModel.getLessonPlanDTL( id, teacherID)





    }

    private fun downloadFile(fileSource:String){
        when (Constant.isPdfUrl(fileSource)) {
            1 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, getString(R.string.assessment))
            }

            2 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
            }

            3 -> {
                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                    putString(Constant.URL_ARGUMENT, fileSource)
                })

            }

            else -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
            }
        } }
    }







