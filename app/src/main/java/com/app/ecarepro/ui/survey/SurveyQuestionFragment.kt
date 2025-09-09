package com.app.ecarepro.ui.survey

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.SurveyQuestionBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.lassi.common.extenstions.hide
import com.lassi.common.extenstions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyQuestionFragment : Fragment() {

    private lateinit var list: ArrayList<Question>
    private val attendanceViewModel: SurveyViewModel by viewModels()
    private lateinit var binding: SurveyQuestionBinding
    private lateinit var id: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SurveyQuestionBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            checkIsValidToSubmit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            attendanceViewModel.surveyQuestionsStateFlow.collectLatest {
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

                            binding.btnSave.show()
                            binding.rvSurveyList.show()
                            binding.tvNoSurvey.hide()
                            list = it.data.questions
                            val noticeAdapter =
                                AdapterSurveyQuestions(
                                    requireContext(),
                                    list,
                                    false
                                )

                            binding.rvSurveyList.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = noticeAdapter
                            }

                        }

                    }


                }
            }
        }

        arguments?.let {
            id = it.getString("ID", "")
            attendanceViewModel.surveyQuestions(id)
        }

        lifecycleScope.launch {
            attendanceViewModel.commonStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)

                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        mainActivity().showMessage(
                            getString(R.string.your_response_has_been_recorded) +
                                getString(R.string.thanks_for_your_response))
                        findNavController().popBackStack()
                    }


                }
            }
        }
    }

    private fun checkIsValidToSubmit() {
        for (i in 0 until list.size) {
            var check = false
            if (list[i].isAnsMandatory) {
                for (j in 0 until list[i].options.size) {
                    if (list[i].options[j]
                            .isSelected
                    ) check = true
                }
                if (list[i].textBoxOnly){
                    if (list[i].answer.isNullOrEmpty()){
                        binding.rvSurveyList.smoothScrollToPosition(i)
                        mainActivity().showMessage(getString(R.string.please_attempt_all_the_mandatory_questions))
                        return
                    }

                }else{
                    if (!check) {
                        binding.rvSurveyList.smoothScrollToPosition(i)
                        mainActivity().showMessage(getString(R.string.please_attempt_all_the_mandatory_questions))
                        return
                    }
                }
            }
        }
        val surveyQuestionsSubmitRequest =
            SurveyQuestionsSubmitRequest(id = id, questions = list)
        attendanceViewModel.surveyQuestionsSubmit(surveyQuestionsSubmitRequest)
    }


}