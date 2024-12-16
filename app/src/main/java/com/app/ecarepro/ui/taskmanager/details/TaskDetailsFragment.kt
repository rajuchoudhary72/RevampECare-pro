package com.app.ecarepro.ui.taskmanager.details

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.app.ecarepro.ui.taskmanager.TaskPriority

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.group
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.TaskFiledName
import com.app.ecarepro.databinding.FragmentTaskDetailsBinding
import com.app.ecarepro.headline
import com.app.ecarepro.model.Assign
import com.app.ecarepro.sendCommentView
import com.app.ecarepro.taskAssigneeCarouselItem
import com.app.ecarepro.taskDetailAttachment
import com.app.ecarepro.taskDetailHistoryItem
import com.app.ecarepro.taskDetails
import com.app.ecarepro.taskDetailsDate
import com.app.ecarepro.taskTabs
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.TaskStatus
import com.app.ecarepro.ui.taskmanager.add.selectDate
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.makeTextWatcher
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: TaskDetailsViewModel by viewModels()

    private var isCommentSelected: Boolean = false

    private var comment: String? = ""

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                //val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
                uploadPhoto(imageString, imageExt)
            }
        }

    fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
        return when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.PNG -> "png"
            Bitmap.CompressFormat.WEBP -> "webp"
            else -> "unknown"
        }
    }

    private fun uploadPhoto(imageString: String, imageExt: String) {
        (requireActivity() as MainActivity).showLoader(true)
        mViewModel.updateAttachment(imageString, imageExt) { message: String ->
            (requireActivity() as MainActivity).showLoader(false)
            mainActivity().showMessage(message ?: "")
        }
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                    /*   val imageExt =
                           FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()*/
                    uploadPhoto(imageString, imageExt)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.uiState.collect { uiState ->
                buildModels(uiState)
            }
        }
    }

    private fun updateTask(status: Int) {
        val items = TaskStatus.getTaskApartFromThis(status)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Status")
            .setItems(items.map { it.value }.toTypedArray()) { dialog, which ->
                (requireActivity() as MainActivity).showLoader(true)
                mViewModel.updateTask(items[which].id) { _, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage(message ?: "")

                }
                dialog.dismiss()
            }
            .show()
    }

    private fun buildModels(uiState: TaskDetailsUiState) {
        if (uiState.isLoading()) {
            comment = null
        }
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        if (uiState is TaskDetailsUiState.Success && uiState.taskDetails != null) {
            try {
                binding.recyclerView.withModels {
                    taskDetails {
                        id(uiState.taskDetails.task?.id)
                        title(uiState.taskDetails.task?.taskTitle)
                        assignBy(uiState.taskDetails.task?.assignBy)
                        description(uiState.taskDetails.task?.description)
                        canEdit(uiState.taskDetails.task?.imOwner)
                        onClickEdit { v: View ->
                            if (v.id == R.id.title) {
                                openTextInputDialog(
                                    "Title",
                                    uiState.taskDetails.task?.taskTitle ?: ""
                                ) {
                                    (requireActivity() as MainActivity).showLoader(true)
                                    mViewModel.updateTask(
                                        TaskFiledName.TASK_TITLE,
                                        uiState.taskDetails.task?.taskList,
                                        it
                                    ) { isSuccess, message ->
                                        (requireActivity() as MainActivity).showLoader(false)
                                        mainActivity().showMessage(message ?: "")
                                    }
                                }
                            } else if (v.id == R.id.description) {
                                openTextInputDialog(
                                    "Description",
                                    uiState.taskDetails.task?.description ?: ""
                                ) {
                                    (requireActivity() as MainActivity).showLoader(true)
                                    mViewModel.updateTask(
                                        TaskFiledName.DESCRIPTION,
                                        uiState.taskDetails.task?.description,
                                        it
                                    ) { isSuccess, message ->
                                        (requireActivity() as MainActivity).showLoader(false)
                                        mainActivity().showMessage(message ?: "")

                                    }
                                }
                            }
                        }
                    }

                    taskDetailsDate {
                        id(uiState.taskDetails.task?.startDate)
                        startDate(uiState.taskDetails.task?.startDate)
                        endDate(uiState.taskDetails.task?.dueDate)
                        priority(uiState.taskDetails.task?.priority)
                        status(uiState.taskDetails.task?.status)
                        canEdit(uiState.taskDetails.task?.imOwner)
                        updatePriority { _ ->
                            updatePriority(uiState.taskDetails.task?.priority!!)
                        }
                        editStartDate { _ ->
                            selectDate("Start Date") {
                                (requireActivity() as MainActivity).showLoader(true)
                                mViewModel.updateTask(
                                    TaskFiledName.START_DATE,
                                    uiState.taskDetails.task?.startDate,
                                    it
                                ) { isSuccess, message ->
                                    (requireActivity() as MainActivity).showLoader(false)
                                    mainActivity().showMessage(message ?: "")
                                }
                            }
                        }
                        editEndDate { _ ->
                            selectDate("Due Date") {
                                (requireActivity() as MainActivity).showLoader(true)
                                mViewModel.updateTask(
                                    TaskFiledName.DUE_DATE,
                                    uiState.taskDetails.task?.dueDate,
                                    it
                                ) { isSuccess, message ->
                                    (requireActivity() as MainActivity).showLoader(false)
                                    mainActivity().showMessage(message ?: "")
                                }
                            }
                        }
                        updateStatusListener { _ ->
                            if (uiState.taskDetails.task?.canChangeStatus == true) {
                                uiState.taskDetails.task.status?.let { updateTask(it) }
                            }

                        }
                    }

                    if(uiState.taskDetails.task?.assignTo.isNullOrEmpty().not()){
                        group {
                            id("group")
                            layout(R.layout.group_task_assign)
                            carouselNoSnapBuilder {
                                id("car")
                                numViewsToShowOnScreen(1.9f)
                                uiState.taskDetails.task?.assignTo?.forEach { assign: Assign ->
                                    taskAssigneeCarouselItem {
                                        id(assign.userID)
                                        photo(assign.photo)
                                        name(assign.name)
                                        designation(assign.designation)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.taskDetails.task?.attachment != null || uiState.taskDetails.task?.imOwner == true) {
                        taskDetailAttachment {
                            id("attachment")
                            attachment(uiState.taskDetails.task.attachment)
                            canUploadAttachment(uiState.taskDetails.task.imOwner)
                            clickListener { _ -> selectImageOptionDialog() }
                        }
                    }

                    taskTabs {
                        id("taskTabs")
                        clickListener { v ->
                            isCommentSelected = v.id == R.id.btn_comment
                            binding.recyclerView.requestModelBuild()
                        }
                    }

                    headline {
                        id("history")
                        title(if (isCommentSelected) "Comment" else "History")
                    }

                    if (isCommentSelected) {
                        uiState.taskDetails.comments?.forEach { comment ->
                            taskDetailHistoryItem {
                                id(comment.hashCode())
                                title(comment.name + ": " + comment.comment)
                                date(comment.commentOn)
                            }
                        }

                        sendCommentView {
                            id("sendComment")
                            text(comment)
                            textWatcher(makeTextWatcher {
                                comment = it.toString()
                            })
                            clickListener { _ ->
                                if (comment.isNullOrEmpty()) {
                                    mainActivity().showMessage("Please enter comment")
                                    return@clickListener
                                }
                                (requireActivity() as MainActivity).showLoader(true)
                                mViewModel.sendComment(comment!!) { isSuccess, message ->
                                    (requireActivity() as MainActivity).showLoader(false)
                                    mainActivity().showMessage(message ?: "")
                                    if (isSuccess) {
                                        this@TaskDetailsFragment.comment = null
                                        mViewModel.refresh()
                                    }
                                }
                            }
                        }


                    } else {
                        uiState.taskDetails.activities?.forEach { activity ->
                            taskDetailHistoryItem {
                                id(activity.hashCode())
                                title(activity.name + ": " + activity.activity)
                                date(activity.actionOn)
                            }
                        }
                    }


                }
            }catch (e:NullPointerException){
                e.message
            }

        }
    }
    private fun updatePriority(priority: Int) {
        val items = TaskPriority.getTaskPriorityFromThis(priority)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Status")
            .setItems(items.map { it.value }.toTypedArray()) { dialog, which ->
                mViewModel.updateTask(
                    TaskFiledName.PRIORITY,
                    priority.toString(),
                    items[which].id.toString()
                ) { isSuccess, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage(message ?: "")
                }
            }
            .show()
    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this)
            if (items[item] == "Take Photo") {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == "Choose from Library") {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    private fun openTextInputDialog(
        title: String,
        value: String,
        updateText: (String) -> Unit
    ) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update $title")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(value)
        input.setSelection(value.length)
        input.hint = "Enter $title..."
        input.textSize = 15f
        input.setBackgroundResource(R.drawable.bg_outline_round_corner_green)
        input.setPadding(30, 30, 30, 30)

        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(resources.getDimensionPixelSize(com.app.ecarepro.R.dimen.dp_20))
        input.layoutParams = params
        container.addView(input)
        builder.setView(container)


        builder.setPositiveButton(
            "Update"
        ) { _, _ ->
            updateText(input.text.toString())
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(R.dimen.horizontal_margin)
            )
        )
    }

    fun EditText.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}