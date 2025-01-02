package com.app.ecarepro.ui.message.chat

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.databinding.FragmentChatBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.receiverChatMessage
import com.app.ecarepro.senderChatMessage
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant.Companion.boldFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.boldFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindStartIndexes
import com.app.ecarepro.utils.FileClickListener
import com.app.ecarepro.utils.imageUrl
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.utils.isAudioUrl


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
                mainActivity().showMessage(message)
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
            mainActivity().showMessage(error.message ?: "")
        }
        if (uiState is ChatUiState.Success || uiState == ChatUiState.EmptyInbox) {
            binding.recyclerView.withModels {
                when (uiState) {
                    ChatUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                            binding.toolbar.title = "Message"
                        }
                    }
                    is ChatUiState.Success -> {
                        uiState.senderDTL?.let {
                            setUpToolbar(it)
                        }
                        binding.toolbar.title = "Message"
                        binding.tvSubject.text = "Sub: ${uiState.subject}"
                        setUpFontStyle(binding)
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
    private fun setUpToolbar(sender: Sender) {
        if (chatViewModel.messageType==MessageType.INBOX.value){
            binding.apply {
                headerView.isVisible = true
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
        }else{
            binding.headerView.isVisible =false
            binding.toolbar.setTitle("Message")
        }

    }
    private fun openPhoto(photo: String?) {
        if (photo.isNullOrEmpty()) return
        if (isPdfUrl(photo)) {
            openPdfFromUrl(photo)
        } else if (isAudioUrl(photo)) {
            openPdfFromUrl(photo)
        } else {
            try {
                findNavController().navigate(
                    R.id.photoViewFragmentFragment,
                    bundleOf(PhotoViewFragmentFragment.PHOTO to photo)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun openPdfFromUrl(url: String) {
        if(isAdded){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val chooser = Intent.createChooser(intent, "Choose an app to open with")
            startActivity(chooser)
        }
    }

    fun isPdfUrl(url: String): Boolean {
        val pdfExtension = "pdf"
        val doc = "doc"
        val docx = "docx"
        val extension = url.substringAfterLast(".", "").lowercase()
        return pdfExtension == extension || doc == extension || docx == extension
    }

   /* fun isAudioUrl(url: String): Boolean {
        val audioExtensions = listOf("mp3", "wav", "ogg", "flac", "aac", "m4a")
        val extension = url.substringAfterLast(".", "").lowercase()
        return audioExtensions.contains(extension)
    }
*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun setUpFontStyle(binding: FragmentChatBinding) {

        if (binding.tvSubject.getText().toString() != "") {
            val ssb = SpannableStringBuilder(binding.tvSubject.getText())


            try {
                var cs: CharacterStyle


                val sentence: String = binding.tvSubject.getText().toString()

                val boldStartIndexes: List<Int> = boldFindStartIndexes(sentence)
                val boldEndIndexes: List<Int> = boldFindEndStarIndexes(sentence)


                Log.v("Okkkkk", "Word Start Indexes BOLD : $boldStartIndexes")
                Log.v("Okkkkk", "Word End Indexes BOLD : $boldEndIndexes")


                var boldstart = 0
                var boldend = 0
                var deleteIndesx = 0
                if (boldStartIndexes.size >= 1 && boldEndIndexes.size >= 1) {
                    for (i in boldStartIndexes.indices) {
                        boldstart = boldStartIndexes[i]
                        for (j in i until boldEndIndexes.size) {
                            boldend = boldEndIndexes[j]
                            cs = StyleSpan(Typeface.BOLD)
                            ssb.setSpan(cs, boldstart, boldend, 1)
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssb)


                val ssbbb = SpannableStringBuilder(binding.tvSubject.getText())

                var dboldstart = 0
                var dboldend = 0
                if (boldStartIndexes.size >= 1 && boldEndIndexes.size >= 1) {
                    for (i in boldStartIndexes.indices) {
                        dboldstart = boldStartIndexes[i]
                        for (j in i until boldEndIndexes.size) {
                            dboldend = boldEndIndexes[j]
                            ssbbb.delete(dboldstart - deleteIndesx, dboldstart - deleteIndesx + 1)
                            ssbbb.delete(dboldend - deleteIndesx - 1, dboldend - deleteIndesx)
                            deleteIndesx = deleteIndesx + 2
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssbbb)


                val ssbbbitalic = SpannableStringBuilder(binding.tvSubject.getText())

                val sentenceit: String = binding.tvSubject.getText().toString()

                val italicStartIndexes: List<Int> = italicFindStartIndexes(sentenceit)
                val italicEndIndexes: List<Int> = italicFindEndStarIndexes(sentenceit)

                Log.v("Okkkkk", "Word Start Indexes Italic : $italicStartIndexes")
                Log.v("Okkkkk", "Word End Indexes Italic : $italicEndIndexes")


                var italicstart = 0
                var italicdend = 0
                if (italicStartIndexes.size >= 1 && italicEndIndexes.size >= 1) {
                    for (i in italicStartIndexes.indices) {
                        italicstart = italicStartIndexes[i]
                        for (j in i until italicEndIndexes.size) {
                            italicdend = italicEndIndexes[j]
                            cs = StyleSpan(Typeface.ITALIC)
                            ssbbbitalic.setSpan(cs, italicstart, italicdend, 1)
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssbbbitalic)


                val ssbbbitalicDelte = SpannableStringBuilder(binding.tvSubject.getText())


                var itlicDeleteIndesx = 0
                var ditalicstart = 0
                var ditalicdend = 0
                if (italicStartIndexes.size >= 1 && italicEndIndexes.size >= 1) {
                    for (i in italicStartIndexes.indices) {
                        ditalicstart = italicStartIndexes[i]
                        for (j in i until italicEndIndexes.size) {
                            ditalicdend = italicEndIndexes[j]
                            ssbbbitalicDelte.delete(
                                ditalicstart - itlicDeleteIndesx,
                                ditalicstart - itlicDeleteIndesx + 1
                            )
                            ssbbbitalicDelte.delete(
                                ditalicdend - itlicDeleteIndesx - 1,
                                ditalicdend - itlicDeleteIndesx
                            )
                            itlicDeleteIndesx = itlicDeleteIndesx + 2
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssbbbitalicDelte)

                val ssbbbitalicstrikethrough = SpannableStringBuilder(binding.tvSubject.getText())

                val sentenceStric: String = binding.tvSubject.getText().toString()

                val strikethroughStartIndexes: List<Int> =
                    strikethroughFindStartIndexes(sentenceStric)
                val strikethroughEndIndexes: List<Int> =
                    strikethroughFindEndStarIndexes(sentenceStric)

                Log.v(
                    "Okkkkk",
                    "Word Start Indexes strikethrough : $strikethroughStartIndexes"
                )
                Log.v("Okkkkk", "Word End Indexes strikethrough : $strikethroughEndIndexes")

                var strikethroughstart = 0
                var strikethroughend = 0
                if (strikethroughStartIndexes.size >= 1 && strikethroughEndIndexes.size >= 1) {
                    for (i in strikethroughStartIndexes.indices) {
                        strikethroughstart = strikethroughStartIndexes[i]
                        for (j in i until strikethroughEndIndexes.size) {
                            strikethroughend = strikethroughEndIndexes[j]
                            cs = UnderlineSpan()
                            ssbbbitalicstrikethrough.setSpan(
                                cs,
                                strikethroughstart,
                                strikethroughend,
                                1
                            )
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssbbbitalicstrikethrough)

                val ssbbbitalicstrikethroughDelete =
                    SpannableStringBuilder(binding.tvSubject.getText())


                var strikethroughstartDeleteIndex = 0
                var strikethroughstartDelete = 0
                var strikethroughendDelete = 0
                if (strikethroughStartIndexes.size >= 1 && strikethroughEndIndexes.size >= 1) {
                    for (i in strikethroughStartIndexes.indices) {
                        strikethroughstartDelete = strikethroughStartIndexes[i]
                        for (j in i until strikethroughEndIndexes.size) {
                            strikethroughendDelete = strikethroughEndIndexes[j]
                            ssbbbitalicstrikethroughDelete.delete(
                                strikethroughstartDelete - strikethroughstartDeleteIndex,
                                strikethroughstartDelete - strikethroughstartDeleteIndex + 1
                            )
                            ssbbbitalicstrikethroughDelete.delete(
                                strikethroughendDelete - strikethroughstartDeleteIndex - 1,
                                strikethroughendDelete - strikethroughstartDeleteIndex
                            )
                            strikethroughstartDeleteIndex = strikethroughstartDeleteIndex + 2
                            break
                        }
                    }
                }

                binding.tvSubject.setText(ssbbbitalicstrikethroughDelete)
            } catch (ignored: Exception) {
            }
        }
    }

}