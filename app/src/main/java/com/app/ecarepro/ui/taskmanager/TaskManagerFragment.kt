package com.app.ecarepro.ui.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTaskManagerBinding
import com.app.ecarepro.model.Task
import com.app.ecarepro.task
import com.app.ecarepro.taskSummary
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.AddTaskBottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskManagerFragment : Fragment() {

    private var _binding: FragmentTaskManagerBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: TaskManagerViewModel by viewModels()

    private var extendedTaskId = R.id.overdue_tasks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskManagerBinding.inflate(inflater, container, false).apply {
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

    private fun buildModels(uiState: TaskManagerUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is TaskManagerUiState.Success && uiState.tasksDto != null) {
            binding.recyclerView.withModels {

                if (uiState.tasksDto.overdue.isNullOrEmpty().not()) {
                    taskSummary {
                        id(R.id.overdue_tasks)
                        title("Overdue")
                        count("${uiState.tasksDto.overdue?.size ?: 0} Tasks")
                        clickListener { _ ->
                            if (extendedTaskId != R.id.overdue_tasks) {
                                extendedTaskId = R.id.overdue_tasks
                                this@withModels.requestModelBuild()
                            } else {
                                extendedTaskId = -1
                                this@withModels.requestModelBuild()
                            }
                        }

                    }

                    if (extendedTaskId == R.id.overdue_tasks)
                        uiState
                            .tasksDto
                            .overdue
                            ?.filter {
                                it.taskTitle?.contains(uiState.searchQuery) ?: true || it.taskList?.contains(
                                    uiState.searchQuery
                                ) ?: true
                            }
                            ?.forEach { task ->
                                task {
                                    id(task.id)
                                    task(task)
                                    clickListener { _ ->
                                        navigateToDetails(task)
                                    }
                                    updateStatusListener { _ ->
                                        updateTask(task)
                                    }
                                }
                            }

                }
                if (uiState.tasksDto.todays.isNullOrEmpty().not()) {
                    taskSummary {
                        id(R.id.today_tasks)
                        title("Today's")
                        count("${uiState.tasksDto.todays?.size ?: 0} Tasks")
                        clickListener { _ ->
                            if (extendedTaskId != R.id.today_tasks) {
                                extendedTaskId = R.id.today_tasks
                                this@withModels.requestModelBuild()
                            } else {
                                extendedTaskId = -1
                                this@withModels.requestModelBuild()
                            }
                        }
                    }

                    if (extendedTaskId == R.id.today_tasks)
                        uiState
                            .tasksDto
                            .todays
                            ?.filter {
                                it.taskTitle?.contains(uiState.searchQuery) ?: true || it.taskList?.contains(
                                    uiState.searchQuery
                                ) ?: true
                            }
                            ?.forEach { task ->
                                task {
                                    id(task.id)
                                    task(task)
                                    clickListener { _ ->
                                        navigateToDetails(task)
                                    }
                                    updateStatusListener { _ ->
                                        updateTask(task)
                                    }
                                }
                            }

                }
                if (uiState.tasksDto.upcoming.isNullOrEmpty().not()) {
                    taskSummary {
                        id(R.id.upcoming_tasks)
                        title("Upcoming")
                        count("${uiState.tasksDto.upcoming?.size ?: 0} Tasks")
                        clickListener { _ ->
                            if (extendedTaskId != R.id.upcoming_tasks) {
                                extendedTaskId = R.id.upcoming_tasks
                                this@withModels.requestModelBuild()
                            } else {
                                extendedTaskId = -1
                                this@withModels.requestModelBuild()
                            }
                        }
                    }
                    if (extendedTaskId == R.id.upcoming_tasks)
                        uiState
                            .tasksDto
                            .upcoming
                            ?.filter {
                                it.taskTitle?.contains(uiState.searchQuery) ?: true || it.taskList?.contains(
                                    uiState.searchQuery
                                ) ?: true
                            }
                            ?.forEach { task ->
                                task {
                                    id(task.id)
                                    task(task)
                                    clickListener { _ ->
                                        navigateToDetails(task)
                                    }
                                    updateStatusListener { _ ->
                                        updateTask(task)
                                    }
                                }
                            }

                }
                if (uiState.tasksDto.closed.isNullOrEmpty().not()) {
                    taskSummary {
                        id(R.id.closed_tasks)
                        title("Closed")
                        count("${uiState.tasksDto.closed?.size ?: 0} Tasks")
                        clickListener { _ ->
                            if (extendedTaskId != R.id.closed_tasks) {
                                extendedTaskId = R.id.closed_tasks
                                this@withModels.requestModelBuild()
                            } else {
                                extendedTaskId = -1
                                this@withModels.requestModelBuild()
                            }
                        }
                    }

                    if (extendedTaskId == R.id.closed_tasks)
                        uiState
                            .tasksDto
                            .closed
                            ?.filter {
                                it.taskTitle?.contains(uiState.searchQuery) ?: true || it.taskList?.contains(
                                    uiState.searchQuery
                                ) ?: true
                            }
                            ?.forEach { task ->
                                task {
                                    id(task.id)
                                    task(task)
                                    clickListener { _ ->
                                        navigateToDetails(task)
                                    }
                                    updateStatusListener { _ ->
                                        updateTask(task)
                                    }
                                }
                            }

                }

            }
        }

    }

    private fun updateTask(task: Task) {
        val items = TaskStatus.getTaskApartFromThis(task.status)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Status")
            .setItems(items.map { it.value }.toTypedArray()) { dialog, which ->
                (requireActivity() as MainActivity).showLoader(true)
                mViewModel.updateTask(task, items[which].id) { _, message ->
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage(message?:"")

                }
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToDetails(task: Task) {
        findNavController().navigate(
            R.id.taskDetailsFragment,
            bundleOf(
                "taskId" to task.id,
                "taskTitle" to task.taskList
            )
        )
    }

    private fun initViews() {

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnAddTask.setOnClickListener {
            AddTaskBottomSheet().show(childFragmentManager, "")
        }

        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(R.dimen.vertical_margin)
            )
        )

        TaskFilter.values().forEach { filter ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().apply {
                setText(filter.key)
                setTag(filter.value)
            })
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mViewModel.setFilter(tab?.tag as Int)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class TaskStatus(val value: String, val id: Int) {
    OPEN("Open", 0),
    IN_PROGRESS("In Progress", 1),
    HOLD("Hold", 2),
    CLOSED("Closed", 3);

    companion object {
        fun getTaskApartFromThis(id: Int): List<TaskStatus> {
            return values().filterNot { it.id == id }
        }
    }
}
enum class TaskPriority(val value: String, val id: Int) {
    LOW("Low", 0),
    HIGH("High", 2),
    NORMAL("Normal", 1),
    URGENT("Urgent", 3);


    companion object {
        fun getTaskPriorityFromThis(id: Int): List<TaskPriority> {
            return values().filterNot { it.id == id }
        }
    }
}