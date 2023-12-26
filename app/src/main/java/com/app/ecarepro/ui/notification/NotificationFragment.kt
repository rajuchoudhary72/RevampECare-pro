package com.app.ecarepro.ui.notification

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
import com.app.ecarepro.databinding.FragmentNotificationBinding
import com.app.ecarepro.databinding.FragmentWidgetsBinding
import com.app.ecarepro.notificationCard
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerView.apply {

            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )

            withModels {
                (0..25).forEach {
                    notificationCard { id(it) }
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}