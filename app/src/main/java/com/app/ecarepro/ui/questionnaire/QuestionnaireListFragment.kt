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
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionnaireListBinding
import com.app.ecarepro.model.Question
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuestionnaireListFragment : Fragment(), ItemListener<Question> {

    private lateinit var noticeAdapter: QuestionnaireAdapter
    private var isLoading:Boolean = true
    private var myQues: Boolean = false
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private lateinit var binding: FragmentQuestionnaireListBinding
    private val questionnaireViewModel: QuestionnaireViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQuestionnaireListBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        noticeAdapter = QuestionnaireAdapter(
            ArrayList(),
            this@QuestionnaireListFragment
        )

        binding.recyclerQuestionnaire.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = noticeAdapter
        }
          return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleButtonTypeQuestion.addOnButtonCheckedListener { _, checkedId, isChecked ->
            myQues = when (binding.toggleButtonTypeQuestion.checkedButtonId) {
                R.id.btn_all_ques -> {
                    noticeAdapter.clearData( )

                    pageIndex=1
                    questionnaireViewModel.getQuestionnaireList(pageIndex, false)
                    false
                }
                else -> {
                    pageIndex=1
                    noticeAdapter.clearData( )
                    questionnaireViewModel.getQuestionnaireList(pageIndex, true)
                    true
                }
            }
        }

        lifecycleScope.launch {
            questionnaireViewModel._questionnaireStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                         (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        isLoading=true
                         (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerQuestionnaire.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                         (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerQuestionnaire.isVisible = true

                        if (it.data != null) {

                            isLoading=true
                            if (it.data.questions != null) {
                                if (pageIndex==1){
                                    noticeAdapter.clearData()
                                }
                                binding.recyclerQuestionnaire.isVisible = true
                                binding.tvNoData.isVisible = false
                                noticeAdapter.setData(it.data.questions,myQues)

                            } else {
                                if (pageIndex==1){
                                    binding.recyclerQuestionnaire.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }

                            }

                        }

                    }


                }
            }
        }

        binding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.postQuestionnaireFragment)

        }

        questionnaireViewModel.getQuestionnaireList(pageIndex, false)

        binding.recyclerQuestionnaire.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null) {
                    if (dy > 0) {
                        visibleItemCount = linearLayoutManager.childCount;
                        totalItemCount = linearLayoutManager.itemCount;
                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                         if ( isLoading){
                             if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                 isLoading=false
                                 pageIndex += 1
                                 questionnaireViewModel.getQuestionnaireList(pageIndex, myQues)
                              }
                         }

                    }
                }
            }
        })

    }

    override fun onItemClick(t: Question, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                questionnaireViewModel.questionnaireLike(t.qid, boolean)
            }
            3 -> {
                questionnaireViewModel.deleteAnswer(t.qid )
                lifecycleScope.launch {
                    questionnaireViewModel.deleteAnswerStateFlow.collectLatest {
                        when (it) { is NetworkResult.Loading -> {
                                (requireActivity() as MainActivity).showLoader(true)
                            } is NetworkResult.Error -> {
                                (requireActivity() as MainActivity).showLoader(false)
                            }  is NetworkResult.Success -> {
                                (requireActivity() as MainActivity).showLoader(false)
                            mainActivity().showMessage(it.data!!.message.toString())
                            questionnaireViewModel.getQuestionnaireList(pageIndex, myQues)
                            } }  } }
            }

            4 -> {
                if (t.isVerified){
                    findNavController().navigate(
                        R.id.action_questionnaireListFragment_to_answerDetailsFragment,
                        Bundle().apply {
                            putInt(Constant.QUES_ID_ARGUMENT, t.qid)
                        })
                }

            }


        }
    }
}