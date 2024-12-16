package com.app.ecarepro.ui.students_list.students_new_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentListNavHostBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.students_list.StudentListViewModel
import com.app.ecarepro.utils.Constant
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class StudentListNavHost : Fragment() {

    private var toFragment: String = ""
    private lateinit var binding: FragmentStudentListNavHostBinding
    private val studentListViewModel: StudentListViewModel by viewModels()
    private var schoolType = 2
    private var isDataLoaded = false

    @Inject
    lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentStudentListNavHostBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = studentListViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment = requireArguments().getString(Constant.TO).toString()
        } catch (_: Exception) {
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            studentListViewModel.studentListStateFlow.collectLatest { listNetworkResult ->
                when (listNetworkResult) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                     }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                         Log.d("main", "Error$listNetworkResult")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (listNetworkResult.data != null) {
                        if (listNetworkResult.data.students != null) {



                            try {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val classList = mutableListOf<String> ()
                                    val fragmentList : ArrayList<Fragment> = ArrayList()

                                    withContext(Dispatchers.Default) {
                                        listNetworkResult.data.students.forEach {
                                            if (!classList.contains(it.`class`)) {
                                                classList.add(it.`class`)
                                            }
                                        }
                                        classList. forEach { itemDat ->
                                            fragmentList.add( StudentListSubFragment(listNetworkResult.data.students, itemDat ,toFragment ))
                                        }
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
                                        tab.text = classList[position]
                                    }.attach()

                                    isDataLoaded=true

                                }
                            }catch (_:Exception){ }



                        }
                        }

                    }


                    else -> {}
                }


            }

        }


        if (!isDataLoaded) {
            studentListViewModel.getStudentList(schoolType,toFragment)
        }


        binding.toggleButtonSchoolType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonSchoolType.checkedButtonId) {
                R.id.btn_all -> {
                    schoolType = 2
                    studentListViewModel.getStudentList(schoolType,toFragment)
                }
                R.id.btn_boarding -> {
                    schoolType = 1
                    studentListViewModel.getStudentList(schoolType,toFragment)

                }

                else -> {
                    schoolType = 0
                    studentListViewModel.getStudentList(schoolType,toFragment)
                }
            }
        }
        checkIsBoarding()
    }

    private fun checkIsBoarding() {
        (requireActivity() as MainActivity).showLoader(true)
        lifecycleScope.launch {
            userDataStore.getSchoolData()?.let {
                it.schoolCode.let { schoolCode ->
                    studentListViewModel.validateSchoolCode(schoolCode) { it1 ->
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it1?.errorCode == 0) {
                            binding.toggleButtonSchoolType.isVisible = it1.isBoardingSchool!!
                        }
                    }
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        studentListViewModel.sendScreenEvent()
    }
}