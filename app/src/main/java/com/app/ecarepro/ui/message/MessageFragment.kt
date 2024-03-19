package com.app.ecarepro.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMessageBinding
import com.app.ecarepro.ui.message.inbox.InboxMessageFragment
import com.app.ecarepro.ui.message.sent.SentMessageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update


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
            findNavController().navigate(R.id.composeFragment)
        }
        binding.btnFilter.setOnClickListener {
            messageViewModel.showDateRangePicker()
        }

        binding.btnClearFilter.setOnClickListener {
            messageViewModel.clearFilter()
            messageViewModel.isFilterApplied.update { false }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}