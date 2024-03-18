package com.app.ecarepro.ui.message.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentChatBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.receiverChatMessage
import com.app.ecarepro.senderChatMessage
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.FileClickListener
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = chatViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun setUpViews() {

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRecipient.setOnClickListener {
            showRecipients()
        }

        binding.btnReplyMessage.setOnClickListener {
            chatViewModel.replyMessage { _, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            chatViewModel.refresh()
        }
        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }

    }

    private fun showRecipients() {
        RecipientsDialog.getInstance(
            (chatViewModel.uiState.value as ChatUiState.Success).recipients
        )
            .show(childFragmentManager, "")
    }

    private fun handleUiState(uiState: ChatUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is ChatUiState.Success || uiState == ChatUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    ChatUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is ChatUiState.Success -> {
                        binding.tvSubject.text = "Sub: ${uiState.subject}"
                        binding.sendMessageLayout.isVisible = uiState.canReply ?: false
                        binding.btnRecipient.isVisible = uiState.recipients.isNullOrEmpty().not()
                        uiState.messages.forEach { message ->
                            if (message.isMine) {
                                senderChatMessage {
                                    id(message.msgID.toString() + message.body + message.sentOn)
                                    message(message.body)
                                    date(message.sentOn)
                                    image(
                                        if ((message.filePaths?.size
                                                ?: 0) == 1
                                        ) message.filePaths?.firstOrNull() else null
                                    )
                                    onClickPhoto(object : FileClickListener {
                                        override fun onClick(file: String) {
                                            openPhoto(file)
                                        }
                                    })
                                    files(message.filePaths ?: emptyList())
                                }
                            } else {
                                receiverChatMessage {
                                    id(message.msgID.toString() + message.body + message.sentOn)
                                    message(message.body)
                                    date(message.sentOn)
                                    files(message.filePaths ?: emptyList())
                                    image(
                                        if ((message.filePaths?.size
                                                ?: 0) == 1
                                        ) message.filePaths?.firstOrNull() else null
                                    )
                                    onClickPhoto(object : FileClickListener {
                                        override fun onClick(file: String) {
                                            openPhoto(file)
                                        }
                                    })
                                }
                            }
                        }
                        viewLifecycleOwner.lifecycleScope.launch {
                            delay(500)
                            binding.recyclerView.smoothScrollToPosition(uiState.messages.size)
                        }

                    }

                    else -> {}
                }
            }
        }
    }

    private fun openPhoto(photo: String?) {
        findNavController().navigate(
            R.id.photoViewFragmentFragment,
            bundleOf(PhotoViewFragmentFragment.PHOTO to photo)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}