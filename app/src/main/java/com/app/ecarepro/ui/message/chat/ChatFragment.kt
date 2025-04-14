package com.app.ecarepro.ui.message.chat

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.app.ecarepro.attachment
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.databinding.FragmentChatBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.receiverChatMessage
import com.app.ecarepro.recipientChip
import com.app.ecarepro.senderChatMessage
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.ui.message.compose.ComposeUiState
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant.Companion.boldFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.boldFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindStartIndexes
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.FileClickListener
import com.app.ecarepro.utils.ImageCompressionHelper
import com.app.ecarepro.utils.imageUrl
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.utils.isAudioUrl
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.lassi.data.media.MiMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var imageCompressionHelper: ImageCompressionHelper
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by viewModels()
    private var lastClickAttachmentType: AttachmentType? = null


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

    private fun handleAttachmentTypes(messageSettings: MessageSettings?) {
        messageSettings?.let { settings ->
            binding.btnCamera.isVisible = settings.media?.browseImg == true
            binding.btnGallery.isVisible = settings.media?.browseImg == true
            binding.btnRecord.isVisible = settings.media?.browseAudio == true
            binding.btnBrowseAudio.isVisible = settings.media?.browseAudio == true
            binding.btnBrowsePdf.isVisible = settings.media?.browsePDF == true
        }
    }

    private fun buildAttachmentModels(attachments: List<MiMedia>) {
        binding.attachments.isVisible = attachments.isNotEmpty()
        binding.attachments.withModels {
            attachments.forEachIndexed { index, attachment ->
                attachment {
                    id(index)
                    image(attachment.path)
                    onClickRemove { _ ->
                        chatViewModel.removeAttachment(attachment)
                    }
                }
            }
        }
    }


    private fun setUpViews() {
        imageCompressionHelper = ImageCompressionHelper(requireContext())
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRecipient.setOnClickListener {
            showRecipients()
        }

        binding.btnReplyMessage.setOnClickListener {
            mainActivity().showLoader(true)
            chatViewModel.replyMessage { success, message ->
                mainActivity().showLoader(false)
                mainActivity().showMessage(message)
                if(success){
                    chatViewModel.clearAttachment()
                }
            }
        }

        binding.btnAddAttachment.setOnClickListener {
            binding.cardAttachmentOptions.isVisible = binding.cardAttachmentOptions.isVisible.not()
        }

        binding.btnBrowsePdf.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.PDF
            launchPicker()
        }

        binding.btnBrowseAudio.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.AUDIO
            launchPicker()
        }



        binding.btnGallery.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.GALLERY
            // Request necessary permissions and open the gallery
            if (checkAndRequestPermissions()) {
                // Permission is already granted, start image picker
                launchPicker()
            }
            // requestExternalStoragePermission()
        }

        binding.btnRecord.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.AUDIO
            openAudioRecorder()
        }
        FileAccess.checkPermission(this@ChatFragment)
        binding.btnCamera.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.CAMERA
            FileAccess.checkPermission(this@ChatFragment)
            if(checkCameraPermissions()){
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    cameraLauncher.launch(FileAccess.cameraIntent())
                }
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


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                val file = File(requireContext().cacheDir, UUID.randomUUID().toString() + ".png")
                file.writeBitmap(
                    bitmap, Bitmap.CompressFormat.PNG, 100
                )
                chatViewModel.setAttachments(listOf(MiMedia(path = file.absolutePath)))
            }
        }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }


    private fun checkAndRequestPermissions(): Boolean {
        val permissionList = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        return if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionList.toTypedArray(),
                1001
            )
            false
        } else {
            true
        }
    }
    private fun checkCameraPermissions(): Boolean {
        val permissionList = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.CAMERA)
        }

        return if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), permissionList.toTypedArray(), 1001
            )
            false
        } else {
            true
        }
    }
    private fun openAudioRecorder() {

        VoiceSenderDialog(object : AudioRecordListener {
            override fun onAudioReady(audioUri: String?) {
                chatViewModel.setAttachments(
                    listOf(
                        MiMedia(
                            path = audioUri,
                            name = AttachmentType.RECORDING.name
                        )
                    )
                )
            }

            override fun onReadyForRecord() {}

            override fun onRecordFailed(errorMessage: String?) {
                mainActivity().showMessage(errorMessage ?: "")
            }
        }).show(childFragmentManager, "VOICE")
    }

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImages = mutableListOf<Uri>()
                result.data?.let { data ->
                    val clipData = data.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            if (selectedImages.size < 7) {
                                val imageUri = clipData.getItemAt(i).uri
                                selectedImages.add(imageUri)
                            }
                        }

                        // Check if total size exceeds the limit
                        if (imageCompressionHelper.exceedsPayloadLimit(selectedImages)) {
                            // Show compression dialog
                            imageCompressionHelper.showCompressionDialog(
                                parentFragmentManager,
                                selectedImages
                            ) { compressionOption ->
                                // Process images with selected compression
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val compressedUris = withContext(Dispatchers.IO) {
                                        selectedImages.map { uri ->
                                            imageCompressionHelper.compressImage(
                                                uri,
                                                compressionOption
                                            )
                                        }
                                    }

                                    // Now we have compressed images, upload them
                                    chatViewModel.setAttachments(compressedUris.map {
                                        MiMedia(
                                            path = it.toString(),
                                            name = lastClickAttachmentType?.name
                                        )
                                    })
                                }
                            }
                        } else {
                            /*  // Process images normally (still might want to compress slightly)
                               composeViewModel.setAttachments(files)*/
                            chatViewModel.setAttachments(selectedImages.map {
                                MiMedia(
                                    path = it.toString(),
                                    name = lastClickAttachmentType?.name
                                )
                            })
                        }
                    } else {
                        data.data?.let { imageUri ->
                            if (selectedImages.size < 7) {
                                selectedImages.add(imageUri)
                            }
                        }
                        chatViewModel.setAttachments(selectedImages.map {
                            MiMedia(
                                path = it.toString(),
                                name = lastClickAttachmentType?.name
                            )
                        })
                    }

                }
            }
        }


    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        pickImagesLauncher.launch(Intent.createChooser(intent, "Select Image(s)"))
    }

    private fun launchPicker() {
        when (lastClickAttachmentType) {
            AttachmentType.GALLERY -> {
                openGallery()
            }
            /* AttachmentType.GALLERY -> {
                 // Request necessary permissions and open the gallery
                 if (checkAndRequestPermissions()) {
                     launchPhotoPicker()
                 }
                 //  launchPhotoPicker()
             }*/

            AttachmentType.AUDIO -> {
                launchAudioPicker()
            }

            AttachmentType.PDF -> {
                launchPdfPicker()
            }

            else -> {}
        }
    }

    private fun launchAudioPicker() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfLauncher.launch(intent)
    }

    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        chatViewModel.setAttachments(
                            listOf(
                                MiMedia(
                                    path = mImageUri.toString(),
                                    name = lastClickAttachmentType?.name
                                )
                            )
                        )
                    } else {
                        if (data.clipData != null) {
                            val count: Int = data.clipData!!.itemCount
                            val files = mutableListOf<MiMedia>()
                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                                files.add(
                                    MiMedia(
                                        path = imageUri.toString(),
                                        name = lastClickAttachmentType?.name
                                    )
                                )
                            }
                            chatViewModel.setAttachments(files)
                        }
                    }
                }
            }
        }

    private fun launchPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow any file type
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pdfLauncher.launch(intent)
    }

    private fun hideAttachmentCard() {
        binding.cardAttachmentOptions.isVisible = false
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
           /* handleAttachmentTypes(uiState.messageSettings)
            buildAttachmentModels(uiState.attachments)*/
            binding.recyclerView.withModels {
                when (uiState) {
                    ChatUiState.EmptyInbox -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                            binding.toolbar.title = "Message"
                        }
                    }
                    is ChatUiState.Success -> {
                      //  handleAttachmentTypes(uiState.messageSettings)
                        buildAttachmentModels(uiState.attachments)
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

        if (binding.tvSubject.text.toString() != "") {
            val ssb = SpannableStringBuilder(binding.tvSubject.text)


            try {
                var cs: CharacterStyle


                val sentence: String = binding.tvSubject.text.toString()

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

                binding.tvSubject.text = ssb


                val ssbbb = SpannableStringBuilder(binding.tvSubject.text)

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