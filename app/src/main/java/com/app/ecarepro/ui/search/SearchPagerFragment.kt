package com.app.ecarepro.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSearchPagerBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.searchResultStudent
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.search.SearchPagerViewModel.Companion.SEARCH_TYPE_STUDENT
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchPagerFragment : Fragment() {
    private var _binding: FragmentSearchPagerBinding? = null

    private val binding get() = _binding!!

    private val searchPagerViewModel: SearchPagerViewModel by viewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setupObservers()
    }

    private fun setUpViews() {
        binding.recyclerView.addItemDecoration(GridMarginDecoration.create(
            margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin),
            columnProvider = object : ColumnProvider {
                override fun getNumberOfColumns(): Int {
                    return 2
                }
            }
        ))
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                searchViewModel.searchQuery.collectLatest { query ->
                    searchPagerViewModel.updateSearchQuery(query)
                }
            }

            launch {
                searchPagerViewModel.uiState.collectLatest { uiState ->
                    buildUiModels(uiState)
                }
            }
        }
    }

    private fun buildUiModels(uiState: SearchUiState) {
        mainActivity().showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        binding.recyclerView.withModels {
            if (uiState is SearchUiState.NoResultFound) {
                noDataFoundView {
                    id(R.id.empty_view)
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                }
            } else if (uiState is SearchUiState.Success) {
                if (uiState.searchType == SEARCH_TYPE_STUDENT) {
                    uiState.students.forEach { student ->
                        searchResultStudent {
                            id(student. stID)
                            student(student)
                        }
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SEARCH_TYPE = "search_type"

        fun newInstance(searchType: String) = SearchPagerFragment().apply {
            arguments = bundleOf(SEARCH_TYPE to searchType)
        }
    }
}