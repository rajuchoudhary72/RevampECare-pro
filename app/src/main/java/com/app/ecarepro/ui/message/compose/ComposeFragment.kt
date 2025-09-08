package com.app.ecarepro.ui.message.compose

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.Spannable
import com.app.ecarepro.data.network.model.MessageSettings
import android.app.ProgressDialog
import android.os.Environment

import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.attachment
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.ContactsDto
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.network.model.Template
import com.app.ecarepro.databinding.FragmentComposeBinding
import com.app.ecarepro.recipientChip
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.selectRecipients.ScholarType
import com.app.ecarepro.ui.message.selectRecipients.SelectRecipientsFragment
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.FileUtils
import com.app.ecarepro.utils.ImageCompressionHelper
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.domain.media.SortingOption
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


@AndroidEntryPoint
class ComposeFragment : Fragment() {

    private var _binding: FragmentComposeBinding? = null
    private val binding get() = _binding!!
    private var lvalue = "null"

    private var isFormatd = false
    private lateinit var imageCompressionHelper: ImageCompressionHelper
    private val composeViewModel: ComposeViewModel by viewModels()
    private var capturedImageFile: File? = null
    private var capturedImageUri: Uri? = null
    private var lastClickAttachmentType: AttachmentType? = null
    private val fileUtils: FileUtils by lazy { FileUtils(requireContext()) }

