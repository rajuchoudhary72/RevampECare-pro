package com.app.ecarepro.ui.question_bank

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionBankBinding
import com.app.ecarepro.model.QBQuestion
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuestionBankFragment : Fragment(), ItemListener<QBQuestion> {

    private lateinit var binding : FragmentQuestionBankBinding
    private val questionBankViewModel : QuestionBankViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentQuestionBankBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            questionBankViewModel.questionBankStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                         (requireActivity() as MainActivity).showLoader(false)
                        binding.rvQuestionBankList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvQuestionBankList.isVisible = true

                        if (it.data!=null){

                            if (it.data.qB_Questions!=null){

                                binding.rvQuestionBankList.isVisible=true
                                binding.tvNoData.isVisible=false

                                val bankAdapter = QuestionBankAdapter(it.data.qB_Questions , this@QuestionBankFragment)

                                binding.rvQuestionBankList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = bankAdapter
                                }
                            }else{
                                binding.rvQuestionBankList.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }


                }
            }
        }

        questionBankViewModel.getMyQuestionBank()

    }

    override fun onItemClick(t: QBQuestion, pos: Int, boolean: Boolean) {
        when (pos) {
            Constant.DELETE -> {
                questionBankViewModel.getDeleteQuestion(t.id).invokeOnCompletion {
                    questionBankViewModel.getMyQuestionBank()
                }
            }
            Constant.EDIT -> {
                findNavController().navigate(R.id.addQuestionBankFragment)
            }
            Constant.DOWNLOAD -> {
                try {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(t.filename, getString(R.string.question_paper))
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.download_started_check_you_status_bar_for_more_information),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }
        }
    }
}