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
import com.app.ecarepro.data.network.model.NetworkTeacherSyllabus
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

    private var _binding: FragmentTeacherSyllabusBinding? = null
    private val binding get() = _binding!!

    private val teacherSyllabusViewModel: TeacherSyllabusViewModel by viewModels()
    private var filterType = 0
    private var syllabusListFilter: List<Syllabuse> = emptyList() // Initialize with an empty list
    private var syllabusList: List<Syllabuse> = emptyList() // Initialize with an empty list
    private var shouldRefresh = false
    private lateinit var spinnerAdapter: ArrayAdapter<CharSequence>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherSyllabusBinding.inflate(inflater, container, false).apply {
            viewModel = teacherSyllabusViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.syllabus)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        teacherSyllabusViewModel.getTeacherSyllabuses() // Fetch data on fragment creation
    }

    private fun setupUI() {
        binding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.addSyllabusFragment)
        }

        // Move adapter setup here to avoid re-creating it each time data loads
        spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filterTypeSyllabus,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerSelectFilterType.adapter = spinnerAdapter

        binding.spinnerSelectFilterType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // The view parameter is usually not null, but it's good practice to check
                if (view == null) {
                    Log.e("SpinnerListener", "View is null")
                    return
                }
                filterType = position
                binding.searchBar.setText("")
                applySearchFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case where nothing is selected if needed
            }
        }

    }

    private fun setupObservers() {
        lifecycleScope.launch {
            teacherSyllabusViewModel.teacherSyllabusState.collectLatest { result ->
                handleSyllabusState(result)
            }
        }

        lifecycleScope.launch {
            teacherSyllabusViewModel.searchQuery.collectLatest { query ->
                applySearchFilter(query)
            }
        }

        lifecycleScope.launch {
            teacherSyllabusViewModel.deleteSyllabusState.collectLatest { result ->
                if (result is NetworkResult.Success)
                    teacherSyllabusViewModel.getTeacherSyllabuses()
            }
        }
    }

    private fun handleSyllabusState(result: NetworkResult<NetworkTeacherSyllabus>) {
        when (result) {
            is NetworkResult.Loading -> {
                showLoadingState(true)
            }

            is NetworkResult.Error -> {
                showLoadingState(false)
                Log.e("TeacherSyllabusFragment", "Error: ${result.message}")
                // Optionally, show a Toast or a specific error view
            }

            is NetworkResult.Success -> {
                showLoadingState(false)
                result.data?.let { data ->
                    if (data.syllabuses.isNotEmpty()) {
                        syllabusList = data.syllabuses
                        setupRecycleViewSyllabusList(syllabusList)
                    } else {
                        showNoDataState(true)
                    }
                } ?: showNoDataState(true)
            }
        }
    }

    private fun showLoadingState(isLoading: Boolean) {
        (requireActivity() as MainActivity).showLoader(isLoading)
        binding.recyclerSyllabus.isVisible = !isLoading
        binding.tvNoData.isVisible = false
    }

    private fun showNoDataState(show: Boolean) {
        binding.recyclerSyllabus.isVisible = !show
        binding.tvNoData.isVisible = show
    }

    private fun applySearchFilter(query: String? = null) {
        if (syllabusList.isEmpty()) return
        val filteredList = if (query?.isNotEmpty() == true) {
            syllabusList.filter { syllabus ->
                when (filterType) {
                    0 -> matchesAllFields(syllabus, query)
                    1 -> syllabus.classSTD.contains(query, ignoreCase = true)
                    2 -> syllabus.title.contains(query, ignoreCase = true)
                    3 -> syllabus.subject.contains(query, ignoreCase = true)
                    else -> false
                }
            }
        } else {
            syllabusList
        }
        setupRecycleViewSyllabusList(filteredList)
    }

    private fun matchesAllFields(syllabus: Syllabuse, query: String): Boolean {
        return syllabus.classSTD.contains(query, ignoreCase = true) ||
                syllabus.title.contains(query, ignoreCase = true) ||
                syllabus.subject.contains(query, ignoreCase = true)
    }

    override fun onItemClick(item: Syllabuse, position: Int, boolean: Boolean) {
        when (position) {
            1 -> {
                findNavController().navigate(
                    R.id.openPdfFragment,
                    Bundle().apply {
                        putString(Constant.URL_ARGUMENT, item.filePath)
                    }
                )
            }

            2 -> {
                try {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(item.filePath, getString(R.string.syallabus))
                } catch (e: NullPointerException) {
                    Log.e("DownloadError", "Error downloading file", e)
                }
            }

            3 -> {
                shouldRefresh = true
                findNavController().navigate(R.id.addSyllabusFragment, Bundle().apply {
                    putBoolean("edit", true)
                    putString(Constant.ID, item.id)
                    putInt("classID", item.classID)
                    putString("classSTD", item.classSTD)
                    putString("sections", item.sections)
                    putInt("subID", item.subID)
                    putString("subject", item.subject)
                    putString("title", item.title)
                    putString("fileName", item.fileName)
                })
            }

            4 -> {
                teacherSyllabusViewModel.deleteSyllabus(item.id)
            }
        }
    }

    private fun setupRecycleViewSyllabusList(syllabuses: List<Syllabuse>) {
        val adapter = TeacherSyllabusListAdapter(syllabuses, this)
        binding.recyclerSyllabus.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        showNoDataState(syllabuses.isEmpty())
    }

    override fun onResume() {
        super.onResume()
        if (shouldRefresh) {
            teacherSyllabusViewModel.getTeacherSyllabuses()
            shouldRefresh = false
        }
    }
}
