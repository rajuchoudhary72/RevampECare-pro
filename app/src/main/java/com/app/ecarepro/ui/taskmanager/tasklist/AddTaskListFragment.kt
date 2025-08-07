package com.app.ecarepro.ui.taskmanager.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentAddTaskListBinding
import com.app.ecarepro.ui.mainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddTaskListFragment : Fragment() {
    private var _binding: FragmentAddTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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

            launch {

            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}