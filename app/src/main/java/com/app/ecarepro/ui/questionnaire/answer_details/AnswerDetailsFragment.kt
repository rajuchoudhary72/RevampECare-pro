package com.app.ecarepro.ui.questionnaire.answer_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionnaireDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.questionnaire.AnswerAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AnswerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentQuestionnaireDetailsBinding
    private val answerDetailsViewModel : AnswerDetailsViewModel  by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentQuestionnaireDetailsBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quesID=  requireArguments().getInt(Constant.QUES_ID_ARGUMENT)



        binding.etAnswer.doAfterTextChanged {
            if (it != null) {
                if (it.isNotEmpty()){
                    binding.postAnswer.isEnabled = true
                    binding.postAnswer.setImageResource(R.drawable.send_icon_enable)



                }else{
                    binding.postAnswer.isEnabled = false
                    binding.postAnswer.setImageResource(R.drawable.send_icon_light)
                }

            }
        }

        binding.postAnswer.setOnClickListener {
            answerDetailsViewModel.postAnswer(quesID.toString(),binding.etAnswer.text.toString())
        }

        lifecycleScope.launch {
            answerDetailsViewModel.postAnswerStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                     }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.relSend.isVisible=false

                    }


                }
            }

        }

        lifecycleScope.launch {
            answerDetailsViewModel.answerDetailStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAnswer.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAnswer.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAnswer.isVisible = true

                        if (it.data!=null){

                            binding.questionData=it.data.question

                            if (it.data.list!=null){

                                binding.rvAnswer.isVisible=true

                                val answerAdapter = AnswerAdapter(it.data.list ,
                                    this@AnswerDetailsFragment)

                                binding.rvAnswer.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = answerAdapter
                                }
                            }else{
                                binding.rvAnswer.isVisible=false
                             }

                        }

                    }

                    else -> {}
                }
            }

    }

        answerDetailsViewModel.getAnswerList(quesID)
}}