package com.app.ecarepro.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.ecarepro.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat

/**
 * Dialog that shows image compression options to the user
 */
class ImageCompressionDialog : BottomSheetDialogFragment() {

    private var totalSize: Long = 0
    private var onCompressionSelected: ((Int) -> Unit)? = null

    companion object {
        const val TAG = "ImageCompressionDialog"
        private const val ARG_TOTAL_SIZE = "arg_total_size"

        fun newInstance(totalSize: Long, callback: (Int) -> Unit): ImageCompressionDialog {
            val fragment = ImageCompressionDialog()
            fragment.onCompressionSelected = callback

            val args = Bundle()
            args.putLong(ARG_TOTAL_SIZE, totalSize)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            totalSize = it.getLong(ARG_TOTAL_SIZE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_image_compression, container, false)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog
                if (dialog != null) {
                    val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    bottomSheet?.let {
                        val background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_top_rounded_corner2)
                        bottomSheet.background = background
                    }
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Format the total size to show in MB
        val sizeInMB = totalSize / (1024.0 * 1024.0)
        val df = DecimalFormat("#.##")
        val formattedSize = df.format(sizeInMB)

        // Setup UI elements
        view.findViewById<TextView>(R.id.tvMessageSize).text =
            "This message is $formattedSize MB. You can reduce message size by scaling the image to one of the sizes below."

        // Calculate sizes for each compression percentage
        val smallSizeBytes = (totalSize * 0.4).toLong()
        val mediumSizeBytes = (totalSize * 0.7).toLong()
        val largeSizeBytes = (totalSize * 0.85).toLong()

        // Format sizes for display
        val smallSizeFormatted = formatFileSize(smallSizeBytes)
        val mediumSizeFormatted = formatFileSize(mediumSizeBytes)
        val largeSizeFormatted = formatFileSize(largeSizeBytes)
        val actualSizeFormatted = formatFileSize(totalSize)

        // Setup buttons with calculated sizes
        view.findViewById<Button>(R.id.btnSmall).apply {
            text = "Small (40%) - $smallSizeFormatted"
            setOnClickListener {
                onCompressionSelected?.invoke(ImageCompressionHelper.SIZE_SMALL)
                dismiss()
            }
        }

        view.findViewById<Button>(R.id.btnMedium).apply {
            text = "Medium (70%) - $mediumSizeFormatted"
            setOnClickListener {
                onCompressionSelected?.invoke(ImageCompressionHelper.SIZE_MEDIUM)
                dismiss()
            }
        }

        view.findViewById<Button>(R.id.btnLarge).apply {
            text = "Large (85%) - $largeSizeFormatted"
            setOnClickListener {
                onCompressionSelected?.invoke(ImageCompressionHelper.SIZE_LARGE)
                dismiss()
            }
        }

        view.findViewById<Button>(R.id.btnActual).apply {
            text = "Actual Size (100%) - $actualSizeFormatted"
            setOnClickListener {
                onCompressionSelected?.invoke(ImageCompressionHelper.SIZE_ACTUAL)
                dismiss()
            }
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }
    }

    /**
     * Formats file size into a human-readable format (KB, MB)
     */
    private fun formatFileSize(bytes: Long): String {
        val df = DecimalFormat("#.##")
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${df.format(bytes / 1024.0)} KB"
            else -> "${df.format(bytes / (1024.0 * 1024.0))} MB"
        }
    }
}