    private val mPermissionSettingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requestExternalStoragePermission()
        }


    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (selectedMedia.isNotEmpty()) {
                    composeViewModel.setAttachments(selectedMedia)
                }
            }
        }

    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    if (data.data != null) {
                        val mImageUri: Uri = data.data!!
                        composeViewModel.setAttachments(
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
                            composeViewModel.setAttachments(files)
                        }
                    }
                }
            }
        }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentComposeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = composeViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeViewModel.fetchMessageSettings()
        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            composeViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState: ComposeUiState ->
                handleUiState(uiState)
            }
        }

        setUpFontStyle()
    }

    private fun setUpFontStyle() {


        binding.message.customSelectionActionModeCallback = StyleCallback()

        binding.message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //                if (et_reply.getText().toString().trim().length() > 0) {
//                    if (iv_post_reply2.getVisibility() == GONE) {
//                        iv_post_reply2.setVisibility(VISIBLE);
//                    }
//                    if (iv_post_reply.getVisibility() == VISIBLE) {
//                        iv_post_reply.setVisibility(GONE);
//                    }
//                } else {
//                    if (iv_post_reply2.getVisibility() == VISIBLE) {
//                        iv_post_reply2.setVisibility(GONE);
//                    }
//                    if (iv_post_reply.getVisibility() == GONE) {
//                        iv_post_reply.setVisibility(VISIBLE);
//                    }
//                }
            }

            override fun afterTextChanged(s: Editable) {
                val ssb: Spannable = SpannableStringBuilder(binding.message.text)

                if (lvalue != binding.message.text.toString()) {
                    try {
                        val spans: Array<out StyleSpan>? =
                            ssb.getSpans(0, ssb.length, StyleSpan::class.java)
                        for (styleSpan in spans!!) ssb.removeSpan(styleSpan)


                        val spans2: Array<out UnderlineSpan>? = ssb.getSpans(
                            0, ssb.length,
                            UnderlineSpan::class.java
                        )
                        for (styleSpan in spans2!!) ssb.removeSpan(styleSpan)

                        val spans3: Array<out ForegroundColorSpan>? = ssb.getSpans(
                            0, ssb.length,
                            ForegroundColorSpan::class.java
                        )
                        for (styleSpan in spans3!!) ssb.removeSpan(styleSpan)


                        var cs: CharacterStyle
                        var cs1: CharacterStyle
                        var cs2: CharacterStyle

                        val sentence: String = binding.message.text.toString()
                        val boldStartIndexes: List<Int> =
                            boldFindStartIndexes(
                                sentence
                            )
                        val boldEndIndexes: List<Int> =
                            boldFindEndStarIndexes(
                                sentence
                            )

                        val italicStartIndexes: List<Int> =
                            italicFindStartIndexes(
                                sentence
                            )
                        val italicEndIndexes: List<Int> =
                            italicFindEndStarIndexes(
                                sentence
                            )

                        val strikethroughStartIndexes: List<Int> =
                            strikethroughFindStartIndexes(
                                sentence
                            )
                        val strikethroughEndIndexes: List<Int> =
                            strikethroughFindEndStarIndexes(
                                sentence
                            )


                        /* List<Integer> allStartIndexes = AllStartIndexes(sentence);
                    List<Integer> allEndIndexes = AllEndIndexes(sentence);*/
                        Log.v("Okkkkk", "Word Start Indexes BOLD : $boldStartIndexes")
                        Log.v("Okkkkk", "Word End Indexes BOLD : $boldEndIndexes")

                        Log.v("Okkkkk", "Word Start Indexes Italic : $italicStartIndexes")
                        Log.v("Okkkkk", "Word End Indexes Italic : $italicEndIndexes")

                        Log.v(
                            "Okkkkk",
                            "Word Start Indexes strikethrough : $strikethroughStartIndexes"
                        )
                        Log.v(
                            "Okkkkk",
                            "Word End Indexes strikethrough : $strikethroughEndIndexes"
                        )

                        /*Log.v("Okkkkk", "Word Start Indexes All : " + allStartIndexes);
                    Log.v("Okkkkk", "Word End Indexes All : " + allEndIndexes);*/
                        isFormatd = false


                        var boldstart = 0
                        var boldend = 0
                        if (boldStartIndexes.size >= 1 && boldEndIndexes.size >= 1) {
                            for (i in boldStartIndexes.indices) {
                                boldstart = boldStartIndexes[i]
                                for (j in i until boldEndIndexes.size) {
                                    boldend = boldEndIndexes[j]
                                    cs = StyleSpan(Typeface.BOLD)
                                    cs1 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    cs2 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    ssb.setSpan(cs, boldstart, boldend, 1)
                                    ssb.setSpan(cs1, boldstart, boldstart + 1, 1)
                                    ssb.setSpan(cs2, boldend, boldend + 1, 1)
                                    isFormatd = true
                                    lvalue = binding.message.text.toString()
                                    break
                                }
                            }
                        }

                        var italicstart = 0
                        var italicdend = 0
                        if (italicStartIndexes.size >= 1 && italicEndIndexes.size >= 1) {
                            for (i in italicStartIndexes.indices) {
                                italicstart = italicStartIndexes[i]
                                for (j in i until italicEndIndexes.size) {
                                    italicdend = italicEndIndexes[j]
                                    cs = StyleSpan(Typeface.ITALIC)
                                    cs1 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    cs2 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    ssb.setSpan(cs, italicstart, italicdend, 1)
                                    ssb.setSpan(cs1, italicstart, italicstart + 1, 1)
                                    ssb.setSpan(cs2, italicdend, italicdend + 1, 1)
                                    isFormatd = true
                                    lvalue = binding.message.text.toString()
                                    break
                                }
                            }
                        }

                        var strikethroughstart = 0
                        var strikethroughend = 0
                        if (strikethroughStartIndexes.size >= 1 && strikethroughEndIndexes.size >= 1) {
                            for (i in strikethroughStartIndexes.indices) {
                                strikethroughstart = strikethroughStartIndexes[i]
                                for (j in i until strikethroughEndIndexes.size) {
                                    strikethroughend = strikethroughEndIndexes[j]
                                    cs = UnderlineSpan()
                                    cs1 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    cs2 =
                                        ForegroundColorSpan(resources.getColor(R.color.light_text))
                                    ssb.setSpan(cs, strikethroughstart, strikethroughend, 1)
                                    ssb.setSpan(cs1, strikethroughstart, strikethroughstart + 1, 1)
                                    ssb.setSpan(cs2, strikethroughend, strikethroughend + 1, 1)
                                    isFormatd = true
                                    lvalue = binding.message.text.toString()
                                    break
                                }
                            }
                        }
                        lvalue = binding.message.text.toString()
                        val pos: Int = binding.message.selectionEnd
                        binding.message.setText(ssb)
                        binding.message.setSelection(pos)
                    } catch (e: Exception) {
                    }
                }
            }
        })

    }

    private fun handleUiState(uiState: ComposeUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        if (uiState is ComposeUiState.Success) {
            handleAttachmentTypes(uiState.messageSettings)
            buildAttachmentModels(uiState.attachments)
            buildChipGroup(uiState.contacts)
            setUpSmsTypes(uiState.smsTypes)
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
    private fun setUpSmsTypes(smsTypes: List<SmsType>) {
        binding.spinnerSmsTypeLayout.isVisible = smsTypes.isNotEmpty()
        if (smsTypes.isEmpty()) return
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            smsTypes.map { it.subject ?: "" },
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSmsType.apply {
                this.adapter = adapter
                if (composeViewModel.smsType == null) {
                    composeViewModel.smsType = smsTypes.first()
                    setSelection(0)
                } else {
                    setSelection(smsTypes.indexOf(composeViewModel.smsType))
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        composeViewModel.smsType = smsTypes[p2]
                        setUpTemplates(smsTypes[p2].templates ?: emptyList())
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }
            }
        }

        setUpTemplates(smsTypes.first().templates ?: emptyList())
    }

    private fun setUpTemplates(templates: List<Template>) {
        binding.spinnerTemplateLayout.isVisible = templates.isNotEmpty()
        if (templates.isEmpty()) return

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            templates.map { it.template ?: "" },
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_multiline_spinner_dropdown)
            binding.spinnerTemplate.apply {
                this.adapter = adapter
                if (composeViewModel.template == null) {
                    composeViewModel.template = templates.first()
                    binding.message.setText(templates.first().template)
                    setSelection(0)
                } else {
                    val index = templates.indexOf(composeViewModel.template)
                    if (index != -1) {
                        binding.message.setText(templates[index].template)
                        setSelection(index)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        composeViewModel.template = templates[p2]
                        binding.message.setText(templates[p2].template)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }
            }
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
                        composeViewModel.removeAttachment(attachment)
                    }
                }
            }
        }
    }

    private fun setUpViews() {
        binding.btnAddAttachment.bringToFront()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        imageCompressionHelper = ImageCompressionHelper(requireContext())

        binding.btnReplyMessage.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)

            composeViewModel.sendMessage { isSuccess, message ->
                (requireActivity() as MainActivity).showLoader(false)
                mainActivity().showMessage(message)
                if (isSuccess) {
                    findNavController().popBackStack()
                }
            }
        }

        binding.btnAddRecipient.setOnClickListener {

            setFragmentResultListener(SelectRecipientsFragment.SELECT_CONTACT_REQUEST_KEY) { requestKey, bundle ->
                if (bundle.containsKey(SelectRecipientsFragment.SELECTED_CONTACT)) {
                    val contacts: ContactsDto = bundle.getSerializable(SelectRecipientsFragment.SELECTED_CONTACT) as ContactsDto
                    val scholarType: ScholarType =
                        ScholarType.getScholarType(bundle.getInt(SelectRecipientsFragment.SCHOLAR_TYPE))
                    composeViewModel.setContacts(contacts.contacts)
                    composeViewModel.setScholarType(scholarType)
                }
            }

            findNavController().navigate(R.id.selectRecipientsFragment)
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
        FileAccess.checkPermission(this@ComposeFragment)
        binding.btnCamera.setOnClickListener {
            try {
                hideAttachmentCard()
                lastClickAttachmentType = AttachmentType.CAMERA
                FileAccess.checkPermission(this@ComposeFragment)

                val imageFile = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "${UUID.randomUUID()}.jpg"
                )
                capturedImageFile = imageFile

                val authority = "${requireContext().packageName}.myFileProvider"
                capturedImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    authority,
                    imageFile
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    cameraLauncher.launch(FileAccess.cameraIntent(capturedImageUri!!))
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                /* val bitmap = result.data?.extras?.get("data") as Bitmap
                 val file = File(requireContext().cacheDir, UUID.randomUUID().toString() + ".png")
                 file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 100)

                 composeViewModel.setAttachments(listOf(MiMedia(path = file.absolutePath)))*/


                capturedImageFile?.let { file ->
                    if (file.exists()) {
                        composeViewModel.setAttachments(
                            listOf(MiMedia(path = file.absolutePath))
                        )
                    }
                }
            }
        }

    private fun openAudioRecorder() {

        VoiceSenderDialog(object : AudioRecordListener {
            override fun onAudioReady(audioUri: String?) {
                composeViewModel.setAttachments(
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

    private fun hideAttachmentCard() {
        binding.cardAttachmentOptions.isVisible = false
    }

    private fun buildChipGroup(contacts: List<Contact>) {
        binding.recipientsRecyclerView.isVisible = contacts.isNotEmpty()
        binding.recipientsRecyclerView.withModels {
            contacts.forEach { contact ->
                recipientChip {
                    id(contact.receiverID)
                    text(contact.name)
                    image(contact.photo)
                    closeClickistener { _ ->
                        composeViewModel.removeContacts(contact)
                    }
                }
            }
        }
    }

    /**
     *   If Android device SDK is >= 30 and wants to access document (only for choose the non media file)
     */
    private fun requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                12342
            )
        } else {
            // Permission is already granted, start image picker
            launchPicker()
        }
    }

    private fun pickImage(type: String) {


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

    // Register to get the result of the image selection
    private val selectImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val clipData = result.data?.clipData
                if (clipData != null) {
                    // Multiple images selected
                    for (i in 0 until clipData.itemCount) {
                        val imageUri: Uri = clipData.getItemAt(i).uri
                        // Process each image URI here
                        println("Selected Image URI: $imageUri")
                    }
                } else {
                    // Single image selected
                    val imageUri: Uri? = result.data?.data
                    imageUri?.let {
                        println("Selected Single Image URI: $it")
                    }
                }
            }
        }




    /*with process base */
    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val selectedImages = mutableListOf<Uri>()

            // Request persistent permissions for all URIs
            uris.forEach { uri ->
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Add all selected URIs
            selectedImages.addAll(uris)

            // Check if total size exceeds the limit
            if (imageCompressionHelper.exceedsPayloadLimit(selectedImages)) {
                // Show compression dialog
                imageCompressionHelper.showCompressionDialog(
                    parentFragmentManager,
                    selectedImages
                ) { compressionOption ->
                    // Show progress dialog
                    val progressDialog = ProgressDialog(requireContext()).apply {
                        setMessage("Compressing images... 0/${selectedImages.size}")
                        setCancelable(false)
                        show()
                    }

                    // Process images with selected compression
                    viewLifecycleOwner.lifecycleScope.launch {
                        val compressedUris = mutableListOf<Uri>()

                        // Process each image individually to track progress
                        for (i in selectedImages.indices) {
                            val uri = selectedImages[i]

                            // Update progress message
                            withContext(Dispatchers.Main) {
                                progressDialog.setMessage("Compressing images... ${i+1}/${selectedImages.size}")
                            }

                            // Compress image in background
                            val compressedUri = withContext(Dispatchers.IO) {
                                imageCompressionHelper.compressImage(
                                    uri,
                                    compressionOption
                                )
                            }

                            compressedUris.add(compressedUri)
                        }

                        // Dismiss progress dialog
                        progressDialog.dismiss()

                        // Now we have compressed images, upload them
                        composeViewModel.setAttachments(compressedUris.map {
                            MiMedia(
                                path = it.toString(),
                                name = lastClickAttachmentType?.name
                            )
                        })
                    }
                }
            } else {
                // Process images normally without compression
                composeViewModel.setAttachments(selectedImages.map {
                    MiMedia(
                        path = it.toString(),
                        name = lastClickAttachmentType?.name
                    )
                })
            }
        }
    }

    private fun openGallery() {
        pickImagesLauncher.launch("image/*")
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

    private fun launchPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
           // type = "*/*" // Allow any file type
            type = "application/pdf" // More specific
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf",
                "application/msword",// .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",// .docx
              //  "application/vnd.ms-excel", // .xls
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                ))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pdfLauncher.launch(intent)
    }


    private fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION || requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermissions()
            } else {
                mainActivity().showMessage("Camera permission denied")
            }
        } else if (requestCode == 120) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationFetch()
            } else {
                mainActivity().showMessage("GPS permission denied")
            }
        } else if (requestCode == 12342) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start image picker
                launchPicker()
            } else {
                // Permission denied, show a message to the user
                mainActivity().showMessage("Permission denied, cannot pick image")
            }
        } else if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, start image picker
                launchPicker()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            val file = File(requireContext().cacheDir, UUID.randomUUID().toString() + ".png")
            file.writeBitmap(
                bitmap, Bitmap.CompressFormat.PNG, 100
            )
            composeViewModel.setAttachments(listOf(MiMedia(path = file.absolutePath)))
        }
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        startLocationFetch()
    }

    var locationPermissionDeniedDialogSeen = false

    private fun startLocationFetch() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Check if permission was denied before requesting
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    .not() && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION).not()
            ) {
                // Permission is denied permanently, show a dialog and guide to settings
                if (locationPermissionDeniedDialogSeen.not()) {
                    showPermissionDeniedDialog()
                    locationPermissionDeniedDialogSeen = true
                }
            } else {
                // Permission is denied temporarily, request it
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 120
                )

            }
        } else {
            if (isGPSEnabled().not()) {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Turn On GPS")
                    .setCancelable(false)
                    .setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setPositiveButton("No") { d, _ ->
                        d.dismiss()
                        findNavController().popBackStack()
                    }.setPositiveButton("Goto Settings, To Enable GPS") { d, _ ->
                        d.dismiss()
                        val callGPSSettingIntent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                        startActivity(callGPSSettingIntent)
                    }.show()
            } else {
                fusedLocationClient
                    .lastLocation
                    .addOnSuccessListener { location: Location? ->
                        composeViewModel.currentLocation =
                            Pair(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                    }
                    .addOnFailureListener {
                        Log.e("MSG", "startLocationFetch: " + it.message)

                    }
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.location_permission_required))
            .setMessage(getString(R.string.location_permission_is_required_to_send_your_current_location_please_enable_it_in_app_settings))
            .setPositiveButton(getString(R.string.settings)) { dialog, _ ->
                locationPermissionDeniedDialogSeen = false
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                requireActivity().onBackPressed()
            }
            .show()
    }


    private fun isGPSEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1003
        private const val REQUEST_CAMERA_PERMISSION = 1001
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1002
    }

    inner class StyleCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.custom_font, menu)
            menu.removeItem(android.R.id.selectAll)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            var cs: CharacterStyle
            val start: Int = binding.message.selectionStart
            val end: Int = binding.message.selectionEnd
            val ssb: Spannable = SpannableStringBuilder(binding.message.text)
            val star = "*"
            val underScore = "_"
            val stricketStart = "~"
            val stricketEnd = "~"
            val value: String = binding.message.text.toString().substring(start, end)

            when (item.itemId) {
                R.id.bold -> {
                    //et_reply.setText(Html.fromHtml(sourceString));
                    // et_reply.getText().insert(et_reply.getSelectionStart(), Html.fromHtml(sourceString));
                    /*et_reply.getText().replace(Math.min(start, end), Math.max(start, end),
                            Html.fromHtml(sourceString), 0, et_reply.length()+1);*/
                    //int startw =et_reply.getSelectionStart();//this is to get the the cursor position
                    val sourceString = "$star<b>$value</b>$star"
                    binding.message.text.replace(start, end, Html.fromHtml(sourceString))

                    return true
                }

                R.id.italic -> {
                    /*cs = new  StyleSpan(Typeface.ITALIC);
                    ssb.setSpan(cs, start, end, 1);
                    et_reply.setText(ssb);*/
                    val italicString = "$underScore<i>$value</i>$underScore"
                    binding.message.text.replace(start, end, Html.fromHtml(italicString))
                    return true
                }

                R.id.underline -> {
                    /* cs = new UnderlineSpan();
                    ssb.setSpan(cs, start, end, 1);
                    et_reply.setText(ssb);*/
                    val stricktString = "$stricketStart<u>$value</u>$stricketEnd"
                    binding.message.text.replace(start, end, Html.fromHtml(stricktString))
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
        }
    }


    fun boldFindStartIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var startIndex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                val start = word[0].toString()
                if (start == "*") {
                    if (firstTime) {
                        startIndex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        startIndex = sentence.indexOf(word, startIndex + 1)
                    }
                    indexes.add(startIndex)
                }
            }
        }

        return indexes
    }

    fun boldFindEndStarIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var firstindex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                if (word.endsWith("*")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 1
                    indexes.add(endIndex)
                } else if (word.endsWith("*,")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                } else if (word.endsWith("*.")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                }
            }
        }

        return indexes
    }

    fun italicFindStartIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var startIndex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                val start = word[0].toString()
                if (start == "_") {
                    if (firstTime) {
                        startIndex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        startIndex = sentence.indexOf(word, startIndex + 1)
                    }
                    indexes.add(startIndex)
                }
            }
        }

        return indexes
    }

    fun italicFindEndStarIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var firstindex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                if (word.endsWith("_")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 1
                    indexes.add(endIndex)
                } else if (word.endsWith("_,")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                } else if (word.endsWith("_.")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                }
            }
        }

        return indexes
    }

    fun strikethroughFindEndStarIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var firstindex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                if (word.endsWith("~")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 1
                    indexes.add(endIndex)
                } else if (word.endsWith("~,")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                } else if (word.endsWith("~.")) {
                    if (firstTime) {
                        firstindex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        firstindex = sentence.indexOf(word, firstindex + 1)
                    }
                    val worlem = word.length
                    val endIndex = firstindex + worlem - 2
                    indexes.add(endIndex)
                }
            }
        }

        return indexes
    }

    fun strikethroughFindStartIndexes(sentence: String): List<Int> {
        val indexes: MutableList<Int> = java.util.ArrayList()
        val words = sentence.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var startIndex = 0
        var firstTime = true
        for (word in words) {
            if (word.length >= 2) {
                val start = word[0].toString()
                if (start == "~") {
                    if (firstTime) {
                        startIndex = sentence.indexOf(word)
                        firstTime = false
                    } else {
                        startIndex = sentence.indexOf(word, startIndex + 1)
                    }
                    indexes.add(startIndex)
                }
            }
        }

        return indexes
    }

}

enum class AttachmentType {
    CAMERA,
    GALLERY,
    RECORDING,
    AUDIO,
    PDF
}