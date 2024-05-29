package com.app.ecarepro.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.databinding.FragmentWidgetsBinding
import com.app.ecarepro.ui.MainActivity
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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

        initView()

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.card.collectLatest {
                binding.recyclerView.withModels {
                    it.forEachIndexed { index, card ->
                        dashboardCard {
                            id(index)
                            card(card)
                            clickListener { _ ->
                                if (card.menuID > 0 && card.chmenuID > 0 && card.sbChMenuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        card.menuID, card.chmenuID,card.sbChMenuID
                                    )
                                } else if (card.menuID > 0 && card.chmenuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        card.menuID, card.chmenuID
                                    )
                                } else if (card.menuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        card.menuID
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.recyclerView.apply {

            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }
        /*
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


                    })*/
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}