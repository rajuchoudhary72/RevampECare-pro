package com.app.ecarepro.ui.timetableviewer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassAndTeacherListBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassAndTeacherListFragment : Fragment() {

    private lateinit var binding : FragmentClassAndTeacherListBinding
     private val timeTableViewerViewModel : TimeTableViewerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentClassAndTeacherListBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTimeTableViewer()

    }

    private fun getTimeTableViewer() {
        lifecycleScope.launch {
            timeTableViewerViewModel.timeTableViewerStateFlow.collectLatest {

                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            if (it.data !=null ) {

                                val fragmentList : ArrayList<Fragment> = ArrayList()


                                fragmentList.add( ClassTimeTableFragment( it.data.classes))

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

                                    if (position==0){
                                        tab.text =  "Class"
                                    }else if (position==1) {
                                        tab.text =  "Teacher"
                                    }




                                }.attach()


                            }

                        }

                    }

                    else -> {}
                }


            }
        }

        timeTableViewerViewModel.getTimetableViewer( )

    }
}