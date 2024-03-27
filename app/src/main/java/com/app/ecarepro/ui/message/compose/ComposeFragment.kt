package com.app.ecarepro.ui.message.compose

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.app.ecarepro.R
import com.app.ecarepro.attachment
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.ContactsDto
import com.app.ecarepro.databinding.FragmentComposeBinding
import com.app.ecarepro.recipientChip
import com.app.ecarepro.ui.message.selectRecipients.SelectRecipientsFragment
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ComposeFragment : Fragment() {

    private var _binding: FragmentComposeBinding? = null
    private val binding get() = _binding!!

    private val composeViewModel: ComposeViewModel by viewModels()

    private var lastClickAttachmentType: AttachmentType? = null

    private val mPermissionSettingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requestExternalStoragePermission()
        }


    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                composeViewModel.setAttachments(selectedMedia)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComposeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                composeViewModel.attachments.collectLatest { attachments ->
                    buildAttachmentModels(attachments)
                }
            }
            launch {
                composeViewModel.contacts.collectLatest { contacts ->
                    buildChipGroup(contacts)
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
            lastClickAttachmentType = AttachmentType.PDF
            requestExternalStoragePermission()
        }

        binding.btnBrowseAudio.setOnClickListener {
            lastClickAttachmentType = AttachmentType.AUDIO
            requestExternalStoragePermission()
        }

        binding.btnGallery.setOnClickListener {
            lastClickAttachmentType = AttachmentType.GALLERY
            requestExternalStoragePermission()
        }
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

    private fun convertUrlToDrawable(url: String?, result: (Drawable) -> Unit) {
        val loader = ImageLoader(context = requireContext())
        val req = ImageRequest.Builder(requireContext())
            .data(url)
            .transformations(RoundedCornersTransformation(100f, 100f, 100f, 100f))
            .target { drawable ->
                result(drawable)
            }
            .build()
        loader.enqueue(req)
    }

    /**
     *   If Android device SDK is >= 30 and wants to access document (only for choose the non media file)
     *   then ask for "android.permission.MANAGE_EXTERNAL_STORAGE" permission
     */
    private fun requestExternalStoragePermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (Environment.isExternalStorageManager()) {
                    launchPicker()

                } else {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(
                            String.format("package:%s", requireContext().packageName)
                        )
                        mPermissionSettingResult.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        mPermissionSettingResult.launch(intent)
                    }
                }
            }

            else -> {
                launchPicker()
            }
        }
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
        val intent = getLasiIntent()
            .setMediaType(MediaType.IMAGE)
            .setMaxCount(7)
            .build()
        receiveData.launch(intent)
    }

    private fun launchAudioPicker() {
        val intent = getLasiIntent()
            .setMediaType(MediaType.AUDIO)
            .setMaxCount(1)
            .build()
        receiveData.launch(intent)
    }

    private fun launchPdfPicker() {
        val intent = getLasiIntent()
            .setMediaType(MediaType.DOC)
            .setMaxCount(7)
            .setSupportedFileTypes(
                "pdf"
            )
            .build()
        receiveData.launch(intent)
    }

    private fun getLasiIntent() = Lassi(requireContext())
        .setStatusBarColor(R.color.md_theme_light_primary)
        .setToolbarColor(R.color.md_theme_light_primary)
        .setToolbarResourceColor(android.R.color.white)
        .setAlertDialogNegativeButtonColor(R.color.black)
        .setAlertDialogPositiveButtonColor(R.color.md_theme_light_primary)
        .setGalleryBackgroundColor(R.color.white)
        .setProgressBarColor(R.color.md_theme_light_primary)
        .setGridSize(3)


    /* private fun getMimeType(uri: Uri): String? {
         return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
             contentResolver.getType(uri)
         } else {
             val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
             MimeTypeMap.getSingleton()
                 .getMimeTypeFromExtension(fileExtension.lowercase(Locale.getDefault()))
         }
     }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class AttachmentType {
    CAMERA,
    GALLERY,
    RECORDING,
    AUDIO,
    PDF
}