package com.app.ecarepro.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.classAttendanceCard
import com.app.ecarepro.databinding.FragmentAttendancesBinding
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendancesBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: AttendanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAttendancesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.attendanceSummary.collectLatest { attendanceSummary ->
                binding.recyclerView.withModels {
                    attendanceSummary?.classSummary?.forEach { classSummary ->
                        classAttendanceCard {
                            id(classSummary.classID)
                            classSummary(classSummary)
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}