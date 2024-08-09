package com.app.ecarepro.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentFeedsBinding
import com.app.ecarepro.feedCard
import com.app.ecarepro.loadMoreView
import com.app.ecarepro.model.Feed
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.PaginationScrollListener
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.noDataFoundView


@AndroidEntryPoint
class FeedsFragment : Fragment() {

    private var _binding: FragmentFeedsBinding? = null

    private val binding get() = _binding!!

    private val feedsViewModel: FeedsViewModel by viewModels()

    private val systemViewModel: SystemViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            feedsViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun setUpViews() {
       /* binding.toolbar.setNavigationOnClickListener {
            systemViewModel.navigateBack(true)
        }*/

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            feedsViewModel.refresh()
        }
        binding.recyclerView.apply {
            /*addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )*/

            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    feedsViewModel.loadNextPage()
                }

                override val totalPageCount: Int
                    get() = feedsViewModel.totalPageCount()
                override val isLastPage: Boolean
                    get() = feedsViewModel.isLastPage()
                override val isLoading: Boolean
                    get() = feedsViewModel.isLoading()
            })
        }

    }

    private fun handleUiState(uiState: FeedsUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is FeedsUiState.Success || uiState == FeedsUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    FeedsUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is FeedsUiState.Success -> {
                        uiState.feeds.forEach { feed: Feed ->
                            feedCard {
                                id(feed.id)
                                feed(feed)
                                clickListener { _->
                                    (requireActivity() as MainActivity).getFragmentId(
                                        feed.menuID, feed.chMenuID
                                    )
                                }
                            }
                        }

                        if (uiState.showLoadMoreView || uiState.loadMoreError != null) {
                            loadMoreView {
                                id(R.id.load_more_view)
                                isLoading(uiState.showLoadMoreView)
                                errorMessage(uiState.loadMoreError?.message)
                                onClickRetry { _ ->
                                    feedsViewModel.loadNextPage(true)
                                }
                            }
                        }

                    }

                    else -> {}
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}