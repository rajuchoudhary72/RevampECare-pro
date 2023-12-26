package com.app.ecarepro.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.databinding.FragmentMessageBinding
import com.app.ecarepro.ui.message.inbox.InboxMessageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    private fun setUpViewPager() {

        val tabItem = mutableListOf("Inbox", "Sent")

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {
                return InboxMessageFragment()
            }

        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}