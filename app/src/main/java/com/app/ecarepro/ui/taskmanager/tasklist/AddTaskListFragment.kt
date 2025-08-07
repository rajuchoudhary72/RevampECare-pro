package com.app.ecarepro.ui.taskmanager.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.StaffTypeDto
import com.app.ecarepro.databinding.FragmentAddTaskListBinding
import com.app.ecarepro.model.Assignee
import com.app.ecarepro.taskAssigneeCarouselItem
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.selectRecipients.SelectStaffTypesFragment
import com.app.ecarepro.ui.taskmanager.add.selectAssignee
import com.google.protobuf.LazyStringArrayList.emptyList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.collections.forEach


@AndroidEntryPoint
class AddTaskListFragment : Fragment() {
    private var _binding: FragmentAddTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@AddTaskListFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCreateTask.setOnClickListener {
            if(viewModel.name.value.isEmpty() || viewModel.selectedStaffTypes.isNullOrEmpty() || viewModel.selectedAssignees.isEmpty()){
                mainActivity().showMessage("Please fill all the fields.")
                return@setOnClickListener
            }
            viewModel.saveTaskList { findNavController().popBackStack() }
        }

        binding.selectStaff.setOnClickListener {
            SelectStaffTypesFragment
                .getInstance(
                    StaffTypeDto(
                        staffType = viewModel.staffTypes.value,
                        selectedStaffType = viewModel.selectedStaffTypes
                    )

                )
                .onContactSelected {
                    binding.textStaffTypes.text = ""
                    binding.textStaffTypes.text = it.joinToString { it.staffType ?: "" }
                    binding.textStaffTypes.isVisible = it.isNotEmpty()
                    viewModel.selectedStaffTypes = it
                    viewModel.getStaffAssignee()
                }
                .show(childFragmentManager, "")
        }


        binding.selectAssinee.setOnClickListener {
            if (viewModel.selectedStaffTypes.isNullOrEmpty()) {
                mainActivity().showMessage("Please select task first to select assignee.")
                return@setOnClickListener
            }
            selectAssignee(
                requireContext(),
                viewModel.assignees.value,
                {
                    buildAssigneeModels(it)
                    viewModel.selectedAssignees = it
                }
            )


        }


        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.loading.collect {
                    mainActivity().showLoader(it)
                }
            }

            launch {
                viewModel.error.collect {
                    mainActivity().showMessage(it)
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
                    id(it.toString())
                    photo(it.photo)
                    name(it.name)
                    designation(it.designation)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}