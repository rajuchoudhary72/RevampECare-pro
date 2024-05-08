package com.app.ecarepro.ui.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTaskManagerBinding
import com.app.ecarepro.task
import com.app.ecarepro.taskSummary
import com.app.ecarepro.ui.MainActivity
import com.google.android.material.tabs.TabLayout
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskManagerFragment : Fragment() {

    private var _binding: FragmentTaskManagerBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: TaskManagerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskManagerBinding.inflate(inflater, container, false)
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
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is TaskManagerUiState.Success && uiState.tasksDto != null) {
            binding.recyclerView.withModels {

                if (uiState.tasksDto.overdue.isNullOrEmpty().not()) {
                    taskSummary {
                        id("overdue")
                        title("Overdue")
                        count("${uiState.tasksDto.overdue?.size ?: 0} Tasks")
                    }

                    uiState.tasksDto.overdue?.forEach { task ->
                        task {
                            id(task.id)
                            task(task)
                        }
                    }

                }
                if (uiState.tasksDto.todays.isNullOrEmpty().not()) {
                    taskSummary {
                        id("todays")
                        title("Today's")
                        count("${uiState.tasksDto.todays?.size ?: 0} Tasks")
                    }

                    uiState.tasksDto.todays?.forEach { task ->
                        task {
                            id(task.id)
                            task(task)
                        }
                    }

                }
                if (uiState.tasksDto.upcoming.isNullOrEmpty().not()) {
                    taskSummary {
                        id("upcoming")
                        title("Upcoming")
                        count("${uiState.tasksDto.upcoming?.size ?: 0} Tasks")
                    }

                    uiState.tasksDto.upcoming?.forEach { task ->
                        task {
                            id(task.id)
                            task(task)
                        }
                    }

                }
                if (uiState.tasksDto.closed.isNullOrEmpty().not()) {
                    taskSummary {
                        id("closed")
                        title("Closed")
                        count("${uiState.tasksDto.closed?.size ?: 0} Tasks")
                    }

                    uiState.tasksDto.closed?.forEach { task ->
                        task {
                            id(task.id)
                            task(task)
                        }
                    }

                }

            }
        }

    }

    private fun initViews() {

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