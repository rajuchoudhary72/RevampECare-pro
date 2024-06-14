package com.app.ecarepro.ui.question_paper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentQuestionPaperBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionPaperFragment : Fragment() {

    private val yearClassDataString = mutableListOf<String>()
    private var mYearList = mutableListOf<AcademicYear>()
    private var classSelectedID: Int = 0
    private lateinit var binding: FragmentQuestionPaperBinding
    private val questionPaperViewModel: QuestionPaperViewModel by viewModels()
    private lateinit var mMyClass: List<MyClasseItem>
    private var mMyClassDataString: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuestionPaperBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            binding.autoCompleteClass.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, pos, id ->
                    mMyClass[pos].classID?.let {
                        getQuestionPaper(it, 0)
                        classSelectedID = it
                    }

                }
            binding.autoCompleteSelectYear.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, pos, id ->
                    getQuestionPaper(classSelectedID, mYearList[pos].yrID)
                }
        }

        getClasses()

    }


    private fun getClasses() {
        lifecycleScope.launch {
            questionPaperViewModel.classStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        mMyClassDataString.clear()

                        if (it.data != null) {
                            if (it.data.myClasses != null) {
                                mMyClass = it.data.myClasses

                                mMyClass.forEach { data ->
                                    mMyClassDataString.add(data.className.toString())
                                }

                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.view_drop_down_menu,
                                    mMyClassDataString
                                )
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
                            }
                        }


                    }


                }
            }
        }
        questionPaperViewModel.getMyClass(Constant.SUB_ID, Constant.MY_CLASS_ID)
    }

    private fun getQuestionPaper(classID: Int, yearID: Int) {
        lifecycleScope.launch {
            questionPaperViewModel.questionPaperStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            yearClassDataString.clear()

                            if (it.data.academicYear != null) {
                                mYearList = it.data.academicYear.toMutableList()
                                mYearList.forEach { data ->
                                    yearClassDataString.add(data.session)
                                }
                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.view_drop_down_menu,
                                    yearClassDataString
                                )
                                binding.autoCompleteSelectYear.setAdapter(arrayAdapter)
                            }
                            if (it.data.qP_List != null) {
                                val fragmentList: ArrayList<Fragment> = ArrayList()
                                it.data.qP_List.forEach {
                                    fragmentList.add(QuestionPaperSubFragment(it.questionPapers))
                                }
                                val viewPagerAdapter = ViewPagerAdapter(
                                    fragmentList,
                                    activity?.supportFragmentManager!!,
                                    lifecycle
                                )
                                binding.viewPager.adapter = viewPagerAdapter
                                TabLayoutMediator(
                                    binding.tabLayout,
                                    binding.viewPager
                                ) { tab, position ->
                                    tab.text = it.data.qP_List[position].subjectName
                                }.attach()
                            }
                        }
                    }
                }


            }
        }

        questionPaperViewModel.getQuestionPaper(classID, yearID)
    }
}