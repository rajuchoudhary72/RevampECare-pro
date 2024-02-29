package com.app.ecarepro.ui.message.sent

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
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.SentMessage
import com.app.ecarepro.databinding.FragmentSentMessageBinding
import com.app.ecarepro.sentMessageCard
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.message.MessageViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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
        _binding = FragmentSentMessageBinding.inflate(inflater, container, false)
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
            messageViewModel
                .showDateRangePicker
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    pickDateRange()
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
        val from = "${dateFrom.get(Calendar.DAY_OF_MONTH)} ${dateFrom.get(Calendar.MONTH)} ${
            dateFrom.get(Calendar.YEAR)
        }"

        val to = "${dateTo.get(Calendar.DAY_OF_MONTH)} ${dateTo.get(Calendar.MONTH)} ${
            dateTo.get(Calendar.YEAR)
        }"
        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }

        if (setAsFilter)
            sentMessageViewModel.updateDateFilter(from, to)
    }

    private fun setUpViews() {
        updateDateFilterText()
        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }

    }

    private fun handleUiState(uiState: SentMessageUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is SentMessageUiState.Success || uiState == SentMessageUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    SentMessageUiState.EmptyInbox -> {}
                    is SentMessageUiState.Success -> {
                        uiState.messages.forEach { message: SentMessage ->

                            sentMessageCard {
                                id(message.id)
                                name(message.subject)
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