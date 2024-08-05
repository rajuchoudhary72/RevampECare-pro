package com.app.ecarepro.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.classAttendanceCard
import com.app.ecarepro.databinding.FragmentAttendancesBinding
import com.app.ecarepro.ui.SystemViewModel
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.utils.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendancesBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: AttendanceViewModel by viewModels()

    private val systemViewModel: SystemViewModel by activityViewModels()


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
                            onClickView { _ ->
                                findNavController().navigate(
                                    R.id.classAttendanceFragment,
                                    Bundle().apply {
                                        putString(Constant.CLASS_ID_ARGUMENT, classSummary.id)
                                        putString(Constant.NAME, classSummary.className)
                                        putString(Constant.DATE, getCurrentDate())
                                    })
                               /* findNavController().navigate(
                                    R.id.classAttendanceFragment,
                                    bundleOf(
                                        Constant.CLASS_ID_ARGUMENT to classSummary.id,
                                        Constant.DATE to getCurrentDate(),
                                        Constant.NAME to classSummary.className
                                    )
                                )*/
                            }
                        }
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