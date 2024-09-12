package com.app.ecarepro.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.classAttendanceCard
import com.app.ecarepro.databinding.FragmentAttendancesBinding
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendancesBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: AttendanceViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAttendancesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                mViewModel.classes.collectLatest { classes ->
                    binding.recyclerView.withModels {
                        classes?.forEach { classSummary ->
                            classAttendanceCard {
                                id(classSummary.classID)
                                classSummary(classSummary)
                                onClickView { _ ->
                                    findNavController().navigate(
                                        R.id.classAttendanceFragment,
                                        Bundle().apply {
                                            putString(Constant.CLASS_ID_ARGUMENT, classSummary.id)
                                            putString(Constant.NAME, classSummary.className)
                                            putString(Constant.DATE, getCurrentDate())
                                        })
                                }
                            }
                        }
                    }
                }
            }

            launch {
                mViewModel.sortOptions.collectLatest { sortOptions ->
                    val arrayAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                        sortOptions.map { it.second })
                    binding.filters.setAdapter(arrayAdapter)
                    binding.filters.setText(sortOptions[0].second, false)
                    binding.filters.setOnItemClickListener { _, _, index, _ ->
                        mViewModel.sortBy(sortOptions[index].first)
                    }
                }
            }

        }
    }

    fun getCurrentDate(): String {
        val now = Date()
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return formatter.format(now)
    }

    private fun initView() {
        /*binding.toolbar.setNavigationOnClickListener {
            systemViewModel.navigateBack(true)
        }*/

        /* binding.recyclerView.apply {
             addItemDecoration(
                 LinearMarginDecoration.create(
                     margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                 )
             )
         }*/
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}