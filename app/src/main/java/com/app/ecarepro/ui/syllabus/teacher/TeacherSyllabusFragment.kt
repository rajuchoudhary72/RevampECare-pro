package com.app.ecarepro.ui.syllabus.teacher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentTeacherSyllabusBinding
import com.app.ecarepro.model.Syllabuse
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class TeacherSyllabusFragment : Fragment() {


    private lateinit var binding: FragmentTeacherSyllabusBinding
    private val teacherSyllabusViewModel: TeacherSyllabusViewModel by activityViewModels()
    private var filterType = 0
    private lateinit var syllabusListFilter: List<Syllabuse>
    private var syllabustList: List<Syllabuse>? = null
    private var shouldRefresh = false
    private val syllabusShareViewModel : SyllabusShareViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeacherSyllabusBinding.inflate(inflater, container, false).apply {
            viewModel = teacherSyllabusViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.syllabus)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.addSyllabusFragment)
        }

        binding.spinnerSelectFilterType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Check if view is null before proceeding
                if (view == null) {
                    Log.e("SpinnerListener", "View is null")
                    return
                }

                filterType = position
                teacherSyllabusViewModel.lastSpinnerPos=position
                binding.searchBar.setText("")

                teacherSyllabusViewModel.isAll=filterType!=1

                if (teacherSyllabusViewModel.isDataLoaded){
                    syllabustList?.let { setUpTabLayout(it) }
                }

                if (position==0){
                    binding.searchBar.hint="Search By Subject"
                }else{
                    binding.searchBar.hint="Search By Title"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        if (!teacherSyllabusViewModel.isDataLoaded){
            getData()
        }


        lifecycleScope.launch {
            teacherSyllabusViewModel.teacherSyllabusStateFlow.observe(viewLifecycleOwner) { networkResult ->
                when (networkResult) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$networkResult")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        teacherSyllabusViewModel.isDataLoaded=true

                        if (networkResult.data?.syllabuses != null) {
                            syllabustList=networkResult.data.syllabuses
                            val spinnerTripTypeAdapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_list_item_1,
                                resources.getStringArray(R.array.filterTypeSyllabus)
                            )
                            binding.spinnerSelectFilterType.adapter = spinnerTripTypeAdapter

                            setUpTabLayout(networkResult.data.syllabuses)
                        } else {
                            binding.tvNoData.isVisible = true
                        }

                    }


                }
            }
        }





//        lifecycleScope.launch {
//            teacherSyllabusViewModel.searchQuery.collectLatest {
//                if (it.isNotEmpty() && syllabustList != null) {
//                    when (filterType) {
//                        0 -> {
//                            syllabusListFilter = syllabustList!!.filter { s ->
//                                s.subject.lowercase().contains(it.lowercase())
//                            }
//                            setUpTabLayout(syllabusListFilter)
//
//                        }
//                        1 -> {
//                            syllabusListFilter = syllabustList!!.filter { s ->
//                                s.title.lowercase().contains(it.lowercase())
//                            }
//                            setUpTabLayout(syllabusListFilter)
//
//                        }
//                    }
//                } else { }
//
//
//            }
//        }


    }

    private fun setupRecycleViewSyllabusList(syllabusListFilter: List<Syllabuse>) {
        syllabusShareViewModel.setSyllabusMutableLiveData(syllabusListFilter)
    }

    private fun setUpTabLayout(syllabuses: List<Syllabuse>){
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                val classList = mutableListOf<String> ()
                val fragmentList : ArrayList<Fragment> = ArrayList()

                setupRecycleViewSyllabusList(syllabuses)

                classList.add("All")

                   withContext(Dispatchers.Default) {
                       syllabuses.forEach {
                           if (!classList.contains(it.classSTD)) {
                               classList.add(it.classSTD!!)
                           }
                       }
                       classList. forEach { itemDat ->
                           fragmentList.add( TeacherSyllabusSubFragment.newInstance(itemDat,filterType))

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

                binding.viewPager.setCurrentItem(teacherSyllabusViewModel.selectedTabIndex, false)
                binding.tabLayout.getTabAt(teacherSyllabusViewModel.selectedTabIndex)?.select()

            }
        }catch (_:Exception){ }
    }


    override fun onResume() {
        super.onResume()
        if (shouldRefresh) {
            teacherSyllabusViewModel.getTeacherSyllabuses()
            shouldRefresh = false
        }
    }

    override fun onPause() {
        super.onPause()
       teacherSyllabusViewModel.selectedTabIndex = binding.tabLayout.selectedTabPosition
    }

    fun getData(){
       teacherSyllabusViewModel.getTeacherSyllabuses()
    }
}