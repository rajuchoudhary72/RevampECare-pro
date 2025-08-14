package com.app.ecarepro.ui.survey

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAttendenceBinding
import com.app.ecarepro.databinding.SurveyLayoutBinding
import com.app.ecarepro.model.MonthModel
import com.app.ecarepro.model.YearModel
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.staffAttendence.AttendanceViewModel
import com.app.ecarepro.ui.staffAttendence.StaffAttendenceListAdapter
import com.lassi.common.extenstions.hide
import com.lassi.common.extenstions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class SurveyListFragment : Fragment() {

    private val attendanceViewModel: SurveyViewModel by viewModels()
    private lateinit var binding: SurveyLayoutBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SurveyLayoutBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        lifecycleScope.launch {
            attendanceViewModel.surveyListStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvSurveyList.hide()
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvSurveyList.hide()
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)


                        if (it.data != null) {


                            if (it.data.allSurvey.isNotEmpty()) {

                                binding.rvSurveyList.show()
                                binding.tvNoSurvey.hide()

                                val noticeAdapter =
                                    SurveyAdapter(it.data.allSurvey,this@SurveyListFragment ) { poss, data ->
                                        if (data.isOpen && !data.isResponded) {
                                            val bundle = Bundle()
                                            bundle.putString("ID", data.id)
                                            findNavController().navigate(
                                                R.id.surveyQuestionFragment,
                                                bundle
                                            )

                                        }
                                        else if (data.resultDeclared){
                                            // mContext.startActivity(Intent(mContext, ActivitySurveyResult::class.java).putExtra("surId", surveyModel.getSurID()) )

                                            mainActivity().showMessage(getString(R.string.survey_result))
                                        }


                                        else if (data.isResponded)
                                            mainActivity().showMessage(getString(R.string.thanks_for_your_response_your_response_has_already_been_recorded))
                                        else
                                            mainActivity().showMessage(getString(R.string.survey_closed))
                                    }

                                binding.rvSurveyList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            } else {
                                binding.rvSurveyList.hide()
                                binding.tvNoSurvey.show()
                            }

                        }

                    }


                }
            }
        }

        attendanceViewModel.surveyList(1, false)

    }


}