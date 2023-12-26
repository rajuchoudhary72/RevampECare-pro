package com.app.ecarepro.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.epoxy.EpoxyTouchHelper.DragCallbacks
import com.app.ecarepro.DashboardCardBindingModel_
import com.app.ecarepro.R
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.databinding.FragmentWidgetsBinding
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WidgetsFragment : Fragment() {

    private var _binding: FragmentWidgetsBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: WidgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWidgetsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var controller:EpoxyController? = null

        binding.recyclerView.apply {

            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )

            withModels {controller = this
                (0..25).forEach {
                    dashboardCard { id(it) }
                }
            }
        }

        EpoxyTouchHelper
            .initDragging(controller)
            .withRecyclerView(binding.recyclerView)
            .forVerticalList()
            .withTarget(DashboardCardBindingModel_::class.java)
            .andCallbacks(object : DragCallbacks<DashboardCardBindingModel_>() {
                override fun onModelMoved(
                    fromPosition: Int,
                    toPosition: Int,
                    modelBeingMoved: DashboardCardBindingModel_?,
                    itemView: View?
                ) {

                }


            })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}