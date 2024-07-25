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
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.databinding.FragmentSearchPagerBinding
import com.app.ecarepro.menuCard
import com.app.ecarepro.model.Staff
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.searchResultStudent
import com.app.ecarepro.ui.MainActivityUiState
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.search.SearchPagerViewModel.Companion.SEARCH_TYPE_MODULE
import com.app.ecarepro.ui.search.SearchPagerViewModel.Companion.SEARCH_TYPE_STUDENT
import com.app.ecarepro.utils.Constant
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
    private val systemViewModel: SystemViewModel by activityViewModels()

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
                systemViewModel.uiState.collectLatest { uiState ->
                    if (uiState is MainActivityUiState.Success) {
                        searchPagerViewModel.setModules(uiState.menus)
                    }
                }
            }

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
                when (uiState.searchType) {
                    SEARCH_TYPE_STUDENT -> {
                        uiState.students.forEach { student ->
                            searchResultStudent {
                                id(student.stID)
                                photo(student.photo)
                                title(student.nameAndClass())
                                details(student.details())
                                clickListener { _ ->
                                    findNavController().navigate(
                                        R.id.studentProfileNavHostFragment,
                                        bundleOf(Constant.STUDENT_ID_ARGUMENT to student.stID)
                                    )
                                }
                            }
                        }
                    }
                    SEARCH_TYPE_MODULE -> {
                        buildModuleModels(uiState.modules)
                    }
                    else -> {
                        uiState.staffs.forEach { staff: Staff ->
                            searchResultStudent {
                                id(staff.id + staff.name + staff.sid)
                                photo(staff.photo)
                                title(staff.name)
                                details(staff.details())
                                clickListener { _ ->
                                    findNavController().navigate(
                                        R.id.staffProfileNavHostFragment,
                                        bundleOf(Constant.STAFF_ID_ARGUMENT to staff.sid)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun EpoxyController.buildModuleModels(modules: List<Menu>) {
        modules.forEach { menu ->
            if (menu.childMenus.isNullOrEmpty()) {
                menuCard {
                    id(menu.menuID)
                    title(menu.title)
                    icon(menu.icon)
                    clickListener { _ ->
                        mainActivity().getFragmentId(menu.menuID)
                    }
                }
            } else {
                menu.childMenus.forEach { childMenu ->
                    if (childMenu.childMenus.isNullOrEmpty()) {
                        menuCard {
                            id(menu.menuID, childMenu.menuID)
                            title(childMenu.title)
                            icon(childMenu.icon)
                            parentMenuIcon(menu.icon)
                            clickListener { _ ->
                                mainActivity().getFragmentId(
                                    menu.menuID,
                                    childMenu.chMenuID
                                )
                            }
                        }
                    } else {
                        childMenu.childMenus.forEach { childChildMenu ->
                            menuCard {
                                id(
                                    menu.menuID,
                                    childMenu.chMenuID,
                                    childChildMenu.menuID
                                )
                                title(childChildMenu.title)
                                icon(childChildMenu.icon)
                                parentMenuIcon(childMenu.icon)
                                clickListener { _ ->
                                    mainActivity().getFragmentId(
                                        menu.menuID,
                                        childMenu.chMenuID,
                                        childChildMenu.sbChMenuID
                                    )
                                }
                            }
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