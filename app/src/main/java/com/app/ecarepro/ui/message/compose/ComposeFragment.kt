package com.app.ecarepro.ui.message.compose

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.app.ecarepro.ui.message.selectRecipients.SelectRecipientsFragment
import com.app.ecarepro.utils.FileUtils
import com.app.ecarepro.utils.getFile
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


@AndroidEntryPoint
class ComposeFragment : Fragment() {

    private var _binding: FragmentComposeBinding? = null
    private val binding get() = _binding!!

    private val composeViewModel: ComposeViewModel by viewModels()

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
                composeViewModel.setAttachments(selectedMedia)
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = composeViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            composeViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState: ComposeUiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun handleUiState(uiState: ComposeUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        if (uiState is ComposeUiState.Success) {
            buildAttachmentModels(uiState.attachments)
            buildChipGroup(uiState.contacts)
            setUpSmsTypes(uiState.smsTypes)
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
            attachments.forEach { attachment ->
                attachment {
                    id(attachment.id)
                    image(attachment.path)
                    onClickRemove { _ ->
                        composeViewModel.removeAttachment(attachment)
                    }
                }
            }
        }
    }

    private fun setUpViews() {

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnReplyMessage.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            composeViewModel.sendMessage { isSuccess, message ->
                (requireActivity() as MainActivity).showLoader(false)
                mainActivity().showMessage(message ?: "")
                if (isSuccess) {
                    findNavController().popBackStack()
                }
            }
        }

        binding.btnAddRecipient.setOnClickListener {

            setFragmentResultListener(SelectRecipientsFragment.SELECT_CONTACT_REQUEST_KEY) { requestKey, bundle ->
                if (bundle.containsKey(SelectRecipientsFragment.SELECTED_CONTACT)) {
                    val contacts: ContactsDto =
                        bundle.getSerializable(SelectRecipientsFragment.SELECTED_CONTACT) as ContactsDto
                    composeViewModel.setContacts(contacts.contacts)
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
            launchPicker()
        }

        binding.btnRecord.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.AUDIO
            openAudioRecorder()
        }

        binding.btnCamera.setOnClickListener {
            hideAttachmentCard()
            lastClickAttachmentType = AttachmentType.CAMERA
            checkCameraPermissions()
        }
    }

    private fun openAudioRecorder() {
        VoiceSenderDialog(object : AudioRecordListener {
            override fun onAudioReady(audioUri: String?) {
                composeViewModel.setAttachments(listOf(MiMedia(path = audioUri)))
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

    private fun launchPicker() {
        when (lastClickAttachmentType) {
            AttachmentType.GALLERY -> {
                launchPhotoPicker()
            }

            AttachmentType.AUDIO -> {
                launchAudioPicker()
            }

            AttachmentType.PDF -> {
                launchPdfPicker()
            }

            else -> {}
        }
    }

    private fun launchPhotoPicker() {
        val intent = getLasiIntent().setMediaType(MediaType.IMAGE).setMaxCount(7).build()
        receiveData.launch(intent)
    }

    private fun launchAudioPicker() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pdfLauncher.launch(intent)
    }

    private fun launchPdfPicker() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pdfLauncher.launch(intent)
    }

    private fun getLasiIntent() =
        Lassi(requireContext()).setStatusBarColor(R.color.md_theme_light_primary)
            .setToolbarColor(R.color.md_theme_light_primary)
            .setToolbarResourceColor(android.R.color.white)
            .setAlertDialogNegativeButtonColor(R.color.black)
            .setAlertDialogPositiveButtonColor(R.color.md_theme_light_primary)
            .setGalleryBackgroundColor(R.color.white)
            .setProgressBarColor(R.color.md_theme_light_primary).setGridSize(3)

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
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
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


    private fun startLocationFetch() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 120
            )
            return
        }
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
}

enum class AttachmentType {
    CAMERA,
    GALLERY,
    RECORDING,
    AUDIO,
    PDF
}