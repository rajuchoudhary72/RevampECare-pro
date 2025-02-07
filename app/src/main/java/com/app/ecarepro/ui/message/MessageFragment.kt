package com.app.ecarepro.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMessageBinding
import com.app.ecarepro.model.ComposeMessageType
import com.app.ecarepro.ui.message.inbox.InboxMessageFragment
import com.app.ecarepro.ui.message.sent.SentMessageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import com.app.ecarepro.ui.message.chat.MessageType
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null

    private val binding get() = _binding!!

    private val messageViewModel: MessageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = messageViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageViewModel.fetchMessageSettings()
        setUpViewPager()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSendSmsAppMessage.setOnClickListener {
            lifecycleScope.launch {
                messageViewModel.sendAnalyticEvent(
                    AnalyticsConstants.Events.SEND_SMS_APP_MESSAGE,
                    mapOf(
                        AnalyticsConstants.Attributes.USER_ID to messageViewModel.userDataStore.getUser()?.userId.toString(),
                        AnalyticsConstants.Attributes.USER_TYPE to messageViewModel.userDataStore.getUser()?.userType.toString(),
                        AnalyticsConstants.Attributes.SCHOOL_CODE to messageViewModel.userDataStore.getSchoolData()?.schoolCode.toString(),
                    )
                )
            }
            findNavController().navigate(
                R.id.composeFragment,
                bundleOf("composeMessageType" to ComposeMessageType.SMS_AND_APP_MESSAGE)
            )
        }

        binding.btnOnlyAppMessage.setOnClickListener {
            lifecycleScope.launch {
                messageViewModel.sendAnalyticEvent(
                    AnalyticsConstants.Events.ONLY_APP_MESSAGE,
                    mapOf(
                        AnalyticsConstants.Attributes.USER_ID to messageViewModel.userDataStore.getUser()?.userId.toString(),
                        AnalyticsConstants.Attributes.USER_TYPE to messageViewModel.userDataStore.getUser()?.userType.toString(),
                        AnalyticsConstants.Attributes.SCHOOL_CODE to messageViewModel.userDataStore.getSchoolData()?.schoolCode.toString(),
                    )
                )
            }
            findNavController().navigate(
                R.id.composeFragment,
                bundleOf("composeMessageType" to ComposeMessageType.ONLY_APP_MESSAGE)
            )
        }

        binding.btnFilter.setOnClickListener {
            messageViewModel.showDateRangePicker()
        }

        binding.btnClearFilter.setOnClickListener {
            messageViewModel.clearFilter()
            messageViewModel.isFilterApplied.update { false }
        }
        arguments?.let {args ->
            if (args.getString("ID")=="Menu"){

            }else{
                if(args.getString("ID").isNullOrEmpty().not()){
                    findNavController().navigate(
                        R.id.chatFragment,
                        bundleOf(
                            "ID" to args.getString("ID"),
                            "MessageType" to MessageType.INBOX.value
                        )
                    )
                    args.remove("ID")
                }
            }
        }
    }

    private fun setUpViewPager() {

        val tabItem = mutableListOf("Inbox", "Sent")

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    InboxMessageFragment()
                } else {
                    SentMessageFragment()
                }
            }

        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.btnFilter.isVisible = position == 1
                binding.btnClearFilter.isVisible =
                    position == 1 && messageViewModel.isFilterApplied.value
            }
        })

    }

    override fun onResume() {
        super.onResume()
        messageViewModel.sendScreenEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}