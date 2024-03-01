package com.app.ecarepro.ui.message.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentInboxFragmentBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.recentMessageCard
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.E_MMM_DD_YYYY_HH_MM_A
import com.app.ecarepro.utils.HH_MM_A
import com.app.ecarepro.utils.formatDate
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InboxMessageFragment : Fragment() {

    private var _binding: FragmentInboxFragmentBinding? = null

    private val binding get() = _binding!!

    private val inboxMessageViewModel: InboxMessageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInboxFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            inboxMessageViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun setUpViews() {
        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }

    }

    private fun handleUiState(uiState: InboxMessageUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is InboxMessageUiState.Success || uiState == InboxMessageUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    InboxMessageUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is InboxMessageUiState.Success -> {
                        uiState.messages.forEach { message ->
                            recentMessageCard {
                                id(message.id)
                                name(message.name)
                                designation(message.designation)
                                photo(message.photo)
                                date(message.sentOn)
                                time(formatDate(message.sentOn, E_MMM_DD_YYYY_HH_MM_A, HH_MM_A))
                                unReadMessageCount(message.unread)
                                clickListener { _ ->
                                    findNavController().navigate(
                                        R.id.conversationFragment,
                                        bundleOf("ID" to message.id)
                                    )
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