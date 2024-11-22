package com.app.ecarepro.ui.message.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.conversation
import com.app.ecarepro.data.network.model.Conversation
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.databinding.FragmentConversationBinding
import com.app.ecarepro.loadMoreView
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.PaginationScrollListener
import com.app.ecarepro.utils.imageUrl
import com.app.ecarepro.utils.stringFormat2String
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConversationFragment : Fragment() {

    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!

    private val conversationViewModel: ConversationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = conversationViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            conversationViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun setUpViews() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            conversationViewModel.refresh()
        }

        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    conversationViewModel.loadNextPage()
                }

                override val totalPageCount: Int
                    get() = conversationViewModel.totalPageCount()
                override val isLastPage: Boolean
                    get() = conversationViewModel.isLastPage()
                override val isLoading: Boolean
                    get() = conversationViewModel.isLoading()
            })
        }

    }

    private fun handleUiState(uiState: ConversationMessageUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is ConversationMessageUiState.Success || uiState == ConversationMessageUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    ConversationMessageUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is ConversationMessageUiState.Success -> {
                        setUpToolbar(uiState.sender)

                        uiState.messages.forEach { message: Conversation ->
                            conversation {
                                id(message.msgID)
                                abbreviation(message.abbreviation)
                                date(message.sentOn)
                                name(message.subject)
                                hasRead(message.hasRead)
                                msgTypeImageRes(
                                    if (message.msgType == 2) {
                                        R.drawable.ic_photo
                                    } else if (message.msgType == 3) {
                                        R.drawable.ic_audio
                                    } else if (message.msgType == 4) {
                                        R.drawable.ic_msg_type_sms
                                    }  else if (message.msgType == 5) {
                                        R.drawable.pdf
                                    } else {
                                        null
                                    }
                                )
                                msgType(
                                    if (message.msgType == 2) {
                                        "Photo"
                                    } else if (message.msgType == 3) {
                                        "Audio"
                                    } else if (message.msgType == 4) {
                                        "SMS"
                                    } else if (message.msgType == 5) {
                                        "PDF"
                                    } else {
                                        null
                                    }
                                )
                                clickListener { _ ->
                                    findNavController().navigate(
                                        R.id.chatFragment,
                                        bundleOf("ID" to message.id)
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
                                    conversationViewModel.loadNextPage(true)
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setUpToolbar(sender: Sender) {
        _binding?.apply {
            photo.imageUrl(
                sender.photo,
                ContextCompat.getDrawable(requireContext(), R.drawable.default_profile)
            )
            name.text = sender.name
            if (sender.senderType==3){
                designation.text = sender.designation
            }else  if (sender.senderType==1){
                designation.text = "Class :- "+ sender.className
            } else  if (sender.senderType==2){
                designation.text = "P/O  " + sender.childName+" , "+ sender.className
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}