package com.app.ecarepro.ui.taskmanager.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTaskListBinding
import com.app.ecarepro.model.Title
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.taskList
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.selectAssignee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.addTaskListFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                buildModels(it)
            }
        }


    }

    private fun buildModels(uiState: TaskListUiState) {

        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        binding.recyclerView.withModels {
            if (uiState is TaskListUiState.Empty) {
                noDataFoundView {
                    id(R.id.empty_view)
                }
            } else if (uiState is TaskListUiState.Success) {

                uiState.tasks.forEachIndexed { index, title: Title ->
                    taskList {
                        id(index)
                        title(title)
                        clickListener { v ->
                            if (v.id == R.id.linearLayoutAssignTo) {
                                selectAssignee(
                                    requireContext(),
                                    title.assignees ?: emptyList()
                                )
                            } else {
                                findNavController().navigate(R.id.addTaskListFragment, bundleOf("task" to title))
                            }

                        }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}