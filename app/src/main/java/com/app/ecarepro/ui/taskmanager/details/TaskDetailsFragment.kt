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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
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
import com.app.ecarepro.taskAssigneeCarouselItem
import com.app.ecarepro.taskDetailAttachment
import com.app.ecarepro.taskDetailHistoryItem
import com.app.ecarepro.taskDetails
import com.app.ecarepro.taskDetailsDate
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.selectDate
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.utils.FileAccess
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: TaskDetailsViewModel by viewModels()

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
                uploadPhoto(imageString, imageExt)
            }
        }

    private fun uploadPhoto(imageString: String, imageExt: String) {
        (requireActivity() as MainActivity).showLoader(true)
        mViewModel.updateAttachment(imageString, imageExt){ message:String ->
            (requireActivity() as MainActivity).showLoader(false)
            mainActivity().showMessage(message?:"")
        }
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt =
                        FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
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

    private fun buildModels(uiState: TaskDetailsUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is TaskDetailsUiState.Success && uiState.taskDetails != null) {
            binding.recyclerView.withModels {
                taskDetails {
                    id(uiState.taskDetails.task?.id)
                    title(uiState.taskDetails.task?.taskTitle)
                    assignBy(uiState.taskDetails.task?.assignBy)
                    description(uiState.taskDetails.task?.description)
                    canEdit(uiState.taskDetails.task?.imOwner)
                    onClickEdit { v: View ->
                        if (v.id == R.id.title) {
                            openTextInputDialog("Title", uiState.taskDetails.task?.taskTitle ?: "") {
                                (requireActivity() as MainActivity).showLoader(true)
                                mViewModel.updateTask(
                                    TaskFiledName.TASK_TITLE,
                                    uiState.taskDetails.task?.taskList,
                                    it
                                ) { isSuccess, message ->
                                    (requireActivity() as MainActivity).showLoader(false)
                                    mainActivity().showMessage(message?:"")

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
                                    mainActivity().showMessage(message?:"")


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
                    canEdit(uiState.taskDetails.task?.imOwner)
                    editStartDate { _ ->
                        selectDate("Start Date") {
                            (requireActivity() as MainActivity).showLoader(true)
                            mViewModel.updateTask(
                                TaskFiledName.START_DATE,
                                uiState.taskDetails.task?.startDate,
                                it
                            ) { isSuccess, message ->
                                (requireActivity() as MainActivity).showLoader(false)
                                mainActivity().showMessage(message?:"")
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
                                mainActivity().showMessage(message?:"")
                            }
                        }
                    }
                }

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

                if (uiState.taskDetails.task?.attachment != null || uiState.taskDetails.task?.imOwner == true) {
                    taskDetailAttachment {
                        id("attachment")
                        attachment(uiState.taskDetails.task.attachment)
                        canUploadAttachment(uiState.taskDetails.task.imOwner)
                        clickListener { _ -> selectImageOptionDialog()}
                    }
                }

                if (uiState.taskDetails.activities.isNullOrEmpty().not()) {
                    headline {
                        id("history")
                        title("History")
                    }
                    uiState.taskDetails.activities?.forEach { activity ->
                        taskDetailHistoryItem {
                            id(activity.actorID)
                            title(activity.name + ": " + activity.activity)
                            date(activity.actionOn)
                        }
                    }
                }
            }
        }

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