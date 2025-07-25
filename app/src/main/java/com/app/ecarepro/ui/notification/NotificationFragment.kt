package com.app.ecarepro.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentNotificationBinding
import com.app.ecarepro.notificationCard
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.data.network.model.Notification
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.noDataFoundView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var _binding: FragmentNotificationBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: NotificationViewModel by viewModels()


    private fun onSomeNotificationAction() {
        mViewModel.updateBadgeCount()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            )
                .collectLatest { uiState: NotificationUiState ->
                    handleUiState(uiState)
                }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val bundle = Bundle().apply {
            putString("ScreenName", "NotificationList")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun handleUiState(uiState: NotificationUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is NotificationUiState.Success) {
            binding.recyclerView.withModels {
                if(uiState.notifications.isEmpty()){
                    noDataFoundView {
                        id("noDataFound")
                    }
                }else{
                    uiState.notifications.forEach { notification: Notification ->
                        notificationCard {
                            id(notification.id)
                            notification(notification)
                            clickListener { _ ->
                                notification.id?.let {
                                    mViewModel.markNotificationAsSeen(it)
                                }
                                notification.moduleID?.let {
                                    notification.chMenuID?.let { it1 ->
                                        (requireActivity() as MainActivity).getFragmentId(
                                            it, it1, notification.refID
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.sendScreenEvent()
        onSomeNotificationAction()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}