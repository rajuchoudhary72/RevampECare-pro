package com.app.ecarepro.ui.taskmanager.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.group
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTaskDetailsBinding
import com.app.ecarepro.headline
import com.app.ecarepro.model.Assign
import com.app.ecarepro.taskAssigneeCarouselItem
import com.app.ecarepro.taskDetailAttachment
import com.app.ecarepro.taskDetailHistoryItem
import com.app.ecarepro.taskDetails
import com.app.ecarepro.taskDetailsDate
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: TaskDetailsViewModel by viewModels()

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
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is TaskDetailsUiState.Success && uiState.taskDetails != null) {
            binding.recyclerView.withModels {
                taskDetails {
                    id(uiState.taskDetails.task?.id)
                    title(uiState.taskDetails.task?.taskList)
                    assignBy(uiState.taskDetails.task?.assignBy)
                    description(uiState.taskDetails.task?.description)
                }

                taskDetailsDate {
                    id(uiState.taskDetails.task?.startDate)
                    startDate(uiState.taskDetails.task?.startDate)
                    endDate(uiState.taskDetails.task?.dueDate)
                    priority(uiState.taskDetails.task?.priority)
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

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(R.dimen.horizontal_margin)
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}