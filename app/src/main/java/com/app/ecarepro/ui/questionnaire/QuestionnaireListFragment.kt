package com.app.ecarepro.ui.questionnaire

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionnaireListBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.notice.NoticeListAdapter
import com.app.ecarepro.ui.thought.ThoughtsAdapter
import com.app.ecarepro.utils.ResponseState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class QuestionnaireListFragment : Fragment() {

    private lateinit var binding: FragmentQuestionnaireListBinding
    private val questionnaireViewModel: QuestionnaireViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding=FragmentQuestionnaireListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            questionnaireViewModel._questionnaireStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerQuestionnaire.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerQuestionnaire.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerQuestionnaire.isVisible = true

                        if (it.data!=null){


                           /* if (it.data.questions!=null){

                                binding.recyclerQuestionnaire.isVisible=true
                                binding.tvNoData.isVisible=false

                                val noticeAdapter = NoticeListAdapter(it.data.noticeList , this@NoticeListFragment)

                                binding.recyclerQuestionnaire.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            }else{
                                binding.recyclerQuestionnaire.isVisible=false
                                binding.tvNoData.isVisible=true
                            }*/

                        }

                    }

                    else -> {}
                }
            }
        }

    }
}