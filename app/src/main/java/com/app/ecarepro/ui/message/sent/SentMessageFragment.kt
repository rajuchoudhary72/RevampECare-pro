package com.app.ecarepro.ui.message.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.SentMessage
import com.app.ecarepro.databinding.FragmentSentMessageBinding
import com.app.ecarepro.loadMoreView
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.sentMessageCard
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.MessageViewModel
import com.app.ecarepro.ui.message.chat.MessageType
import com.app.ecarepro.utils.PaginationScrollListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@AndroidEntryPoint
class SentMessageFragment : Fragment() {

    private var _binding: FragmentSentMessageBinding? = null
    private val binding get() = _binding!!

    private val sentMessageViewModel: SentMessageViewModel by viewModels()

    private val messageViewModel: MessageViewModel by activityViewModels()

    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSentMessageBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sentMessageViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            sentMessageViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                messageViewModel
                    .showDateRangePicker
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collectLatest {
                        pickDateRange()
                    }
            }

            launch {
                messageViewModel
                    .clearFilter
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collectLatest {
                        dateFrom.timeInMillis = Calendar.getInstance().timeInMillis
                        dateTo.timeInMillis = Calendar.getInstance().timeInMillis
                        sentMessageViewModel.updateDateFilter(null, null)
                    }
            }
        }
    }

    private fun pickDateRange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(dateFrom.timeInMillis, dateTo.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormate.format(Date(dateFrom.timeInMillis))
        val from = dateFormate.format(Date(dateFrom.timeInMillis))
        val to = dateFormate.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }

        if (setAsFilter) {
            sentMessageViewModel.updateDateFilter(from, to)
            messageViewModel.isFilterApplied.update { true }
        }
    }

    private fun setUpViews() {

        binding.apply {
            dateFrom.setOnClickListener { pickDateRange() }
            dateTo.setOnClickListener { pickDateRange() }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            sentMessageViewModel.refresh()
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
                    sentMessageViewModel.loadNextPage()
                }

                override val totalPageCount: Int
                    get() = sentMessageViewModel.totalPageCount()
                override val isLastPage: Boolean
                    get() = sentMessageViewModel.isLastPage()
                override val isLoading: Boolean
                    get() = sentMessageViewModel.isLoading()

            })
        }

    }

    private fun handleUiState(uiState: SentMessageUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is SentMessageUiState.Success || uiState == SentMessageUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    SentMessageUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is SentMessageUiState.Success -> {
                        uiState.messages.forEach { message: SentMessage ->
                            sentMessageCard {
                                id(message.id)
                                name(message.subject)
                                canDelete(message.canDelete)
                                recipients(
                                    if (message.recipients.isNullOrEmpty()) {
                                        null
                                    } else {
                                        if (message.recipients.size > 1) {
                                            "To:${message.recipients.first().name} and ${
                                                message.recipients.size.minus(
                                                    1
                                                )
                                            } more"
                                        } else {
                                            "To:${message.recipients.first().name}"
                                        }
                                    }
                                )
                                photo(message.recipients?.firstOrNull()?.photo)
                                abbreviation(message.abbreviation)
                                date(message.sentOn)
                                clickListener { _ ->
                                    findNavController().navigate(
                                        R.id.chatFragment,
                                        bundleOf(
                                            "ID" to message.id,
                                            "MessageType" to MessageType.SENT.value
                                        )
                                    )
                                }
                                deleteMessageListener { _ ->
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Delete Message")
                                        .setMessage("Are you sure to delete this message?")
                                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                            sentMessageViewModel.deleteMessage(message.id) { isLoading, msg ->
                                                mainActivity().showLoader(isLoading)
                                                msg?.let {
                                                    mainActivity().showMessage(it)
                                                }
                                            }
                                        }
                                        .setNegativeButton(getString(R.string.no)) { _, _ -> }
                                        .show()
                                }
                            }
                        }

                        if (uiState.showLoadMoreView || uiState.loadMoreError != null) {
                            loadMoreView {
                                id(R.id.load_more_view)
                                isLoading(uiState.showLoadMoreView)
                                errorMessage(uiState.loadMoreError?.message)
                                onClickRetry { _ ->
                                    sentMessageViewModel.loadNextPage(true)
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        sentMessageViewModel.sendScreenEvent()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}