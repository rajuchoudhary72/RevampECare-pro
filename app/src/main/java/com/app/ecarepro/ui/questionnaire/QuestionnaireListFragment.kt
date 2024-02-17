package com.app.ecarepro.ui.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionnaireListBinding
import com.app.ecarepro.model.Question
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuestionnaireListFragment : Fragment() , ItemListener<Question> {

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

        binding.toggleButtonTypeQuestion.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeQuestion.checkedButtonId) {
                R.id.btn_all_ques -> {
                    questionnaireViewModel.getQuestionnaireList(Constant.PAGE_INDEX,false)

                }

                else -> {
                    questionnaireViewModel.getQuestionnaireList(Constant.PAGE_INDEX,true)

                }
            }
        }

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


                            if (it.data.questions!=null){

                                binding.recyclerQuestionnaire.isVisible=true
                                binding.tvNoData.isVisible=false

                                val noticeAdapter = QuestionnaireAdapter(it.data.questions ,
                                    this@QuestionnaireListFragment)

                                binding.recyclerQuestionnaire.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            }else{
                                binding.recyclerQuestionnaire.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }

        binding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.postQuestionnaireFragment)
        }

        questionnaireViewModel.getQuestionnaireList(Constant.PAGE_INDEX,false)

        /*binding.recyclerQuestionnaire.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }

            }
        })*/


    }

    override fun onItemClick(t: Question, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                questionnaireViewModel.questionnaireLike(t.qid, boolean)
            }
            4->{
                findNavController().navigate(R.id.action_questionnaireListFragment_to_answerDetailsFragment,Bundle( ).apply {
                    putInt(Constant.QUES_ID_ARGUMENT, t.qid)
                })
            }


        }
    }
}