package com.app.ecarepro.utils

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentManager
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener


fun launchGallery(
    launcher: ActivityResultLauncher<Intent>,
    multiSelection: Boolean = true
) {
    val intent = Intent()
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
    intent.action = Intent.ACTION_GET_CONTENT
    launcher.launch(Intent.createChooser(intent, "Select Image(s)"))
}

fun launchAudioPicker(
    launcher: ActivityResultLauncher<Intent>,
    multiSelection: Boolean = true
) {
    val intent = Intent()
    intent.type = "audio/*"
    intent.action = Intent.ACTION_GET_CONTENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
    launcher.launch(intent)
}

fun launchPdfPicker(
    launcher: ActivityResultLauncher<Intent>,
    multiSelection: Boolean = true
) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*" // Allow any file type
        putExtra(
            Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
        )
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
    }
    launcher.launch(intent)
}


fun openAudioRecorder(
    fragmentManager: FragmentManager,
    onSuccess: (uri: String?) -> Unit,
    onFailure: (errorMessage: String?) -> Unit,
) {

    VoiceSenderDialog(object : AudioRecordListener {
        override fun onAudioReady(audioUri: String?) {
            onSuccess(audioUri)
        }

        override fun onReadyForRecord() {}

        override fun onRecordFailed(errorMessage: String?) {
            onFailure(errorMessage)
        }
    }).show(fragmentManager, "VOICE")
}

