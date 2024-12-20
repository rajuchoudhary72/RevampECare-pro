package com.app.ecarepro.ui.syllabus.teacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentTeacherSyllabusBinding
import com.app.ecarepro.model.Syllabuse
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TeacherSyllabusFragment : Fragment(), ItemListener<Syllabuse> {


    private lateinit var binding: FragmentTeacherSyllabusBinding
    private val teacherSyllabusViewModel: TeacherSyllabusViewModel by viewModels()
    private var filterType = 0
    private lateinit var syllabusListFilter: List<Syllabuse>
    private var syllabustList: List<Syllabuse>? = null

    private var shouldRefresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeacherSyllabusBinding.inflate(inflater, container, false).apply {
            viewModel = teacherSyllabusViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

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
                binding.searchBar.setText("")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        lifecycleScope.launch {
            teacherSyllabusViewModel.teacherSyllabusStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerSyllabus.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSyllabus.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSyllabus.isVisible = true

                        if (it.data != null) {
                            if (it.data.syllabuses != null) {
                                if (it.data.syllabuses.isNotEmpty()) {
                                    syllabustList = it.data.syllabuses
                                    setupRecycleViewSyllabusList(it.data.syllabuses)

                                    val spinnerTripTypeAdapter = ArrayAdapter(
                                        requireActivity(),
                                        android.R.layout.simple_list_item_1,
                                        resources.getStringArray(R.array.filterTypeSyllabus)
                                    )
                                    binding.spinnerSelectFilterType.adapter = spinnerTripTypeAdapter

                                } else {
                                    binding.recyclerSyllabus.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }

                            } else {
                                binding.recyclerSyllabus.isVisible = false
                                binding.tvNoData.isVisible = true
                            }
                        } else {
                            binding.recyclerSyllabus.isVisible = false
                            binding.tvNoData.isVisible = true
                        }

                    }


                }
            }
        }

        lifecycleScope.launch {
            teacherSyllabusViewModel.searchQuery.collectLatest {
                if (it.isNotEmpty() && syllabustList != null) {
                    when (filterType) {
                        0 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.classSTD.lowercase()
                                    .contains(it.lowercase()) || s.title.lowercase()
                                    .contains(it.lowercase()) || s.subject.lowercase()
                                    .contains(it.lowercase())
                            }
                            setupRecycleViewSyllabusList(syllabusListFilter)

                        }

                        1 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.classSTD.lowercase().contains(it.lowercase())
                            }
                            setupRecycleViewSyllabusList(syllabusListFilter)

                        }

                        2 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.title.lowercase().contains(it.lowercase())
                            }
                            setupRecycleViewSyllabusList(syllabusListFilter)

                        }

                        3 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.subject.lowercase().contains(it.lowercase())
                            }
                            setupRecycleViewSyllabusList(syllabusListFilter)

                        }
                    }
                } else {
                    syllabustList?.let { it1 -> setupRecycleViewSyllabusList(it1) }
                }


            }
        }

        //  teacherSyllabusViewModel.getTeacherSyllabuses()


    }

    override fun onItemClick(t: Syllabuse, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                findNavController().navigate(
                    R.id.openPdfFragment,
                    Bundle().apply {
                        putString(Constant.URL_ARGUMENT, t.filePath)
                    })
            }

            2 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(t.filePath, getString(R.string.syallabus))
            }

            3 -> {
                shouldRefresh = true
                findNavController().navigate(R.id.addSyllabusFragment, Bundle().apply {
                    putBoolean("edit", true)
                    putString(Constant.ID, t.id)
                    putInt("classID", t.classID)
                    putString("classSTD", t.classSTD)
                    putString("sections", t.sections)
                    putInt("subID", t.subID)
                    putString("subject", t.subject)
                    putString("title", t.title)
                    putString("fileName", t.fileName)
                })
            }

            4 -> {
                teacherSyllabusViewModel.deleteSyllabus(t.id).invokeOnCompletion {
                    teacherSyllabusViewModel.getTeacherSyllabuses()
                }
            }
        }

    }


    private fun setupRecycleViewSyllabusList(syllabuses: List<Syllabuse>) {
        if (syllabuses != null) {

            if (syllabuses.isNotEmpty()) {
                val syllabusesListAdapter =
                    TeacherSyllabusListAdapter(syllabuses, this@TeacherSyllabusFragment)


                binding.recyclerSyllabus.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = syllabusesListAdapter
                }
                binding.recyclerSyllabus.isVisible = true
                binding.tvNoData.isVisible = false


            } else {
                binding.recyclerSyllabus.isVisible = false
                binding.tvNoData.isVisible = true

            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldRefresh) {
            teacherSyllabusViewModel.getTeacherSyllabuses()
            shouldRefresh = false
        }
    }
}