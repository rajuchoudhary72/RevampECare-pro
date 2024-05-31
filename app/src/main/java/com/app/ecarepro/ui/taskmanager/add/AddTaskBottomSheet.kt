package com.app.ecarepro.ui.taskmanager.add

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.DialogAddTaskBinding
import com.app.ecarepro.model.Assignee
import com.app.ecarepro.model.Title
import com.app.ecarepro.model.Watcher
import com.app.ecarepro.taskAssigneeCarouselItem
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.FileAccess
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class AddTaskBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: AddTaskViewModel by viewModels()

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)
                binding.attachment.isVisible = true
                binding.attachment.setImageBitmap(bitmap)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
                uploadPhoto(imageString, imageExt)
            }
        }

    private fun uploadPhoto(imageString: String, imageExt: String) {
        mViewModel.attachment = Pair(imageString, imageExt)
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.attachment.isVisible = true
                    binding.attachment.setImageBitmap(bitmap)
                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt =
                        FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
                    uploadPhoto(imageString, imageExt)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogAddTaskBinding.inflate(inflater, container, false).let {
            _binding = it
            it.viewModel = mViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { dismiss() }

        binding.apply {
            startDate.setOnClickListener {
                selectDate("Select start date") {
                    mViewModel.startDate = it
                    startDate.setText(it)
                }
            }
            endDate.setOnClickListener {
                selectDate("Select end date") {
                    mViewModel.endDate = it
                    endDate.setText(it)
                }
            }

            priority.setOnItemClickListener { _, _, i, l ->
                mViewModel.priority = i
            }

            saveTaskBtn.setOnClickListener {
                (requireActivity() as MainActivity).showLoader(true)
                mViewModel.addTask { isSuccess, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage(message?:"")
                    if (isSuccess) {
                        findNavController().popBackStack()
                    }
                }
            }
            tilAttachment.setOnClickListener {
                selectImageOptionDialog()
            }

            selectAssinee.setOnClickListener {
                if (mViewModel.selectedTitle.value?.assignees.isNullOrEmpty()) {
                    mainActivity().showMessage("Please select task first to select assignee.")
                    return@setOnClickListener
                }
                selectAssignee(mViewModel.selectedTitle.value?.assignees)
            }

            selectWatcher.setOnClickListener {
                selectWatchers(mViewModel.watchers)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.uiState.collect { uiState ->
                buildModels(uiState)
            }
        }
    }

    private fun buildModels(uiState: AddTaskUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is AddTaskUiState.Success) {
            buildTaskModels(uiState.title)
        }
    }

    private fun buildTaskModels(title: List<Title>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            title.map { it.title })
        binding.taskList.setAdapter(adapter)

        binding.taskList.setOnItemClickListener { _, _, position, _ ->
            mViewModel.selectedTitle.value = title[position]
            binding.assigneeCarousel.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun selectAssignee(assignees: List<Assignee>?) {
        val multiItems = assignees?.map { it.name }?.toTypedArray()
        val checkedItems = assignees?.map { it.isSelected }?.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Assignee")
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                println(which)
            }
            .setMultiChoiceItems(multiItems, checkedItems) { dialog, which, checked ->
                checkedItems?.set(which, checked)
                assignees?.forEachIndexed { index, assignee ->
                    if (index == which) {
                        assignee.isSelected = checked
                    }
                }

                buildAssigneeModels(assignees?.filter { it.isSelected })
            }
            .show()
    }

    private fun selectWatchers(assignees: List<Watcher>) {
        val multiItems = assignees.map { it.name }.toTypedArray()
        val checkedItems = assignees.map { it.isSelected }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Watchers")
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                println(which)
            }
            .setMultiChoiceItems(multiItems, checkedItems) { dialog, which, checked ->
                checkedItems[which] = checked
                assignees.forEachIndexed { index, assignee ->
                    if (index == which) {
                        assignee.isSelected = checked
                    }
                }

                buildWatcherModels(assignees.filter { it.isSelected })
            }
            .show()
    }

    private fun buildWatcherModels(assignees: List<Watcher>) {
        binding.watcherCarousel.apply {
            isVisible = assignees.isEmpty().not()
            numViewsToShowOnScreen = 1.8f
            withModels {
                assignees.forEach {
                    taskAssigneeCarouselItem {
                        id(it.userID)
                        photo(it.photo)
                        name(it.name)
                        designation(it.designation)
                    }
                }
            }
        }
    }

    private fun buildAssigneeModels(assignees: List<Assignee>?) {
        binding.assigneeCarousel.isVisible = assignees.isNullOrEmpty().not()
        binding.assigneeCarousel.numViewsToShowOnScreen = 1.8f
        binding.assigneeCarousel.withModels {
            assignees?.forEach {
                taskAssigneeCarouselItem {
                    id(it.id)
                    photo(it.photo)
                    name(it.name)
                    designation(it.designation)
                }
            }
        }
    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(requireContext())
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
}


fun Fragment.selectDate(title: String, onDateSelection: (String) -> Unit) {

    val constraintsBuilder =
        CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

    datePicker.addOnPositiveButtonClickListener { selectedTime: Long ->
        onDateSelection(convertMillisToDateString(selectedTime))
    }
    datePicker.show(childFragmentManager, "tag");
}

private fun convertMillisToDateString(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return formatter.format(calendar.time)
}